package com.example.softwaredip.gestionecomunicazioni;

import com.example.softwaredip.commons.DBMSBound;
import com.example.softwaredip.commons.Utils;
import com.example.softwaredip.controls.ComunicazioneCtrl;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**Interfaccia della finestra PVWin
 * @version 1.0
 * @see ComunicazioneCtrl ComunicazioneCtrl
 * */
public class PVWinInterfaccia {

    private int id;

    private boolean isStraordinari;

    //INFO ACCETTA

    private LocalDate dataInizioPV;
    private LocalDate dataFinePV;
    private int oraInizioPV;
    private int oraFinePV;
    private int ruolo;
    private int oraInizioTurno;
    private int oraFineTurno;
    private int mittente;
    private int tipoPV;

    @FXML
    private HBox hBox;

    @FXML
    private Label tipologiaLabel;
    @FXML
    private Label oggettolabel;

    @FXML
    private Label contenutoLabel;

    @FXML
    private Label dataTurnoLabel;

    @FXML
    private Label entrataTurnoLabel;

    @FXML
    private Label uscitaTurnoLabel;

    @FXML
    private Label dataCambioLabel;

    @FXML
    private Label entrataCambioLabel;

    @FXML
    private Label uscitaCambioLabel;

    @FXML
    private Label statoPVLabel;

    @FXML
    private Button accettaBtn;

    @FXML
    private Button rifiutaBtn;
    public void inizializzaPV(int id, int tipoPV , String messaggio, LocalDate dataInizioPV, LocalDate dataFinePV, int oraInizioPV, int oraFinePV, int stato, int ruolo, int oraInizioTurno, int oraFineTurno, int mittente){
        this.id = id;

        //INFO ACCETTA
        this.dataInizioPV = dataInizioPV;
        this.dataFinePV = dataFinePV;
        this.oraInizioPV = oraInizioPV;
        this.oraFinePV = oraFinePV;
        this.ruolo = ruolo;
        this.oraInizioTurno = oraInizioTurno;
        this.oraFineTurno = oraFineTurno;
        this.mittente = mittente;
        this.tipoPV = tipoPV;

        tipologiaLabel.setText("PV");

        contenutoLabel.setText(messaggio);

        int oraFineProposta = oraFinePV;
        if(oraFinePV < oraInizioPV){
            oraFineProposta += 24;
        }

        System.out.println((tipoPV == 7 || tipoPV == 4));
        System.out.println((oraFinePV - oraInizioPV < 4));
        System.out.println((oraFineProposta - oraInizioPV > 0));
        System.out.println((oraFinePV <= oraInizioTurno || oraInizioPV >= oraFineTurno));

        //DA RIVEDERE
        if(((tipoPV == 7 || tipoPV == 4) && (oraFineProposta - oraInizioPV < 4) && (oraFineProposta - oraInizioPV > 0) && (oraFinePV <= oraInizioTurno || oraInizioPV >= oraFineTurno))){

            isStraordinari = true;

            hBox.getChildren().remove(1);
            hBox.getChildren().remove(1);

            oggettolabel.setText("Straordinari");

            dataTurnoLabel.setText(Utils.formattaData(dataInizioPV));
            entrataTurnoLabel.setText(String.format("I: %d:00", oraInizioPV));
            uscitaTurnoLabel.setText(String.format("U: %d:00", oraFinePV));

        }else{

            isStraordinari = false;

            oggettolabel.setText("Cambio turno");

            dataTurnoLabel.setText(Utils.formattaData(dataInizioPV));
            entrataTurnoLabel.setText(String.format("I: %d:00", oraInizioTurno));
            uscitaTurnoLabel.setText(String.format("U: %d:00", oraFineTurno));

            dataCambioLabel.setText(Utils.formattaData(dataInizioPV));
            entrataCambioLabel.setText(String.format("I: %d:00", oraInizioPV));
            uscitaCambioLabel.setText(String.format("U: %d:00", oraFinePV));
        }

        switch(stato){
            case 1 -> {
                this.accettaBtn.setDisable(true);
                this.accettaBtn.setVisible(false);
                this.rifiutaBtn.setDisable(true);
                this.rifiutaBtn.setVisible(false);

                this.statoPVLabel.setVisible(true);
                this.statoPVLabel.setStyle("-fx-text-fill: #32cf4e");
                this.statoPVLabel.setText("ACCETTATA");
            }

            case 2 -> {
                this.accettaBtn.setDisable(true);
                this.accettaBtn.setVisible(false);
                this.rifiutaBtn.setDisable(true);
                this.rifiutaBtn.setVisible(false);

                this.statoPVLabel.setVisible(true);
                this.statoPVLabel.setStyle("-fx-text-fill: #e30e0e");
                this.statoPVLabel.setText("RIFIUTATA");
            }

            case 3 -> {
                statoPVLabel.setVisible(false);
            }
        }

    }

    @FXML
    private void rifiutaPV(){
        System.out.println("RIFIUTATA");
        ComunicazioneCtrl.rifiutaPV(id);
        this.accettaBtn.setDisable(true);
        this.accettaBtn.setVisible(false);
        this.rifiutaBtn.setDisable(true);
        this.rifiutaBtn.setVisible(false);

        this.statoPVLabel.setVisible(true);
        this.statoPVLabel.setStyle("-fx-text-fill: #e30e0e");
        this.statoPVLabel.setText("RIFIUTATA");
    }

    @FXML
    private void accettaPV(){
        System.out.println("ACCETTATA");
        ComunicazioneCtrl.accettaPV(id, isStraordinari, this.dataInizioPV, this.dataFinePV, this.oraInizioPV, this.oraFinePV, this.oraInizioTurno, this.oraFineTurno, this.ruolo, this.mittente, this.tipoPV);
        this.accettaBtn.setDisable(true);
        this.accettaBtn.setVisible(false);
        this.rifiutaBtn.setDisable(true);
        this.rifiutaBtn.setVisible(false);

        this.statoPVLabel.setVisible(true);
        this.statoPVLabel.setStyle("-fx-text-fill: #32cf4e");
        this.statoPVLabel.setText("ACCETTATA");
    }

}
