package com.example.softwareamm.gestioneaccount;

import com.example.softwareamm.controls.AccountCtrl;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.io.IOException;

/**Interfaccia della finestra RecPswWin
 * @version 1.0
 * @see AccountCtrl AccountCtrl
 * */
public class RecPswWinInterfaccia {

    @FXML
    private TextField matricolaTextField;

    @FXML
    private void verificaMatricola() throws IOException {
        AccountCtrl.verificaMatricola(matricolaTextField.getText());
    }

    @FXML
    private void annulla() throws IOException {
        AccountCtrl.mostraAccessoWin();
    }
}
