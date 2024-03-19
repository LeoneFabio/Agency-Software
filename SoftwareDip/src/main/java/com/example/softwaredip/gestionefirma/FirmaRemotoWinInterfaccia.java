package com.example.softwaredip.gestionefirma;

import com.example.softwaredip.controls.FirmaRemotoCtrl;
import com.example.softwaredip.entity.Utente;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.util.ArrayList;

/**Interfaccia della finestra FirmaRemotoWin
 * @version 1.0
 * @see FirmaRemotoCtrl FirmaRemotoCtrl
 * */
public class FirmaRemotoWinInterfaccia {

    @FXML
    private TextField nomeTextField;

    @FXML
    private TextField cognomeTextField;

    @FXML
    private TextField matricolaTextField;

    @FXML
    private TextArea motivazioneTextArea;

    @FXML
    private Button firmaButton;

    @FXML
    private void initialize(){
        firmaButton.setDisable(true);
        prelevaInfoDipendente();
    }

    private void prelevaInfoDipendente(){

         ArrayList<String> info = FirmaRemotoCtrl.prelevaInfoDipendete();

        nomeTextField.setText(info.get(0));
        cognomeTextField.setText(info.get(1));
        matricolaTextField.setText( String.format("%04d", Utente.utenteLoggato.getMatricola()));

        nomeTextField.setDisable(true);
        cognomeTextField.setDisable(true);
        matricolaTextField.setDisable(true);
    }

    @FXML
    private void abilitaMotivazione(){
        if(!motivazioneTextArea.getText().isEmpty()){
            firmaButton.setDisable(false);
        } else {
            firmaButton.setDisable(true);
        }
    }

    @FXML
    private void inviaFirma(){
        FirmaRemotoCtrl.effettuaFirmaIngresso(motivazioneTextArea.getText());
        motivazioneTextArea.clear();
        abilitaMotivazione();

    }
}
