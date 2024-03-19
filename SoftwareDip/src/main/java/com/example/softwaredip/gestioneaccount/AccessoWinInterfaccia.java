package com.example.softwaredip.gestioneaccount;

import com.example.softwaredip.controls.AccountCtrl;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;

/**Interfaccia della finestra AccessoWin
 * @version 1.0
 * @see AccountCtrl AccountCtrl
 * */

public class AccessoWinInterfaccia {

    @FXML
    private TextField matricolaTextField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private void accedi(){
        AccountCtrl.verificaCredenziali(matricolaTextField.getText(), passwordField.getText());
    }

    @FXML
    private void apriRecuperaPassword() throws IOException {
        AccountCtrl.mostraRecuperaPsw();
    }
}
