package com.example.softwareamm.gestioneaccount;

import com.example.softwareamm.controls.AccountCtrl;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**Interfaccia della finestra ModificaAccWin
 * @version 1.0
 * @see AccountCtrl AccountCtrl
 * */
public class ModificaAccWinInterfaccia {

    @FXML
    private TextField emailTextField;

    @FXML
    private TextField indirizzoTextField;

    @FXML
    private TextField cellulareTextField;

    @FXML
    private void initialize(){
        ResultSet rs = AccountCtrl.acquisisciInfo();

        try{
            emailTextField.setText(rs.getString("email"));
            indirizzoTextField.setText(rs.getString("indirizzo"));
            cellulareTextField.setText(rs.getString("cellulare"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @FXML
    private void salvaInfo(){
        AccountCtrl.salvaInformazioniDipendente(emailTextField.getText(), indirizzoTextField.getText(), cellulareTextField.getText());
        initialize();
    }

    @FXML
    private void modificaPsw() throws IOException {
        AccountCtrl.mostraVecchiaPswWin();
    }

}
