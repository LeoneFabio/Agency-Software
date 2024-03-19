package com.example.softwareamm.controls;

import com.example.softwareamm.commons.DBMSBound;

import javax.xml.transform.Result;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;

public class TimeCtrl {

    DBMSBound db = new DBMSBound();
    public boolean disponibilitaOrario (int oraInizioTurnoPrecedente, int oraInizioTurno){
        if(oraInizioTurnoPrecedente < oraInizioTurno || oraInizioTurnoPrecedente == -1){
            return true;
        }else return false;
    }
    public void disponibilitaGiornata(LocalDate oggi) throws SQLException {
        db.creaConnessione();

        ResultSet turniDipendentiIeri = db.myQuery("SELECT * FROM dipendenti d, turni tt    " +
                " WHERE d.matricola = tt.ref_dipendente and tt.data_odierna = '" + oggi.minusDays(1) + "'");

        while (turniDipendentiIeri.next()){
            if(turniDipendentiIeri.getInt("ora_inizio_turno") > 17) {
                db.myUpdate("UPDATE dipendenti SET ref_stato = 5 WHERE matricola = " + turniDipendentiIeri.getInt("matricola"));
            }else {
                db.myUpdate("UPDATE dipendenti SET ref_stato = 1 WHERE matricola = " + turniDipendentiIeri.getInt("matricola"));
            }
        }

        ResultSet listaDipendentiNonOperativi = db.myQuery( "SELECT *     " +
                                                "FROM dipendenti d, comunicazioni c, comunicazioni_proposta_variazione cpv " +
                                                "WHERE d.matricola = c.ref_utente_mittente and c.id_comunicazione = cpv.ref_comunicazione and cpv.data_inizio_pv <= '" + oggi + "' and cpv.data_fine_pv >= '" + oggi + "' and (cpv.ref_tipo_proposta_variazione = 6 or ref_tipo_proposta_variazione = 3) and ref_stato_comunicazione != 2 ");

        while (listaDipendentiNonOperativi.next()){
            switch (listaDipendentiNonOperativi.getInt("ref_tipo_proposta_variazione")){
                case 7:{
                    db.myUpdate("UPDATE dipendenti SET ref_stato = 7 WHERE matricola = " + listaDipendentiNonOperativi.getInt("matricola"));
                    break;
                }
                case 2:{
                    db.myUpdate("UPDATE dipendenti SET ref_stato = 2 WHERE matricola = " + listaDipendentiNonOperativi.getInt("matricola"));
                    break;
                }
                case 3:{
                    db.myUpdate("UPDATE dipendenti SET ref_stato = 3 WHERE matricola = " + listaDipendentiNonOperativi.getInt("matricola"));
                    break;
                }
                case 4:{
                    db.myUpdate("UPDATE dipendenti SET ref_stato = 4 WHERE matricola = " + listaDipendentiNonOperativi.getInt("matricola"));
                    break;
                }
                case 6: {
                    db.myUpdate("UPDATE dipendenti SET ref_stato = 6 WHERE matricola = " + listaDipendentiNonOperativi.getInt("matricola"));
                    break;
                }
            }
        }

    }
    public void turnazioneTriemstrale()  {
        try{
        ResultSet rs = db.myQuery("SELECT MAX(data_odierna) from turni");
        rs.next();
        LocalDate inizio = rs.getDate(1).toLocalDate().plusDays(1);
      
       if(LocalDate.now().isAfter(inizio.minusDays(10))) {
           var a = System.currentTimeMillis();
           LocalDate oggi = inizio;
           db.creaConnessione();
           LocalDate fine = inizio.plusMonths(3);
           ResultSet listaDipendenti;
           int mattina;
           int pomeriggio;
           int notte;
           int ruolo;
           int numDipendenti;

           while (oggi.isBefore(fine)) {
               ruolo = 1;
               while (ruolo <= 4) {
                   mattina = 0;
                   pomeriggio = 0;
                   notte = 0;
                   disponibilitaGiornata(oggi);
                   listaDipendenti = db.myQuery("SELECT * FROM dipendenti d, turni tt  " +
                           "WHERE d.matricola = tt.ref_dipendente and ref_ruolo = " + ruolo + " and tt.data_odierna = '" + oggi.minusDays(1) + "' " +
                           "ORDER BY data_odierna, d.ref_ruolo, ref_dipendente");
                   listaDipendenti.afterLast();
                   listaDipendenti.previous();
                   numDipendenti = listaDipendenti.getRow();
                   listaDipendenti.beforeFirst();

                   switch (ruolo) {

                       case 1: {

                           while (listaDipendenti.next()) {


                               if (listaDipendenti.getInt("ref_stato") == 1) {

                                   if (listaDipendenti.getInt("tt.ora_inizio_turno") == -1 && mattina < (numDipendenti / 3)) {
                                       db.myUpdate("INSERT INTO `turni`(`ref_dipendente`, `data_odierna`, `ora_inizio_turno`, `ora_fine_turno`, stato_giornaliero, ruolo_giornaliero) " +
                                               "VALUES ('" + listaDipendenti.getInt("matricola") + "','" + oggi + "','6','14', " + listaDipendenti.getInt("ref_stato") + ", 1)");

                                       mattina++;
                                       continue;
                                   } else {
                                       while (mattina < 4 && disponibilitaOrario(listaDipendenti.getInt("ora_inizio_turno"), 6)) {

                                           db.myUpdate("INSERT INTO `turni`(`ref_dipendente`, `data_odierna`, `ora_inizio_turno`, `ora_fine_turno`, stato_giornaliero, ruolo_giornaliero) " +
                                                   "VALUES ('" + listaDipendenti.getInt("matricola") + "','" + oggi + "','6','14', " + listaDipendenti.getInt("ref_stato") + ", 1)");
                                           mattina++;
                                           if (!listaDipendenti.isLast()) {
                                               listaDipendenti.next();
                                           } else {
                                               break;
                                           }
                                       }
                                   }

                                   if (listaDipendenti.getInt("ora_inizio_turno") == 6 && pomeriggio < (numDipendenti / 3)) {
                                       db.myUpdate("INSERT INTO `turni`(`ref_dipendente`, `data_odierna`, `ora_inizio_turno`, `ora_fine_turno`, stato_giornaliero, ruolo_giornaliero) " +
                                               "VALUES ('" + listaDipendenti.getInt("matricola") + "','" + oggi + "','14','22', " + listaDipendenti.getInt("ref_stato") + ", 1)");
                                       pomeriggio++;
                                       continue;
                                   } else {

                                       while (pomeriggio < 4 && disponibilitaOrario(listaDipendenti.getInt("ora_inizio_turno"), 14)) {
                                           db.myUpdate("INSERT INTO `turni`(`ref_dipendente`, `data_odierna`, `ora_inizio_turno`, `ora_fine_turno`, stato_giornaliero, ruolo_giornaliero) " +
                                                   "VALUES ('" + listaDipendenti.getInt("matricola") + "','" + oggi + "','14','22', " + listaDipendenti.getInt("ref_stato") + ", 1)");
                                           pomeriggio++;
                                           if (!listaDipendenti.isLast()) {
                                               listaDipendenti.next();
                                           } else {
                                               break;
                                           }
                                       }
                                   }

                                   if (listaDipendenti.getInt("ora_inizio_turno") == 14 && notte < (numDipendenti / 3)) {
                                       db.myUpdate("INSERT INTO `turni`(`ref_dipendente`, `data_odierna`, `ora_inizio_turno`, `ora_fine_turno`, stato_giornaliero, ruolo_giornaliero) " +
                                               "VALUES ('" + listaDipendenti.getInt("matricola") + "','" + oggi + "','22','6', " + listaDipendenti.getInt("ref_stato") + ", 1)");
                                       notte++;
                                       continue;
                                   } else {

                                       while (notte < 4 && disponibilitaOrario(listaDipendenti.getInt("ora_inizio_turno"), 22)) {

                                           db.myUpdate("INSERT INTO `turni`(`ref_dipendente`, `data_odierna`, `ora_inizio_turno`, `ora_fine_turno`, stato_giornaliero, ruolo_giornaliero) " +
                                                   "VALUES ('" + listaDipendenti.getInt("matricola") + "','" + oggi + "','22','6', " + listaDipendenti.getInt("ref_stato") + ", 1)");
                                           notte++;
                                           if (!listaDipendenti.isLast()) {
                                               listaDipendenti.next();
                                           } else {
                                               break;
                                           }
                                       }
                                   }

                                   if (listaDipendenti.getInt("ora_inizio_turno") == 22) {

                                       db.myUpdate("INSERT INTO `turni`(`ref_dipendente`, `data_odierna`, `ora_inizio_turno`, `ora_fine_turno`, stato_giornaliero, ruolo_giornaliero, firma_ingresso, firma_uscita) " +
                                               "VALUES ('" + listaDipendenti.getInt("matricola") + "','" + oggi + "','-1','-1', " + listaDipendenti.getInt("ref_stato") + ", 1,1,1)");
                                   }

                               } else {
                                   db.myUpdate("INSERT INTO `turni`(`ref_dipendente`, `data_odierna`, `ora_inizio_turno`, `ora_fine_turno`, stato_giornaliero, ruolo_giornaliero, firma_ingresso, firma_uscita) " +
                                           "VALUES ('" + listaDipendenti.getInt("matricola") + "','" + oggi + "','-1','-1', " + listaDipendenti.getInt("ref_stato") + ", 1, 1, 1)");
                               }
                           }

                           break;
                       }

                       case 2: {

                           while (listaDipendenti.next()) {


                               if (listaDipendenti.getInt("ref_stato") == 1) {
                                   if (listaDipendenti.getInt("ora_inizio_turno") == -1 && mattina < (numDipendenti / 3)) {
                                       db.myUpdate("INSERT INTO `turni`(`ref_dipendente`, `data_odierna`, `ora_inizio_turno`, `ora_fine_turno`, stato_giornaliero, ruolo_giornaliero) " +
                                               "VALUES ('" + listaDipendenti.getInt("matricola") + "','" + oggi + "','6','14', " + listaDipendenti.getInt("ref_stato") + ", 2)");
                                       mattina++;
                                       continue;
                                   } else {

                                       while (mattina < 4 && disponibilitaOrario(listaDipendenti.getInt("ora_inizio_turno"), 6)) {

                                           db.myUpdate("INSERT INTO `turni`(`ref_dipendente`, `data_odierna`, `ora_inizio_turno`, `ora_fine_turno`, stato_giornaliero, ruolo_giornaliero) " +
                                                   "VALUES ('" + listaDipendenti.getInt("matricola") + "','" + oggi + "','6','14', " + listaDipendenti.getInt("ref_stato") + ", 2)");
                                           mattina++;
                                           if (!listaDipendenti.isLast()) {
                                               listaDipendenti.next();
                                           } else {
                                               break;
                                           }
                                       }
                                   }

                                   if (listaDipendenti.getInt("ora_inizio_turno") == 6 && pomeriggio < (numDipendenti / 3)) {
                                       db.myUpdate("INSERT INTO `turni`(`ref_dipendente`, `data_odierna`, `ora_inizio_turno`, `ora_fine_turno`, stato_giornaliero, ruolo_giornaliero) " +
                                               "VALUES ('" + listaDipendenti.getInt("matricola") + "','" + oggi + "','14','22', " + listaDipendenti.getInt("ref_stato") + ", 2)");
                                       pomeriggio++;
                                       continue;
                                   } else {

                                       while (pomeriggio < 4 && disponibilitaOrario(listaDipendenti.getInt("ora_inizio_turno"), 14)) {
                                           db.myUpdate("INSERT INTO `turni`(`ref_dipendente`, `data_odierna`, `ora_inizio_turno`, `ora_fine_turno`, stato_giornaliero, ruolo_giornaliero) " +
                                                   "VALUES ('" + listaDipendenti.getInt("matricola") + "','" + oggi + "','14','22', " + listaDipendenti.getInt("ref_stato") + ", 2)");
                                           pomeriggio++;
                                           if (!listaDipendenti.isLast()) {
                                               listaDipendenti.next();
                                           } else {
                                               break;
                                           }
                                       }
                                   }

                                   if (listaDipendenti.getInt("ora_inizio_turno") == 14 && notte < (numDipendenti / 3)) {
                                       db.myUpdate("INSERT INTO `turni`(`ref_dipendente`, `data_odierna`, `ora_inizio_turno`, `ora_fine_turno`, stato_giornaliero, ruolo_giornaliero) " +
                                               "VALUES ('" + listaDipendenti.getInt("matricola") + "','" + oggi + "','18','2', " + listaDipendenti.getInt("ref_stato") + ", 2)");
                                       notte++;
                                       continue;
                                   } else {

                                       while (notte < 4 && disponibilitaOrario(listaDipendenti.getInt("ora_inizio_turno"), 18)) {

                                           db.myUpdate("INSERT INTO `turni`(`ref_dipendente`, `data_odierna`, `ora_inizio_turno`, `ora_fine_turno`, stato_giornaliero, ruolo_giornaliero) " +
                                                   "VALUES ('" + listaDipendenti.getInt("matricola") + "','" + oggi + "','18','2', " + listaDipendenti.getInt("ref_stato") + ", 2)");
                                           notte++;

                                           if (!listaDipendenti.isLast()) {
                                               listaDipendenti.next();
                                           } else {
                                               break;
                                           }

                                       }
                                   }
                                   if (listaDipendenti.getInt("ora_inizio_turno") == 18) {
                                       db.myUpdate("INSERT INTO `turni`(`ref_dipendente`, `data_odierna`, `ora_inizio_turno`, `ora_fine_turno`, stato_giornaliero, ruolo_giornaliero, firma_ingresso, firma_uscita) " +
                                               "VALUES ('" + listaDipendenti.getInt("matricola") + "','" + oggi + "','-1','-1', " + listaDipendenti.getInt("ref_stato") + ", 2, 1, 1)");
                                   }
                               } else {
                                   db.myUpdate("INSERT INTO `turni`(`ref_dipendente`, `data_odierna`, `ora_inizio_turno`, `ora_fine_turno`, stato_giornaliero, ruolo_giornaliero, firma_ingresso, firma_uscita) " +
                                           "VALUES ('" + listaDipendenti.getInt("matricola") + "','" + oggi + "','-1','-1', " + listaDipendenti.getInt("ref_stato") + ", 2, 1, 1)");
                               }
                           }

                           break;
                       }

                       case 3: {
                           while (listaDipendenti.next()) {

                               if (listaDipendenti.getInt("ref_stato") == 1) {
                                   if (listaDipendenti.getInt("ora_inizio_turno") == -1 && mattina < (numDipendenti / 2)) {
                                       db.myUpdate("INSERT INTO `turni`(`ref_dipendente`, `data_odierna`, `ora_inizio_turno`, `ora_fine_turno`, stato_giornaliero, ruolo_giornaliero) " +
                                               "VALUES ('" + listaDipendenti.getInt("matricola") + "','" + oggi + "','6','14', " + listaDipendenti.getInt("ref_stato") + ", 3)");
                                       mattina++;
                                       continue;
                                   } else {

                                       while (mattina < 4 && disponibilitaOrario(listaDipendenti.getInt("ora_inizio_turno"), 6)) {
                                           db.myUpdate("INSERT INTO `turni`(`ref_dipendente`, `data_odierna`, `ora_inizio_turno`, `ora_fine_turno`, stato_giornaliero, ruolo_giornaliero) " +
                                                   "VALUES ('" + listaDipendenti.getInt("matricola") + "','" + oggi + "','6','14', " + listaDipendenti.getInt("ref_stato") + ", 3)");
                                           mattina++;
                                           if (!listaDipendenti.isLast()) {
                                               listaDipendenti.next();
                                           } else {
                                               break;
                                           }
                                       }
                                   }
                                   if (listaDipendenti.getInt("ora_inizio_turno") == 6 && pomeriggio < (numDipendenti / 2)) {
                                       db.myUpdate("INSERT INTO `turni`(`ref_dipendente`, `data_odierna`, `ora_inizio_turno`, `ora_fine_turno`, stato_giornaliero, ruolo_giornaliero) " +
                                               "VALUES ('" + listaDipendenti.getInt("matricola") + "','" + oggi + "','14','22', " + listaDipendenti.getInt("ref_stato") + ", 3)");
                                       pomeriggio++;

                                   } else {

                                       while (pomeriggio < 4 && disponibilitaOrario(listaDipendenti.getInt("ora_inizio_turno"), 14)) {
                                           db.myUpdate("INSERT INTO `turni`(`ref_dipendente`, `data_odierna`, `ora_inizio_turno`, `ora_fine_turno`, stato_giornaliero, ruolo_giornaliero) " +
                                                   "VALUES ('" + listaDipendenti.getInt("matricola") + "','" + oggi + "','14','22', " + listaDipendenti.getInt("ref_stato") + ", 3)");
                                           pomeriggio++;
                                           System.out.println("PROVA " + pomeriggio);
                                           if (!listaDipendenti.isLast()) {
                                               listaDipendenti.next();
                                           } else {
                                               break;
                                           }
                                       }
                                   }
                                   if (listaDipendenti.getInt("ora_inizio_turno") == 14) {
                                       db.myUpdate("INSERT INTO `turni`(`ref_dipendente`, `data_odierna`, `ora_inizio_turno`, `ora_fine_turno`, stato_giornaliero, ruolo_giornaliero, firma_ingresso, firma_uscita) " +
                                               "VALUES ('" + listaDipendenti.getInt("matricola") + "','" + oggi + "','-1','-1', " + listaDipendenti.getInt("ref_stato") + ", 3, 1, 1)");
                                   }
                               } else {

                                   db.myUpdate("INSERT INTO `turni`(`ref_dipendente`, `data_odierna`, `ora_inizio_turno`, `ora_fine_turno`, stato_giornaliero, ruolo_giornaliero, firma_ingresso, firma_uscita) " +
                                           "VALUES ('" + listaDipendenti.getInt("matricola") + "','" + oggi + "','-1','-1', " + listaDipendenti.getInt("ref_stato") + ", 3, 1, 1)");
                               }
                           }
                           break;
                       }

                       case 4: {
                           while (listaDipendenti.next()) {


                               if (listaDipendenti.getInt("ref_stato") == 1) {
                                   if (listaDipendenti.getInt("ora_inizio_turno") == -1) {
                                       db.myUpdate("INSERT INTO `turni`(`ref_dipendente`, `data_odierna`, `ora_inizio_turno`, `ora_fine_turno`, stato_giornaliero, ruolo_giornaliero) " +
                                               "VALUES ('" + listaDipendenti.getInt("matricola") + "','" + oggi + "','6','14', " + listaDipendenti.getInt("ref_stato") + ", 4)");
                                   } else if (listaDipendenti.getInt("ora_inizio_turno") == 6) {
                                       db.myUpdate("INSERT INTO `turni`(`ref_dipendente`, `data_odierna`, `ora_inizio_turno`, `ora_fine_turno`, stato_giornaliero, ruolo_giornaliero) " +
                                               "VALUES ('" + listaDipendenti.getInt("matricola") + "','" + oggi + "','14','22', " + listaDipendenti.getInt("ref_stato") + ", 4)");

                                   } else if (listaDipendenti.getInt("ora_inizio_turno") == 14) {
                                       db.myUpdate("INSERT INTO `turni`(`ref_dipendente`, `data_odierna`, `ora_inizio_turno`, `ora_fine_turno`, stato_giornaliero, ruolo_giornaliero, firma_ingresso, firma_uscita) " +
                                               "VALUES ('" + listaDipendenti.getInt("matricola") + "','" + oggi + "','-1','-1', " + listaDipendenti.getInt("ref_stato" +
                                               "") + ", 4, 1, 1)");
                                   }
                               } else {
                                   db.myUpdate("INSERT INTO `turni`(`ref_dipendente`, `data_odierna`, `ora_inizio_turno`, `ora_fine_turno`, stato_giornaliero, ruolo_giornaliero, firma_ingresso, firma_uscita) " +
                                           "VALUES ('" + listaDipendenti.getInt("matricola") + "','" + oggi + "','-1','-1', " + listaDipendenti.getInt("ref_stato") + ", 4, 1, 1)");
                               }
                           }
                           break;
                       }
                   }
                   ruolo++;
               }
               oggi = oggi.plusDays(1);
           }
           System.out.println(System.currentTimeMillis() - a);
           ResultSet estremiTurnazione = db.myQuery("SELECT MAX(data_odierna) AS ultima, MIN(data_odierna) AS prima FROM turni");
           estremiTurnazione.next();
           LocalDateTime inizioPV = estremiTurnazione.getDate("prima").toLocalDate().atTime(0, 0, 0).plusMonths(3);
           LocalDateTime finePV = estremiTurnazione.getDate("ultima").toLocalDate().atTime(0, 0, 0);
            int idProposta = db.insertComunicazionePV(27, 27, LocalDate.now(), inizioPV, finePV, "turnazione", "Nuova proposta di turnazione trimestrale");
           db.myUpdate("UPDATE comunicazioni SET ref_stato_comunicazione = 3 WHERE id_comunicazione = " + idProposta);
       }
       }catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public double calcoloStipendio(int matricola)  {
            db.creaConnessione();
            double[] datiStipendio;
            int oreEffettuate = db.acquisisciOreEffettuate(matricola);
            datiStipendio = db.acquisisciInfoStipendio(matricola);
            int oreStraordinari = (int) datiStipendio[3];
            double tassoAgevolazioni = datiStipendio[2];
            int baseOraria = (int) datiStipendio[0];
            double tassoStraordinario = datiStipendio[1];
            double totStipendio = (oreEffettuate + tassoStraordinario * oreStraordinari) * baseOraria * tassoAgevolazioni;


            if (LocalDate.now().getDayOfMonth() == 1) {
                db.inserisciStipendio(matricola, oreEffettuate, baseOraria, tassoAgevolazioni, tassoStraordinario, oreStraordinari, totStipendio);
            }

            return totStipendio;

    }
    public void turnazioneGiornaliera(LocalDate oggi) throws SQLException {
        ResultSet dipendenti = db.myQuery("SELECT * FROM DIPENDENTI where ref_ruolo = 4");

        for(int i=0; i<6; i++){
            dipendenti.next();

            if(i < 6){
                db.myUpdate("INSERT INTO `turni`(`ref_dipendente`, `data_odierna`, `ora_inizio_turno`, `ora_fine_turno`, ruolo_giornaliero) " +
                        "VALUES ('" + dipendenti.getInt("matricola") + "','" + oggi + "','6','14',4)");
            } else if (i < 12){
                db.myUpdate("INSERT INTO `turni`(`ref_dipendente`, `data_odierna`, `ora_inizio_turno`, `ora_fine_turno`, ruolo_giornaliero) " +
                        "VALUES ('" + dipendenti.getInt("matricola") + "','" + oggi + "','14','22',4)");
           /* }else if( i< 18){
                db.myUpdate("INSERT INTO `turni`(`ref_dipendente`, `data_odierna`, `ora_inizio_turno`, `ora_fine_turno`, ruolo_giornaliero) " +
                        "VALUES ('" + dipendenti.getInt("matricola") + "','" + oggi + "','18','2',2");*/
            }else{
                db.myUpdate("INSERT INTO `turni`(`ref_dipendente`, `data_odierna`, `ora_inizio_turno`, `ora_fine_turno`, ruolo_giornaliero, stato_giornaliero) " +
                        "VALUES ('" + dipendenti.getInt("matricola") + "','" + oggi + "','-1','-1', 4, 5)");
            }
        }
    } // da rimuovere
    public void comunicazioneRitardo() {
        try {
            db.creaConnessione();
            ResultSet listaRitardi = db.myQuery("SELECT ref_dipendente   FROM turni       WHERE data_odierna = '" + LocalDate.now() + "' and firma_ingresso = 0 AND ora_inizio_turno = " + LocalTime.now().getHour());
            ResultSet dipendente;
            while (listaRitardi.next()) {
                db.creaNuovaComunicazioneCD(27, listaRitardi.getInt("ref_dipendente"), LocalDate.now(), "Le comunico che non risulta effettuata la sua firma in ingresso, la preghiamo di procedere alla firma da remoto tramite il portale. Le comunichiamo inoltre che sarà aggiornato il suo numero di ritardi. ");
                db.myUpdate("UPDATE dipendenti SET numero_ritardi = numero_ritardi + 1     WHERE matricola = " + listaRitardi.getInt("ref_dipendente"));
                dipendente = db.myQuery("SELECT numero_ritardi, soglia_ritardi   FROM dipendenti, ruoli    WHERE matricola = " + listaRitardi.getInt("ref_dipendente") + " and ref_ruolo = id_ruolo ");
                dipendente.next();
                if (dipendente.getInt("numero_ritardi") > dipendente.getInt("soglia_ritardi")) {
                    db.creaNuovaComunicazioneCD(27, listaRitardi.getInt("ref_dipendente"), LocalDate.now(), "La informiamo che ha superato la soglia dei ritardi consentita. ");
                }
            }
            ResultSet listaDipInRitardo = db.myQuery("SELECT d.matricola FROM dipendenti d, ruoli r WHERE d.ref_ruolo = r.id_ruolo AND d.numero_ritardi > r.soglia_ritardi ");
            String matricoleRit = "| ";
            boolean check = false;
            while (listaDipInRitardo.next()) {
                check = true;
                matricoleRit += listaDipInRitardo.getString("matricola") + " | ";
            }
            if (check) {
                db.creaNuovaComunicazioneCD(27, 27, LocalDate.now(), "Di seguito è riportata la lista dei dipendenti che hanno superato la soglia dei ritardi consentita:\n" + matricoleRit);
            }
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }
    public void ComunicazioneDimenticanza() {
        try {
            db.creaConnessione();
            ResultSet listaDimenticanzeDip;
            ResultSet dipendente;
            if (LocalTime.now().getHour() > 6) {
                listaDimenticanzeDip = db.myQuery("SELECT ref_dipendente, firma_ingresso, ora_inizio_turno, ora_fine_turno FROM turni  " +
                        "WHERE data_odierna = '" + LocalDate.now() + "' and firma_uscita = 0 and ora_fine_turno = " + LocalTime.now().getHour());
                while (listaDimenticanzeDip.next()) {
                    db.myUpdate("UPDATE dipendenti SET numero_dimenticanze = numero_dimenticanze + 1     WHERE matricola = " + listaDimenticanzeDip.getInt("ref_dipendente"));
                    if (listaDimenticanzeDip.getBoolean("firma_ingresso")) {
                        db.myUpdate("UPDATE turni SET firma_uscita = 1 WHERE  ref_dipendente = " + listaDimenticanzeDip.getInt("ref_dipendente") + " AND data_odierna = '" + LocalDate.now() + "'");
                    }
                    dipendente = db.myQuery("SELECT numero_dimenticanze, soglia_dimenticanze   FROM dipendenti, ruoli    WHERE matricola = " + listaDimenticanzeDip.getInt("ref_dipendente") + " and ref_ruolo = id_ruolo ");
                    dipendente.next();
                    if (dipendente.getInt("numero_dimenticanze") > dipendente.getInt("soglia_dimenticanze")) {
                        db.creaNuovaComunicazioneCD(27, listaDimenticanzeDip.getInt("ref_dipendente"), LocalDate.now(), "La informiamo che ha superato la soglia delle dimenticanze consentita. ");
                    }
                }

            } else {
                listaDimenticanzeDip = db.myQuery("SELECT ref_dipendente, firma_ingresso, ora_inizio_turno, ora_fine_turno FROM turni  " +
                        "WHERE data_odierna = '" + LocalDate.now().minusDays(1) + "' and firma_uscita = 0 and ora_fine_turno = " + LocalTime.now().getHour());
                while (listaDimenticanzeDip.next()) {
                    db.myUpdate("UPDATE dipendenti SET numero_dimenticanze = numero_dimenticanze + 1     WHERE matricola = " + listaDimenticanzeDip.getInt("ref_dipendente"));
                    if (listaDimenticanzeDip.getBoolean("firma_ingresso")) {
                        db.myUpdate("UPDATE turni SET firma_uscita = 1 WHERE  ref_dipendente = " + listaDimenticanzeDip.getInt("ref_dipendente") + " AND data_odierna = '" + LocalDate.now().minusDays(1) + "'");
                    }
                    dipendente = db.myQuery("SELECT numero_dimenticanze, soglia_dimenticanze   FROM dipendenti, ruoli    WHERE matricola = " + listaDimenticanzeDip.getInt("ref_dipendente") + " and ref_ruolo = id_ruolo ");
                    dipendente.next();
                    if (dipendente.getInt("numero_dimenticanze") > dipendente.getInt("soglia_dimenticanze")) {
                        db.creaNuovaComunicazioneCD(27, listaDimenticanzeDip.getInt("ref_dipendente"), LocalDate.now(), "La informiamo che ha superato la soglia delle dimenticanze consentita. ");
                    }
                }

            }
            ResultSet listaDipInRitardo = db.myQuery("SELECT d.matricola FROM dipendenti d, ruoli r WHERE d.ref_ruolo = r.id_ruolo AND d.numero_dimenticanze > r.soglia_dimenticanze ");
            String matricoleRit = "| ";
            boolean check = false;
            while (listaDipInRitardo.next()) {
                check = true;
                matricoleRit += listaDipInRitardo.getString("matricola") + " | ";
            }
            if (check) {
                db.creaNuovaComunicazioneCD(27, 27, LocalDate.now(), "Di seguito è riportata la lista dei dipendenti che hanno superato la soglia delle dimenticanze consentita:\n" + matricoleRit);
            }
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }


    public void scadenzaPV() {
        try {
            db.creaConnessione();
            ResultSet proposte = db.myQuery("SELECT * FROM comunicazioni_proposta_variazione cpv, comunicazioni c " +
                    "WHERE c.id_comunicazione = cpv.ref_comunicazione AND c.ref_stato_comunicazione = 3 AND cpv.data_scadenza <= '" + LocalDate.now() + "' and ref_tipo_proposta_variazione != 6 and ref_tipo_proposta_variazione != 3 ");
            ResultSet mittente;
            while (proposte.next()) {
                int tipoPV = proposte.getInt("ref_tipo_proposta_variazione");
                mittente = db.myQuery("SELECT * FROM turni WHERE ref_dipendente = " + proposte.getInt("ref_utente_mittente") + " " +
                        "and data_odierna >= '" + proposte.getDate("data_inizio_pv") + "' and data_odierna <= '" + proposte.getDate("data_fine_pv") + "'");
                mittente.next();
                LocalDate contData = proposte.getDate("data_inizio_pv").toLocalDate();
                ResultSet numDipendenti;
                if (tipoPV != 8) {
                    while (contData.isBefore(proposte.getDate("data_fine_pv").toLocalDate().plusDays(1))) {
                        System.out.println(contData);
                        numDipendenti = db.myQuery("SELECT count(*) FROM turni " +
                                "WHERE data_odierna = '" + contData + "' AND ruolo_giornaliero = " + mittente.getInt("ruolo_giornaliero") + " AND ora_inizio_turno = " + mittente.getInt("ora_inizio_turno") + " AND ref_dipendente != " + mittente.getInt("ref_dipendente"));
                        numDipendenti.next();


                        int matricolaDipIdoneo = calcoloDipendenteIdoneo(mittente.getInt("ref_dipendente"), proposte.getDate("data_inizio_pv").toLocalDate());

                        if (numDipendenti.getInt(1) < 4) {
                            //matricolaDipIdoneo = calcoloDipendenteIdoneo(mittente.getInt("ref_dipendente"), proposte.getDate("data_inizio_pv").toLocalDate());
                            if ((tipoPV == 7 || tipoPV == 4) && (proposte.getTime("data_inizio_pv").toLocalTime().getHour() - proposte.getTime("data_inizio_pv").toLocalTime().getHour()) < 4) {
                                //insert dipendente idoneo nel mezzo del turno e mettere straordinari
                                int oraInizioPermesso = proposte.getTime("data_inizio_pv").toLocalTime().getHour();
                                int oraFinePermesso = proposte.getTime("data_fine_pv").toLocalTime().getHour();
                                int oreStraordinarie = oraFinePermesso - oraInizioPermesso;
                                int oraFineTurno = mittente.getInt("ora_fine_turno");
                                ResultSet turnoDipIdoneo = db.acquisisciTurnoDip(matricolaDipIdoneo, contData);
                                turnoDipIdoneo.next();
                                db.myUpdate("UPDATE `turni` SET `ora_fine_turno`='" + oraInizioPermesso + "' WHERE ref_dipendente = " + mittente.getInt("ref_dipendente") + " AND data_odierna = '" + contData + "'");
                                if (turnoDipIdoneo.getInt("ora_inizio_turno") != mittente.getInt("ora_inizio_turno")) {
                                    db.myUpdate("INSERT INTO `turni`(`data_odierna`, `ref_dipendente`, `ora_inizio_turno`, `ora_fine_turno`, `ore_straordinarie_odierne`, `ruolo_giornaliero`) VALUES ('" + contData + "','" + matricolaDipIdoneo + "','" + oraInizioPermesso + "','" + oraFinePermesso + "','" + oreStraordinarie + "','" + mittente.getInt("ruolo_giornaliero") + "')");
                                } else {

                                    db.myUpdate("UPDATE `turni` SET `ora_fine_turno`='" + oraInizioPermesso + "' WHERE ref_dipendente = " + turnoDipIdoneo.getInt("ref_dipendente") + " AND data_odierna = '" + contData + "'");
                                    db.myUpdate("INSERT INTO `turni`(`data_odierna`, `ref_dipendente`, `ora_inizio_turno`, `ora_fine_turno`, `ruolo_giornaliero`) VALUES ('" + contData + "','" + matricolaDipIdoneo + "','" + oraInizioPermesso + "','" + oraFinePermesso + "','" + mittente.getInt("ruolo_giornaliero") + "')");
                                    if (oraFinePermesso != oraFineTurno) {
                                        db.myUpdate("INSERT INTO `turni`(`data_odierna`, `ref_dipendente`, `ora_inizio_turno`, `ora_fine_turno`, `ruolo_giornaliero`) " +
                                                "VALUES ('" + contData + "','" + turnoDipIdoneo.getInt("ref_dipendente") + "','" + oraFinePermesso + "','" + oraFineTurno + "','" + turnoDipIdoneo.getInt("ruolo_giornaliero") + "')");
                                    }
                                    if(oraInizioPermesso == mittente.getInt("ora_inizio_turno"));{
                                        db.myUpdate("DELETE FROM turni WHERE data_odierna = '" +contData+ "' AND ref_dipendente = "+mittente.getInt("ref_dipendente") + " AND ora_inizio_turno = "+ mittente.getInt("ora_inizio_turno"));
                                    }
                                }
                                if (oraFinePermesso != oraFineTurno) {
                                    db.myUpdate("INSERT INTO `turni`(`data_odierna`, `ref_dipendente`, `ora_inizio_turno`, `ora_fine_turno`, `ruolo_giornaliero`) " +
                                            "VALUES ('" + contData + "','" + mittente.getInt("ref_dipendente") + "','" + oraFinePermesso + "','" + oraFineTurno + "','" + mittente.getInt("ruolo_giornaliero") + "')");
                                }

                            } else {
                                int oraInizioTurno = mittente.getInt("ora_inizio_turno");
                                int oraFineTurno = mittente.getInt("ora_fine_turno");
                                int ruoloMittente = mittente.getInt("ruolo_giornaliero");
                                int mittenteMatricola = mittente.getInt("ref_dipendente");
                                db.myUpdate("UPDATE `turni` SET `ora_inizio_turno`='" + oraInizioTurno + "',`ora_fine_turno`='" + oraFineTurno + "',`ruolo_giornaliero`='" + ruoloMittente + "' WHERE data_odierna = '" + contData + "' AND ref_dipendente = " + matricolaDipIdoneo); //cambio il turno del dipIdoneo
                                db.myUpdate("UPDATE `turni` SET `ora_inizio_turno`='-1',`ora_fine_turno`='-1',`stato_giornaliero`='" + tipoPV + "' WHERE data_odierna = '" + contData + "' AND ref_dipendente = " + mittenteMatricola);// metto il mittente a riposos e cambio il suo stato
                            }


                        }
                        System.out.println(matricolaDipIdoneo + " matricola idoneo");
                        db.myUpdate("UPDATE comunicazioni SET ref_stato_comunicazione = 1 WHERE id_comunicazione = "+ proposte.getInt("id_comunicazione") +" AND ref_utente_destinatario = "+ matricolaDipIdoneo);
                        db.myUpdate("UPDATE comunicazioni SET ref_stato_comunicazione = 2 WHERE id_comunicazione = "+ proposte.getInt("id_comunicazione") +" AND ref_utente_destinatario != "+ matricolaDipIdoneo);
                        db.myUpdate("UPDATE comunicazioni SET ref_stato_comunicazione = 1 WHERE id_comunicazione = "+ proposte.getInt("id_comunicazione") +" AND ref_utente_mittente = ref_utente_destinatario ");

                        contData = contData.plusDays(1);
                        if (tipoPV == 7) {
                            int orePermesso = proposte.getTime("data_inizio_pv").toLocalTime().getHour() - proposte.getTime("data_inizio_pv").toLocalTime().getHour();
                            db.myUpdate("UPDATE dipendenti SET ore_permessi_prese = ore_permessi_prese + " + orePermesso + "WHERE matricola = " + mittente.getInt("ref_dipendente"));
                        }
                    }
                } else {
                    //APPROVA TURNAZIONE
                    db.aggiornaStatoPV(proposte.getInt("ref_comunicazione"), 1);
                    ResultSet ferieCongedo = db.myQuery("SELECT * FROM comunicazioni_proposta_variazione, comunicazioni WHERE id_comunicazione = ref_comunicazione and ref_tipo_proposta_variazione = 6 or ref_tipo_proposta_variazione = 3 AND ref_stato_comunicazione = 3");
                    while (ferieCongedo.next()) {
                        db.myUpdate("UPDATE comunicazioni SET ref_stato_comunicazione = 1 WHERE id_comunicazione = " + ferieCongedo.getInt("ref_comunicazione"));

                    }
                }
            }
        }catch(SQLException e){
            throw new RuntimeException(e);
        }
    }

    public int calcoloDipendenteIdoneo(int mittente, LocalDate dataInizio) throws SQLException {
      db.creaConnessione();
      ResultSet rs = db.acquisisciNumColleghi(dataInizio, mittente);
      int max = 0;
      int ruolo=0, turno=0;
      while(rs.next()){
        if(rs.getInt("num_dipendenti") > max ) {
            max = rs.getInt("num_dipendenti");
            turno = rs.getInt("ora_inizio_turno");
            ruolo = rs.getInt("ruolo_giornaliero");
        }
      }
      if(max <= 4 && ruolo != 4){
          rs.beforeFirst();
          while (rs.next()){
              if(rs.getInt("ruolo_giornaliero") == 4){
                  turno = rs.getInt("ora_inizio_turno");
                  ruolo = rs.getInt("ruolo_giornaliero");
              }
          }
      }
      rs = db.acquisisciTurnoOra(dataInizio, turno, ruolo, mittente);
      while (rs.next()){
          if(rs.getInt("ora_fine_turno") == turno+8 && rs.getInt("ref_dipendente") != mittente){
              return rs.getInt("ref_dipendente");
          }
      }

      return 0;
    }
    public void ripristinoSoglie(){
        if(LocalDate.now().getDayOfYear() == 1){
            db.myUpdate("UPDATE dipendenti SET ore_permessi_prese = 0, giorni_ferie_presi = 0, numero_ritardi = 0, numero_dimenticanze = 0 ");
            db.myUpdate("UPDATE amministratori SET rimanentiPIA = 90 ");
        }else if(LocalDate.now().getDayOfMonth() == 1 && ((LocalDate.now().getMonthValue() % 3) == 1)){
            db.myUpdate("UPDATE dipendenti SET ore_permessi_prese = 0, numero_ritardi = 0, numero_dimenticanze = 0 ");
        }
    }
    public void eliminaTurnazionePrecedente(){
        if((LocalDate.now().getDayOfMonth() == 1 && ((LocalDate.now().getMonthValue() % 3) == 1)) && (LocalDate.now().getMonthValue() != 1)){
            db.myUpdate("DELETE FROM turni WHERE data_odierna < '" + LocalDate.now() + "'");
        }
    }












}
