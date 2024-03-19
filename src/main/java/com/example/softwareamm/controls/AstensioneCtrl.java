package com.example.softwareamm.controls;

import com.example.softwareamm.Main;
import com.example.softwareamm.commons.DBMSBound;
import com.example.softwareamm.commons.Utils;
import com.example.softwareamm.commons.Richiesta;
import javafx.scene.control.Alert;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

import static com.example.softwareamm.commons.Richiesta.TipoRichiesta.*;
import static com.example.softwareamm.commons.Utils.Tipo_messaggio.*;
import static com.example.softwareamm.commons.Utils.numeroGiorni;
import static com.example.softwareamm.commons.Utils.numeroOre;

/**Control dei casi d'uso NuovaRichiestaDip, NuovaRichiestaAmm, CercaDipRichiesta e InviaRichiesta
 * @version 1.0*/
public class AstensioneCtrl {

    private static int matricolaSelezionata;

    /*--------------------------------------------- NUOVA_RICHIESTA_DIP_WIN ---------------------------------------------*/

    /**Effettua una nuova richiesta di astensione lavorativa, esegue tutti i controlli se tutti sono verificati allora procede con l'aggiornare il DataBase
     * (Effettuare la modifica dei turni ed l'inserimento di eventuali sostituzioni) - Alcuni parametri non verranno tenuti in considerazione in base alla
     * tipologia della richiesta
     * @param tipoRichiesta Tipologia della richiesta
     * @param giornataSingola Indica se la giornata è singola o meno
     * @param dataDiInizio Indica la data di inizio del periodo di astensione
     * @param dataDiFine Indica la data di fine del periodo di astensione (Se giornataSingola = true, allora dataDiFine coincide con dataDiInizio)
     * @param oraInizio Indica l'ora di inizio del periodo di astensione
     * @param oraFine Indica l'ora di fine del periodo di astensione
     * @param messaggio Motivazione della richiesta*/

    public static void inviaRichiesta(Richiesta.TipoRichiesta tipoRichiesta, boolean giornataSingola, LocalDate dataDiInizio, LocalDate dataDiFine, String oraInizio, String oraFine, String messaggio) {
        //OTTENIMENTO DEGLI ORARI - SE SOLO VALORI -> CONVERSIONE IN INT - SE NON SONO VALORI - > valore = -1

        int oraInizioInt ;
        int oraFineInt;
        try {
            oraInizioInt = Integer.parseInt(oraInizio);
            oraFineInt = Integer.parseInt(oraFine);
        } catch (NumberFormatException e) {
            oraInizioInt = -1;
            oraFineInt = -1;
        }

        if(tipoRichiesta.equals(PERMESSO)){
            //SE LE ORE INSERITE SONO VALIDE
            if ((oraInizioInt >= 0 && oraInizioInt < 24) && (oraFineInt >= 0 && oraFineInt < 24) && oraInizioInt != oraFineInt) {

                int orePeriodo = oraInizioInt <= oraFineInt ? oraFineInt - oraInizioInt : oraFineInt - oraInizioInt + 24;

                //SE LE ORE SONO CORRETTE
                if (orePeriodo == 8 || orePeriodo <= 3) {
                    //SE LE ORE SONO CORRETTE
                    inviaPermesso(tipoRichiesta, dataDiInizio, dataDiFine, oraInizioInt, oraFineInt, messaggio);
                } else {
                    Utils.mostraPopUp(Alert.AlertType.ERROR, "Errore orario", "Inserire correttamente ora di inizio e ora di fine.");
                }

            } else {
                Utils.mostraPopUp(Alert.AlertType.ERROR, "Errore orario", "Inserire correttamente ora di inizio e ora di fine.");
            }
        } else {
            if(dataDiFine.isBefore(dataDiInizio)){
                Utils.mostraPopUp(Alert.AlertType.ERROR, "Errore date", "Inserire correttamente la data di inizio e la data di fine.");
            } else {

                switch(tipoRichiesta){
                    case FERIE -> {
                        inviaFerie(tipoRichiesta, dataDiInizio, dataDiFine, oraInizioInt, oraFineInt, messaggio);
                    }
                    case CONGEDO_PARENTALE -> {
                        inviaCongedo(tipoRichiesta, dataDiInizio, dataDiFine, oraInizioInt, oraFineInt, messaggio);
                    }

                    case MALATTIA -> {
                        inviaMalattia(tipoRichiesta, dataDiInizio, dataDiFine, oraInizioInt, oraFineInt, messaggio);
                    }

                    case SCIOPERO -> {
                        if ((oraInizioInt >= 0 && oraInizioInt < 24) && (oraFineInt >= 0 && oraFineInt < 24) && oraInizioInt != oraFineInt) {

                            int orePeriodo = oraInizioInt <= oraFineInt ? oraFineInt - oraInizioInt : oraFineInt - oraInizioInt + 24;

                            //SE LE ORE SONO CORRETTE
                            if (orePeriodo == 8 || orePeriodo <= 3) {
                                //SE LE ORE SONO CORRETTE
                                inviaSciopero(tipoRichiesta, dataDiInizio, dataDiFine, oraInizioInt, oraFineInt, messaggio);
                            } else {
                                Utils.mostraPopUp(Alert.AlertType.ERROR, "Errore orario", "Inserire correttamente ora di inizio e ora di fine.");
                            }

                        } else {
                            Utils.mostraPopUp(Alert.AlertType.ERROR, "Errore orario", "Inserire correttamente ora di inizio e ora di fine.");
                        }
                    }
                }
            }
        }

    }

