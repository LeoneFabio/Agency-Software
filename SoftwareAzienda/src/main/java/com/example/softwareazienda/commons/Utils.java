package com.example.softwareazienda.commons;

import com.example.softwareazienda.Main;
import com.example.softwareazienda.controls.FirmaSedeCtrl;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

import static java.time.temporal.ChronoUnit.DAYS;
import static javafx.scene.control.Alert.AlertType.*;

/**Classe utilitaria
 * @version 1.0
 * @see  FirmaSedeCtrl FirmaSedeCtrl
 * */
public class Utils {

    /**Insieme dei caratteri speciali consentiti nella password*/
    public static final String[] italianMonth = {"Gennaio", "Febbraio", "Marzo", "Aprile", "Maggio", "Giugno", "Luglio", "Agosto", "Settembre", "Ottobre", "Novembre", "Dicembre"};

    public static FXMLLoader mostraScena(Stage stage, String title , String winPath) throws IOException {
        /*CARICHIAMO LA FINESTRA DELLA winPath*/
        FXMLLoader fxmlLoader = new FXMLLoader(Objects.requireNonNull(Main.class.getResource(winPath)));
        Parent root = fxmlLoader.load();

        Pane pane = (Pane)root;
        setStageSize(Main.mainStage, pane.getPrefWidth(), pane.getPrefHeight());

        /*CREIAMO LA SCENA*/
        Scene scene = new Scene(root);

        stage.setTitle(title);
        stage.setScene(scene);
        stage.show();

        messaggioConsole("Finestra: " + title, Tipo_messaggio.NAVIGAZIONE);

        /*RITORNIAMO IL LOADER DA CUI è POSSIBILE PRELEVARE IL CONTROLLER DELLA SCENA APPENA CREATA*/
        return fxmlLoader;
    }

    /**Imposta le dimensioni minime di uno stage e gestisce quelle attuali (se sono inferiori
     * di quelle minime allora aumenta le dimensioni dello stage fino a quelle minime)
     * @param stage Stage in cui verrà mostrata la scena
     * @param paneWidth Larghezza dello stage
     * @param paneHeight Altezza dello stage*/
    public static void setStageSize(Stage stage, double paneWidth, double paneHeight){
        //IMPOSTIAMO LE DIMENSIONI MINIME
        stage.setMinHeight(paneHeight + 40);
        stage.setMinWidth(paneWidth + 40);

        //IMPOSTIAMO LE DIMENSIONI REALI
        if(Double.isNaN(stage.getWidth()) && Double.isNaN(stage.getHeight())){
            stage.setWidth(paneWidth+40);
            stage.setHeight(paneHeight+40);
        } else {
            stage.setWidth(stage.getWidth()<paneWidth ? paneWidth+40 : stage.getWidth());
            stage.setHeight(stage.getHeight()<paneHeight ? paneHeight + 40 : stage.getHeight());
        }
    }

    /**Imposta le dimensioni minime di uno stage
     * @param stage Stage in cui verrà mostrata la scena
     * @param width Larghezza dello stage
     * @param height Altezza dello stage
     * @param title Titolo dello stage*/
    public static void setMinSizeOfStage(Stage stage, double width, double height, String title){
        stage.setMinHeight(height + 40);
        stage.setMinWidth(width + 40);
        stage.setTitle(title);
    }

    private static Alert nuovoPopUp(Alert.AlertType tipo, String specifica, String messaggio){
        Alert alert = new Alert(tipo);
        alert.getDialogPane().getStyleClass().add("testo");

        setAlert(alert);

        alert.setTitle(traduciTipiAllert(tipo));
        alert.setHeaderText(specifica);
        alert.setContentText(messaggio);

        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        Image icon = new Image(Objects.requireNonNull(Main.class.getResourceAsStream("Icone/logo/iconaLogoBlu2.png")));
        stage.getIcons().add(icon);

        return alert;
    }

    /**Crea e mostra un pop-up
     * @param tipo Tipologia (ERRORE, ATTENZIONE, CONFERMA, INFORMAZIONE)
     * @param specifica Titolo del pop-up
     * @param messaggio Contenuto informativo del pop-up
     * @return Se il pop-up è di CONFERMA viene ritornato true se si è confermato, in tutti gli altri casi false*/
    public static boolean mostraPopUp(Alert.AlertType tipo, String specifica, String messaggio){
        Alert alert = nuovoPopUp(tipo, specifica, messaggio);

        boolean conferma = false;

        messaggioConsole("Pop-Up: " + specifica, Tipo_messaggio.WARNING);
        if(tipo == CONFIRMATION){
            Optional<ButtonType> result = alert.showAndWait();
            if(result.get() == ButtonType.OK){
                conferma = true;
            } else if(result.get() == ButtonType.CANCEL){
                conferma = false;
            }
        } else {
            alert.showAndWait();
        }

        return conferma;
    }

    /**Crea e mostra un pop-up di errore comunicazione con il DataBase*/

