package com.example.softwareamm.gestioneturni;

import com.example.softwareamm.commons.Utils;
import com.example.softwareamm.controls.PIACtrl;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;

/**Interfaccia della finestra PIAWin
 * @version 1.0
 * @see PIACtrl PIACtrl
 * */
public class PIAWinInterfaccia {

    private String PIASelezionata;

    @FXML
    private Label giorniUtilizatiLabel;

    @FXML
    private Label giorniRimanentiLabel;

    @FXML
    private ListView<String> PIAListView;

    @FXML
    private Button rimuoviBtn;

    @FXML
    private void initialize(){
        inizializzaLista();

        int[] infoPIA = PIACtrl.prelevaGiorniInfoPIA();

        giorniRimanentiLabel.setText("Giorni rimanenti: " + infoPIA[0] + " giorni");
        giorniUtilizatiLabel.setText("Giorni utilizzati: " + infoPIA[1] + " giorni");

        rimuoviBtn.setVisible(false);
    }

    private void inizializzaLista(){

        PIAListView.getSelectionModel().selectedItemProperty().addListener(this::selectionChanged);

        PIAListView.getItems().addAll(PIACtrl.prelevaListaPIA());
    }

    private void selectionChanged(ObservableValue<? extends String> observable, String oldValue, String newValue){
        ObservableList<String> selectedItems = PIAListView.getSelectionModel().getSelectedItems();

        if(!selectedItems.isEmpty()){
            PIASelezionata = selectedItems.get(0);
        }

        rimuoviBtn.setVisible(!PIASelezionata.isEmpty());
    }

    @FXML
    private void nuovaPIA() throws IOException {
        PIACtrl.mostraAggiungiPIAWin();
    }

    @FXML
    private void rimuoviPIA(){
        String PIASelezionata = PIAListView.getSelectionModel().getSelectedItem();
        String[] valoriPIA = PIASelezionata.split(" ");
        String[] dataInizioStr = valoriPIA[0].split("/");
        String[] dataFineStr = valoriPIA[2].split("/");

        LocalDate dataInizio = LocalDate.of(Integer.parseInt(dataInizioStr[2]), Integer.parseInt(dataInizioStr[1]), Integer.parseInt(dataInizioStr[0]));
        LocalDate dataFine = LocalDate.of(Integer.parseInt(dataFineStr[2]), Integer.parseInt(dataFineStr[1]), Integer.parseInt(dataFineStr[0]));

        PIACtrl.rimuoviPIA(dataInizio, dataFine);
        PIAListView.getSelectionModel().clearSelection();
        PIAListView.getItems().clear();

        initialize();
    }
}
