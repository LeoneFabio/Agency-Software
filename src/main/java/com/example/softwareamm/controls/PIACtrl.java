package com.example.softwareamm.controls;

import com.example.softwareamm.Main;
import com.example.softwareamm.commons.DBMSBound;
import com.example.softwareamm.commons.Utils;
import com.example.softwareamm.entity.Utente;
import javafx.scene.control.Alert;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

import static com.example.softwareamm.commons.Utils.Tipo_messaggio.DEBUGGING;
import static com.example.softwareamm.commons.Utils.numeroGiorni;

/**Control dei casi d'uso AggiungiPIA, RimuoviPIA e VisualizzaPIA
 * @version 1.0*/

public class PIACtrl {

    private static final int SOGLIA_PIA = 90;

    /*--------------------------------------------- PIA_WIN ---------------------------------------------*/

    /**Preleva la lista dei P.I.A annuale fino a quel momento creati
     * @return Ritorna la lista dei P.I.A.*/
    public static ArrayList<String> prelevaListaPIA(){

        DBMSBound db = new DBMSBound();

        ArrayList<String> lista = new ArrayList<>();

        if(db.creaConnessione()){

            ResultSet rs = db.acquisisciListaPia();
            try{
                while(rs.next()){
                    lista.add(Utils.formattaData(rs.getDate("data_inizio_pia").toLocalDate()) + " - " +
                            Utils.formattaData(rs.getDate("data_fine_pia").toLocalDate()) + " " +
                            rs.getString("descrizione"));
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        } else {
            Utils.mostraPopUpErroreDB();
        }
        return lista;
    }

    public static int[] prelevaGiorniInfoPIA(){
        DBMSBound db = new DBMSBound();

        if(db.creaConnessione()){

            int giorniPIARimanenti = db.acquisisciPIARimanenti();
            return new int[]{giorniPIARimanenti, SOGLIA_PIA - giorniPIARimanenti};

        } else {
            Utils.mostraPopUpErroreDB();
        }

        return new int[]{};
    }

    public static void rimuoviPIA(LocalDate dataInizio, LocalDate dataFine){
        DBMSBound db = new DBMSBound();

        if(db.creaConnessione()){

            LocalDate oggi = LocalDate.now();

            //SE LA DATA DI INIZIO E SUCCESSIVA A OGGI
            if(dataInizio.isAfter(oggi)){

                boolean scelta = Utils.mostraPopUp(Alert.AlertType.CONFIRMATION, "Sicuro di voler eliminare il P.I.A. selezionato?", "L'operazione di rimozione è irreversibile.");

                if(scelta){
                    db.rimuoviPIA(dataInizio);
                    db.aggiornaPIARimanenti((db.acquisisciPIARimanenti() + numeroGiorni(dataInizio,dataFine)));

                    //MANDA COMUNICAZIONI AVVISO AI DIPENDENTI
                    String messaggioCD = "Si comunica la rimozione del P.I.A.\n" +
                            "Da: " + Utils.formattaData(dataInizio) + "\n" +
                            "A: " + Utils.formattaData(dataFine) + "\n" +
                            "\n\n" + "In tale periodo sarà dunque possibile effettuare richieste di ferie\n\n - Amministratore";

                    ResultSet rsDip = db.acquisisciListaDipendenti();

                    try{
                        while (rsDip.next()){
                            //PROBLEMA TROPPE CONNESSIONI
                            db.creaNuovaComunicazioneCD(Utente.utenteLoggato.getMatricola(), rsDip.getInt("matricola"), LocalDate.now(), messaggioCD);
                        }
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }

                    Utils.mostraPopUp(Alert.AlertType.INFORMATION, "Rimozione avvenuta con successo", "Il P.I.A. è stato rimosso dalla lista");
                }
            } else {
                Utils.mostraPopUp(Alert.AlertType.ERROR, "Impossibile rimuvere", "Il P.I.A. selezionato è in corso o già passato.");
            }

        } else {
            Utils.mostraPopUpErroreDB();
        }
    }

    /**Mostra la finestra AggiungiPIAWin*/
    public static void mostraAggiungiPIAWin() throws IOException {
        Utils.mostraScena(Main.mainStage, "Nuova P.I.A.", "gestioneturni/AggiungiPIAWin.fxml");
    }

    /*--------------------------------------------- AGGIUNGI_PIA_WIN ---------------------------------------------*/

    /**Verifica se la soglia dei P.I.A. non è stata superata, se non lo è allora viene salvata nel DB il nuovo P.I.A.
     * @param dataInizio Data di inizio
     * @param dataFine Data di fine
     * @param messaggio del P.I.A.*/
    public static void creaNuovoPIA(LocalDate dataInizio, LocalDate dataFine, String messaggio){

        /*QUERY - prelevaSogliaPIA() = sogliaPIA*/

        DBMSBound db = new DBMSBound();

        if(db.creaConnessione()){
            //SE IL PERIODO INSERITO E SUCCESSIVO AL TRIMESTRE IN CORSO, SE LA DATA DI FINE E' SUCCESSIVA A QUELLA DI INIZIO, SE LE DATE SONO DELL'ANNO CORRENTE
            if(dataInizio.isAfter(db.acquisisciDataFineTurnazione()) && dataFine.isAfter(dataInizio) && (dataInizio.getYear() == LocalDate.now().getYear() && dataFine.getYear() == LocalDate.now().getYear())){

                //SE CI SONO ALTRI PIA OPPURE IL NUMERO DI GIORNI INSERITO E SUPERIORE A QUELLI CHE SI POSSEGGONO
                if(verificaPIA(dataInizio, dataFine) || numeroGiorni(dataInizio,dataFine) > db.acquisisciPIARimanenti()){
                    Utils.mostraPopUp(Alert.AlertType.ERROR, "Impossibile aggiungere P.I.A.", "Giorni P.I.A. superati o conflitti di richieste, reinserire correttamente le date.");
                } else {
                    boolean rifiuta = false;
                    try {

                        //PRELEVAMENTO RICHIESTE NEL PERIODO DEL PIA SELEZIONATO, SE ESISTE UNA RICHIESTA "ACCETTATA" ALLORA IL PIA VIENE RIFIUTATO
                        ResultSet rs = db.acquisisciRichieste();
                        ArrayList<Integer> listaComunicazioni = new ArrayList<>();
                        while(rs.next()){
                            LocalDate dataInizioRichiesta = rs.getDate("data_inizio_pv").toLocalDate();
                            LocalDate dataFineRichiesta = rs.getDate("data_fine_pv").toLocalDate();
                            if(dataInizioRichiesta.isAfter(dataInizio.minusDays(1)) && dataFineRichiesta.isBefore(dataFine.plusDays(1))){
                                listaComunicazioni.add(rs.getInt("ref_comunicazione"));

                                if(rs.getInt("ref_stato_comunicazione") == 1){
                                    rifiuta = true;
                                    break;
                                }
                            }
                        }

                        //SE NON E' RIFIUTATA
                        if(!rifiuta){

                            //SALVA PIA
                            db.creaPIA(dataInizio, dataFine, messaggio);
                            db.aggiornaPIARimanenti(db.acquisisciPIARimanenti() - numeroGiorni(dataInizio, dataFine));

                            //RIFIUTA TUTTE LE RICHIESTE IN QUEL PERIODO CON STATO IN ATTESA
                            for(int id : listaComunicazioni){
                                db.aggiornaStatoPV(id, 2);
                            }

                            //MANDA COMUNICAZIONI AVVISO AI DIPENDENTI
                            String messaggioCD = "Si comunica nuovo inserimento P.I.A.\n" +
                                    "Da: " + Utils.formattaData(dataInizio) + "\n" +
                                    "A: " + Utils.formattaData(dataFine) + "\n" +
                                    "Descrizione: \n" + messaggio +
                                    "\n\n" + "In tale periodo non sarà possibile effettuare richieste di ferie\n\n - Amministratore";

                            ResultSet rsDip = db.acquisisciListaDipendenti();

                            while (rsDip.next()){
                                //PROBLEMA TROPPE CONNESSIONI
                                db.creaNuovaComunicazioneCD(Utente.utenteLoggato.getMatricola(), rsDip.getInt("matricola"), LocalDate.now(), messaggioCD);
                            }

                            Utils.mostraPopUp(Alert.AlertType.INFORMATION, "Inserimento effettuato", "P.I.A. aggiunto con successo.");
                            Utils.mostraScenaMenu(Main.mainStage, "P.I.A", "gestioneturni/PIAWin.fxml");

                        } else {
                            Utils.mostraPopUp(Alert.AlertType.ERROR, "Impossibile aggiungere P.I.A.", "Giorni P.I.A. superati o conflitti di richieste, reinserire correttamente le date.");
                        }

                    } catch (SQLException | IOException e) {
                        throw new RuntimeException(e);
                    }
                }

            } else {
                Utils.mostraPopUp(Alert.AlertType.ERROR, "Errore date", "Inserire correttamente la data di inizio e la data di fine.");
            }
        } else {
            Utils.mostraPopUpErroreDB();
        }

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

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        } else {
            Utils.mostraPopUpErroreDB();
        }

        return rifiuta;
    }

    /**Mostra la finestra HomeWin*/
    public static void mostraPIAWin() throws IOException {
        Utils.mostraScenaMenu(Main.mainStage, "P.I.A.", "gestioneturni/PIAWin.fxml");
    }

}