    public static void mostraPopUpErroreDB(){
        messaggioConsole("Pop-Up: Errore comunicazione con il database", Tipo_messaggio.ERRORE);

        Alert errore = nuovoPopUp(ERROR, "Impossibile comunicare con il database", "Controllare la propria connesione. Se il problema persiste contattare amministratore.");
        DialogPane dialog = errore.getDialogPane();
        StackPane stackPane = new StackPane(new ImageView(new Image(Objects.requireNonNull(Main.class.getResourceAsStream("icone/pop-up/erroreDB.png")))));
        stackPane.setPrefSize(24, 24);
        stackPane.setAlignment(Pos.CENTER);
        dialog.setGraphic(stackPane);

        errore.showAndWait();
    }

    private static void setAlert(Alert alert){

        DialogPane dialog = alert.getDialogPane();
        dialog.getStylesheets().add(Objects.requireNonNull(Main.class.getResource("popUp_styleSheet.css")).toString());
        ButtonBar buttonBar = (ButtonBar)alert.getDialogPane().lookup(".button-bar");
        buttonBar.getButtons().get(0).getStyleClass().add("okConfermaBtn");

        String iconPath = "";

        if(alert.getAlertType() == CONFIRMATION){
            buttonBar.getButtons().get(1).getStyleClass().add("annullaBtn");
            Button okButton = (Button) alert.getDialogPane().lookupButton(ButtonType.OK);
            okButton.setText("Conferma");
            iconPath = "icone/pop-up/interrogativo.png";
        } else if(alert.getAlertType() == ERROR){
            iconPath = "icone/pop-up/errore.png";
        } else if(alert.getAlertType() == INFORMATION){
            iconPath = "icone/pop-up/informativo.png";
        } else if(alert.getAlertType() == WARNING){
            iconPath = "icone/pop-up/attenzione.png";
        }

        //IMPOSTIAMO L'ICONA
        StackPane stackPane = new StackPane(new ImageView(new Image(Objects.requireNonNull(Main.class.getResourceAsStream(iconPath)))));
        stackPane.setPrefSize(24, 24);
        stackPane.setAlignment(Pos.CENTER);
        dialog.setGraphic(stackPane);
    }

    private static String traduciTipiAllert(Alert.AlertType tipo){
        String[] traduzioni = {"Conferma", "Errore", "Informazione", "Attenzione"};
        int temp = 0;
        if(tipo == CONFIRMATION){
            temp = 0;
        } else if(tipo == ERROR){
            temp = 1;
        } else if(tipo == INFORMATION){
            temp = 2;
        } else if(tipo == WARNING){
            temp = 3;
        }
        return traduzioni[temp];
    }

    public static String formattaData(LocalDate data){
        return String.format("%d/%02d/%d", data.getDayOfMonth(), data.getMonthValue(), data.getYear());
    }

    public enum Tipo_messaggio {NAVIGAZIONE, INFO, WARNING, ERRORE, DEBUGGING};

    public static void messaggioConsole(String messaggio, Tipo_messaggio tipo){

        StringBuilder strBr = new StringBuilder();
        strBr.append('\t');
        switch (tipo){
            case NAVIGAZIONE -> strBr.append("\033[1;92m" + "[→]" + "\033[0;97m");

            case INFO -> strBr.append("\033[1;94m" + "[i]" + "\033[0;97m");

            case WARNING -> strBr.append("\033[1;93m" + "[!]" + "\033[0;97m");

            case ERRORE -> strBr.append("\033[1;91m" + "[X]" + "\033[0;97m");

            case DEBUGGING -> strBr.append("\u001B[95m" + "[#]" + "\033[0;97m");
        }
        strBr.append(" " + messaggio);
        System.out.println(strBr);
    }

    public static int prelevaValoreMese(String mese){
        int valore = 0;
        for(int i = 0; i<italianMonth.length; i++){
            if(mese.equals(italianMonth[i])){
                valore = i;
                break;
            }
        }

        return valore;
    }

    public static boolean isEmptyResulset(ResultSet rs){
        try{
            rs.beforeFirst();
            boolean isEmpty = !rs.next();
            rs.beforeFirst();
            return isEmpty;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static int numeroGiorni(LocalDate data1, LocalDate data2){

        int numeroGiorni;

        if(!data2.isBefore(data1)){
            if(data2.isAfter(data1.plusDays(1))){
                numeroGiorni = (int)(DAYS.between(data1, data2) + 1);
            } else if(data2.isAfter(data1)){
                numeroGiorni = 2;
            } else {
                numeroGiorni = 0;
            }
        } else {
            throw new RuntimeException();
        }

        return numeroGiorni;
    }

    public static int numeroOre(int ora1, int ora2){
        int numOre;
        if(ora1 <= ora2){
            numOre = ora2 - ora1;
        } else {
            numOre = ora2 - ora1 + 24;
        }
        return numOre;
    }

}
