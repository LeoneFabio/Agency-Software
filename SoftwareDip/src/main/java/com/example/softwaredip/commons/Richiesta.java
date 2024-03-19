package com.example.softwaredip.commons;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import com.example.softwaredip.controls.ComunicazioneCtrl;

import java.time.LocalDate;

/**Richiesta con attributi visibili anche dalle tableView
 * @version 1.0
 * */

public class Richiesta {

    /**Tipologie di una richiesta*/
    public enum TipoRichiesta {FERIE, CONGEDO_PARENTALE, MALATTIA, SCIOPERO, PERMESSO};

    /**Stati che può assumere una richiesta*/
    public enum StatoRichiesta {ACCETTATA, IN_ATTESA, RIFIUTATA};
    private StringProperty tipo;
    private StringProperty periodo;
    private StringProperty motivazione;
    private StringProperty stato;

    /**Permette la creazione di una richiesta
     * @param tipo Tipologia
     * @param dataInizio Data di inizio
     * @param dataFine Data di fine
     * @param motivazione Motivazione
     * @param stato Stato
     * @see ComunicazioneCtrl ComunicazioneCtrl
     * */
    public Richiesta(TipoRichiesta tipo, LocalDate dataInizio, LocalDate dataFine, String motivazione, StatoRichiesta stato) {
        this.tipo = new SimpleStringProperty(tipo.toString());
        this.periodo = new SimpleStringProperty(getPeriodoString(dataInizio, dataFine));
        this.motivazione = new SimpleStringProperty(motivazione);
        this.stato = new SimpleStringProperty(stato.toString());
    }

    private String getPeriodoString(LocalDate dataInizio, LocalDate dataFine){
        String dataInizioString = dataInizio.getDayOfMonth() + "/" + dataInizio.getMonth().getValue() + "/" +  dataInizio.getYear();
        String periodoString;

        if(dataFine == null){
            periodoString = dataInizioString;
        } else {
            String dataFineString = dataFine.getDayOfMonth() + "/" + dataFine.getMonth().getValue() + "/" +  dataFine.getYear();
            periodoString = "Da: " + dataInizioString + "\nA: " + dataFineString;
        }

        return periodoString;
    }

    /**Tipologia della richiesta visibile per una tableView
     * @return ritorna una stringa
     * */
    public StringProperty tipoProperty() {
        return tipo;
    }

    /**Periodo della richiesta visibile per una tableView
     * @return ritorna una stringa
     * */
    public StringProperty periodoProperty() {
        return periodo;
    }

    /**Motivazione della richiesta visibile per una tableView
     * @return ritorna una stringa
     * */
    public StringProperty motivazioneProperty() {
        return motivazione;
    }

    /**Stato della richiesta visibile per una tableView
     * @return ritorna una stringa
     * */
    public StringProperty statoProperty() {
        return stato;
    }

    /**Ritorna il tipo di richiesta
     * @param tipo Tipologia in stringa
     * @return Tipologia in TipoRichiesta*/
    public static TipoRichiesta getTipo(String tipo){
        TipoRichiesta tipoRichiesta = null;
        if(tipo.equals(TipoRichiesta.CONGEDO_PARENTALE.toString())){
            tipoRichiesta = TipoRichiesta.CONGEDO_PARENTALE;
        } else if(tipo.equals(TipoRichiesta.SCIOPERO.toString())){
            tipoRichiesta = TipoRichiesta.SCIOPERO;
        } else if(tipo.equals(TipoRichiesta.PERMESSO.toString())){
            tipoRichiesta = TipoRichiesta.PERMESSO;
        } else if(tipo.equals(TipoRichiesta.MALATTIA.toString())){
            tipoRichiesta = TipoRichiesta.MALATTIA;
        } else if(tipo.equals(TipoRichiesta.FERIE.toString())){
            tipoRichiesta = TipoRichiesta.FERIE;
        } else {
            System.out.println("ERRORE TIPO");;
        }

        return tipoRichiesta;
    }
}
