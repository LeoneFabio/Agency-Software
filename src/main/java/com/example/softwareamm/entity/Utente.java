package com.example.softwareamm.entity;

import com.example.softwareamm.commons.Utils;
import com.example.softwareamm.controls.AccountCtrl;

/**Utente con le credenziali di accesso - Entity
 * @version 1.0
 * */

public class Utente {

    /**Unica istanza della classe*/
    public static Utente utenteLoggato;

    private int matricola;

    private String password;

    private Utente(int matricola, String password) {
        this.matricola = matricola;
        this.password = password;
    }

    /**Consente la creazione di un'istanza Utente, se è già presente fornisce errore
     * @param  matricola matricola
     * @param password password
     * @see AccountCtrl AccountCtrl
     * */
    public static void creaEntity(int matricola, String password){
        if(utenteLoggato == null){
            utenteLoggato = new Utente(matricola, password);
        } else {
            System.out.println("Utente già loggato");
        }
    }

    /**Consente la modifica delle credenziali dell'istanza Utente
     * @param matricola matricola
     * @param password password*/
    public void aggiornaInformazioni(int matricola, String password){
        this.setMatricola(matricola);
        this.setPassword(password);
    }

    /**Consente di eliminare l'istanza fino a quel momento presente,
     * con conseguente cancellazione delle credenziali
     * @see Utils Utils*/
    public static void eliminaInformazioni(){
        Utente.utenteLoggato = null;
        System.gc();
    }

    /**Matricola dell'utente*/
    public int getMatricola() {
        return this.matricola;
    }

    /**Consente la modifica della matricola
     * @param matricola matricola*/
    private void setMatricola(int matricola) {
        this.matricola = matricola;
    }

    /**Password dell'utente*/
    public String getPassword() {
        return this.password;
    }

    /**Consente la modifica della password
     * @param password matricola*/
    private void setPassword(String password) {
        this.password = password;
    }
}
