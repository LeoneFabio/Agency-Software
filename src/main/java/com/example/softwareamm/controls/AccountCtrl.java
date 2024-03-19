package com.example.softwareamm.controls;

import com.example.softwareamm.Main;
import com.example.softwareamm.commons.DBMSBound;
import com.example.softwareamm.commons.EmailUtils;
import com.example.softwareamm.commons.Utils;
import com.example.softwareamm.entity.Utente;
import javafx.scene.control.Alert;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;

/**Control dei casi d'uso Autenticazione, NuovaPsw, ModificaAccount, ModificaPsw, RecuperaPsw e LogOut
 * @version 1.0*/

public class AccountCtrl {

    private static final int MIN_CARATTERI = 8;

    private static int matricola;

    private static int OTP;

    /*--------------------------------------------- ACCESSO_WIN ---------------------------------------------*/

    /**Verifica se le credenziali sono valide e se esistono nel DataBase, se è tutto corretto allora vengono salvate le credenziali nell'entity e mostra la HomeWin
     * @param matricola matricola
     * @param password password*/
    public static void verificaCredenziali(String matricola, String password) {

        //SISTEMARE INSERIMENTO DELLA MATRICOLA CHE DEVONO ESSERE 4 CIFRE

        DBMSBound db = new DBMSBound();
        if(db.creaConnessione()){
            if(!matricola.isEmpty() && !password.isEmpty()){
                try {
                    int matricolaNum = -1;
                    matricolaNum = Integer.parseInt(matricola);
                    String psw = db.acquisisciPassword(matricolaNum);
                    int matricolaAmm = db.acquisisciMatricolaAmm();
                    if((matricola.length() == 4) && (matricolaNum == matricolaAmm) && password.equals(psw)){
                        Utente.creaEntity(matricolaNum, password);
                        Utils.messaggioConsole("Accesso effettuato: " + Utente.utenteLoggato.getMatricola() + " : " + Utente.utenteLoggato.getPassword(), Utils.Tipo_messaggio.INFO);

                        try{
                            Utils.mostraScena(Main.mainStage, "Home", "gestioneaccount/MenuWin.fxml");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    } else {
                        Utils.mostraPopUp(Alert.AlertType.ERROR, "Errore credenziali", "Matricola o password non inserite correttamente");
                    }

                } catch (NumberFormatException e) {
                    Utils.mostraPopUp(Alert.AlertType.ERROR, "Errore credenziali", "Matricola o password non inserite correttamente");
                }

            } else {
                Utils.mostraPopUp(Alert.AlertType.ERROR, "Errore credenziali", "Compilare tutti i campi");
            }
        } else {
            Utils.mostraPopUpErroreDB();
        }
    }

    /**Mostra la finestra RecuperaWin*/
    public static void mostraRecuperaPswWin() throws IOException {
        Utils.mostraScena(Main.mainStage, "Recupera Password", "gestioneaccount/RecPswWin.fxml");
    }

    /*---------------------------------------------HOME_WIN ---------------------------------------------*/

    /**Preleva tutte le informazioni del dipendente dal DataBase*/

    //IL RITORNO DEVE ESSERE DI TIPO RESULTSET
    public static ResultSet acquisisciInfo(){
        /*QUERY - acquisisciInfoDipendente(matricola) = info*/

        DBMSBound db = new DBMSBound();
        ResultSet rs = null;
        if(db.creaConnessione()){
            rs = db.acquisisciInfoUtente(Utente.utenteLoggato.getMatricola());
            try{
                rs.next();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        } else {
            Utils.mostraPopUpErroreDB();
        }

        return rs;
    }

    /*--------------------------------------------- MODIFICA_ACC_WIN ---------------------------------------------*/

    /**Aggiorna le nuove informazioni dell'utente loggato
     * @param email Email
     * @param indirizzo Indirizzo abitativo
     * @param cellulare Cellulare*/
    public static void salvaInformazioniDipendente(String email, String indirizzo, String cellulare){
        /*QUERY - salvaNuoveInfoDipendente(matricola, email, indirizzo, cellulare) = @salvataggioDelleInformazioni@*/

        DBMSBound db = new DBMSBound();

        if(db.creaConnessione()){
            db.aggiornaInfo(Utente.utenteLoggato.getMatricola(), email, indirizzo, cellulare);
            Utils.mostraPopUp(Alert.AlertType.INFORMATION, "Operazione completata", "Informazioni aggiornate con successo.");
            Utils.messaggioConsole("Informazioni aggiornate- > " + email + " : " + indirizzo + " : " + cellulare, Utils.Tipo_messaggio.INFO);
        } else {
            Utils.mostraPopUpErroreDB();
        }

    }

    /**Mostra la finestra VecchiaPswWin*/
    public static void mostraVecchiaPswWin() throws IOException {
        Utils.mostraScena(Main.mainStage,"Modifica password", "gestioneaccount/VecchiaPswWin.fxml");
    }

    /*--------------------------------------------- NUOVA_PSW_WIN ---------------------------------------------*/

    /**Verifica se la password inserita è idonea, se lo è viene aggiornata la password dell'utente con la matricola precedentemente inserita
     * @param nuovaPsw Nuova password
     * @param confermaPsw Password di conferma*/
    public static void verificaNuovaPsw(String nuovaPsw, String confermaPsw) throws IOException {
        if(nuovaPsw.equals(confermaPsw)){
            //I due campi coincidono
            if(passwordIdonea(nuovaPsw)){

                DBMSBound db = new DBMSBound();

                if(db.creaConnessione()){
                    if(Utente.utenteLoggato != null){
                        matricola = Utente.utenteLoggato.getMatricola();
                    }

                    db.aggiornaPsw(matricola, nuovaPsw);

                    Utils.mostraPopUp(Alert.AlertType.INFORMATION, "Cambio password avvenuto con successo", "Rieffettuare l'accesso con le nuove credenziali");
                    Utils.logOut(Main.mainStage);
                } else {
                    Utils.mostraPopUpErroreDB();
                }
            } else {

                StringBuilder caratteriSpeciali = new StringBuilder("(");

                for(int i = 0; i < Utils.caratteriSpeciali.length; i++){
                    caratteriSpeciali.insert(caratteriSpeciali.length(), Utils.caratteriSpeciali[i]);
                    if(i != Utils.caratteriSpeciali.length-1){
                        caratteriSpeciali.insert(caratteriSpeciali.length(), ", ");
                    }
                }

                caratteriSpeciali.insert(caratteriSpeciali.length(), ")");

                Utils.mostraPopUp(Alert.AlertType.ERROR, "Password non idonea", "La password deve possedere almeno 8 caratteri, una maiuscola, una minuscola, un numero e un carattere speciale " + caratteriSpeciali);
            }
        } else {
            Utils.mostraPopUp(Alert.AlertType.ERROR, "Password non valida", "I due cambi non coincidono. Riprovare.");
        }
    }

    private static boolean passwordIdonea(String password){
        /*LA PASSWORD DEVE AVERE UN CARATTERE MINUSCOLO, MAIUSCOLO, CARATTERE SPECIALE, NUMERO CARATTERI*/

        String passwordDaverificare = password.trim();
        int dim = passwordDaverificare.length();

        boolean maiuscolo = false;
        boolean minuscolo = false;
        boolean numero = false;
        boolean speciali = false;
        boolean numeroCaratteri = false;

        boolean carattereNonvalido = false;

        if(dim >= MIN_CARATTERI){
            numeroCaratteri = true;
        }

        for(int i = 0; i < dim; i++){
            if(Character.isUpperCase(passwordDaverificare.charAt(i))){
                maiuscolo = true;

            } else if(Character.isLowerCase(passwordDaverificare.charAt(i))){
                minuscolo = true;

            } else if(Character.isDigit(passwordDaverificare.charAt(i))){
                numero = true;

            } else if(isSpecialChar(passwordDaverificare.charAt(i))){
                speciali = true;

            } else {
                carattereNonvalido = true;
            }
        }

        return maiuscolo && minuscolo && numero && speciali && numeroCaratteri && !carattereNonvalido;

    }

    private static boolean isSpecialChar(char a){
        boolean isSpecial = false;

        for(int i = 0; i < Utils.caratteriSpeciali.length; i++){
            if(a == Utils.caratteriSpeciali[i]){
                isSpecial = true;
                break;
            }
        }
        return isSpecial;
    }

    /*--------------------------------------------- REC_PSW_WIN ---------------------------------------------*/

    /**Verifica se la matricola inserita esiste, se lo è procede con il generare un OTP e a inviarlo all'email associata alla matricola, infine mostra la finsetra OTPWin
     * @param matricolaInserita matricola da verificare*/
    public static void verificaMatricola(String matricolaInserita) throws IOException {

        /*QUERY - esisteMatricola(matricola) = esiste*/

        DBMSBound db = new DBMSBound();

        if(db.creaConnessione()){
            boolean esiste = false;

            try{
                ResultSet rs = db.acquisisciListaUtenti();
                while(rs.next()){
                    if( String.format("%04d", rs.getInt("matricola")).equals(matricolaInserita)){
                        esiste = true;
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            if(esiste){
                AccountCtrl.matricola = Integer.parseInt(matricolaInserita);
                generaOTP();
                Utils.mostraScena(Main.mainStage, "OTP", "gestioneaccount/OTPWin.fxml");
            } else {
                Utils.mostraPopUp(Alert.AlertType.ERROR, "Matricola non presente nel sistema", "Riprovare");
            }
        } else {
            Utils.mostraPopUpErroreDB();
        }
    }

    private static void generaOTP(){

        /*GENERA OTP*/
        Random random = new Random();
        int OTP = random.nextInt(10000);
        AccountCtrl.OTP = OTP;

        String email = "mario.rossi.test52@gmail.com";

        EmailUtils.inviaOTP(email, OTP);
        Utils.messaggioConsole("OTP generato -> " + AccountCtrl.OTP, Utils.Tipo_messaggio.INFO);
    }

    /**Mostra la finestra AccessoWin*/
    public static void mostraAccessoWin() throws IOException {
        Utils.mostraScena(Main.mainStage, "Accesso", "gestioneaccount/AccessoWin.fxml");
    }

    /*--------------------------------------------- OTP_WIN ---------------------------------------------*/

    /**Se esiste già creato un OTP lo confronta con quello inserito, se coincidono allora mostra la finestra NuovaPswWin
     * @param OTP OTP da verificare
     * @return Restituisce true se la verifica è andata a buon fine, false altrimenti (NESSUN OTP ANCORA GENERATO)*/
    public static boolean verificaOTP(String OTP){

        if(AccountCtrl.OTP == 0){
            return false;
        }

        try{
            int OTPInserito = Integer.parseInt(OTP.trim());
            if(OTPInserito == AccountCtrl.OTP){
                Utils.mostraScena(Main.mainStage, "Nuova password", "gestioneaccount/NuovaPswWin.fxml");
            } else {
                Utils.mostraPopUp(Alert.AlertType.ERROR, "OTP non corretto", "Riprovare");
            }
        } catch (NumberFormatException ex){
            Utils.mostraPopUp(Alert.AlertType.ERROR, "OTP non corretto", "Riprovare");
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        return true;
    }

    /*--------------------------------------------- MENU_WIN ---------------------------------------------*/

    /**Consente l'apertura di pagine all'interno del menu. note: imposta anche nuove dimensioni minime allo stage in base alle finestre che vogliamo aprire
     * @param stage Stage
     * @param borderPane Piano su cui verrà aperta la finestra
     * @param title Titolo da dare allo stage
     * @param scenePath Url della struttura grafica della finestra da aprire*/
    public static void apriPaginaMenu(Stage stage, BorderPane borderPane, String title, String scenePath) throws IOException {
        Pane p = Utils.getPianoScena(scenePath);
        borderPane.setCenter(p);
        if(scenePath.equals("gestioneturni/CalendarioAmmWin.fxml") || scenePath.equals("gestionecomunicazioni/ComunRicevWin.fxml") || scenePath.equals("gestioneturni/PIAWin.fxml")){
            Utils.setMinSizeOfStage(stage,1100, 800, title);
        } else {
            Utils.setMinSizeOfStage(stage,800, 800, title);
        }

        Utils.messaggioConsole("Menu: " + title, Utils.Tipo_messaggio.NAVIGAZIONE);
    }

    /**Esegue il logOut
     * @see Utils Utils - LogOut*/
    public static void logOut() throws IOException {
        Utils.logOut(Main.mainStage);
    }

    /*--------------------------------------------- VECCHIA_PSW_WIN ---------------------------------------------*/

    /**Verifica la password inserita con la password attuale dell'utente loggato, se è valida viene avviata la procedura di modifica password
     * @param passwordInserita Password inserita
     * @return Ritorna true se la verifica è andata a buon fine, false altrimenti (SE NON ESISTE ALCUN UTENTE LOGGATO)*/
    public static boolean verificaPassword(String passwordInserita) throws IOException {

        /*PRELEVARE LA PASSWORD DALL'ENTITY, SE LA PASSWORD COINCIDE CON QUELLA INSERITA ALLORA PROCEDI*/

        if (Utente.utenteLoggato == null){
            return false;
        }

        if(Utente.utenteLoggato.getPassword().equals(passwordInserita)){
            Utils.mostraScena(Main.mainStage, "Nuova password", "gestioneaccount/NuovaPswWin.fxml");
        } else {
            Utils.mostraPopUp(Alert.AlertType.ERROR, "Password non corretta", "La password non coincide con quella registrata. Riprovare.");
        }

        return true;
    }

    /**Mostra la finestra HomeWin*/
    public static void mostraHomeWin() throws IOException {
        Utils.mostraScenaMenu(Main.mainStage, "Modifica Account", "gestioneaccount/ModificaAccWin.fxml");
    }
}
