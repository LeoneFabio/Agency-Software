package com.example.softwaredip.commons;

import java.time.LocalDate;

public class Comunicazione {

    private int id;

    private boolean flagVisualizzazione;

    private LocalDate dataInvio;

    private String tipo;

    public Comunicazione(int id, boolean flagVisualizzazione, LocalDate dataInvio, String tipo) {
        this.id = id;
        this.flagVisualizzazione = flagVisualizzazione;
        this.dataInvio = dataInvio;
        this.tipo = tipo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isFlagVisualizzazione() {
        return flagVisualizzazione;
    }

    public void setFlagVisualizzazione(boolean flagVisualizzazione) {
        this.flagVisualizzazione = flagVisualizzazione;
    }

    public LocalDate getDataInvio() {
        return dataInvio;
    }

    public void setDataInvio(LocalDate dataInvio) {
        this.dataInvio = dataInvio;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getInfo(){
        return this.getId() + " " + this.getTipo() + " " + this.isFlagVisualizzazione() + " " + getDataInvio();
    }

    @Override
    public String toString(){
        StringBuilder message = new StringBuilder();
        if(!flagVisualizzazione){
            message.append("\t⚫");
        }
        message.append(" ").append(tipo).append(" - ").append(Utils.formattaData(dataInvio));

        return message.toString();
    }

}
