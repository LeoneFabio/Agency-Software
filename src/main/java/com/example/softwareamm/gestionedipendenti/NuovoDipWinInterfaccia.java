package com.example.softwareamm.gestionedipendenti;

import com.example.softwareamm.controls.GestioneDipendentiCtrl;
import javafx.beans.Observable;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**Interfaccia della finestra NuovoDipWin
 * @version 1.0
 * @see GestioneDipendentiCtrl GestioneDipendentiCtrl
 * */

public class NuovoDipWinInterfaccia {

    @FXML
    private TextField nomeField;

    @FXML
    private TextField cognomeField;

    @FXML
    private TextField CFField;

    @FXML
    private ChoiceBox<String> ruoloChoice;

    @FXML
    private TextField emailField;

    @FXML
    private TextField indirizzoField;

    @FXML
    private TextField cellulareField;

    @FXML
    private ChoiceBox<String> agevolazioniChoiceBox;

    @FXML
    private ListView<String> agevolazioniListView;

    @FXML
    private TextArea noteField;

    @FXML
    private Button aggiungiBtn;

    @FXML
    private void initialize(){
        ruoloChoice.getItems().addAll("Ruolo 1", "Ruolo 2", "Ruolo 3", "Ruolo 4");
        ruoloChoice.setValue("Ruolo 1");

        agevolazioniListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        agevolazioniListView.getSelectionModel().selectedItemProperty().addListener(this::selectionChanged);

        aggiungiBtn.setDisable(true);
    }

    @FXML
    private void abilitaBtn(){
        boolean disabilita =
                nomeField.getText().isEmpty() ||
                cognomeField.getText().isEmpty() ||
                CFField.getText().isEmpty() ||
                emailField.getText().isEmpty() ||
                indirizzoField.getText().isEmpty() ||
                cellulareField.getText().isEmpty() ||
                (ruoloChoice.getValue().isEmpty()) ||
                (agevolazioniChoiceBox.getValue() == null);

        aggiungiBtn.setDisable(disabilita);
    }

    @FXML
    private void aggiungiNuovoDip(){

        int ruolo = ruoloChoice.getSelectionModel().getSelectedIndex() + 1;

        GestioneDipendentiCtrl.aggiungiNuovoDip(
                nomeField.getText(),
                cognomeField.getText(),
                CFField.getText(),
                emailField.getText(),
                indirizzoField.getText(),
                cellulareField.getText(),
                ruolo,
                prelevaAgevolazione(),
                noteField.getText()
        );
    }

    @FXML
    private void indietro() throws IOException {
        GestioneDipendentiCtrl.mostraListaDipWin();
    }

    private int[] prelevaAgevolazione(){
        ArrayList<Integer> list = new ArrayList<>();
        String[] arr = agevolazioniChoiceBox.getValue().split(", ");
        for(String x: arr){
            if(x.equals("Figli a carico")){
                list.add(4);
            } else if(x.equals("Disabilità")){
                list.add(3);
            } else if(x.equals("Nessuna")){
                list.add(1);
            } else {
                System.out.println("errore");
            }
        }

        int[] result = new int[list.size()];
        for(int i = 0; i < list.size(); i++) result[i] = list.get(i);
        return result;

    }

    private void selectionChanged(Observable observable) {
        List<String> lista = agevolazioniListView.getSelectionModel().getSelectedItems();

        if(!lista.isEmpty()){
            if(lista.contains("Nessuna")){
                agevolazioniChoiceBox.setValue("Nessuna");
            } else {
                StringBuilder stringBr = new StringBuilder(agevolazioniListView.getSelectionModel().getSelectedItems().toString());
                stringBr.deleteCharAt(stringBr.indexOf("["));
                stringBr.deleteCharAt(stringBr.indexOf("]"));
                agevolazioniChoiceBox.setValue(stringBr.toString());
            }
        }
        abilitaBtn();
    }

    @FXML
    private void tendina(){
        if(agevolazioniListView.getHeight()>2){
            agevolazioniListView.setPrefHeight(0);
            agevolazioniListView.getItems().clear();
        } else {
            agevolazioniListView.setPrefHeight(86);
            agevolazioniListView.getItems().addAll("Figli a carico","Disabilità","Nessuna");
        }
        abilitaBtn();
    }

}
