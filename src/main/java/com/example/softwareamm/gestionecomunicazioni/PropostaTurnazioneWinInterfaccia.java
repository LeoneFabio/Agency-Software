package com.example.softwareamm.gestionecomunicazioni;

import com.example.softwareamm.Main;
import com.example.softwareamm.commons.DBMSBound;
import com.example.softwareamm.commons.Utils;
import com.example.softwareamm.gestioneturni.CalendarioAmmWinInterfaccia;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.Objects;

public class PropostaTurnazioneWinInterfaccia {

    @FXML
    private BorderPane borderPane;

    @FXML
    private void initialize() {
        try {
            DBMSBound db = new DBMSBound();
            LocalDate dataInizioTurnazione = db.acquisisciDataInizioTurnazione();
            FXMLLoader fxmlLoader = new FXMLLoader(Objects.requireNonNull(Main.class.getResource("gestioneturni/CalendarioAmmWin.fxml")));
            Parent root = fxmlLoader.load();
            CalendarioAmmWinInterfaccia interfaccia = fxmlLoader.getController();
            interfaccia.cambiaDataVisualizzata(LocalDate.of(dataInizioTurnazione.getYear(), dataInizioTurnazione.plusMonths(3).getMonthValue(), 1));
            borderPane.setCenter((Pane)root);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void indietro() throws IOException {
        Utils.mostraScenaMenu(Main.mainStage,"Comunicazione","gestionecomunicazioni/ComunRicevWin.fxml");
    }

}
