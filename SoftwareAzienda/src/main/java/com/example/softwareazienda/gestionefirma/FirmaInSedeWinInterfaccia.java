package com.example.softwareazienda.gestionefirma;

import com.example.softwareazienda.controls.FirmaSedeCtrl;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;

public class FirmaInSedeWinInterfaccia {

    @FXML
    private ToggleGroup toggleGroup;

    @FXML
    private Button firmaBtn;

    @FXML
    private RadioButton ingressoRButton;

    @FXML
    private RadioButton uscitaRButton;

    @FXML
    private TextField nomeField;

    @FXML
    private TextField cognomeField;

    @FXML
    private TextField matricolaField;

    @FXML
    private void initialize(){
        ingressoRButton.setSelected(true);
        firmaBtn.setDisable(true);
    }

    @FXML
    private void abilitaBtn(){
        boolean disabilita = nomeField.getText().isEmpty() || cognomeField.getText().isEmpty() || matricolaField.getText().isEmpty();
        firmaBtn.setDisable(disabilita);
    }

    @FXML
    private void firma(){
        if(ingressoRButton.isSelected()) {
            FirmaSedeCtrl.effettuaFirmaIngresso(nomeField.getText(), cognomeField.getText(), matricolaField.getText());
        }else{
            FirmaSedeCtrl.effettuaFirmaUscita(nomeField.getText(), cognomeField.getText(), matricolaField.getText());
        }
    }

}
