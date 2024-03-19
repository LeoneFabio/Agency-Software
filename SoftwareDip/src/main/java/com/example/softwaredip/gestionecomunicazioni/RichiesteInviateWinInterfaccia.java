package com.example.softwaredip.gestionecomunicazioni;

import com.example.softwaredip.commons.Richiesta;
import com.example.softwaredip.controls.ComunicazioneCtrl;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.text.Text;

import java.io.IOException;

/**Interfaccia della finestra RichiesteInviateWin
 * @version 1.0
 * @see ComunicazioneCtrl ComunicazioneCtrl
 * */
public class RichiesteInviateWinInterfaccia {

    @FXML
    private TableView<Richiesta> inviateTableView;

    @FXML
    private TableColumn<Richiesta, String> tipoColonna;

    @FXML
    private TableColumn<Richiesta, String> dateColonna;

    @FXML
    private TableColumn<Richiesta, String> motivazioneColonna;

    @FXML
    private TableColumn<Richiesta, String> statoColonna;

    @FXML
    private void initialize(){

        //Impostiamo la grandezza del font
        inviateTableView.getStyleClass().add("testo");

        //Disabilitiamo la selezione delle righe
        inviateTableView.getSelectionModel().selectedIndexProperty()
                .addListener((observable, oldvalue, newValue) -> {
                    Platform.runLater(() -> {
                        inviateTableView.getSelectionModel().clearSelection();
                    });
                });

        //Disabilitiamo il riordinamento manuale delle colonne
        inviateTableView.getColumns().forEach(e -> e.setReorderable(false));

        //Implementiamo la possibilità di auto-ridimensionamento dell'altezza di una riga quando la descrizione e molto lunga
        motivazioneColonna.setCellFactory(param -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    setText(null);
                    setStyle("");
                } else {
                    Text text = new Text(item);
                    text.setStyle("-fx-text-alignment:justify;");
                    text.wrappingWidthProperty().bind(getTableColumn().widthProperty().subtract(35));
                    setGraphic(text);
                }
            }
        });

        tipoColonna.setCellValueFactory(cellData -> cellData.getValue().tipoProperty());
        dateColonna.setCellValueFactory(cellData -> cellData.getValue().periodoProperty());
        motivazioneColonna.setCellValueFactory(cellData -> cellData.getValue().motivazioneProperty());
        statoColonna.setCellValueFactory(cellData -> cellData.getValue().statoProperty());

        creaLista();
    }

    private void creaLista(){
        inviateTableView.setItems(ComunicazioneCtrl.prelevalistaRichieste());
    }

    @FXML
    private void indietro() throws IOException {
        ComunicazioneCtrl.mostraHomeWin();
    }

}
