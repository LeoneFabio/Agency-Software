package com.example.softwareamm.gestionecomunicazioni;

import com.example.softwareamm.controls.ComunicazioneCtrl;
import com.example.softwareamm.controls.GestioneDipendentiCtrl;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

/**Interfaccia della finestra NuovaComunWin
 * @version 1.0
 * @see ComunicazioneCtrl ComunicazioneCtrl
 * */

public class NuovaComunWinInterfaccia {

    private ArrayList<String> lista = new ArrayList<>();

    @FXML
    private ToggleGroup toggleGroup;

    @FXML
    private RadioButton singoloRBtn;

    @FXML
    private RadioButton ruoloRBtn;

    @FXML
    private TextArea descrizioneField;

    @FXML
    private ComboBox<String> comboBox;

    @FXML
    private Button cercaBtn;

    @FXML
    private Button inviaBtn;

    @FXML
    private void initialize(){
        caricaLista();
        selezionaLista();
        comboBox.getSelectionModel().selectedItemProperty().addListener(this::selectionChanged);
        comboBox.setValue("");
        inviaBtn.setDisable(true);

        comboBox.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            comboBox.setValue(newValue);
            abilitaBtn();
        });
    }

    private void caricaLista(){
        ResultSet rs = ComunicazioneCtrl.prelevaListaDipendenti();
        try{
            do{
                lista.add(String.format("%04d", Integer.valueOf(rs.getString("Matricola"))) + " - " + rs.getString("Nome") + " " + rs.getString("Cognome"));
            } while(rs.next());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        comboBox.getItems().addAll(lista);
        comboBox.maxHeight(150);
    }

    private void selectionChanged(ObservableValue<? extends String> observable, String oldValue, String newValue){
        if(newValue != null){
            if(!newValue.isEmpty()){
                abilitaBtn();
            }
        }
    }

    @FXML
    private void selezionaLista(){
        comboBox.getItems().clear();
        if(singoloRBtn.isSelected()){
            comboBox.getItems().addAll(lista);
            cercaBtn.setDisable(false);
            comboBox.setEditable(true);
            comboBox.setValue("");
        } else if(ruoloRBtn.isSelected()) {
            comboBox.getItems().addAll(Arrays.asList(
                    "Ruolo 1",
                    "Ruolo 2",
                    "Ruolo 3",
                    "Ruolo 4",
                    "Tutti"
            ));
            cercaBtn.setDisable(true);
            comboBox.setEditable(false);
            comboBox.setValue("Ruolo 1");
        } else {
            System.out.println("Errore");
        }

        descrizioneField.clear();
        abilitaBtn();
    }

    @FXML
    private void aggiornaLista(){
        comboBox.getItems().clear();
        if(comboBox.getValue() == null){
            comboBox.getItems().addAll(lista);
        } else {
            for(String x: lista){
                String lowerCaseX = x.toLowerCase();
                if(lowerCaseX.contains(comboBox.getValue().toLowerCase())){
                    comboBox.getItems().add(x);
                }
            }
        }
        comboBox.show();
    }

    @FXML
    private void abilitaBtn(){
        boolean corretto = false;
        if(singoloRBtn.isSelected()){
            corretto = esiste(comboBox.getValue());
        } else if(ruoloRBtn.isSelected()){
            if(comboBox.getValue() != null){
                if(!comboBox.getValue().isEmpty()){
                    corretto = true;
                }
            }
            corretto = true;
        } else {
            System.out.println("Errore");
        }

        if(corretto && !descrizioneField.getText().isEmpty()){
            inviaBtn.setDisable(false);
        } else {
            inviaBtn.setDisable(true);
        }
    }

    private boolean esiste(String valore){
        for(String x: lista){
            if(valore.equals(x)){
                return true;
            }
        }
        return false;
    }

    @FXML
    private void inviaComunicazione() {

        ComunicazioneCtrl.inviaCD(ruoloRBtn.isSelected(), comboBox.getValue(), descrizioneField.getText());
        comboBox.setValue("");
        descrizioneField.clear();
    }

    @FXML
    private void nuovaRichiestaDip() throws IOException {
        ComunicazioneCtrl.mostraNuovaRichiestaAmmWin();
    }

    @FXML
    private void indietro() throws IOException {
        ComunicazioneCtrl.mostraComunRicevWin();
    }

}
