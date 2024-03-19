package com.example.softwareamm.gestionedipendenti;

import com.example.softwareamm.controls.GestioneDipendentiCtrl;
import com.example.softwareamm.entity.Dipendente;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.io.IOException;

/**Interfaccia della finestra VisDipendenteWin
 * @version 1.0
 * @see GestioneDipendentiCtrl GestioneDipendentiCtrl
 * */
public class VisDipendenteWinInterfaccia {

    @FXML
    private Label nomeCompletoLabel;

    @FXML
    private Label matricolaLabel;

    @FXML
    private void initialize(){
        nomeCompletoLabel.setText(Dipendente.dipendenteSelezionato.getNome() + " " + Dipendente.dipendenteSelezionato.getCognome());
        matricolaLabel.setText(String.format("%04d", Dipendente.dipendenteSelezionato.getMatricola()));
    }

    @FXML
    private void infoDipendente() throws IOException {
        GestioneDipendentiCtrl.mostraInfoDipWin();
    }

    @FXML
    private void stipendioDipendente() throws IOException {
        GestioneDipendentiCtrl.mostraStipendioWin();
    }

    @FXML
    private void turniDipendente() throws IOException {
        GestioneDipendentiCtrl.mostraTurniWin();
    }

    @FXML
    private void indietro() throws IOException {
        GestioneDipendentiCtrl.mostraListaDipWin();
    }
}
