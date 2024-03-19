package com.example.softwareamm.gestioneturni;

import com.example.softwareamm.controls.GestioneDipendentiCtrl;
import com.example.softwareamm.controls.TurnazioneCtrl;
import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

/**Interfaccia della finestra TurniWin
 * @version 1.0
 * @see TurnazioneCtrl TurnazioneCtrl
 * */
public class TurniWinInterfaccia {

    @FXML
    private BorderPane borderPaneCalendar;

    @FXML
    private void initialize(){
        borderPaneCalendar.setCenter(TurnazioneCtrl.aquisisciTurni().getCalendar());
    }

    @FXML
    private void indietro() throws IOException {
        GestioneDipendentiCtrl.mostraVisDipendenteWin();
    }
}
