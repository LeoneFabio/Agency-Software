package com.example.softwareamm.gestionedipendenti;

import com.example.softwareamm.controls.GestioneDipendentiCtrl;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**Interfaccia della finestra ListaDipWin
 * @version 1.0
 * @see GestioneDipendentiCtrl GestioneDipendentiCtrl
 * */

public class ListaDipInterfaccia {

    private final ArrayList<String> listaDipendenti = new ArrayList<>();

    private int dipSelezionato;

    @FXML
    private TextField casellaRicercaField;

    @FXML
    private ListView<String> dipendentiListView;

    @FXML
    private Button visualizzaBtn;

    @FXML
    private Button eliminaBtn;

    @FXML
    private void initialize(){

        prelevaListaDip();

        dipendentiListView.getStyleClass().add("testo");

        dipendentiListView.getItems().addAll(listaDipendenti);

        dipendentiListView.getSelectionModel().selectedItemProperty().addListener(this::selectionChanged);

        visualizzaBtn.setVisible(false);
        eliminaBtn.setVisible(false);
    }

    private void prelevaListaDip(){
        ResultSet rs = GestioneDipendentiCtrl.prelevaListaDipendenti();
        try{
            do{
                listaDipendenti.add(String.format("%04d", Integer.valueOf(rs.getString("Matricola"))) + " - " + rs.getString("Nome") + " " + rs.getString("Cognome"));
            } while(rs.next());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void selectionChanged(ObservableValue<? extends String> observable, String oldValue, String newValue){
        ObservableList<String> selectedItems = dipendentiListView.getSelectionModel().getSelectedItems();

        if(!selectedItems.isEmpty()){
            dipSelezionato = Integer.parseInt(selectedItems.get(0).substring(0,4));
        } else {
            dipSelezionato = -1;
        }

        if(dipSelezionato != -1){
            visualizzaBtn.setVisible(true);
            eliminaBtn.setVisible(true);
        } else {
            visualizzaBtn.setVisible(false);
            eliminaBtn.setVisible(false);
        }
    }

    @FXML
    private void aggiornaLista(){
        ObservableList<String> listaOsservabile = dipendentiListView.getItems();
        listaOsservabile.clear();

        for(String x: listaDipendenti){
            String lowerCaseX = x.toLowerCase();
            if(lowerCaseX.contains(casellaRicercaField.getText().toLowerCase())){
                listaOsservabile.add(x);
            }
        }
        dipendentiListView.setItems(listaOsservabile);

        dipSelezionato = -1;
        visualizzaBtn.setVisible(false);
        eliminaBtn.setVisible(false);
    }

    @FXML
    private void apriNuovoDipendente() throws IOException {
        dipSelezionato = -1;
        GestioneDipendentiCtrl.mostraNuovoDipWin();
    }

    @FXML
    private void visualizzaDipendente() throws IOException {
        GestioneDipendentiCtrl.selezionaDipendente(dipSelezionato);
        GestioneDipendentiCtrl.mostraVisDipendenteWin();
    }

    @FXML
    private void eliminaDipendente(){
        GestioneDipendentiCtrl.eliminaDipendente(dipSelezionato);

        dipendentiListView.getSelectionModel().clearSelection();
        listaDipendenti.clear();
        dipendentiListView.getItems().clear();
        initialize();
    }



}
