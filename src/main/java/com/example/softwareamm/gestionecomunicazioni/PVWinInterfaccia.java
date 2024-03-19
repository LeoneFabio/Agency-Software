package com.example.softwareamm.gestionecomunicazioni;

import com.example.softwareamm.Main;
import com.example.softwareamm.commons.Utils;
import com.example.softwareamm.controls.ComunicazioneCtrl;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**Interfaccia della finestra PVWin
 * @version 1.0
 * @see ComunicazioneCtrl ComunicazioneCtrl
 * */
public class PVWinInterfaccia {

    private int id;

    @FXML
    private Label tipologiaLabel;
    @FXML
    private Label oggettolabel;
    @FXML
    private Label contenutoLabel;
    @FXML
    private Hyperlink linkCalendario;
    @FXML
    private Label statoPVLabel;
    @FXML
    private Button accettaBtn;
    @FXML
    private Button rifiutaBtn;
    @FXML
    private VBox propostaBox;
    @FXML
    private VBox headerBox;

    /**Consente di caricare le informazioni nella finestra
     * @param tipoComunicazione Tipologia
     * @param tipoPV Oggetto
     * @param messaggio Contenuto
     * @param dataInizio Data e ora del turno del dipendente
     * @param dataFine Data e ora del turno proposto
     * @param stato Indica se si è rifiutata o accettata la proposta variazione*/
    public void inizializzaPV(int idComunicazione , boolean isTurnazione, String tipoComunicazione, int tipoPV, String messaggio, LocalDateTime dataInizio, LocalDateTime dataFine, int stato, String nome, String cognome, int matricola, int ruolo){

        this.id = idComunicazione;

        this.tipologiaLabel.setText(tipoComunicazione);
        this.oggettolabel.setText(selezionaTipo(tipoPV));
        this.contenutoLabel.setText(messaggio);

        String oggetto;

        if(isTurnazione){

            oggetto = "Descrizione: \n" + messaggio;

        } else {

            String oraInizioStr = dataInizio.getHour() != 0 ? "  -  " + String.format("%02d", dataInizio.getHour()) + ":" + String.format("%02d", dataInizio.getMinute()) : "";

            String oraFineStr = dataFine.getHour() != 0 ? "  -  " + String.format("%02d", dataFine.getHour()) + ":" + String.format("%02d", dataFine.getMinute()) : "";

            oggetto = "Nome: " + nome + "\n" +
                    "Cognome: " + cognome +"\n" +
                    "Matricola: " + String.format("%04d", matricola) + "\n" +
                    "Ruolo: " + ruolo + "\n" +
                    "Da: " + Utils.formattaData(dataInizio.toLocalDate()) + oraInizioStr + "\n" +
                    (dataInizio.equals(dataFine) ? "" : "A: " + Utils.formattaData(dataFine.toLocalDate()) + oraFineStr + "\n") + "\n" +
                    "Motivazione: \n" + messaggio;

            propostaBox.setVisible(false);
            propostaBox.setMinHeight(0);
            propostaBox.setPrefHeight(0);
            propostaBox.setMaxHeight(0);
        }

        contenutoLabel.setText(oggetto);

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

    private String selezionaTipo(int tipo){
        switch (tipo){
            case 2 ->{
                return "Malattia";
            }

            case 3 ->{
                return "Congedo parentale";
            }

            case 4 ->{
                return "Sciopero";
            }

            case 6 ->{
                return "Ferie";
            }

            case 7 ->{
                return "Permesso";
            }

            case 8 ->{
                return "Turnazione";
            }

            default -> {
                return "Tipo non valido";
            }

        }
    }

    @FXML
    private void mostraPropostaTurnazione() throws IOException {
        //INSERIRE LA GIUSTA FINESTRA
        Utils.mostraScena(Main.mainStage, "Proposta", "gestionecomunicazioni/PropostaTurnazioneWin.fxml");
    }

    @FXML
    private void rifiutaPV(){
        ComunicazioneCtrl.rifiutaPV(id);
        this.accettaBtn.setDisable(true);
        this.accettaBtn.setVisible(false);
        this.rifiutaBtn.setDisable(true);
        this.rifiutaBtn.setVisible(false);

        this.statoPVLabel.setVisible(true);
        this.statoPVLabel.setStyle("-fx-text-fill: #e30e0e");
        this.statoPVLabel.setText("RIFIUTATA");
        System.out.println("RIFIUTATA");
    }

    @FXML
    private void accettaPV(){
        ComunicazioneCtrl.accettaPV(id);
        this.accettaBtn.setDisable(true);
        this.accettaBtn.setVisible(false);
        this.rifiutaBtn.setDisable(true);
        this.rifiutaBtn.setVisible(false);

        this.statoPVLabel.setVisible(true);
        this.statoPVLabel.setStyle("-fx-text-fill: #32cf4e");
        this.statoPVLabel.setText("ACCETTATA");
        System.out.println("ACCETTATA");
    }

}
