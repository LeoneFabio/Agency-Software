package com.example.softwareamm.commons;

import com.example.softwareamm.controls.TurnazioneCtrl;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import java.time.LocalTime;

/**Turno dipendente con attributi visibili anche dalle tableView
 * @version 1.0
 * */
public class TurnoDipendente {
    private StringProperty matricola;
    private StringProperty nome;
    private StringProperty cognome;
    private StringProperty ingresso;
    private StringProperty uscita;

    /**Permette la creazione del turno di un dipendente
     * @param matricola Matricola
     * @param nome Nome
     * @param cognome Cognome
     * @param ingresso Ingresso
     * @param uscita Uscita
     * @see TurnazioneCtrl TurnazioneCtrl
     * */
    public TurnoDipendente(int matricola, String nome, String cognome, int ingresso, int uscita) {
        this.matricola = new SimpleStringProperty(String.format("%04d", matricola));
        this.nome = new SimpleStringProperty(nome);
        this.cognome = new SimpleStringProperty(cognome);
        this.ingresso = new SimpleStringProperty(ingresso + ":00");
        this.uscita = new SimpleStringProperty(uscita + ":00");
    }

    /**Matricola del dipendente del turno visibile per una tableView
     * @return ritorna una stringa
     * */
    public StringProperty matricolaProperty() {
        return matricola;
    }

    /**Nome del dipendente del turno visibile per una tableView
     * @return ritorna una stringa
     * */
    public StringProperty nomeProperty() {
        return nome;
    }

    /**Cognome del dipendente del turno visibile per una tableView
     * @return ritorna una stringa
     * */
    public StringProperty cognomeProperty() {
        return cognome;
    }

    /**Ingresso del turno visibile per una tableView
     * @return ritorna una stringa
     * */
    public StringProperty ingressoProperty() {
        return ingresso;
    }

    /**Uscita del turno visibile per una tableView
     * @return ritorna una stringa
     * */
    public StringProperty uscitaProperty() {
        return uscita;
    }
}
