package com.example.softwareamm.entity;

import com.example.softwareamm.commons.Utils;
import com.example.softwareamm.controls.GestioneDipendentiCtrl;

import java.time.LocalDate;

/**Dipendente selezionato con la relativa matricola - Entity
 * @version 1.0
 * */
public class Dipendente {

    /**Unica istanza della classe*/
    public static Dipendente dipendenteSelezionato;

    private int matricola;

    private String nome;

    private String cognome;

    private String CF;

    private int oreMensili;

    private int ruolo;

    private int[] agevolazioni;

    private LocalDate dataAssunzione;

    private String email;

    private String indirizzo;

    private String cellulare;

    private String note;

    public Dipendente(int matricola, String nome, String cognome, String CF, int oreMensili, int ruolo, int[] agevolazioni, LocalDate dataAssunzione, String email, String indirizzo, String cellulare, String note) {
        this.matricola = matricola;
        this.nome = nome;
        this.cognome = cognome;
        this.CF = CF.toUpperCase();
        this.oreMensili = oreMensili;
        this.ruolo = ruolo;
        this.agevolazioni = agevolazioni;
        this.dataAssunzione = dataAssunzione;
        this.email = email;
        this.indirizzo = indirizzo;
        this.cellulare = cellulare;
        if(note.isEmpty()){
            this.note = "";
        } else {
            this.note = note;
        }
    }

    /**Consente la creazione di un'istanza Utente, se è già presente fornisce errore
     * @param  matricola matricola
     * @see  GestioneDipendentiCtrl GestioneDipendentiCtrl
     * */
    public static void creaEntity(int matricola, String nome, String cognome, String CF, int oreMensili, int ruolo, int[] agevolazioni, LocalDate dataAssunzione, String email, String indirizzo, String cellulare, String note){
        if(dipendenteSelezionato == null){
            dipendenteSelezionato = new Dipendente(matricola, nome, cognome, CF, oreMensili, ruolo, agevolazioni, dataAssunzione, email, indirizzo, cellulare, note);
        } else {
            System.out.println("Dipendente già selezionato");
        }
    }

    /**Consente di eliminare l'istanza fino a quel momento presente,
     * con conseguente cancellazione della matricola*/
    public static void eliminaInformazioni(){
        Dipendente.dipendenteSelezionato = null;
        System.gc();
    }

    /**Matricola del dipendente selezionato*/
    public int getMatricola() {
        return this.matricola;
    }

    /**Nome del dipendente selezionato*/
    public String getNome() {
        return nome;
    }

    /**Cognome del dipendente selezionato*/
    public String getCognome() {
        return cognome;
    }

    /**Codice fiscale del dipendente selezionato*/
    public String getCF() {
        return CF;
    }

    /**Ore lavorative mensili effettuate del dipendente selezionato*/
    public int getOreMensili() {
        return oreMensili;
    }

    /**Ruolo del dipendente selezionato*/
    public int getRuolo() {
        return ruolo;
    }

    /**Agevolazioni del dipendente selezionato*/
    public int[] getAgevolazioni() {
        return agevolazioni;
    }

    /**Data di assunzione del dipendente selezionato*/
    public LocalDate getDataAssunzione() {
        return dataAssunzione;
    }

    /**Email del dipendente selezionato*/
    public String getEmail() {
        return email;
    }

    /**Indirizzo del dipendente selezionato*/
    public String getIndirizzo() {
        return indirizzo;
    }

    /**Cellulare del dipendente selezionato*/
    public String getCellulare() {
        return cellulare;
    }

    /**Note del dipendente selezionato*/
    public String getNote() {
        return note;
    }

    public String getInfo(){
        return "\n\t\t\tMatricola: " + String.format("%04d", this.matricola) + "\n" +
                "\t\t\tNome: " +this.nome + "\n" +
                "\t\t\tCognome: " +this.cognome + "\n" +
                "\t\t\tCodice fiscale: " +this.CF + "\n" +
                "\t\t\tOre mensili: " + this.oreMensili + "\n" +
                "\t\t\tRuolo: " + this.ruolo + "\n" +
                "\t\t\tAgevolazioni: " + agevolazioniToString() + "\n" +
                "\t\t\tData assunzione: " + Utils.formattaData(this.dataAssunzione) + "\n" +
                "\t\t\tEmail: " + this.email + "\n" +
                "\t\t\tIndirizzo: " + this.indirizzo + "\n" +
                "\t\t\tCellulare: " + this.cellulare + "\n" +
                "\t\t\tNote: " + this.note;
    }

    private String agevolazioniToString(){

        StringBuilder str = new StringBuilder();

        for(int i=0; i<this.agevolazioni.length; i++){

            str.append(agevolazioni[i]);
            if(i != this.agevolazioni.length-1){
                str.append(',');
            }
        }
        return str.toString();
    }
}
