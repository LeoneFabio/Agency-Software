package com.example.softwaredip.gestioneaccount;

import com.example.softwaredip.commons.Utils;
import com.example.softwaredip.controls.AccountCtrl;
import com.example.softwaredip.entity.Utente;
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
    private Label oreLavorativeLabel;
    @FXML
    private Label ruoloLabel;
    @FXML
    private Label agevolazioniLabel;
    @FXML
    private Label dataAssunzioneLabel;
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

        /*ACQUISIAMO LE INFORMAZIONI E LE MOSTRIAMO*/

        ResultSet rs = AccountCtrl.acquisisciInfo();

        try{

            nomeCompletoLabel.setText(rs.getString("nome") + " " + rs.getString("cognome"));
            matricolaLabel.setText(String.format("%04d", Utente.utenteLoggato.getMatricola()));
            CFLabel.setText(rs.getString("cod_fiscale").toUpperCase());
            oreLavorativeLabel.setText(String.valueOf(AccountCtrl.prelevaOreEffettuate()));
            ruoloLabel.setText(rs.getString("ref_ruolo"));
            precopilaAgevolazioni();
            dataAssunzioneLabel.setText(Utils.formattaData(rs.getDate("data_assunzione").toLocalDate()));
            emailLabel.setText(rs.getString("email"));
            indirizzoLabel.setText(rs.getString("indirizzo"));
            cellulareLabel.setText(rs.getString("cellulare"));
            noteLabel.setText(rs.getString("note").isEmpty() ? "Nessuna" : rs.getString("note"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void precopilaAgevolazioni() {
        int[] arr = AccountCtrl.prelevaAgevolazioni();
        StringBuilder strBr = new StringBuilder();
        for (int i=0; i<arr.length; i++) {
            switch (arr[i]) {
                case 1 -> {
                    strBr.append("Nessuna");
                }
                case 4 -> {
                    strBr.append("Più di due figli a carico");
                }
                case 3 -> {
                    strBr.append("Disabilità");
                }
            }

            if (i < arr.length-1) {
                strBr.append(", ");
            }

            agevolazioniLabel.setText(strBr.toString());
        }
    }
}
