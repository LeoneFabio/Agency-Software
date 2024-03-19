package com.example.softwaredip.controls;

import com.example.softwaredip.commons.DBMSBound;
import com.example.softwaredip.commons.Utils;
import com.example.softwaredip.entity.Utente;
import javafx.scene.control.Alert;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;

/**Control del caso d'uso FirmaDaRemoto
 * @version 1.0*/

public class FirmaRemotoCtrl {

    /**Preleva dal DataBase nome e cognome dell'utente loggato*/
    public static ArrayList<String> prelevaInfoDipendete(){

        DBMSBound db = new DBMSBound();
        ArrayList<String> info = new ArrayList<>();

        if(db.creaConnessione()){

            ResultSet rsInfoDip = db.acquisisciInfoUtente(Utente.utenteLoggato.getMatricola());
            try{
                rsInfoDip.next();
                info.add(rsInfoDip.getString("nome"));
                info.add(rsInfoDip.getString("cognome"));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        } else {
            Utils.mostraPopUpErroreDB();
        }

        return info;
    }

    /**Verifica se l'utente loggato può effettuare, se si la firma viene salvata
     * @param motivazione motivazione della firma da remoto*/

    public static void effettuaFirmaIngresso(String motivazione){
        DBMSBound db = new DBMSBound();

        LocalDate dataAttuale = LocalDate.now();

        //CONNESSIONE CORRETTA
        if(db.creaConnessione()){
            try {
                //MATRICOLA INTERA
                int matricola = Utente.utenteLoggato.getMatricola();

                ResultSet rsInfoTurno;
                if (LocalTime.now().getHour() < 6) {
                    rsInfoTurno = db.myQuery("SELECT * FROM turni WHERE ref_dipendente = " + matricola + " AND data_odierna = '" + dataAttuale.minusDays(1) + "' AND ora_inizio_turno <= " + LocalTime.now().getHour() +" AND ora_fine_turno > "+LocalTime.now().getHour()+"" );
                } else {
                    rsInfoTurno = db.myQuery("SELECT * FROM turni WHERE ref_dipendente = " + matricola + " AND data_odierna = '" + dataAttuale + "' AND ora_inizio_turno < " + LocalTime.now().getHour() +" AND ora_fine_turno > "+LocalTime.now().getHour()+"");

                }

                // SE ESISTE UN TURNO
                if (Utils.isNotEmptyResulset(rsInfoTurno)) {
                    while (rsInfoTurno.next()) {
                        // SE NON E GIORNATA RIPOSO
                        if (rsInfoTurno.getInt("ora_inizio_turno") != -1 && rsInfoTurno.getInt("ora_fine_turno") != -1) {

                            LocalTime oraAttuale = LocalTime.now();

                            //SE L'ORA E LA STESSA, ENTRO DIECI MINUTI
                            if ((oraAttuale.getHour() == rsInfoTurno.getInt("ora_inizio_turno") && oraAttuale.getMinute() > 10) || oraAttuale.getHour() > rsInfoTurno.getInt("ora_inizio_turno")) {

                                db.aggiornaFlagIngresso(matricola, rsInfoTurno.getDate("data_odierna").toLocalDate(), rsInfoTurno.getInt("ora_inizio_turno"));

                                String messaggio = "Si comunica che il dipendente " + Utente.utenteLoggato.getMatricola() + " ha effettuato in data " + Utils.formattaData(LocalDate.now()) + " una firma in ritardo\n" +
                                        "Motivazione: " + motivazione;

                                db.creaNuovaComunicazioneCD(Utente.utenteLoggato.getMatricola(), db.acquisisciMatricolaAmm(), LocalDate.now(), messaggio);
                                Utils.mostraPopUp(Alert.AlertType.INFORMATION, "Firma effettuata", "Firma avvenuta con successo");
                            } else {
                                Utils.messaggioConsole("ORARIO NON VALIDO", Utils.Tipo_messaggio.DEBUGGING);
                                Utils.mostraPopUp(Alert.AlertType.ERROR, "Errore orario", "Firma non consentita, orario non valido.");
                            }

                        } else {
                            Utils.messaggioConsole("GIORNATA RIPOSO", Utils.Tipo_messaggio.DEBUGGING);
                            Utils.mostraPopUp(Alert.AlertType.ERROR, "Errore firma", "Non è stato trovato un turno corrispondente. Riprovare.");
                        }
                    }

                } else {
                    Utils.messaggioConsole("NON ESISTE UN TURNO", Utils.Tipo_messaggio.DEBUGGING);
                    Utils.mostraPopUp(Alert.AlertType.ERROR, "Errore firma", "Non è stato trovato un turno corrispondente. Riprovare.");
                }


            } catch (NumberFormatException | SQLException e) {
                Utils.messaggioConsole("ERRORE MATRICOLA", Utils.Tipo_messaggio.DEBUGGING);
                Utils.mostraPopUp(Alert.AlertType.ERROR, "Errore inserimento", "I dati inseriti non sono corretti. Riprovare.");
            }

        } else {
            Utils.mostraPopUpErroreDB();
        }


    }

}
