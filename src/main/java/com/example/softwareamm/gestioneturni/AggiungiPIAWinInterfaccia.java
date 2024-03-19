package com.example.softwareamm.gestioneturni;

import com.example.softwareamm.controls.PIACtrl;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;

import java.io.IOException;

/**Interfaccia della finestra AggiungiPIAWin
 * @version 1.0
 * @see PIACtrl PIACtrl
 * */
public class AggiungiPIAWinInterfaccia {
    @FXML
    private DatePicker inizioDatePicker;

    @FXML
    private DatePicker fineDatePicker;

    @FXML
    private TextArea descrizioneField;

    @FXML
    private Button inviaBtn;

    @FXML
    private void initialize(){
        inizioDatePicker.valueProperty().addListener((ov, oldValue, newValue) -> {
            fineDatePicker.setValue(newValue);
            abilitaBtn();
        });

        fineDatePicker.valueProperty().addListener((ov, oldValue, newValue) -> {
            abilitaBtn();
        });

        abilitaBtn();

    }

    @FXML
    private void abilitaBtn(){
        /*AMPLIARE LOGICA - CON I DATEPICKER*/

        if(inizioDatePicker.getValue() == null || fineDatePicker.getValue() == null || descrizioneField.getText().isEmpty()){
            inviaBtn.setDisable(true);
        } else {
            inviaBtn.setDisable(false);
        }
    }

    @FXML
    private void inviaPIA(){
        PIACtrl.creaNuovoPIA(inizioDatePicker.getValue(), fineDatePicker.getValue(), descrizioneField.getText());
    }

    @FXML
    private void indietro() throws IOException {
        PIACtrl.mostraPIAWin();
    }


}
