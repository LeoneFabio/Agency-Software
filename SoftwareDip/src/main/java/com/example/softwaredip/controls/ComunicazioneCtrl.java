package com.example.softwaredip.controls;

import com.example.softwaredip.commons.DBMSBound;
import com.example.softwaredip.entity.Utente;
import com.example.softwaredip.gestionecomunicazioni.CDWinInterfaccia;
import com.example.softwaredip.gestionecomunicazioni.PVWinInterfaccia;
import com.example.softwaredip.Main;
import com.example.softwaredip.commons.Utils;
import com.example.softwaredip.commons.Richiesta;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;

/**Control dei casi d'uso ComunicazioniRicevute, ComunicazioniUfficio, ComunicazioniPV, PropostaTurnazioneTrimestrale, ComunicazioniInviate, NuovaComunAmm e CercaDestinatario
 * @version 1.0*/

public class ComunicazioneCtrl {

    /*COMUN_RICEV_WIN*/

    /**Preleva la lista delle comunicazioni dell'utente loggato dal DatabBase
     * @return Ritorna la lista delle comunicazioni*/

    public static ResultSet prelevaListaComunicazioni(){

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
     * @return Ritorna la scena della comunicazione, cambia l'interfaccia in base se la comunicazione
     * è CD o PV*/

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

                    LocalDate dataInizioPv = rsComunicazione.getDate("data_inizio_pv").toLocalDate();

                    ResultSet infoComunicazione = db.myQuery("SELECT * FROM comunicazioni_proposta_variazione cpv, comunicazioni c, turni t, dipendenti d " +
                                    "WHERE cpv.ref_comunicazione = c.id_comunicazione AND c.ref_utente_destinatario = t.ref_dipendente AND d.matricola = c.ref_utente_mittente " + /* JOIN */
                                    "AND id_comunicazione = "+ id + " AND t.data_odierna = '" + dataInizioPv + "' AND ref_utente_destinatario = " + Utente.utenteLoggato.getMatricola());

                    infoComunicazione.next();
                    int tipoPV = infoComunicazione.getInt("ref_tipo_proposta_variazione");
                    String messaggio = infoComunicazione.getString("messaggio");
                    LocalDate dataInizioPV = infoComunicazione.getDate("data_inizio_pv").toLocalDate();
                    LocalDate dataFinePV= infoComunicazione.getDate("data_fine_pv").toLocalDate();
                    int oraInizioPV= infoComunicazione.getTime("data_inizio_pv").toLocalTime().getHour();
                    int oraFinePV= infoComunicazione.getTime("data_fine_pv").toLocalTime().getHour();
                    int stato = infoComunicazione.getInt("ref_stato_comunicazione");
                    int ruolo = infoComunicazione.getInt("ref_ruolo");
                    int oraInizioTurno= infoComunicazione.getInt("ora_inizio_turno");
                    int oraFineTurno= infoComunicazione.getInt("ora_fine_turno");

                    int mittente = infoComunicazione.getInt("ref_utente_mittente");
                    Utils.messaggioConsole( id + " - " + tipoPV + " - " + messaggio + " - " + dataInizioPV + " - " + dataFinePV + " - " + oraInizioPV + " - " + oraFinePV + " - " + stato + " - " + ruolo + " - " +oraInizioTurno + " - " + oraFineTurno, Utils.Tipo_messaggio.DEBUGGING);

                    interfaccia.inizializzaPV(id, tipoPV, messaggio, dataInizioPV, dataFinePV, oraInizioPV, oraFinePV, stato, ruolo ,oraInizioTurno, oraFineTurno, mittente);

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

    public static void rifiutaPV(int id){
        int matricola = Utente.utenteLoggato.getMatricola();

        DBMSBound db = new DBMSBound();

        if(db.creaConnessione()){

            db.myUpdate("UPDATE comunicazioni SET ref_stato_comunicazione = 2 WHERE id_comunicazione = " + id + " AND ref_utente_destinatario = " + matricola);

        } else {
            Utils.mostraPopUpErroreDB();
        }
    }

    public static void accettaPV(int id, boolean isStraordinario, LocalDate dataInizioPV, LocalDate dataFinePV, int oraInizioPV, int oraFinePV, int oraInizioTurno, int oraFineTurno, int ruolo, int mittente, int tipoPV){
        int matricola = Utente.utenteLoggato.getMatricola();

        DBMSBound db = new DBMSBound();

        if(db.creaConnessione()){

            db.myUpdate("UPDATE comunicazioni SET ref_stato_comunicazione = 1 WHERE id_comunicazione = " + id + " AND ref_utente_destinatario = " + matricola);

            db.myUpdate("UPDATE comunicazioni SET ref_stato_comunicazione = 2 WHERE id_comunicazione = " + id + " AND ref_utente_destinatario != " + matricola);

            db.myUpdate("UPDATE comunicazioni SET ref_stato_comunicazione = 1 WHERE id_comunicazione = " + id + " AND ref_utente_destinatario = ref_utente_mittente");

            int oraFineProposta = oraFinePV;
            if(oraFinePV < oraInizioPV){
                oraFineProposta += 24;
            }

            ResultSet turnoDipMittente = db.acquisisciTurnoDip(mittente, dataInizioPV);
            if(isStraordinario){
                try{
                    turnoDipMittente.next();
                    if(oraInizioPV != turnoDipMittente.getInt("ora_inizio_turno")){
                        db.myUpdate("UPDATE `turni` SET `ora_fine_turno`='" + oraInizioPV + "' WHERE ref_dipendente = " + mittente + " AND data_odierna = '" + dataInizioPV + "'");

                    } else {

                        db.myUpdate("DELETE FROM `turni` WHERE `ora_inizio_turno`='" + oraInizioPV + "' AND ref_dipendente = " + mittente + " AND data_odierna = '" + dataInizioPV + "'");

                    }
                    db.myUpdate("INSERT INTO `turni`(`data_odierna`, `ref_dipendente`, `ora_inizio_turno`, `ora_fine_turno`, `ore_straordinarie_odierne`, `ruolo_giornaliero`) VALUES ('" + dataInizioPV + "','" + Utente.utenteLoggato.getMatricola() + "','" + oraInizioPV + "','" + oraFinePV + "','" + (oraFineProposta - oraInizioPV)  + "','" + ruolo + "')");


                    if(oraFinePV != oraFineTurno){
                        db.myUpdate("INSERT INTO `turni`(`data_odierna`, `ref_dipendente`, `ora_inizio_turno`, `ora_fine_turno`, `ruolo_giornaliero`) " +
                                "VALUES ('" + dataInizioPV + "','" + mittente + "','" + oraFinePV + "','" + turnoDipMittente.getInt("ora_fine_turno") + "','" + ruolo + "')");
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }

            } else {
                if((oraFineProposta - oraInizioPV) < 4 && (oraFineProposta - oraInizioPV) > 0){

                    try{
                        System.out.println("ORA INIZIO TURNO: " + oraInizioTurno);
                        turnoDipMittente.next();
                        System.out.println(oraInizioTurno);
                        System.out.println(turnoDipMittente.getInt("ora_inizio_turno"));
                        if(oraInizioTurno != turnoDipMittente.getInt("ora_inizio_turno")) {
                            if (oraInizioPV != turnoDipMittente.getInt("ora_inizio_turno")) {
                                System.out.println("primo if");
                                db.myUpdate("UPDATE `turni` SET `ora_fine_turno`='" + oraInizioPV + "' WHERE ref_dipendente = " + turnoDipMittente.getInt("ref_dipendente") + " AND data_odierna = '" + dataInizioPV + "'");
                                System.out.println(oraInizioPV);

                            } else {
                                System.out.println("elsee");
                                db.myUpdate("DELETE FROM `turni` WHERE `ora_inizio_turno`='" + oraInizioPV + "' AND ref_dipendente = " + mittente + " AND data_odierna = '" + dataInizioPV + "'");

                            }

                            db.myUpdate("INSERT INTO `turni`(`data_odierna`, `ref_dipendente`, `ora_inizio_turno`, `ora_fine_turno`, `ruolo_giornaliero`) VALUES ('" + dataInizioPV + "','" + Utente.utenteLoggato.getMatricola() + "','" + oraInizioPV + "','" + oraFinePV + "','" + ruolo + "')");

                            if (oraFinePV != oraFineTurno) {
                                db.myUpdate("INSERT INTO `turni`(`data_odierna`, `ref_dipendente`, `ora_inizio_turno`, `ora_fine_turno`, `ruolo_giornaliero`) " +
                                        "VALUES ('" + dataInizioPV + "','" + mittente + "','" + oraFinePV + "','" + turnoDipMittente.getInt("ora_fine_turno") + "','" + ruolo + "')");
                            }
                        }else{
                            if (oraInizioPV != turnoDipMittente.getInt("ora_inizio_turno")) {
                                System.out.println("secondo if");
                                db.myUpdate("UPDATE `turni` SET `ora_fine_turno`='" + oraInizioPV + "' WHERE ref_dipendente = " + turnoDipMittente.getInt("ref_dipendente") + " AND data_odierna = '" + dataInizioPV + "'");
                                db.myUpdate("UPDATE `turni` SET `ora_fine_turno`='" + oraInizioPV + "' WHERE ref_dipendente = " + Utente.utenteLoggato.getMatricola() + " AND data_odierna = '" + dataInizioPV + "'");

                            } else {
                                System.out.println("elsee");
                                db.myUpdate("DELETE FROM `turni` WHERE `ora_inizio_turno`='" + oraInizioPV + "' AND ref_dipendente = " + mittente + " AND data_odierna = '" + dataInizioPV + "'");

                            }

                            db.myUpdate("INSERT INTO `turni`(`data_odierna`, `ref_dipendente`, `ora_inizio_turno`, `ora_fine_turno`, `ruolo_giornaliero`) VALUES ('" + dataInizioPV + "','" + Utente.utenteLoggato.getMatricola() + "','" + oraInizioPV + "','" + oraFinePV + "','" + ruolo + "')");

                            if (oraFinePV != oraFineTurno) {
                                db.myUpdate("INSERT INTO `turni`(`data_odierna`, `ref_dipendente`, `ora_inizio_turno`, `ora_fine_turno`, `ruolo_giornaliero`) " +
                                        "VALUES ('" + dataInizioPV + "','" + mittente + "','" + oraFinePV + "','" + turnoDipMittente.getInt("ora_fine_turno") + "','" + ruolo + "')");
                                db.myUpdate("INSERT INTO `turni`(`data_odierna`, `ref_dipendente`, `ora_inizio_turno`, `ora_fine_turno`, `ruolo_giornaliero`) " +
                                        "VALUES ('" + dataInizioPV + "','" + Utente.utenteLoggato.getMatricola() + "','" + oraFinePV + "','" + turnoDipMittente.getInt("ora_fine_turno") + "','" + ruolo + "')");
                            }
                        }
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }

                } else {
                    try{
                        db.myUpdate("UPDATE `turni` SET `ora_inizio_turno`='" + turnoDipMittente.getInt("ora_inizio_turno") + "',`ora_fine_turno`='" + turnoDipMittente.getInt("ora_fine_turno") + "',`ruolo_giornaliero`='" + ruolo + "' WHERE data_odierna = '" + dataInizioPV + "' AND ref_dipendente = " + Utente.utenteLoggato.getMatricola()); //cambio il turno del dipIdoneo
                        db.myUpdate("UPDATE `turni` SET `ora_inizio_turno`='-1',`ora_fine_turno`='-1',`stato_giornaliero`='" + tipoPV + "' WHERE data_odierna = '" + dataInizioPV + "' AND ref_dipendente = " + mittente);// metto il mittente a riposos e cambio il suo stato
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

        } else {
            Utils.mostraPopUpErroreDB();
        }
    }



    /**Mostra la finsetra NuovaRichiestaWin*/

    public static void mostraNuovaRichiesta() throws IOException {
        Utils.mostraScena(Main.mainStage, "Nuova richiesta", "gestionecomunicazioni/NuovaRichiestaDipWin.fxml");
    }

    /**Mostra la finestra RichiesteInviateWins*/

    public static void mostraRichiesteInviate() throws IOException {
        Utils.mostraScena(Main.mainStage, "Richieste inviate", "gestionecomunicazioni/RichiesteInviateWin.fxml");
    }

    /*RICHIESTE_INVIATE_WIN*/

    /**Preleva dal DataBase le richieste effettuate dall'utente loggato
     * @return Ritorna una lista osservabile anche dalle tableView*/

    public static ObservableList<Richiesta> prelevalistaRichieste(){

        DBMSBound db = new DBMSBound();

        ArrayList<Richiesta> listaRichieste = new ArrayList<>();

        if(db.creaConnessione()){

            Richiesta.StatoRichiesta stato = Richiesta.StatoRichiesta.RIFIUTATA;

            try{
                ResultSet rs = db.acquisisciRichiesteDip(Utente.utenteLoggato.getMatricola());

                while (rs.next()){
                    switch (rs.getInt("ref_stato_comunicazione")){
                        case 1 -> {
                            stato = Richiesta.StatoRichiesta.ACCETTATA;
                        }

                        case 2 -> {
                            stato = Richiesta.StatoRichiesta.RIFIUTATA;
                        }

                        case 3 -> {
                            stato = Richiesta.StatoRichiesta.IN_ATTESA;
                        }
                    }

                    listaRichieste.add(new Richiesta(Richiesta.getTipo(rs.getString("nome").toUpperCase()), rs.getDate("data_inizio_pv").toLocalDate(), rs.getDate("data_fine_pv").toLocalDate(), rs.getString("messaggio"), stato));

                }

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else {
            Utils.mostraPopUpErroreDB();
        }

        ObservableList<Richiesta> list = FXCollections.observableArrayList(listaRichieste);

        return list;
    }

    /**Mostra la finestra HomeWin*/

    public static void mostraHomeWin() throws IOException {
        Utils.mostraScenaMenu(Main.mainStage, "Comunicazioni", "gestionecomunicazioni/ComunRicevWin.fxml");
    }

}
