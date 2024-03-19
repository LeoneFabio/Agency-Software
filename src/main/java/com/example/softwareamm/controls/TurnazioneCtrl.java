package com.example.softwareamm.controls;

import com.example.softwareamm.Main;
import com.example.softwareamm.commons.DBMSBound;
import com.example.softwareamm.entity.Dipendente;
import com.example.softwareamm.gestioneturni.RegistroWinInterfaccia;
import com.example.softwareamm.commons.Utils;
import com.example.softwareamm.commons.Calendario;
import com.example.softwareamm.commons.TurnoDipendente;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

/**Control dei casi d'uso CalendarioAmm, RegistroGiornalieroAmm, VisualizzaTurniDip, VisualizzaTurniAmm,
 * VisualizzaTurniTendinaMesi, VisualizzaCalendarioProposta e VisualizzaPropostaTurnazioneTrimestrale
 * @version 1.0*/

public class TurnazioneCtrl {

    /*--------------------------------------------- TURNI_WIN ---------------------------------------------*/

    /**Preleva i turni del dipendente selezionato del trimestre corrente
     * @return Ritorna il calendario con i turni prelevati*/
    public static Calendario aquisisciTurni(){

        /*PRELEVARE TUTTI I TURNI DEL TRIMESTRE DEL DIPENDENTE LOGGATO*/

        /*QUERY - prelevaTurniDipendente(matricola) = listaTurniDipendente*/

        DBMSBound db = new DBMSBound();

        ResultSet rs = null;

        if(db.creaConnessione()){
            rs = db.acquisisciTurnazioneDip(Dipendente.dipendenteSelezionato.getMatricola());

            try {
                rs.next();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else {
            Utils.mostraPopUpErroreDB();
        }

        Calendario calendario = new Calendario(LocalDate.now(), rs);
        Utils.messaggioConsole("Calendario generato", Utils.Tipo_messaggio.INFO);
        return calendario;
    }

    /*--------------------------------------------- CALENDARIO_AMM_WIN ---------------------------------------------*/

    /**Mostra la finestra del registro associato alla data inserita
     * @param data Data del registro
     * @return Ritorna il Pane della finestra generata*/
    public static Parent mostraRegistroWin(LocalDate data){
        Parent root;
        try{
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("gestioneturni/RegistroWin.fxml"));
            root = loader.load();
            RegistroWinInterfaccia interfaccia = loader.getController();
            interfaccia.prelevaRegistro(data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return root;
    }

    /*--------------------------------------------- REGISTRO_WIN ---------------------------------------------*/

    /**Preleva dal DB il registro della data inserita
     * @param data Data di cui si vuole il registro
     * @return ritorna una lista osservabile del registro*/
    public static ObservableList<TurnoDipendente> prelevaTurniRegistro(LocalDate data){

        DBMSBound db = new DBMSBound();

        ArrayList<TurnoDipendente> registro = new ArrayList<>();

        if(db.creaConnessione()){

            ResultSet rs = db.acquisisciTurno(data);

            try{
                while(rs.next()){
                    if(rs.getInt("ora_inizio_turno") != -1 && rs.getInt("ora_fine_turno") != -1){
                        registro.add(new TurnoDipendente(rs.getInt("matricola"),
                                rs.getString("nome"), rs.getString("cognome"),
                                rs.getInt("ora_inizio_turno"),
                                rs.getInt("ora_fine_turno"))
                        );
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        } else {
            Utils.mostraPopUpErroreDB();
        }

        ObservableList<TurnoDipendente> list = FXCollections.observableArrayList(registro);

        return list;
    }
}
