package com.example.softwareamm.gestionecomunicazioni;

import com.example.softwareamm.commons.Comunicazione;
import com.example.softwareamm.commons.DBMSBound;
import com.example.softwareamm.commons.Utils;
import com.example.softwareamm.controls.ComunicazioneCtrl;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**Interfaccia della finestra ComunRicevWin
 * @version 1.0
 * @see ComunicazioneCtrl ComunicazioneCtrl
 * */
public class ComunRicevWinInterfaccia {

    @FXML
    private BorderPane borderPane;

    @FXML
    private ListView<Comunicazione> ricevutiListView;

    @FXML
    private void initialize(){
        /*PRELEVIAMO LE COMUNICAZIONI DEL DIPENDENTE E LE INSERIAMO NELLA LISTA*/
        ricevutiListView.getStyleClass().add("testo");
        prelevaListaComunicazioni();

        //Creiamo un listener per la lista delle comunicazioni
        ricevutiListView.getSelectionModel().selectedItemProperty().addListener(this::selectionChanged);

        Label nessunElemento = new Label("Nessun elemento selezionato");
        nessunElemento.setStyle("-fx-font-size: 15px");

        borderPane.setCenter(nessunElemento);
    }

    private void prelevaListaComunicazioni(){
        ResultSet rs = ComunicazioneCtrl.prelevaListaComunicazioni();
        try{
            rs.beforeFirst();
            if(!rs.next()){
                Utils.messaggioConsole("Non sono presenti comunicazioni", Utils.Tipo_messaggio.INFO);
            } else {
                do{
                    ricevutiListView.getItems().add(
                            new Comunicazione(rs.getInt("id_comunicazione"),
                                    rs.getBoolean("flag_visualizzazione"),
                                    rs.getDate("data_comunicazione").toLocalDate(),
                                    rs.getString("tipo_comunicazione")));
                } while (rs.next());
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    private void selectionChanged(ObservableValue<? extends Comunicazione> observable, Comunicazione oldValue, Comunicazione newValue){
        Comunicazione selectedItems = ricevutiListView.getSelectionModel().getSelectedItems().get(0);

        if(!selectedItems.isFlagVisualizzazione()){
            DBMSBound db = new DBMSBound();
            db.creaConnessione();
            db.aggiornaVisualizzazione(selectedItems.getId());
        }
        borderPane.setCenter(ComunicazioneCtrl.prelevaComunicazione(selectedItems.getId(), selectedItems.getTipo()));
    }

    @FXML
    private void apriNuovaComunicazione() throws IOException {
        ComunicazioneCtrl.mostraNuovaComunWin();
    }
}
