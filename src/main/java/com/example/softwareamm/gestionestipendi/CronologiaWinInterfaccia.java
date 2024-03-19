package com.example.softwareamm.gestionestipendi;

import com.example.softwareamm.commons.Utils;
import com.example.softwareamm.controls.StipendioCtrl;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.net.URI;
import java.time.LocalDate;

/**Interfaccia della finestra CronologiaWin
 * @version 1.0
 * @see StipendioCtrl StipendioCtrl
 * */
public class CronologiaWinInterfaccia {

    @FXML
    private ListView<String> listaStipendi;

    @FXML
    private BorderPane borderPane;

    @FXML
    private void initialize(){
        //Inseriamo nella lista degli elementi
        listaStipendi.getStyleClass().add("testo");
        listaStipendi.getItems().addAll(StipendioCtrl.prelevaListaStipendiPassati());

        //Diamo alla lista un listener che effettua modifiche ogni volta che viene selezionato un elemento
        listaStipendi.getSelectionModel().selectedItemProperty().addListener(this::selectionChanged);

        Label nessunElementoLabel = new Label("Nessun elemento selezionato");
        nessunElementoLabel.setStyle("-fx-font-size: 15px");

        borderPane.setCenter(nessunElementoLabel);
    }

    //LISTENER DELLA LISTA
    private void selectionChanged(ObservableValue<? extends String> observable, String oldValue, String newValue){
        String selectedItems = listaStipendi.getSelectionModel().getSelectedItems().get(0);
        /*INSERIRE CIO CHE SI DEVE FARE QUANDO VIENE CLICCATO UN ELEMENTO DELLA LISTA*/
        try{

            String[] values = selectedItems.split(" ");

            LocalDate data = LocalDate.of(Integer.parseInt(values[1]), Utils.prelevaValoreMese(values[0])+1, 1);
            borderPane.setCenter(StipendioCtrl.prelevaStipendioPassato(data));
        } catch(Exception ex){
            ex.printStackTrace();
        }
    }

    @FXML
    private void indietro() throws IOException {
        StipendioCtrl.mostraStipendioWin();
    }
}
