package com.example.softwareamm.gestioneturni;

import com.example.softwareamm.Main;
import com.example.softwareamm.commons.Calendario;
import com.example.softwareamm.controls.TurnazioneCtrl;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Objects;

/**Interfaccia della finestra CalendarioAmmWin
 * @version 1.0
 * @see TurnazioneCtrl TurnazioneCtrl
 * */
public class CalendarioAmmWinInterfaccia {

    private LocalDate tempDate;

    @FXML
    private Label meseLabel;

    @FXML
    private ListView<String> calendarioList;

    @FXML
    private BorderPane borderPane;

    @FXML
    private void initialize(){
        tempDate = LocalDate.now();

        calendarioList.getStyleClass().add("testo");
        calendarioList.getSelectionModel().selectedItemProperty().addListener(this::selectionChanged);

        Label nessunElemento = new Label("Nessun elemento selezionato");
        nessunElemento.setStyle("-fx-font-size: 15px");

        borderPane.setCenter(nessunElemento);

        creaCalendario(tempDate);

    }

    private void selectionChanged(ObservableValue<? extends String> observable, String oldValue, String newValue){
        ObservableList<String> selectedItems = calendarioList.getSelectionModel().getSelectedItems();

        if (!selectedItems.isEmpty()){
            int dayValue = Integer.parseInt(selectedItems.get(0).substring(0, 2).trim());

            borderPane.setCenter(TurnazioneCtrl.mostraRegistroWin(LocalDate.of(tempDate.getYear(), tempDate.getMonthValue(), dayValue)));
        }
    }

    private void creaCalendario(LocalDate data){
        calendarioList.getItems().clear();

        int dayOfMouth = data.getDayOfMonth();
        int mouthLenght = data.lengthOfMonth();
        LocalDate dateIterator = data.minusDays(dayOfMouth-1);

        meseLabel.setText(Calendario.italianMonth[data.getMonthValue()-1] + " " + data.getYear());

        String[] giorniMese = new String[mouthLenght];

        for(int i = 0; i < giorniMese.length; i++){
            giorniMese[i] = dateIterator.getDayOfMonth() + " - " + Calendario.italianDays[dateIterator.getDayOfWeek().getValue()-1];
            dateIterator = dateIterator.plusDays(1);
        }

        calendarioList.getItems().addAll(giorniMese);
    }

    public void cambiaDataVisualizzata(LocalDate data){
        tempDate = data;
        creaCalendario(data);
    }

    @FXML
    private void nextMonth(){
        tempDate = tempDate.plusMonths(1);
        creaCalendario(tempDate);
    }

    @FXML
    private void prevMonth(){
        tempDate = tempDate.minusMonths(1);
        creaCalendario(tempDate);
    }
}
