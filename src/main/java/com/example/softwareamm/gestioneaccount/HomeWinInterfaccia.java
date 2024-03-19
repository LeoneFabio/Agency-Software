package com.example.softwareamm.gestioneaccount;

import com.example.softwareamm.controls.AccountCtrl;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.sql.ResultSet;
import java.sql.SQLException;

/**Interfaccia della finestra HomeWin
 * @version 1.0
 * @see AccountCtrl AccountCtrl
 * */

public class HomeWinInterfaccia {
    @FXML
    private Label nomeCompletoLabel;
    @FXML
    private Label matricolaLabel;
    @FXML
    private Label CFLabel;
    @FXML
    private Label ruoloLabel;
    @FXML
    private Label emailLabel;
    @FXML
    private Label indirizzoLabel;
    @FXML
    private Label cellulareLabel;
    @FXML
    private Label noteLabel;

    @FXML
    private void initialize(){

        ResultSet rs = AccountCtrl.acquisisciInfo();

        // ESEMPIO

        try{

            nomeCompletoLabel.setText(rs.getString("Nome") + " " + rs.getString("Cognome"));
            matricolaLabel.setText( String.format("%04d", rs.getInt("Matricola")));
            CFLabel.setText(rs.getString("Cod_fiscale").toUpperCase());
            ruoloLabel.setText("Amministratore");
            emailLabel.setText(rs.getString("Email"));
            indirizzoLabel.setText(rs.getString("Indirizzo"));
            cellulareLabel.setText(rs.getString("Cellulare"));
            noteLabel.setText("Nessuna");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // ESEMPIO
    }
}
