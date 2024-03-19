package com.example.softwareamm.controls;

import com.example.softwareamm.Main;
import com.example.softwareamm.commons.DBMSBound;
import com.example.softwareamm.commons.Utils;
import com.example.softwareamm.entity.Dipendente;
import javafx.scene.control.Alert;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

/**Control dei casi d'uso VisualizzaListaDipendenti, CercaLista, AggiungiDipendente, RimuoviDipendente, VisualizzaDipendente, VisualizzaInfoDipendente e ModificaInfoDipendente
 * @version 1.0*/

public class GestioneDipendentiCtrl {

    /*--------------------------------------------- LISTA_DIP_WIN ---------------------------------------------*/

    /**Preleva la lista dei dipendenti
     *@return Ritorna la lista dei dipendenti*/
    public static ResultSet prelevaListaDipendenti(){
        /*QUERY - prelevaListaDip() = listaDipendenti*/

        DBMSBound db = new DBMSBound();

        ResultSet rs = null;

        if(db.creaConnessione()){
            rs = db.acquisisciListaDipendenti();

            try{
                rs.next();
            } catch (Exception e){
                Utils.mostraPopUpErroreDB();
                e.printStackTrace();
            }
        } else {
            Utils.mostraPopUpErroreDB();
        }

        return rs;
    }

