package com.example.softwaredip.gestioneturni;

import com.example.softwaredip.controls.TurnazioneCtrl;
import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;

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
}
