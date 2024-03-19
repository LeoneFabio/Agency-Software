package com.example.softwareamm.controls;

import com.example.softwareamm.commons.DBMSBound;
import com.example.softwareamm.entity.Utente;
import com.example.softwareamm.gestionecomunicazioni.CDWinInterfaccia;
import com.example.softwareamm.gestionecomunicazioni.PVWinInterfaccia;
import com.example.softwareamm.Main;
import com.example.softwareamm.commons.Utils;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**Control dei casi d'uso ComunicazioniRicevute, ComunicazioniUfficio, ComunicazioniPV, PropostaTurnazioneTrimestrale, ComunicazioniInviate, NuovaComunAmm e CercaDestinatario
 * @version 1.0*/
public class ComunicazioneCtrl {

    /*--------------------------------------------- COMUN_RICEV_WIN ---------------------------------------------*/

    /**Preleva dal DatabBase la lista delle comunicazioni dell'utente loggato
     * @return Ritorna la lista delle comunicazioni*/
    public static ResultSet prelevaListaComunicazioni(){

        /*QUERY - prelevaListaComunicazioni(matricola) = listaDelleComunicazioni*/

        DBMSBound db = new DBMSBound();

        ResultSet rs = null;

        if(db.creaConnessione()){

            rs = db.acquisisciListaComuniazioni(Utente.utenteLoggato.getMatricola());
            try{
                rs.next();
            } catch (SQLException e) {
                e.printStackTrace();
            }

        } else {
            Utils.mostraPopUpErroreDB();
        }

        return rs;
    }

