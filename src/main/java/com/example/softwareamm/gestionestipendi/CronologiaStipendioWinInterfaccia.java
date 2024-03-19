package com.example.softwareamm.gestionestipendi;

import com.example.softwareamm.commons.Calendario;
import com.example.softwareamm.controls.StipendioCtrl;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.time.LocalDate;


/**Interfaccia della finestra CronologiaStipendioWin
 * @version 1.0
 * @see StipendioCtrl StipendioCtrl
 * */
public class CronologiaStipendioWinInterfaccia {
    @FXML
    private Label meseLabel;

    @FXML
    private Label annoLabel;

    @FXML
    private Label baseStipendialeLabel;

    @FXML
    private Label agevolazioniLabel;

    @FXML
    private Label tassoAgevolazioniLabel;

    @FXML
    private Label straordinariLabel;

    @FXML
    private Label tassoStraordinariLabel;

    @FXML
    private Label totaleLabel;

    @FXML
    private Label oreStraordinariLabel;

    @FXML
    private Label oreMensiliLabel;

    /**Consente di caricare le informazioni nella finestra
     * @param data Data dello stipendio
     * @param baseOraria Base stipendiale oraria
     * @param pagaAgevolazioni Paga per agevolazioni
     * @param tassoAgevolazioni tasso agevolazioni (%)
     * @param pagaStraordinari Paga per gli straordinari
     * @param tassoStraordinari tasso straordinari (%)
     * @param totale Totale
     * @param oreMensili Ore mensili effettuate
     * @param oreStraordinari Ore straordinarie effettuate*/
    public void inizializza(LocalDate data, int baseOraria, double pagaAgevolazioni, double tassoAgevolazioni, double pagaStraordinari, double tassoStraordinari, double  totale, int oreMensili, int oreStraordinari){
        meseLabel.setText(Calendario.italianMonth[data.getMonthValue()-1]);
        annoLabel.setText("" + data.getYear());
        baseStipendialeLabel.setText(" €" + baseOraria);
        agevolazioniLabel.setText(" €" + String.format("%.2f", pagaAgevolazioni));
        tassoAgevolazioniLabel.setText("Agevolazioni (" + String.format("Tasso: +%.2f", tassoAgevolazioni) + ")");
        straordinariLabel.setText(" €" + String.format("%.2f", pagaStraordinari));
        tassoStraordinariLabel.setText("Straordinari (" + String.format("Tasso: +%.2f", tassoStraordinari) + ")");
        totaleLabel.setText(" €" + String.format("%.2f", totale));
        oreMensiliLabel.setText("" + oreMensili);
        oreStraordinariLabel.setText("" + oreStraordinari);
    }
}
