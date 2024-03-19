package com.example.softwareamm.gestioneturni;

import com.example.softwareamm.commons.Calendario;
import com.example.softwareamm.commons.TurnoDipendente;
import com.example.softwareamm.controls.TurnazioneCtrl;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.time.LocalDate;

/**Interfaccia della finestra RegistroWin
 * @version 1.0
 * @see TurnazioneCtrl TurnazioneCtrl
 * */
public class RegistroWinInterfaccia {

    @FXML
    private Label dataLabel;

    @FXML
    private TableView<TurnoDipendente> registroTableView;

    @FXML
    private TableColumn<TurnoDipendente, String> matricolaCol;

    @FXML
    private TableColumn<TurnoDipendente, String> nomeCol;

    @FXML
    private TableColumn<TurnoDipendente, String> cognomeCol;

    @FXML
    private TableColumn<TurnoDipendente, String> ingressoCol;

    @FXML
    private TableColumn<TurnoDipendente, String> uscitaCol;

    @FXML
    private void initialize(){
        //Impostiamo la grandezza del font
        registroTableView.getStyleClass().add("testo");

        //Disabilitiamo la selezione delle righe
        registroTableView.getSelectionModel().selectedIndexProperty()
                .addListener((observable, oldvalue, newValue) -> {
                    Platform.runLater(() -> {
                        registroTableView.getSelectionModel().clearSelection();
                    });
                });

        //Disabilitiamo il riordinamento manuale delle colonne
        registroTableView.getColumns().forEach(e -> e.setReorderable(false));

        matricolaCol.setCellValueFactory(cellData -> cellData.getValue().matricolaProperty());
        nomeCol.setCellValueFactory(cellData -> cellData.getValue().nomeProperty());
        cognomeCol.setCellValueFactory(cellData -> cellData.getValue().cognomeProperty());
        ingressoCol.setCellValueFactory(cellData -> cellData.getValue().ingressoProperty());
        uscitaCol.setCellValueFactory(cellData -> cellData.getValue().uscitaProperty());
    }

    /**Preleva il registro della data fornita e lo inserisce nella listView
     * @param data Data del registro che si vuole prelevare
     * */
    public void prelevaRegistro(LocalDate data){
        dataLabel.setText(data.getDayOfMonth() + " " + Calendario.italianMonth[data.getMonthValue()-1] + " " + data.getYear()) ;
        registroTableView.setItems(TurnazioneCtrl.prelevaTurniRegistro(data));
    }
}
