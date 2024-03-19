package com.example.softwaredip.controls;

import com.example.softwaredip.Main;
import com.example.softwaredip.commons.Calendario;
import com.example.softwaredip.commons.DBMSBound;
import com.example.softwaredip.commons.Utils;
import com.example.softwaredip.gestionestipendi.CronologiaStipendioWinInterfaccia;
import com.example.softwaredip.entity.Utente;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

/**Control dei casi d'uso VisualizzaStipendioAmm, CronologiaStipendiAmm, VisualizzaStipendioDip e CronologiaStipendiDip
 * @version 1.0*/

public class StipendioCtrl {

    /*--------------------------------------------- CRONOLOGIA_WIN ---------------------------------------------*/

    /**Preleva lo stipendio passato del dipendente selezionato
     * @param data Data dello stipendio passato da prelevare
     * @return Ritorna la scena dello stipendio passato*/
    public static Parent prelevaStipendioPassato(LocalDate data) throws IOException {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("gestionestipendi/CronologiaStipendioWin.fxml"));
        Parent root = loader.load();
        CronologiaStipendioWinInterfaccia interfaccia = loader.getController();

        /*QUERY - prelevaInfoStipendioPassato(matricola, mese, anno) = infoStipendioPassato*/

        int matricola = Utente.utenteLoggato.getMatricola();

        DBMSBound db = new DBMSBound();

        if(db.creaConnessione()){

            ResultSet rs = db.acquisisciStipendioPassatoDip(matricola, data);

            try{
                rs.next();
                LocalDate dataStipendio = rs.getDate("data_stipendio").toLocalDate();
                int baseOraria = rs.getInt("base_stipendiale_oraria");
                int oreStraordinari = rs.getInt("ore_straordinari");
                int oreMensili = rs.getInt("ore_effettuate");
                double tassoStraordinari =  rs.getDouble("tasso_incremento_straordinari");
                double tassoAgevolazioni =  rs.getDouble("tasso_incremento_agevolazione");
                double pagaStraordinari = (baseOraria * oreStraordinari) * tassoStraordinari;
                double pagaAgevolazioni = (baseOraria * oreMensili) * tassoAgevolazioni - (baseOraria * oreMensili);
                double totale = oreMensili*baseOraria + pagaStraordinari + pagaAgevolazioni;

                interfaccia.inizializza(dataStipendio, baseOraria, pagaAgevolazioni, tassoAgevolazioni, pagaStraordinari, tassoStraordinari, totale, oreMensili, oreStraordinari);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        } else {
            Utils.mostraPopUpErroreDB();
        }

        /*ESEMPIO*/

        /*ESEMPIO*/

        return root;
    }

    /**Preleva dal DataBase la lista degli stipendi passati del dipendente selezionato
     * @return Ritorna la lista degli stipendi*/
    public static ArrayList<String> prelevaListaStipendiPassati(){
        /*INSERIRE QUI LA CREAZIONE DELLA LISTA CHE DOVRA ESSERE VISUALIZZATA*/

        DBMSBound db = new DBMSBound();

        ArrayList<String> cronologia = new ArrayList<>();

        if(db.creaConnessione()) {

            ResultSet rs = db.acquisisciCronologiaStipendiDip(Utente.utenteLoggato.getMatricola());
            try{
                while(rs.next()){
                    LocalDate data = rs.getDate("data_stipendio").toLocalDate();
                    cronologia.add(Calendario.italianMonth[data.getMonthValue()-1] + " " + data.getYear());
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        } else {
            Utils.mostraPopUpErroreDB();
        }

        return cronologia;
    }

    /**Mostra la finestra StipendioWin*/
    public static void mostraStipendioWin() throws IOException {
        Utils.mostraScenaMenu(Main.mainStage, "Situazione stipendiale", "gestionestipendi/StipendioWin.fxml");
    }

    /*--------------------------------------------- STIPENDIO_WIN ---------------------------------------------*/

    /**Preleva dal DataBase lo stipendio del mese corrente del dipendente selezionato
     * @return Ritorna lo stipendio del mese corrente*/
    public static double[] prelevaInfoStipendioAttuale(){

        DBMSBound db = new DBMSBound();

        if(db.creaConnessione()){
            return  db.acquisisciInfoStipendio(Utente.utenteLoggato.getMatricola());
        } else {
            Utils.mostraPopUpErroreDB();
        }

        return new double[]{};
    }

    /**Preleva dal DataBase lo stipendio del mese corrente del dipendente selezionato
     * @return Ritorna lo stipendio del mese corrente*/
    public static int prelevaOreEffettuate(){
        DBMSBound db = new DBMSBound();

        if(db.creaConnessione()){

            return db.acquisisciOreEffettuate(Utente.utenteLoggato.getMatricola());

        } else {
            Utils.mostraPopUpErroreDB();
        }

        return -1;
    }

    /**Mostra la finestra CronologiaStipendiWin*/
    public static void mostraCronologiaWin() throws IOException {
        Utils.mostraScena(Main.mainStage, "Cronologia Stipendi", "gestionestipendi/CronologiaWin.fxml");
    }

}