    /**Salva in memoria (Entity) la matricola del dipendente selezioanto
     * @param matricola Matricola*/
    public static void selezionaDipendente(int matricola){

        DBMSBound db = new DBMSBound();

        if(db.creaConnessione()){

            if(Dipendente.dipendenteSelezionato != null){
                Dipendente.eliminaInformazioni();
            }

            try{
                ResultSet rs = db.acquisisciInfoUtente(matricola);
                rs.next();
                Dipendente.creaEntity(
                        matricola,
                        rs.getString("nome"),
                        rs.getString("cognome"),
                        rs.getString("cod_fiscale"),
                        db.acquisisciOreEffettuate(matricola),
                        rs.getInt("ref_ruolo"),
                        db.acquisisciAgevolazioni(matricola),
                        rs.getDate("data_assunzione").toLocalDate(),
                        rs.getString("email"),
                        rs.getString("indirizzo"),
                        rs.getString("cellulare"),
                        rs.getString("note") == null ? "" : rs.getString("note"));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            Utils.messaggioConsole("Entity Dipendente:" + Dipendente.dipendenteSelezionato.getInfo(), Utils.Tipo_messaggio.INFO);
        } else {
            Utils.mostraPopUpErroreDB();
        }

    }

    /**Elimina il dipendente selezionato dal DB se si è arrivati alla soglia mimin apre un pop-up di avviso
     * @param matricola Matricola del dipendente da rimuovere*/
    public static void eliminaDipendente(int matricola){

        DBMSBound db = new DBMSBound();

        if(db.creaConnessione()){
            try{
                //OTTENIAMO IL RUOLO DEL DIPENDENTE DA RIMUOVERE
                ResultSet rsInfoDip = db.acquisisciInfoUtente(matricola);
                rsInfoDip.next();
                int ruolo = rsInfoDip.getInt("ref_ruolo");

                //OTTENIAMO LA SOGLIA MINIMA DI DIPENDENTI DEL RUOLO DEL DIPENDENTE DA RIMUOVERE
                ResultSet rsRuolo = db.acquisisciInfoRuolo(ruolo);
                rsRuolo.next();
                int sogliaMinimaDip = rsRuolo.getInt("soglia_minima_dipendenti");

                //OTTENIAMO IL NUMERO DI DIPENDENTI NEL RUOLO DEL DIPENDENTE DA RIMUOVERE
                ResultSet rsDipendenti = db.acquisisciListaDipendenti();
                int numDip = 0;
                while (rsDipendenti.next()){
                    if(rsDipendenti.getInt("ref_ruolo") == ruolo){
                        numDip++;
                    }
                }

                boolean confermato = Utils.mostraPopUp(Alert.AlertType.CONFIRMATION, "Sicuro di voler eliminare il dipendente di matricola: " + String.format("%04d", matricola), "L'operazione sarà irreversibile");

                //SE SI ACCETTA
                if(confermato){
                    db.eliminaDipendente(matricola);
                    if(numDip <= sogliaMinimaDip){
                        Utils.mostraPopUp(Alert.AlertType.WARNING, "Criticità ruolo " + ruolo , "Il ruolo " + ruolo + " è appena entrato in criticità, con un numero di dipendenti pari a " + numDip);
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else {
            Utils.mostraPopUpErroreDB();
        }
    }

    /**Mostra la finestra NuovoDipWin*/
    public static void mostraNuovoDipWin() throws IOException {
        Utils.mostraScena(Main.mainStage, "Nuovo dipendente", "gestionedipendenti/NuovoDipWin.fxml");
    }

    /**Mostra la finestra VisDipendenteWin*/
    public static void mostraVisDipendenteWin() throws IOException {
        Utils.mostraScena(Main.mainStage, "Profilo dipendente", "gestionedipendenti/VisDipendenteWin.fxml");
    }

    /*--------------------------------------------- NUOVO_DIP_WIN ---------------------------------------------*/

    /**Crea un nuovo dipendente, associando ad esso matricola e password, e lo salva nel DB. Mostra infine un pop-up informativo con la nuova matricola e password
     * @param nome Nome
     * @param cognome Cognome
     * @param CF CodiceFiscale
     * @param email Email
     * @param indirizo Indirizzo di casa
     * @param cellulare Cellulare
     * @param ruolo Ruolo
     * @param agevolazioni Agevolazioni
     * @param note Note aggiuntive*/
    public static void aggiungiNuovoDip(String nome, String cognome, String CF, String email, String indirizo, String cellulare, int ruolo, int[] agevolazioni, String note){
        //DA SISTEMARE LE AGEVOLAZIONI

        DBMSBound db = new DBMSBound();
        if(db.creaConnessione()){
            boolean annulla = false;
            try {
                ResultSet rsDip = db.acquisisciListaDipendenti();
                while (rsDip.next()){
                    String CFDip = rsDip.getString("cod_fiscale").toUpperCase();
                    if(CFDip.equals(CF.toUpperCase())){
                        annulla = true;
                        break;
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            if(!annulla){String password = generaCredenziali();
                int matricola = db.aggiungiDipendente(nome, cognome, CF, email, indirizo, cellulare, LocalDate.now(), note, ruolo, agevolazioni, password);
                Utils.mostraPopUp(Alert.AlertType.INFORMATION, "Inserimento avvenuto con successo" , "Dipendente inserito correttamente\nMatricola: " + matricola + "\nPassword: " + password);

                try{
                    Utils.mostraScenaMenu(Main.mainStage, "Visualizza dipendenti", "gestionedipendenti/ListaDipWin.fxml");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            } else {
                Utils.mostraPopUp(Alert.AlertType.ERROR, "Errore inserimento" , "Dipendente esistente");
            }
        } else {
            Utils.mostraPopUpErroreDB();
        }
    }

    public static String generaCredenziali(){  //modificare per mettere una password adeguata  almeno 8 caratteri, almeno 1 mauscola, 1 muniuscola e un carattere speciale
        String alphabet = "abcdefghijklmnopqrstuvwxyz";
        String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String numbers = "1234567890";
        String special = "?!<>-*[]{}/";
        String password = "";
        for (int i = 0; i < 3; i++) {
            // Scelgo una delle lettere dell'alfabeto.
            int randomIndexCharInAlphabet = (int) (Math.random() * alphabet.length());
            password += alphabet.charAt(randomIndexCharInAlphabet);
            int randomIndexCharInALPHABET = (int) (Math.random() * ALPHABET.length());
            password += ALPHABET.charAt(randomIndexCharInALPHABET);
            int randomIndexCharInNumbers = (int) (Math.random() * numbers.length());
            password += numbers.charAt(randomIndexCharInNumbers);
            int randomIndexCharInSpecial = (int) (Math.random() * special.length());
            password += special.charAt(randomIndexCharInSpecial);
        }
        return password;
    }

    private static String generaNuovaPsw(){
        StringBuilder nuovaPsw = new StringBuilder();

        Random random = new Random();

        Stack<Integer> stack = new Stack<>();
        stack.addAll(Arrays.asList(0,1,2,3,4,5,6,7,8,9));
        Collections.shuffle(stack);

        int indexUpper = stack.pop();
        int indexLower = stack.pop();
        int indexSpecial = stack.pop();
        int indexNumber = stack.pop();

        int size = random.nextInt(8, 15);

        for(int i=0; i<size; i++){

            if(i == indexUpper){
                //Posizione sicura di una maiuscola
                nuovaPsw.insert(nuovaPsw.length(), (char)random.nextInt(65, 90+1));

            } else if(i == indexLower) {
                //Posizione sicura di una minuscola
                nuovaPsw.insert(nuovaPsw.length(), (char)random.nextInt(97, 122+1));

            } else if(i == indexSpecial){
                //Posizione sicura di un carattere speciale
                nuovaPsw.insert(nuovaPsw.length(), Utils.caratteriSpeciali[random.nextInt(Utils.caratteriSpeciali.length)]);

            } else if(i == indexNumber){
                //Posizione sicura di un numero
                nuovaPsw.insert(nuovaPsw.length(), (char)random.nextInt(48, 57+1));

            } else {
                if(random.nextInt(8)==0 && i != 0 && i != size-1){
                    //Probabilità del 12.5% di ottenere un carattere speciale
                    nuovaPsw.insert(nuovaPsw.length(), Utils.caratteriSpeciali[random.nextInt(Utils.caratteriSpeciali.length)]);
                } else{
                    //Propabilità del 87.5% di ottenere una lettera o un numero
                    //Probabilità del 25% di ottenere una maiuscola, minuscola o numero
                    switch (random.nextInt(3)) {
                        case 0 -> {
                            nuovaPsw.insert(nuovaPsw.length(), (char)random.nextInt(65, 90+1));
                        }
                        case 1 -> {
                            nuovaPsw.insert(nuovaPsw.length(), (char)random.nextInt(97, 122+1));
                        }
                        case 2 -> {
                            nuovaPsw.insert(nuovaPsw.length(), random.nextInt(0, 9+1));
                        }
                    }

                }

            }
        }

        //System.out.println("Maiuscola: " + indexUpper + "\nMinuscola:  " + indexLower + "\nNumero: " + indexNumber + "\nSpeciale: " + indexSpecial +"\nPassword --> " + nuovaPsw);

        return nuovaPsw.toString();
    }

    /**Elimina le informazioni del dipendente selezionato (Entity) e mostra la finestra HomeWin*/
    public static void mostraListaDipWin() throws IOException {
        Dipendente.eliminaInformazioni();
        Utils.mostraScenaMenu(Main.mainStage, "Visualizza dipendenti", "gestionedipendenti/ListaDipWin.fxml");
    }

    /*--------------------------------------------- VIS_DIPENDENTE_WIN ---------------------------------------------*/

    /**Mostra la finestra VisDipendenteWin*/
    public static void mostraInfoDipWin() throws IOException {
        Utils.mostraScena(Main.mainStage, "Informazioni dipendente", "gestionedipendenti/InfoDipWin.fxml");
    }

    /**Mostra la finestra VisDipendenteWin*/
    public static void mostraStipendioWin() throws IOException {
        Utils.mostraScena(Main.mainStage, "Stipendio dipendente", "gestionestipendi/StipendioWin.fxml");
    }

    /**Mostra la finestra VisDipendenteWin*/
    public static void mostraTurniWin() throws IOException {
        Utils.mostraScena(Main.mainStage, "Turni dipendente", "gestioneturni/TurniWin.fxml");
    }

    /*--------------------------------------------- MODIFICA_INFO_DIP_WIN ---------------------------------------------*/

    /**Salva le nuove informazioni del dipendente selezionato
     * @param nome Nome
     * @param cognome Cognome
     * @param CF CodiceFiscale
     * @param email Email
     * @param indirizzo Indirizzo di casa
     * @param cellulare Cellulare
     * @param ruolo Ruolo
     * @param agevolazioni Agevolazioni
     * @param note Note aggiuntive*/
    public static void salvaInfoDip(String nome, String cognome, String CF, String email, String indirizzo, String cellulare, int ruolo, int[] agevolazioni, String note){

        /*QUERY - salvaNuoveInformazioniDip() = @salvaInfoDip*/

        DBMSBound db = new DBMSBound();

        if(db.creaConnessione()){

            try{

                boolean annulla = false;
                try {
                    ResultSet rsDip = db.acquisisciListaDipendenti();
                    while (rsDip.next()){
                        String CFDip = rsDip.getString("cod_fiscale").toUpperCase();
                        if(Dipendente.dipendenteSelezionato.getMatricola() != rsDip.getInt("matricola") && CFDip.equals(CF.toUpperCase())){
                            annulla = true;
                            break;
                        }
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }

                if(!annulla){
                    int matricola = Dipendente.dipendenteSelezionato.getMatricola();

                    Utils.mostraPopUp(Alert.AlertType.INFORMATION, "Modifiche effettuate", "Le nuove informazioni sono state correttamente caricate nel profilo del dipendente");

                    db.aggiornaInfoDip(matricola, nome, cognome, CF, email, indirizzo, cellulare, note, ruolo, agevolazioni);

                    Dipendente.eliminaInformazioni();

                    ResultSet rs = db.acquisisciInfoUtente(matricola);
                    rs.next();
                    Dipendente.creaEntity(
                            matricola,
                            rs.getString("nome"),
                            rs.getString("cognome"),
                            rs.getString("cod_fiscale"),
                            db.acquisisciOreEffettuate(matricola),
                            rs.getInt("ref_ruolo"),
                            db.acquisisciAgevolazioni(matricola),
                            rs.getDate("data_assunzione").toLocalDate(),
                            rs.getString("email"),
                            rs.getString("indirizzo"),
                            rs.getString("cellulare"),
                            rs.getString("note"));

                    try{
                        Utils.mostraScena(Main.mainStage, "Visualizza dipendenti", "gestionedipendenti/InfoDipWin.fxml");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                } else {
                    Utils.mostraPopUp(Alert.AlertType.ERROR, "Errore inserimento" , "Dipendente esistente");
                }

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        } else {
            Utils.mostraPopUpErroreDB();
        }

    }

    /**Mostra la finestra ModificaInfoDipWin*/
    public static void mostraModificaInfoDipWin() throws IOException {
        Utils.mostraScena(Main.mainStage, "Modifica info dipendente", "gestionedipendenti/ModificaInfoDipWin.fxml");
    }
}
