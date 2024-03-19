package com.example.softwareamm.commons;

import com.example.softwareamm.gestioneaccount.MenuWinInterfaccia;
import com.example.softwareamm.Main;
import com.example.softwareamm.controls.*;
import com.example.softwareamm.entity.Utente;
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
 * @see AccountCtrl AccountCtrl
 * @see AstensioneCtrl AstensioneCtrl
 * @see ComunicazioneCtrl ComunicazioneCtrl
 * @see GestioneDipendentiCtrl GestioneDipendentiCtrl
 * @see PIACtrl PIACtrl
 * @see StipendioCtrl StipendioCtrl
 * @see TimeCtrl TimeCtrl
 * @see TurnazioneCtrl TurnazioneCtrl
 * */
public class Utils {

    /**Insieme dei caratteri speciali consentiti nella password*/
    public static final char[] caratteriSpeciali = {'-', '_', '@', '.', '!', '?', '+', '*', '<', '>','[', ']', '{', '}', '/'};

    /**Consente la creazione di una nuova scena in uno stage
     * @param stage Stage in cui verrà mostrata la scena
     * @param title Titolo da dare allo stage
     * @param winPath Url della struttura grafica della scena
     * @return Ritorna il loader della nuova scena da cui è possibile prelevare il controller*/
    public static FXMLLoader mostraScena(Stage stage, String title , String winPath) throws IOException{
        /*CARICHIAMO LA FINESTRA DELLA winPath*/
        FXMLLoader fxmlLoader = new FXMLLoader(Objects.requireNonNull(Main.class.getResource(winPath)));
        Parent root = fxmlLoader.load();

        /*SE SI VUOLE CARICARE LA MenuWin, APRIAMO IL MENU CON LA HOME*/
        if(winPath.equals("gestioneaccount/MenuWin.fxml")){
            MenuWinInterfaccia menuWinInterfaccia = fxmlLoader.getController();
            Pane p = FXMLLoader.load(Objects.requireNonNull(Main.class.getResource("gestioneaccount/HomeWin.fxml")));
            menuWinInterfaccia.borderPaneSwitch.setCenter(p);
        }

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

    /**Consente la creazione di una nuova scena Menu con una finestra aperta, in uno stage
     * @param stage Stage in cui verrà mostrata la scena
     * @param title Titolo da dare allo stage
     * @param winPath Url della struttura grafica della finestra da aprire nel menu
     * @return Ritorna il loader della nuova scena da cui è possibile prelevare il controller*/
    public static FXMLLoader mostraScenaMenu(Stage stage, String title , String winPath) throws IOException{
        /*CARICHIAMO LA FINESTRA DELLA winPath NEL MENU*/
        FXMLLoader fxmlLoader = new FXMLLoader(Objects.requireNonNull(Main.class.getResource("gestioneaccount/MenuWin.fxml")));
        Parent root = fxmlLoader.load();

        MenuWinInterfaccia menuWinInterfaccia = fxmlLoader.getController();
        Pane p = FXMLLoader.load(Objects.requireNonNull(Main.class.getResource(winPath)));
        menuWinInterfaccia.borderPaneSwitch.setCenter(p);
        menuWinInterfaccia.seleziona(selezionaPaginaMenu(winPath));

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

    private static int selezionaPaginaMenu(String path){
        int value = -1;
        if(path.equals("gestioneaccount/HomeWin.fxml")){
            value = 0;
        } else if(path.equals("gestionedipendenti/ListaDipWin.fxml")){
            value = 1;
        } else if(path.equals("gestioneturni/CalendarioAmmWin.fxml")){
            value = 2;
        }else if(path.equals("gestioneturni/PIAWin.fxml")){
            value = 3;
        } else if(path.equals("gestionecomunicazioni/ComunRicevWin.fxml")){
            value = 4;
        } else {
            value = 5;
        }

        return value;
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

    /**Ricava il piano di una scena
     * @param scenePath Url della struttura grafica della scena
     * @return Ritorna il piano della scena*/
    public static Pane getPianoScena(String scenePath) throws IOException {

        return FXMLLoader.load(Objects.requireNonNull(Main.class.getResource(scenePath)));
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

    /**Mostra la finestra di accesso ed elimina le credenziali nell'entity Utente
     * @param stage Stage in cui effettuare il logOut*/
    public static void logOut(Stage stage) throws IOException {
        if(Utente.utenteLoggato != null){
            Utente.eliminaInformazioni();
        }

        messaggioConsole("LogOut effettuato", Tipo_messaggio.INFO);

        mostraScena(stage, "Accesso", "gestioneaccount/AccessoWin.fxml");
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
        for(int i = 0; i<Calendario.italianMonth.length; i++){
            if(mese.equals(Calendario.italianMonth[i])){
                valore = i;
                break;
            }
        }

        return valore;
    }

    public static boolean isNotEmptyResulset(ResultSet rs){
        try{
            rs.beforeFirst();
            boolean isNotEmpty = rs.next();
            rs.beforeFirst();
            return isNotEmpty;
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