    private static boolean isCompresoNelTrimestre(LocalDate dataInizio, LocalDate dataFine){
        DBMSBound db = new DBMSBound();
        boolean isCompreso = false;
        if(db.creaConnessione()){
            LocalDate inizioTurnazione = db.acquisisciDataInizioTurnazione();
            LocalDate fineTurnazione = db.acquisisciDataFineTurnazione();

            if(dataInizio.isAfter(inizioTurnazione.minusDays(1)) && dataFine.isBefore(fineTurnazione.plusDays(1))){
                isCompreso = true;
            } else {
                isCompreso = false;
            }
        } else {
            Utils.mostraPopUpErroreDB();
        }

        return isCompreso;
    }
    private static void inviaPermesso(Richiesta.TipoRichiesta tipoRichiesta, LocalDate dataDiInizio, LocalDate dataDiFine, int oraInizio, int oraFine, String messaggio){
        DBMSBound db = new DBMSBound();

        if(db.creaConnessione()){

            LocalDate oggi = LocalDate.now();

            //SE IL PERIODO INSERITO E' ALL'INTERNO DEL TRIMESTRE ATTUALE E SE LA RICHIESTA E' MINIMO PER L'INDOMANI
            if(dataDiInizio.isAfter(oggi) && isCompresoNelTrimestre(dataDiInizio, dataDiFine)){
                try {
                    boolean rifiuta = false;

                    /*----- VERIFICHIAMO SE IL DIPENDENTE POSSIEDE UN TURNO IN QUELLA DATA E A QUELL'ORARIO -----*/

                    ResultSet rsTurno = db.acquisisciTurnoDip(matricolaSelezionata, dataDiInizio);

                    //SE ESISTE ALMENO UN TURNO
                    if(Utils.isNotEmptyResulset(rsTurno)){
                        while(rsTurno.next()) {
                            int oraInizioTurno = rsTurno.getInt("ora_inizio_turno");
                            int oraFineTurno = rsTurno.getInt("ora_fine_turno");
                            Utils.messaggioConsole("Turno trovato: " + oraInizioTurno + " - " + oraFineTurno, DEBUGGING);
                            //SE NON E'GIORNATA RIPOSO
                            if(oraInizioTurno != -1 && oraFineTurno != -1){

                                //SE GLI ORARI DEL PERMESSO NON RIENTRANO NEL TURNO
                                if (oraInizioTurno<oraFineTurno) {

                                    if (!(oraInizio >= oraInizioTurno && oraFine <= oraFineTurno)) {
                                        Utils.messaggioConsole("Turno non valido", DEBUGGING);
                                        rifiuta = true;
                                    }
                                }
                                else if (!((oraInizio == 22 || oraInizio == 23 || oraInizio <= oraFineTurno) && (oraFine == 23 || oraFine <= oraFineTurno))){
                                    rifiuta = true;
                                }

                            } else {
                                rifiuta = true;
                            }
                        }
                    } else {
                        //SE NON CI SONO TURNI
                        Utils.messaggioConsole("Nessun turno presente", DEBUGGING);
                        rifiuta = true;
                    }

                    /*----- VERIFICHIAMO SE ESISTONO GIA' RICHIESTE PER LA DATA INSERITA -----*/

                    rifiuta = rifiuta || verificaRichieste(dataDiInizio, dataDiFine);

                    /*----- VERIFICHIAMO SE LE ORE DEI PERMESSI SONO STATI SUPERATI -----*/

                    ResultSet rsDip = db.acquisisciInfoUtente(matricolaSelezionata);
                    rsDip.next();
                    int orePermessiPrese = rsDip.getInt("ore_permessi_prese");

                    int ruolo = rsDip.getInt("ref_ruolo");

                    ResultSet rsRuolo = db.acquisisciInfoRuolo(ruolo);
                    rsRuolo.next();
                    int sogliaPermessi = rsRuolo.getInt("soglia_permessi");

                    //SE LE ORE PERMESSI SONO STATE SUPERATE
                    if((orePermessiPrese + numeroOre(oraInizio,oraFine)) > sogliaPermessi){
                        Utils.messaggioConsole("Ore permessi superati: " + orePermessiPrese, DEBUGGING);
                        rifiuta = true;
                    }

                /*
                //AGGIORNIAMO SE E' IL CASO IL NUMERO DI PERMESSI
                //SE LA RICHIESTA NON E' STATA RIFIUTATA
                if(!rifiuta){
                    db.aggiornaOrePermesso(matricolaSelezionata, orePermessiPrese + numeroOre(oraInizio, oraFine));
                }
                */

                    //INVIA RICHIESTA
                    int idComunicazione = db.insertComunicazionePV(matricolaSelezionata, matricolaSelezionata, oggi, dataDiInizio.atTime(oraInizio, 0), dataDiFine.atTime(oraFine, 0), tipoRichiesta.toString(), messaggio);
                    db.aggiornaStatoPV(idComunicazione, rifiuta ? 2 : 3);

                    //SE NON E RIFIUTATA
                    if(!rifiuta){
                        int[] matricoleIdonee = matricoleDipendentiIdonei(matricolaSelezionata, oraInizio, dataDiInizio);

                        for(int dip : matricoleIdonee){
                            db.insertComunicazione(idComunicazione, matricolaSelezionata, dip, oggi, "PV");
                            db.myUpdate("UPDATE comunicazioni SET ref_stato_comunicazione = 3 WHERE id_comunicazione = "+ idComunicazione + " AND ref_utente_destinatario = " + dip);

                        }

                    }

                    Utils.messaggioConsole(
                            "Info richiesta: " + "\n" +
                                    "\t\t\tMittente:" + matricolaSelezionata + "\n" +
                                    "\t\t\tDestinatario:" + matricolaSelezionata + "\n" +
                                    "\t\t\tOggi:" + oggi + "\n" +
                                    "\t\t\tDataInizio:" + Utils.formattaData(dataDiInizio) + "\n" +
                                    "\t\t\tOraInizio:" + oraInizio + "\n" +
                                    "\t\t\tDataFine:" + Utils.formattaData(dataDiFine) + "\n" +
                                    "\t\t\tOraFine:" + oraFine + "\n" +
                                    "\t\t\tTipo:" + tipoRichiesta + "\n" +
                                    "\t\t\tStato:" + (rifiuta ? "RIFIUTATA" : "IN ATTESA") + "\n" +
                                    "\t\t\tMessaggio:" + messaggio, DEBUGGING);

                    Utils.mostraPopUp(Alert.AlertType.INFORMATION, "Richiesta inviata", "La richiesta è stata inviata con successo");
                    Utils.mostraScenaMenu(Main.mainStage, "Comunicazioni", "gestionecomunicazioni/ComunRicevWin.fxml");
                } catch (SQLException | IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                Utils.mostraPopUp(Alert.AlertType.ERROR, "Errore date", "Inserire correttamente la data di inizio e la data di fine.");
            }

        } else {
            Utils.mostraPopUpErroreDB();
        }

    }
    private static void inviaFerie(Richiesta.TipoRichiesta tipoRichiesta, LocalDate dataDiInizio, LocalDate dataDiFine, int oraInizio, int oraFine, String messaggio){
        DBMSBound db = new DBMSBound();

        if(db.creaConnessione()){

            LocalDate oggi = LocalDate.now();

            int giorniPresi;
            int sogliaFerie;
            int ruolo;
            try {
                ResultSet rsGiorniPresi = db.acquisisciInfoUtente(matricolaSelezionata);
                rsGiorniPresi.next();
                giorniPresi = rsGiorniPresi.getInt("giorni_ferie_presi");

                ResultSet rsDip = db.acquisisciInfoUtente(matricolaSelezionata);
                rsDip.next();
                ruolo = rsDip.getInt("ref_ruolo");

                ResultSet rsRuolo = db.acquisisciInfoRuolo(ruolo);
                rsRuolo.next();
                sogliaFerie = rsRuolo.getInt("soglia_ferie");

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            //SE LA DATA DI INIZIO E DOPO Il TRIMESTRE, SE IL PERIODO CHE SI VUOLE INSERIRE E INFERIORE ALLA DOGLIA FERIE, SE E DI QUESTANNO
            if(dataDiInizio.isAfter(db.acquisisciDataFineTurnazione()) && (dataDiFine.getYear() == LocalDate.now().getYear()) && numeroGiorni(dataDiInizio, dataDiFine) <= sogliaFerie && LocalDate.now().isBefore(db.acquisisciDataFineTurnazione().minusDays(10))){

                try{
                    boolean rifiuta = false;

                    /*----- VERIFICHIAMO SE ESISTONO GIA' RICHIESTE PER IL PERIODO INSERITO -----*/
                    /*----- VERIFICHIAMO SE ESISTONO PIA PER IL PERIODO INSERITO-----*/

                    rifiuta = verificaRichieste(dataDiInizio, dataDiFine) || verificaPIA(dataDiInizio, dataDiFine);

                    /*----- VERIFICHIAMO SE I GIORNI DI FERIE SONO STATI SUPERATI -----*/

                    //SE I GIORNI DI FERIE SONO STATI SUPERATI
                    if((giorniPresi + numeroGiorni(dataDiInizio, dataDiFine)) > sogliaFerie){
                        Utils.messaggioConsole("Giorni ferie superati: " + giorniPresi, DEBUGGING);
                        rifiuta = true;
                    }

                /*
                //AGGIORNIAMO SE E' IL CASO IL NUMERO DI FERIE PRESE
                //SE LA RICHIESTA NON E' STATA RIFIUTATA
                if(!rifiuta){
                    db.aggiornaGiorniFerie(matricolaSelezionata, giorniPresi + numeroGiorni(dataDiInizio, dataDiFine));
                }
                 */

                    //INVIA RICHIESTA
                    int matricolaAmministratore = db.acquisisciMatricolaAmm();
                    int idComunicazione = db.insertComunicazionePV(matricolaSelezionata, matricolaAmministratore, oggi, dataDiInizio.atTime(0, 0), dataDiFine.atTime(0, 0), tipoRichiesta.toString(), messaggio);
                    db.aggiornaStatoPV(idComunicazione, rifiuta ? 2 : 3);

                    Utils.messaggioConsole(
                            "Info richiesta: " + "\n" +
                                    "\t\t\tMittente:" + matricolaSelezionata + "\n" +
                                    "\t\t\tDestinatario:" + matricolaAmministratore + "\n" +
                                    "\t\t\tOggi:" + oggi + "\n" +
                                    "\t\t\tDataInizio:" + Utils.formattaData(dataDiInizio) + "\n" +
                                    "\t\t\tOraInizio:" + oraInizio + "\n" +
                                    "\t\t\tDataFine:" + Utils.formattaData(dataDiFine) + "\n" +
                                    "\t\t\tOraFine:" + oraFine + "\n" +
                                    "\t\t\tTipo:" + tipoRichiesta + "\n" +
                                    "\t\t\tStato:" + (rifiuta ? "RIFIUTATA" : "IN ATTESA") + "\n" +
                                    "\t\t\tMessaggio:" + messaggio, DEBUGGING);

                    Utils.mostraPopUp(Alert.AlertType.INFORMATION, "Richiesta inviata", "La richiesta è stata inviata con successo");
                    Utils.mostraScenaMenu(Main.mainStage, "Comunicazioni", "gestionecomunicazioni/ComunRicevWin.fxml");
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

            } else {
                Utils.mostraPopUp(Alert.AlertType.ERROR, "Errore date", "Inserire correttamente la data di inizio e la data di fine.");
            }

        } else {
            Utils.mostraPopUpErroreDB();
        }
    }
    private static void inviaCongedo(Richiesta.TipoRichiesta tipoRichiesta, LocalDate dataDiInizio, LocalDate dataDiFine, int oraInizio, int oraFine, String messaggio){
        DBMSBound db = new DBMSBound();

        if(db.creaConnessione()){
            LocalDate oggi = LocalDate.now();

            //SE LA DATA DI INIZIO E DOPO QUESTA TURNAZIONE
            if(dataDiInizio.isAfter(db.acquisisciDataFineTurnazione()) && LocalDate.now().isBefore(db.acquisisciDataFineTurnazione().minusDays(10)) && (dataDiFine.getYear() == LocalDate.now().getYear())){

                try{
                    boolean rifiuta = false;

                    /*----- VERIFICHIAMO SE ESISTONO GIA' RICHIESTE PER IL PERIODO INSERITO -----*/
                    rifiuta = verificaRichieste(dataDiInizio, dataDiFine);

                    //INVIA RICHIESTA
                    int matricolaAmministratore = db.acquisisciMatricolaAmm();
                    int idComunicazione = db.insertComunicazionePV(matricolaSelezionata, matricolaAmministratore, oggi, dataDiInizio.atTime(0, 0), dataDiFine.atTime(0, 0), tipoRichiesta.toString(), messaggio);
                    db.aggiornaStatoPV(idComunicazione, rifiuta ? 2 : 3);

                    Utils.messaggioConsole(
                            "Info richiesta: " + "\n" +
                                    "\t\t\tMittente:" + matricolaSelezionata + "\n" +
                                    "\t\t\tDestinatario:" + matricolaAmministratore + "\n" +
                                    "\t\t\tOggi:" + oggi + "\n" +
                                    "\t\t\tDataInizio:" + Utils.formattaData(dataDiInizio) + "\n" +
                                    "\t\t\tOraInizio:" + oraInizio + "\n" +
                                    "\t\t\tDataFine:" + Utils.formattaData(dataDiFine) + "\n" +
                                    "\t\t\tOraFine:" + oraFine + "\n" +
                                    "\t\t\tTipo:" + tipoRichiesta + "\n" +
                                    "\t\t\tStato:" + (rifiuta ? "RIFIUTATA" : "IN ATTESA") + "\n" +
                                    "\t\t\tMessaggio:" + messaggio, DEBUGGING);

                    Utils.mostraPopUp(Alert.AlertType.INFORMATION, "Richiesta inviata", "La richiesta è stata inviata con successo");
                    Utils.mostraScenaMenu(Main.mainStage, "Comunicazioni", "gestionecomunicazioni/ComunRicevWin.fxml");
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

            } else {
                Utils.mostraPopUp(Alert.AlertType.ERROR, "Errore date", "Inserire correttamente la data di inizio e la data di fine.");
            }
        } else {
            Utils.mostraPopUpErroreDB();
        }

    }
    private static void inviaMalattia(Richiesta.TipoRichiesta tipoRichiesta, LocalDate dataDiInizio, LocalDate dataDiFine, int oraInizio, int oraFine, String messaggio){
        DBMSBound db = new DBMSBound();

        if(db.creaConnessione()){

            LocalDate oggi = LocalDate.now();

            boolean rifiuta = false;

            if(dataDiInizio.isAfter(oggi) && isCompresoNelTrimestre(dataDiInizio, dataDiFine)){

                /*----- VERIFICHIAMO SE ESISTONO GIA' RICHIESTE PER IL PERIODO INSERITO -----*/

                rifiuta = verificaRichieste(dataDiInizio,dataDiFine);

                int ruolo;

                try{
                    ResultSet infoDip = db.acquisisciInfoUtente(matricolaSelezionata);
                    infoDip.next();
                    ruolo = infoDip.getInt("ref_ruolo");
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }

                //INVIA RICHIESTA
                int idComunicazione = db.insertComunicazionePV(matricolaSelezionata, matricolaSelezionata, oggi, dataDiInizio.atTime(0, 0), dataDiFine.atTime(0, 0), tipoRichiesta.toString(), messaggio);
                db.aggiornaStatoPV(idComunicazione, rifiuta ? 2 : 1);

                //SE NON E RIFIUTATA
                if(!rifiuta){

                    //SOSTITUISCI TURNO0
                    LocalDate dateIterator = dataDiInizio;

                    while (dateIterator.isBefore(dataDiFine.plusDays(1))) {
                        int matricolaSostituto = calcoloDipendenteIdoneo(matricolaSelezionata, dateIterator);

                        ResultSet rsTurno = db.acquisisciTurnoDip(matricolaSelezionata, dateIterator);
                        ResultSet rsTurnoSostituto= db.acquisisciTurnoDip(matricolaSostituto, dateIterator);
                        int oraInizioTurno;
                        int oraFineTurno;

                        int oraInizioTurnoSostituto;
                        int oraFineTurnoSostituto;
                        try {
                            rsTurno.next();
                            oraInizioTurno = rsTurno.getInt("ora_inizio_turno");
                            oraFineTurno = rsTurno.getInt("ora_fine_turno");

                            rsTurnoSostituto.next();
                            oraInizioTurnoSostituto = rsTurnoSostituto.getInt("ora_inizio_turno");
                            oraFineTurnoSostituto = rsTurnoSostituto.getInt("ora_fine_turno");
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }

                        if(oraInizioTurno != -1 && oraFineTurno != -1){
                            db.myUpdate("UPDATE `turni` SET `ora_inizio_turno`='" + oraInizioTurno + "',`ora_fine_turno`='" + oraFineTurno + "',`ruolo_giornaliero`='" + ruolo + "' WHERE data_odierna = '" + dateIterator + "' AND ref_dipendente = " + matricolaSostituto); //cambio il turno del dipIdoneo
                            db.myUpdate("UPDATE `turni` SET `ora_inizio_turno`='-1',`ora_fine_turno`='-1',`stato_giornaliero`='" + 2 + "' WHERE data_odierna = '" + dateIterator + "' AND ref_dipendente = " + matricolaSelezionata);// metto il mittente a riposos e cambio il suo stato

                            db.creaNuovaComunicazioneCD(matricolaSelezionata, matricolaSostituto, LocalDate.now(), "La informiamo che il suo turno di data " + dateIterator + " dalle ore " + oraInizioTurnoSostituto + " alle ore " + oraFineTurnoSostituto + " è stato sostituito con il turno dalle ore " + oraInizioTurno + " alle ore " + oraFineTurno + " del dipendente " + String.format("%04d", matricolaSelezionata) + " (ruolo " + ruolo + ")");

                        }

                        dateIterator = dateIterator.plusDays(1);
                    }

                }

                Utils.messaggioConsole(
                        "Info richiesta: " + "\n" +
                                "\t\t\tMittente:" + matricolaSelezionata + "\n" +
                                "\t\t\tDestinatario:" + matricolaSelezionata + "\n" +
                                "\t\t\tOggi:" + oggi + "\n" +
                                "\t\t\tDataInizio:" + Utils.formattaData(dataDiInizio) + "\n" +
                                "\t\t\tOraInizio:" + oraInizio + "\n" +
                                "\t\t\tDataFine:" + Utils.formattaData(dataDiFine) + "\n" +
                                "\t\t\tOraFine:" + oraFine + "\n" +
                                "\t\t\tTipo:" + tipoRichiesta + "\n" +
                                "\t\t\tStato:" + (rifiuta ? "RIFIUTATA" : "ACCETTATA") + "\n" +
                                "\t\t\tMessaggio:" + messaggio, DEBUGGING);

                Utils.mostraPopUp(Alert.AlertType.INFORMATION, "Richiesta inviata", "La richiesta è stata inviata con successo");

                try{
                    Utils.mostraScenaMenu(Main.mainStage, "Comunicazioni", "gestionecomunicazioni/ComunRicevWin.fxml");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            } else {
                Utils.mostraPopUp(Alert.AlertType.ERROR, "Errore date", "Inserire correttamente la data di inizio e la data di fine.");
            }

        } else {
            Utils.mostraPopUpErroreDB();
        }
    }
    private static void inviaSciopero(Richiesta.TipoRichiesta tipoRichiesta, LocalDate dataDiInizio, LocalDate dataDiFine, int oraInizio, int oraFine, String messaggio){
        DBMSBound db = new DBMSBound();

        if(db.creaConnessione()){

            LocalDate oggi = LocalDate.now();

            if(dataDiInizio.isAfter(oggi) && isCompresoNelTrimestre(dataDiInizio, dataDiFine)){
                try{
                    boolean rifiuta = false;

                    /*----- VERIFICHIAMO SE IL DIPENDENTE POSSIEDE UN TURNO IN QUELLA DATA E A QUELL'ORARIO -----*/

                    ResultSet rsTurno = db.acquisisciTurnoDip(matricolaSelezionata, dataDiInizio);

                    //SE ESISTE ALMENO UN TURNO
                    if(Utils.isNotEmptyResulset(rsTurno)){
                        while(rsTurno.next()) {
                            int oraInizioTurno = rsTurno.getInt("ora_inizio_turno");
                            int oraFineTurno = rsTurno.getInt("ora_fine_turno");
                            Utils.messaggioConsole("Turno trovato: " + oraInizioTurno + " - " + oraFineTurno, DEBUGGING);
                            //SE NON E'GIORNATA RIPOSO
                            if(oraInizioTurno != -1 && oraFineTurno != -1){

                                //SE GLI ORARI DEL PERMESSO NON RIENTRANO NEL TURNO
                                if (oraInizioTurno<oraFineTurno) {

                                    if (!(oraInizio >= oraInizioTurno && oraFine <= oraFineTurno)) {
                                        Utils.messaggioConsole("Turno non valido", DEBUGGING);
                                        rifiuta = true;
                                    }
                                }
                                else if (!((oraInizio == 22 || oraInizio == 23 || oraInizio <= oraFineTurno) && (oraFine == 23 || oraFine <= oraFineTurno))){
                                    rifiuta = true;
                                }

                            } else {
                                rifiuta = true;
                            }
                        }
                    } else {
                        //SE NON CI SONO TURNI
                        Utils.messaggioConsole("Nessun turno presente", DEBUGGING);
                        rifiuta = true;
                    }

                    /*----- VERIFICHIAMO SE ESISTONO GIA' RICHIESTE PER LA DATA INSERITA -----*/

                    rifiuta = rifiuta || verificaRichieste(dataDiInizio, dataDiFine);

                    //INVIA RICHIESTA
                    int idComunicazione = db.insertComunicazionePV(matricolaSelezionata, matricolaSelezionata, oggi, dataDiInizio.atTime(oraInizio, 0), dataDiFine.atTime(oraFine, 0), tipoRichiesta.toString(), messaggio);
                    db.aggiornaStatoPV(idComunicazione, rifiuta ? 2 : 3);

                    //SE NON E RIFIUTATA
                    if(!rifiuta){
                        int[] matricoleIdonee = matricoleDipendentiIdonei(matricolaSelezionata, oraInizio, dataDiInizio);

                        for(int dip : matricoleIdonee){
                            db.insertComunicazione(idComunicazione, matricolaSelezionata, dip, oggi, "PV");
                            db.myUpdate("UPDATE comunicazioni SET ref_stato_comunicazione = 3 WHERE id_comunicazione = "+ idComunicazione + " AND ref_utente_destinatario = " + dip);
                        }

                    }

                    Utils.messaggioConsole(
                            "Info richiesta: " + "\n" +
                                    "\t\t\tMittente:" + matricolaSelezionata + "\n" +
                                    "\t\t\tDestinatario:" + matricolaSelezionata + "\n" +
                                    "\t\t\tOggi:" + oggi + "\n" +
                                    "\t\t\tDataInizio:" + Utils.formattaData(dataDiInizio) + "\n" +
                                    "\t\t\tOraInizio:" + oraInizio + "\n" +
                                    "\t\t\tDataFine:" + Utils.formattaData(dataDiFine) + "\n" +
                                    "\t\t\tOraFine:" + oraFine + "\n" +
                                    "\t\t\tTipo:" + tipoRichiesta + "\n" +
                                    "\t\t\tStato:" + (rifiuta ? "RIFIUTATA" : "IN ATTESA") + "\n" +
                                    "\t\t\tMessaggio:" + messaggio, DEBUGGING);

                    Utils.mostraPopUp(Alert.AlertType.INFORMATION, "Richiesta inviata", "La richiesta è stata inviata con successo");
                    Utils.mostraScenaMenu(Main.mainStage, "Comunicazioni", "gestionecomunicazioni/ComunRicevWin.fxml");
                } catch (SQLException | IOException e) {
                    throw new RuntimeException(e);
                }

            } else {
                Utils.mostraPopUp(Alert.AlertType.ERROR, "Errore date", "Inserire correttamente la data di inizio e la data di fine.");
            }

        } else {
            Utils.mostraPopUpErroreDB();
        }

    }

    //VERIFICA SE NEL PERIODO INSERITO LA MATRICOLA SELEZIONATA POSSIEDE GIA' RICHIESTE
    public static boolean verificaRichieste(LocalDate dataDiInizio, LocalDate dataDiFine) {
        DBMSBound db = new DBMSBound();

        boolean esiste = false;

        if(db.creaConnessione()){

            /*----- VERIFICHIAMO SE ESISTONO GIA' RICHIESTE PER IL PERIODO INSERITO -----*/

            try {
                ResultSet rsRichieste = db.acquisisciRichieste(matricolaSelezionata);

                //SE ESISTONO RICHIESTE EFFETTUATE
                if (Utils.isNotEmptyResulset(rsRichieste)) {
                    while (rsRichieste.next()) {
                        int statoRichiesta = rsRichieste.getInt("ref_stato_comunicazione");
                        LocalDate dataInizioRichiesta = rsRichieste.getDate("data_inizio_pv").toLocalDate();
                        LocalDate dataFineRichiesta = rsRichieste.getDate("data_fine_pv").toLocalDate();

                        //SE ESISTE UNA RICHIESTA IN QUEL PERIODO CON STATO "ACCETTATA" O IN "ATTESA"
                        if ((statoRichiesta == 1 || statoRichiesta == 3) && !((dataDiInizio.isBefore(dataInizioRichiesta) && dataDiFine.isBefore(dataInizioRichiesta)) ||
                                (dataDiInizio.isAfter(dataFineRichiesta) && dataDiFine.isAfter(dataFineRichiesta)))) {
                            Utils.messaggioConsole("Richiesta già presente: id: " +
                                    rsRichieste.getInt("ref_comunicazione") + " , stato: " +
                                    statoRichiesta + " , dataInizio:" +
                                    Utils.formattaData(dataInizioRichiesta) + " , dataFine" +
                                    Utils.formattaData(dataFineRichiesta), DEBUGGING);

                            esiste = true;
                            break;
                        }
                    }
                }

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        } else {
            Utils.mostraPopUpErroreDB();
        }

        return esiste;

    }

    //VERIFICA SE NEL PERIODO INSERITO LA MATRICOLA SELEZIONATA POSSIEDE GIA' RICHIESTE
    public static boolean verificaPIA(LocalDate dataDiInizio, LocalDate dataDiFine){
        DBMSBound db = new DBMSBound();

        boolean rifiuta = false;

        if(db.creaConnessione()){

            try{
                ResultSet rsPIA = db.acquisisciListaPia();

                if(Utils.isNotEmptyResulset(rsPIA)){
                    while (rsPIA.next()){
                        LocalDate inizioPIA = rsPIA.getDate("data_inizio_pia").toLocalDate();
                        LocalDate finePIA = rsPIA.getDate("data_fine_pia").toLocalDate();

                        //SE ESISTE UN GIA' UN PIA NEL PERIODO SELEZIONATO (SE LE DATE DI INIZIO E FINE SONO NELLE DATE DI INIZIO E FINE PIA)
                        if(!((dataDiInizio.isBefore(inizioPIA) && dataDiFine.isBefore(inizioPIA)) || (dataDiInizio.isAfter(finePIA) && dataDiFine.isAfter(finePIA)))){

                            Utils.messaggioConsole("PIA già presente: " + Utils.formattaData(inizioPIA) + " - " + Utils.formattaData(finePIA), DEBUGGING);
                            rifiuta = true;
                        }

                    }
                }
                return rifiuta;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        } else {
            Utils.mostraPopUpErroreDB();
        }

        return rifiuta;

    }

    public static int[] matricoleDipendentiIdonei(int matricolaMittente, int oraInizioTurno, LocalDate data){

        try {
            DBMSBound db = new DBMSBound();
            ArrayList<Integer> listaMatricole = new ArrayList<>();
            if(oraInizioTurno >= 14) {
                for (int i = 6; i <= 22; i += 8) {
                    for (int j = 1; j <= 4; j++) {
                        ResultSet contDipendenti = db.myQuery("SELECT count(distinct ref_dipendente) AS numDipendenti FROM turni WHERE ruolo_giornaliero = "+j+" and ref_dipendente != " + matricolaMittente + " AND data_odierna = '" + data + "' AND ora_inizio_turno = " + i);
                        contDipendenti.next();
                        if (contDipendenti.getInt("numDipendenti") > 4) {
                            ResultSet dipendentiTurno = db.myQuery("SELECT ref_dipendente FROM turni WHERE ref_dipendente != " + matricolaMittente + " AND ruolo_giornaliero = "+j+" and data_odierna = '" + data + "' AND ora_inizio_turno = " + i + " ORDER BY ora_inizio_turno");
                            while (dipendentiTurno.next()) {
                                listaMatricole.add(dipendentiTurno.getInt("ref_dipendente"));
                            }
                        }
                    }
                }
            }else{
                for (int i = 6; i <= 14; i += 8) {
                    for (int j = 1; j <= 4; j++) {
                        ResultSet contDipendenti = db.myQuery("SELECT count(distinct ref_dipendente) AS numDipendenti FROM turni WHERE ruolo_giornaliero = "+j+" and ref_dipendente != " + matricolaMittente + " AND data_odierna = '" + data + "' AND ora_inizio_turno = " + i);
                        contDipendenti.next();
                        if (contDipendenti.getInt("numDipendenti") > 4) {
                            ResultSet dipendentiTurno = db.myQuery("SELECT ref_dipendente FROM turni WHERE ref_dipendente != " + matricolaMittente + " AND ruolo_giornaliero = "+j+" and data_odierna = '" + data + "' AND ora_inizio_turno = " + i + " ORDER BY ora_inizio_turno");
                            while (dipendentiTurno.next()) {
                                listaMatricole.add(dipendentiTurno.getInt("ref_dipendente"));
                            }
                        }
                    }
                }
            }
            int[] listaIdonei = new int[listaMatricole.size()];
            for(int i=0;  i< listaMatricole.size(); i++){
                listaIdonei[i] = listaMatricole.get(i);
            }

            return listaIdonei;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static int calcoloDipendenteIdoneo(int mittente, LocalDate dataInizio) {
        DBMSBound db = new DBMSBound();

        if(db.creaConnessione()){

            try{
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

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        } else {
            Utils.mostraPopUpErroreDB();
        }

        return 0;

    }

    /**Mostra la finestra NuovaRichiestaAmmWin*/
    public static void mostraNuovaRichiestaAmmWin() throws IOException {
        Utils.mostraScena(Main.mainStage, "Nuova Richiesta Amm", "gestionecomunicazioni/NuovaRichiestaAmmWin.fxml");
    }

    /*--------------------------------------------- NUOVA_RICHIESTA_AMM_WIN ---------------------------------------------*/

    /**Preleva la lista dei dipendenti
     *@return Ritorna la lista dei dipendenti*/
    public static ResultSet prelevaListaDipendenti(){
        /*QUERY - prelevaListaDip() = listaDipendenti*/

        DBMSBound db = new DBMSBound();
        ResultSet rs = null;
        if(db.creaConnessione()){
            rs = db.acquisisciListaDipendenti();

            try{
                rs.next();
            } catch (Exception e){
                Utils.mostraPopUpErroreDB();
                e.printStackTrace();
            }
        } else {
            Utils.mostraPopUpErroreDB();
        }

        return rs;
    }

    /**Mostra la finestra NuovaRichiestaDipWin*/
    public static void mostraNuovaRichiestaDipWin(int matricola) throws IOException {
        matricolaSelezionata = matricola;
        Utils.mostraScena(Main.mainStage, "Nuova Richiesta Dip", "gestionecomunicazioni/NuovaRichiestaDipWin.fxml");
    }

    /**Mostra la finestra NuovaComunWin*/
    public static void mostraNuovaComunWin() throws IOException {
        Utils.mostraScena(Main.mainStage, "Nuova Comunicazione", "gestionecomunicazioni/NuovaComunWin.fxml");
    }
}