    /**Preleva le informazioni di una comunicazione
     * @param id Identificativo della comunicazione
     * @param tipo tipologia della comunicazione
     * @return Ritorna la scena della comunicazione, cambia l'interfaccia in base al tipo di comunicazione
     * (CD o PV)*/
    public static Parent prelevaComunicazione(int id , String tipo) {

        /*PRELEVA MATRICOLA DALL'ENTITY*/

        DBMSBound db = new DBMSBound();
        Parent root = null;

        if(db.creaConnessione()){

            try{
                ResultSet rsComunicazione = db.acquisisciComunicazione(id, tipo, Utente.utenteLoggato.getMatricola());
                rsComunicazione.next();
                if(tipo.equals("PV")){

                    FXMLLoader loader = new FXMLLoader(Main.class.getResource("gestionecomunicazioni/PVWin.fxml"));
                    root = loader.load();
                    PVWinInterfaccia interfaccia = loader.getController();

                    if(rsComunicazione.getInt("ref_tipo_proposta_variazione") == 8){
                        interfaccia.inizializzaPV(id,
                                true,
                                tipo,
                                rsComunicazione.getInt("ref_tipo_proposta_variazione"),
                                rsComunicazione.getString("messaggio") ,
                                LocalDateTime.now(),
                                LocalDateTime.now(),
                                rsComunicazione.getInt("ref_stato_comunicazione"),
                                "",
                                "",
                                0,
                                0);
                    } else {
                        ResultSet rsMatricola = db.acquisisciInfoComunicazione(id);
                        rsMatricola.next();
                        int matricola = rsMatricola.getInt("ref_utente_mittente");

                        ResultSet rsInfoDip = db.acquisisciInfoUtente(matricola);
                        rsInfoDip.next();

                        LocalTime oraInizio = rsComunicazione.getTime("data_inizio_pv").toLocalTime();
                        LocalTime oraFine = rsComunicazione.getTime("data_fine_pv").toLocalTime();

                        LocalDateTime dataInizio = rsComunicazione.getDate("data_inizio_pv").toLocalDate().atTime(oraInizio);
                        LocalDateTime dataFine = rsComunicazione.getDate("data_fine_pv").toLocalDate().atTime(oraFine);

                        interfaccia.inizializzaPV(id,
                                false,
                                tipo,
                                rsComunicazione.getInt("ref_tipo_proposta_variazione"),
                                rsComunicazione.getString("messaggio") ,
                                dataInizio,
                                dataFine,
                                rsComunicazione.getInt("ref_stato_comunicazione"),
                                rsInfoDip.getString("nome"),
                                rsInfoDip.getString("cognome"),
                                rsInfoDip.getInt("matricola"),
                                rsInfoDip.getInt("ref_ruolo"));
                    }

                } else {
                    //COMUNICAZIONE D'UFFICIO
                    FXMLLoader loader = new FXMLLoader(Main.class.getResource("gestionecomunicazioni/CDWin.fxml"));
                    root = loader.load();
                    CDWinInterfaccia interfaccia = loader.getController();
                    interfaccia.inizializza(tipo, "Informativa", rsComunicazione.getString("messaggio"));
                }

            } catch (IOException ex){
                ex.printStackTrace();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        } else {
            Utils.mostraPopUpErroreDB();
        }

        return root;
    }

    /**Mostra la finestra NuovaComunWin*/
    public static void mostraNuovaComunWin() throws IOException {
        Utils.mostraScena(Main.mainStage, "Nuova Comunicazione", "gestionecomunicazioni/NuovaComunWin.fxml");
    }

    /*--------------------------------------------- NUOVA_COMUN_WIN ---------------------------------------------*/

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

    /**Salva nel sistema una nuova comunicazione d'ufficio (CD)
     * @param destinatario Destinatario della comunicazione, può essere singolo, di ruolo o tutti
     * @param messaggio Descrizione della richiesta*/
    public static void inviaCD(boolean isRuolo, String destinatario, String messaggio){

        DBMSBound db = new DBMSBound();

        if(db.creaConnessione()){
            try{
                if(isRuolo){
                    int ruolo = 0;
                    if(destinatario.equals("Ruolo 1")){
                        ruolo = 1;
                    } else if(destinatario.equals("Ruolo 2")){
                        ruolo = 2;
                    } else if(destinatario.equals("Ruolo 3")){
                        ruolo = 3;
                    } else if(destinatario.equals("Ruolo 4")){
                        ruolo = 4;
                    } else if(destinatario.equals("Tutti")){
                        ruolo = 0;
                    }

                    ResultSet rsDip = db.acquisisciListaDipendenti();

                    while(rsDip.next()){
                        if((ruolo == 0) || (rsDip.getInt("ref_ruolo") == ruolo)){
                            db.creaNuovaComunicazioneCD(Utente.utenteLoggato.getMatricola(), rsDip.getInt("matricola"), LocalDate.now(), messaggio);
                        }
                    }

                } else {
                    int matricolaDip = Integer.parseInt(destinatario.substring(0,4));
                    db.creaNuovaComunicazioneCD(Utente.utenteLoggato.getMatricola(), matricolaDip, LocalDate.now(), messaggio);

                }
                Utils.mostraPopUp(Alert.AlertType.INFORMATION, "Comunicazione inviata", "Comunicazione inviata con sucesso");

                Utils.mostraScenaMenu(Main.mainStage, "Visualizza dipendenti", "gestionecomunicazioni/ComunRicevWin.fxml");

            } catch (SQLException | IOException e) {
                throw new RuntimeException(e);
            }

            Utils.messaggioConsole("NUOVA COMUNICAZIONE (CD):\n\t\t\t" + "A: " + destinatario + "\n\t\t\tDescrizione: " + messaggio, Utils.Tipo_messaggio.INFO);
        } else {
            Utils.mostraPopUpErroreDB();
        }

    }

    /**Mostra la finestra NuovaRichiestaWin*/
    public static void mostraNuovaRichiestaAmmWin() throws IOException {
        Utils.mostraScena(Main.mainStage, "Nuova Richiesta Amm", "gestionecomunicazioni/NuovaRichiestaAmmWin.fxml");
    }

    /**Mostra la finestra HomeWin*/
    public static void mostraComunRicevWin() throws IOException {
        Utils.mostraScenaMenu(Main.mainStage, "Comunicazioni", "gestionecomunicazioni/ComunRicevWin.fxml");
    }

    /*--------------------------------------------- PV_WIN ---------------------------------------------*/

    public static void rifiutaPV(int id){
        DBMSBound db = new DBMSBound();

        if(db.creaConnessione()){
            db.aggiornaStatoPV(id, 2);
        } else {
            Utils.mostraPopUpErroreDB();
        }

    }

    public static void accettaPV(int id){
        DBMSBound db = new DBMSBound();

        if(db.creaConnessione()){
            db.aggiornaStatoPV(id, 1);
        } else {
            Utils.mostraPopUpErroreDB();
        }

    }
}
