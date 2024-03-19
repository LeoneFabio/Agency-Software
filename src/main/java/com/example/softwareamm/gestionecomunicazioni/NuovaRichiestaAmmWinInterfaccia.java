package com.example.softwareamm.gestionecomunicazioni;

import com.example.softwareamm.controls.AstensioneCtrl;
import com.example.softwareamm.controls.ComunicazioneCtrl;
import com.example.softwareamm.controls.GestioneDipendentiCtrl;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

/**Interfaccia della finestra NuovaRichiestaAmmWin
 * @version 1.0
 * @see AstensioneCtrl AstensioneCtrl
 * */
public class NuovaRichiestaAmmWinInterfaccia {

    private final ArrayList<String> listaDipendenti = new ArrayList<>();

    private int dipSelezionato;

    @FXML
    private ComboBox<String> comboBox;

    @FXML
    private Button avantiBtn;

    @FXML
    private void initialize(){
        caricaListaDipendenti();
        comboBox.getSelectionModel().selectedItemProperty().addListener(this::selectionChanged);
        comboBox.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            comboBox.setValue(newValue);
            abilitaBtn();
        });
        comboBox.maxHeight(150);
        avantiBtn.setDisable(true);
    }

    private void caricaListaDipendenti(){
        ResultSet rs = AstensioneCtrl.prelevaListaDipendenti();
        try{
            do{
                listaDipendenti.add(String.format("%04d", Integer.valueOf(rs.getString("Matricola"))) + " - " + rs.getString("Nome") + " " + rs.getString("Cognome"));
            } while(rs.next());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        comboBox.getItems().addAll(listaDipendenti);
    }

    private void selectionChanged(ObservableValue<? extends String> observable, String oldValue, String newValue){
        if(newValue != null){
            if(!newValue.isEmpty()){
                abilitaBtn();
            }

        }
        comboBox.show();
    }

    @FXML
    private void aggiornaLista(){
        comboBox.getItems().clear();

        if(comboBox.getValue() == null){
            comboBox.getItems().addAll(listaDipendenti);
        } else {
            for(String x: listaDipendenti){
                String lowerCaseX = x.toLowerCase();
                if(lowerCaseX.contains(comboBox.getValue().toLowerCase())){
                    comboBox.getItems().add(x);
                }
            }
        }

        comboBox.show();
    }

    private void abilitaBtn(){
        avantiBtn.setDisable(true);
        for(String x: listaDipendenti){
            if(x.equals(comboBox.getValue())){
                avantiBtn.setDisable(false);
                break;
            }
        }
    }

    @FXML
    private void avanti() throws IOException {
        /*SALVARE IL DIPENDENTE NELL'ENTITY??*/
        if(!comboBox.getValue().isEmpty()){
            dipSelezionato = Integer.parseInt(comboBox.getSelectionModel().getSelectedItem().substring(0,4));
            AstensioneCtrl.mostraNuovaRichiestaDipWin(dipSelezionato);
        } else {
            abilitaBtn();
        }
    }

    @FXML
    private void indietro() throws IOException {
        AstensioneCtrl.mostraNuovaComunWin();
    }

}
