package com.example.softwaredip.gestionecomunicazioni;

import com.example.softwaredip.commons.Richiesta;
import com.example.softwaredip.controls.AstensioneCtrl;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;

/**Interfaccia della finestra NuovaRichiestaDipWin
 * @version 1.0
 * @see AstensioneCtrl AstensioneCtrl
 * */
public class NuovaRichiestaDipWinInterfaccia {

    @FXML
    private ComboBox<String> comboBox;

    @FXML
    private ToggleGroup dayToggle;

    @FXML
    private RadioButton siRBtn;

    @FXML
    private RadioButton noRBtn;

    @FXML
    private DatePicker inizioDatePicker;

    @FXML
    private DatePicker fineDatePicker;

    @FXML
    private TextField oraInizioField;

    @FXML
    private TextField oraFineField;

    @FXML
    private TextArea motivazioneField;

    @FXML Button inviaBtn;

    @FXML
    private void initialize(){
        siRBtn.setSelected(true);

        comboBox.getItems().addAll(
                Richiesta.TipoRichiesta.FERIE.toString(),
                Richiesta.TipoRichiesta.MALATTIA.toString(),
                Richiesta.TipoRichiesta.CONGEDO_PARENTALE.toString(),
                Richiesta.TipoRichiesta.PERMESSO.toString(),
                Richiesta.TipoRichiesta.SCIOPERO.toString()
        );

        comboBox.getSelectionModel().selectedIndexProperty().addListener(
                (observableValue, number, number2) -> impostazioniForm(comboBox.getItems().get((Integer) number2)));

        inizioDatePicker.valueProperty().addListener((ov, oldValue, newValue) -> {
            abilitaFirma();
            fineDatePicker.setValue(inizioDatePicker.getValue());
        });

        inizioDatePicker.valueProperty().addListener((ov, oldValue, newValue) -> {
            abilitaFirma();
        });

        disabilitaModuli(true);

        inviaBtn.setDisable(true);
    }

    private void disabilitaModuli(boolean modalita){
        siRBtn.setDisable(modalita);
        noRBtn.setDisable(modalita);
        inizioDatePicker.setDisable(modalita);
        inizioDatePicker.setValue(null);
        fineDatePicker.setDisable(modalita);
        fineDatePicker.setValue(null);
        oraInizioField.setDisable(modalita);
        oraInizioField.setText("");
        oraFineField.setDisable(modalita);
        oraFineField.setText("");
        motivazioneField.setDisable(modalita);
    }

    @FXML
    private void impostazioniForm(String tipologia){

        disabilitaModuli(false);

        if (tipologia.equals("FERIE") || tipologia.equals("MALATTIA")){
            oraInizioField.setDisable(true);
            oraFineField.setDisable(true);

        } else if(tipologia.equals("PERMESSO") || tipologia.equals("SCIOPERO") ){
            siRBtn.setDisable(true);
            siRBtn.setSelected(true);

            noRBtn.setDisable(true);
            noRBtn.setSelected(false);

            fineDatePicker.setDisable(true);

        } else if(tipologia.equals("CONGEDO_PARENTALE")){
            siRBtn.setDisable(true);
            siRBtn.setSelected(false);

            noRBtn.setDisable(true);
            noRBtn.setSelected(true);

            oraInizioField.setDisable(true);
            oraFineField.setDisable(true);

        } else {
            System.out.println("errore");
        }
        giornataSingola();
    }

    @FXML
    private void giornataSingola(){

        if(siRBtn.isSelected()){
            inizioDatePicker.setValue(null);
            fineDatePicker.setDisable(true);
            fineDatePicker.setValue(null);
        } else if(noRBtn.isSelected()){
            fineDatePicker.setDisable(false);

        } else {
            System.out.println("errore");
        }
        abilitaFirma();
    }

    @FXML
    private void abilitaFirma(){
        if(comboBox.getValue() == null ||
                inizioDatePicker.getValue() == null ||
                (fineDatePicker.getValue() == null && !fineDatePicker.isDisable()) ||
                (oraInizioField.getText().isEmpty() && !oraInizioField.isDisable()) ||
                (oraFineField.getText().isEmpty() && !oraFineField.isDisable()) ||
                motivazioneField.getText().isEmpty()){

            inviaBtn.setDisable(true);
        } else {
            inviaBtn.setDisable(false);
        }
    }

    @FXML
    private void inviaRichiesta(){
        /*FARE IN MODO CHE IL PULSANTE INVIA NON SIA CLICCABILE FINCHE' TUTTI I CAMPI NON SONO RIEMPITI*/
        /*PRELEVA TUTTE LE INFORMAZIONI DAL FORM*/

        boolean giornataSingola = false;
        if(siRBtn.isSelected()){
            giornataSingola = true;
        } else if(noRBtn.isSelected()){
            giornataSingola = false;
        } else {
            System.out.println("Errore");
        }

        AstensioneCtrl.inviaRichiesta(Richiesta.getTipo(comboBox.getValue()), giornataSingola, inizioDatePicker.getValue(), fineDatePicker.getValue(), oraInizioField.getText(), oraFineField.getText(), motivazioneField.getText());
    }

    @FXML
    private void aggiornaTextFieldOrario(){
        String tipologia = comboBox.getValue();
        if(!tipologia.equals("FERIE") && !tipologia.equals("SCIOPERO") && !tipologia.equals("MALATTIA")){
            if(siRBtn.isSelected()){
                oraInizioField.setDisable(false);
                oraFineField.setDisable(false);
            } else {
                oraInizioField.setDisable(true);
                oraFineField.setDisable(true);

                oraInizioField.clear();
                oraFineField.clear();
            }
        } else {
            /*Non aggiornare*/
        }
    }

    @FXML
    private void indietro() throws IOException {
        AstensioneCtrl.mostraComunRicevWin();
    }

}
