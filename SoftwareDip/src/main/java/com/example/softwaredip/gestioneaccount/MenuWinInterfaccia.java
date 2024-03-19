package com.example.softwaredip.gestioneaccount;

import com.example.softwaredip.Main;
import com.example.softwaredip.controls.AccountCtrl;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**Interfaccia della finestra MenuWin
 * @version 1.0
 * @see AccountCtrl AccountCtrl
 * */
public class MenuWinInterfaccia {

    @FXML
    private Label nomeLabel;

    @FXML
    private Label ruoloLabel;

    @FXML
    private Button homeBtn;

    @FXML
    private Button stipendioBtn;

    @FXML
    private Button turniBtn;

    @FXML
    private Button firmaBtn;

    @FXML
    private Button comunicazioniBtn;

    @FXML
    private Button moficaAccountBtn;

    /**Piano del menu, ottenibile per poter cambiare le finestre di visualizzazione*/
    @FXML
    public BorderPane borderPaneSwitch;

    @FXML
    private void initialize(){
        seleziona(0);

        ResultSet rs = AccountCtrl.acquisisciInfo();

        try {
            nomeLabel.setText(rs.getString("Nome") + " " + rs.getString("Cognome"));
            ruoloLabel.setText("Ruolo " + rs.getString("ref_ruolo"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void apriHome() throws IOException {
        seleziona(0);
        AccountCtrl.apriPaginaMenu(Main.mainStage, borderPaneSwitch, "Home", "gestioneaccount/HomeWin.fxml");
    }

    @FXML
    private void apriStipendio() throws IOException {
        seleziona(1);
        AccountCtrl.apriPaginaMenu(Main.mainStage, borderPaneSwitch, "Situazione stipendiale", "gestionestipendi/StipendioWin.fxml");
    }

    @FXML
    private void apriTurni() throws IOException {
        seleziona(2);
        AccountCtrl.apriPaginaMenu(Main.mainStage, borderPaneSwitch, "Turni", "gestioneturni/TurniWin.fxml");
    }

    @FXML
    private void apriFirma() throws IOException {
        seleziona(3);
        AccountCtrl.apriPaginaMenu(Main.mainStage, borderPaneSwitch, "Firma da remoto", "gestionefirma/FirmaRemotoWin.fxml");
    }

    @FXML
    private void apriComunicazioni() throws IOException {
        seleziona(4);
        AccountCtrl.apriPaginaMenu(Main.mainStage, borderPaneSwitch, "Comunicazioni", "gestionecomunicazioni/ComunRicevWin.fxml");
    }

    @FXML
    private void apriModificaAccount() throws IOException {
        seleziona(5);
        AccountCtrl.apriPaginaMenu(Main.mainStage, borderPaneSwitch, "Modifica Account", "gestioneaccount/ModificaAccWin.fxml");
    }

    @FXML
    private void logOut() throws IOException {
        AccountCtrl.logOut();
    }

    public void seleziona(int numeroPulsante){

        homeBtn.getStyleClass().remove("menuBtnSelected");
        stipendioBtn.getStyleClass().remove("menuBtnSelected");
        turniBtn.getStyleClass().remove("menuBtnSelected");
        firmaBtn.getStyleClass().remove("menuBtnSelected");
        comunicazioniBtn.getStyleClass().remove("menuBtnSelected");
        moficaAccountBtn.getStyleClass().remove("menuBtnSelected");

        switch (numeroPulsante) {
            case 0 -> {
                homeBtn.getStyleClass().add("menuBtnSelected");
            }
            case 1 -> {
                stipendioBtn.getStyleClass().add("menuBtnSelected");
            }
            case 2 -> {
                turniBtn.getStyleClass().add("menuBtnSelected");
            }
            case 3 -> {
                firmaBtn.getStyleClass().add("menuBtnSelected");
            }
            case 4 -> {
                comunicazioniBtn.getStyleClass().add("menuBtnSelected");
            }
            case 5 -> {
                moficaAccountBtn.getStyleClass().add("menuBtnSelected");
            }
            default -> {
                System.out.println("errore");
            }
        }
    }


}
