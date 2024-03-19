package com.example.softwareamm.gestioneaccount;

import com.example.softwareamm.controls.AccountCtrl;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.io.IOException;

/**Interfaccia della finestra NuovaPswWin
 * @version 1.0
 * @see AccountCtrl AccountCtrl
 * */
public class NuovaPswWinInterfaccia {

    @FXML
    private TextField nuovaPswTextField;

    @FXML
    private TextField confermaPswTextField;

    @FXML
    private void confermaPsw() throws IOException {
        AccountCtrl.verificaNuovaPsw(nuovaPswTextField.getText(), confermaPswTextField.getText());
    }

    @FXML
    private void annulla() throws IOException {
        AccountCtrl.logOut();
    }
}
