package com.example.softwareamm.gestionecomunicazioni;

import com.example.softwareamm.controls.ComunicazioneCtrl;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**Interfaccia della finestra CDWin
 * @version 1.0
 * @see ComunicazioneCtrl ComunicazioneCtrl
 * */
public class CDWinInterfaccia {

    @FXML
    private Label tipologiaLabel;

    @FXML
    private Label oggettoLabel;

    @FXML
    private Label contenutoCDLabel;

    /**Consente di caricare le informazioni all'interno della finestra
     * @param tipologia Tipologia
     * @param oggetto Titolo
     * @param contenutoCD Contenuto*/
    public void inizializza(String tipologia, String oggetto, String contenutoCD){
        this.tipologiaLabel.setText(tipologia);
        this.oggettoLabel.setText(oggetto);
        this.contenutoCDLabel.setText(contenutoCD);
    }
}
