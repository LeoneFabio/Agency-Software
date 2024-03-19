package com.example.softwareamm.gestioneaccount;

import com.example.softwareamm.controls.AccountCtrl;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;

import java.io.IOException;

/**Interfaccia della finestra VecchiaPswWin
 * @version 1.0
 * @see AccountCtrl AccountCtrl
 * */
public class VecchiaPswWinInterfaccia {

    @FXML
    private PasswordField passwordField;

    @FXML
    private void verificaPassword() throws IOException {
        AccountCtrl.verificaPassword(passwordField.getText());
    }

    @FXML
    private void indietro() throws IOException {
        AccountCtrl.mostraHomeWin();
    }

}
