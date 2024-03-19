package com.example.softwaredip.gestioneaccount;

import com.example.softwaredip.controls.AccountCtrl;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.io.IOException;

/**Interfaccia della finestra OTPWin
 * @version 1.0
 * @see AccountCtrl AccountCtrl
 * */
public class OTPWinInterfaccia {

    @FXML
    private TextField OTPTextField;

    @FXML
    private void verificaOTP() {
        AccountCtrl.verificaOTP(OTPTextField.getText());
    }

    @FXML
    private void annulla() throws IOException {
        AccountCtrl.mostraAccessoWin();
    }
}
