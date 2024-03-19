package com.example.softwareamm.gestionedipendenti;

import com.example.softwareamm.commons.Utils;
import com.example.softwareamm.controls.GestioneDipendentiCtrl;
import com.example.softwareamm.entity.Dipendente;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.io.IOException;
import java.util.Arrays;

/**Interfaccia della finestra InfoDipWin
 * @version 1.0
 * @see GestioneDipendentiCtrl GestioneDipendentiCtrl
 * */
public class InfoDipWinInterfaccia {

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
        nomeCompletoLabel.setText(Dipendente.dipendenteSelezionato.getNome() + " " + Dipendente.dipendenteSelezionato.getCognome());
        matricolaLabel.setText(String.format("%04d", Dipendente.dipendenteSelezionato.getMatricola()));
        CFLabel.setText(Dipendente.dipendenteSelezionato.getCF());
        oreLavorativeLabel.setText(Dipendente.dipendenteSelezionato.getOreMensili() + "");
        ruoloLabel.setText(Dipendente.dipendenteSelezionato.getRuolo() + "");
        precopilaAgevolazioni();
        dataAssunzioneLabel.setText(Utils.formattaData(Dipendente.dipendenteSelezionato.getDataAssunzione()));
        emailLabel.setText(Dipendente.dipendenteSelezionato.getEmail());
        indirizzoLabel.setText(Dipendente.dipendenteSelezionato.getIndirizzo());
        cellulareLabel.setText(Dipendente.dipendenteSelezionato.getCellulare());
        noteLabel.setText(Dipendente.dipendenteSelezionato.getNote().isEmpty() ? "Nessuna" : Dipendente.dipendenteSelezionato.getNote());

        // ESEMPIO
    }

    private void precopilaAgevolazioni() {
        int[] arr = Dipendente.dipendenteSelezionato.getAgevolazioni();
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

    @FXML
    private void modificaInfoDip() throws IOException {
        GestioneDipendentiCtrl.mostraModificaInfoDipWin();
    }

    @FXML
    private void indietro() throws IOException {
        GestioneDipendentiCtrl.mostraVisDipendenteWin();
    }

}
