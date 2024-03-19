package com.example.softwareazienda.controls;

import com.example.softwareazienda.commons.DBMSBound;
import com.example.softwareazienda.commons.Utils;
import javafx.scene.control.Alert;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;

public class FirmaSedeCtrl {

    public static void effettuaFirmaIngresso(String nome, String cognome, String matricolaStr){
        DBMSBound db = new DBMSBound();

        LocalDate dataAttuale = LocalDate.now();

        //CONNESSIONE CORRETTA
        if(db.creaConnessione()){
            try{
                //MATRICOLA INTERA
                int matricola = Integer.parseInt(matricolaStr);
                ResultSet rsInfoDip = db.acquisisciInfoUtente(matricola);

                //SE ESISTE UN DIPENDETE
                if(!Utils.isEmptyResulset(rsInfoDip)){
                    rsInfoDip.next();

                    //SE IL NOME E IL COGNOME SONO CORRETTI
                    if(rsInfoDip.getString("nome").equalsIgnoreCase(nome) && rsInfoDip.getString("cognome").equalsIgnoreCase(cognome)){

                        ResultSet rsInfoTurno;
                        if(LocalTime.now().getHour() < 6){
                             rsInfoTurno = db.myQuery("SELECT * FROM turni WHERE ref_dipendente = "+matricola+" AND data_odierna = '"+dataAttuale.minusDays(1)+"' AND ora_inizio_turno = "+ LocalTime.now().getHour());
                        }else{
                             rsInfoTurno = db.myQuery("SELECT * FROM turni WHERE ref_dipendente = "+matricola+" AND data_odierna = '"+dataAttuale+"' AND ora_inizio_turno = "+ LocalTime.now().getHour());

                        }

                            // SE ESISTE UN TURNO
                            if(!Utils.isEmptyResulset(rsInfoTurno)){
                                while(rsInfoTurno.next()){
                                    // SE NON E GIORNATA RIPOSO
                                    if(rsInfoTurno.getInt("ora_inizio_turno") != -1 && rsInfoTurno.getInt("ora_fine_turno") != -1){

                                        LocalTime oraAttuale = LocalTime.now();

                                        //SE L'ORA E LA STESSA, ENTRO DIECI MINUTI
                                        if(oraAttuale.getHour() == rsInfoTurno.getInt("ora_inizio_turno") && oraAttuale.getMinute() < 10){

                                            db.aggiornaFlagIngresso(matricola, rsInfoTurno.getDate("data_odierna").toLocalDate(), oraAttuale.getHour());
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



                    } else {
                        Utils.messaggioConsole("NOME O COGNOME NON VALIDI", Utils.Tipo_messaggio.DEBUGGING);
                        Utils.mostraPopUp(Alert.AlertType.ERROR, "Errore inserimento", "I dati inseriti non sono corretti. Riprovare.");
                    }

                } else {
                    Utils.messaggioConsole("NON ESISTE UN DIPENDENTE VALIDO", Utils.Tipo_messaggio.DEBUGGING);
                    Utils.mostraPopUp(Alert.AlertType.ERROR, "Errore inserimento", "I dati inseriti non sono corretti. Riprovare.");
                }

            } catch (NumberFormatException | SQLException e) {
                Utils.messaggioConsole("ERRORE MATRICOLA", Utils.Tipo_messaggio.DEBUGGING);
                Utils.mostraPopUp(Alert.AlertType.ERROR, "Errore inserimento", "I dati inseriti non sono corretti. Riprovare.");
            }

        } else {
            Utils.mostraPopUpErroreDB();
        }


    }

    public static void effettuaFirmaUscita(String nome, String cognome, String matricolaStr){
        DBMSBound db = new DBMSBound();

        LocalDate dataAttuale = LocalDate.now();

        //CONNESSIONE CORRETTA
        if(db.creaConnessione()){
            try{
                //MATRICOLA INTERA
                int matricola = Integer.parseInt(matricolaStr);
                ResultSet rsInfoDip = db.acquisisciInfoUtente(matricola);

                //SE ESISTE UN DIPENDETE
                if(!Utils.isEmptyResulset(rsInfoDip)){
                    rsInfoDip.next();

                    //SE IL NOME E IL COGNOME SONO CORRETTI
                    if(rsInfoDip.getString("nome").equalsIgnoreCase(nome) && rsInfoDip.getString("cognome").equalsIgnoreCase(cognome)){

                        ResultSet rsInfoTurno;
                        if(LocalTime.now().getHour() <= 6){
                            rsInfoTurno = db.myQuery("SELECT * FROM turni WHERE ref_dipendente = "+matricola+" AND data_odierna = '"+dataAttuale.minusDays(1)+"' AND ora_fine_turno = "+ LocalTime.now().getHour());
                        }else{
                            rsInfoTurno = db.myQuery("SELECT * FROM turni WHERE ref_dipendente = "+matricola+" AND data_odierna = '"+dataAttuale+"' AND ora_fine_turno = "+ LocalTime.now().getHour());

                        }

                        // SE ESISTE UN TURNO
                        if(!Utils.isEmptyResulset(rsInfoTurno)){
                            rsInfoTurno.next();
                            if(rsInfoTurno.getInt("firma_ingresso") == 1) {

                                    // SE NON E GIORNATA RIPOSO
                                    if (rsInfoTurno.getInt("ora_inizio_turno") != -1 && rsInfoTurno.getInt("ora_fine_turno") != -1) {

                                        LocalTime oraAttuale = LocalTime.now();

                                        //SE L'ORA E LA STESSA, ENTRO DIECI MINUTI
                                        if (oraAttuale.getHour() == rsInfoTurno.getInt("ora_fine_turno") && oraAttuale.getMinute() < 10) {

                                            db.aggiornaFlagUscita(matricola, rsInfoTurno.getDate("data_odierna").toLocalDate(), oraAttuale.getHour());
                                            Utils.mostraPopUp(Alert.AlertType.INFORMATION, "Firma effettuata", "Firma avvenuta con successo");
                                        } else {
                                            Utils.messaggioConsole("ORARIO NON VALIDO", Utils.Tipo_messaggio.DEBUGGING);
                                            Utils.mostraPopUp(Alert.AlertType.ERROR, "Errore orario", "Firma non consentita, orario non valido.");
                                        }

                                    } else {
                                        Utils.messaggioConsole("GIORNATA RIPOSO", Utils.Tipo_messaggio.DEBUGGING);
                                        Utils.mostraPopUp(Alert.AlertType.ERROR, "Errore firma", "Non è stato trovato un turno corrispondente. Riprovare.");
                                    }

                            } else {
                                Utils.messaggioConsole("INGRESSO NON FIRMATO", Utils.Tipo_messaggio.DEBUGGING);
                                Utils.mostraPopUp(Alert.AlertType.ERROR, "Errore firma", "Non è stata effettuata la firma di ingresso.");
                            }
                        } else {
                            Utils.messaggioConsole("NON ESISTE UN TURNO", Utils.Tipo_messaggio.DEBUGGING);
                            Utils.mostraPopUp(Alert.AlertType.ERROR, "Errore firma", "Non è stato trovato un turno corrispondente. Riprovare.");
                        }



                    } else {
                        Utils.messaggioConsole("NOME O COGNOME NON VALIDI", Utils.Tipo_messaggio.DEBUGGING);
                        Utils.mostraPopUp(Alert.AlertType.ERROR, "Errore inserimento", "I dati inseriti non sono corretti. Riprovare.");
                    }

                } else {
                    Utils.messaggioConsole("NON ESISTE UN DIPENDENTE VALIDO", Utils.Tipo_messaggio.DEBUGGING);
                    Utils.mostraPopUp(Alert.AlertType.ERROR, "Errore inserimento", "I dati inseriti non sono corretti. Riprovare.");
                }

            } catch (NumberFormatException | SQLException e) {
                Utils.messaggioConsole("ERRORE MATRICOLA", Utils.Tipo_messaggio.DEBUGGING);
                Utils.mostraPopUp(Alert.AlertType.ERROR, "Errore inserimento", "I dati inseriti non sono corretti. Riprovare.");
                e.printStackTrace();
            }

        } else {
            Utils.mostraPopUpErroreDB();
        }


    }

}
