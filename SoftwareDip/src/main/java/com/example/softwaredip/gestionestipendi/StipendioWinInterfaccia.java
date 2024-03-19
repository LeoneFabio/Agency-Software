package com.example.softwaredip.gestionestipendi;

import com.example.softwaredip.commons.Calendario;
import com.example.softwaredip.controls.StipendioCtrl;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.io.IOException;
import java.time.LocalDate;

/**Interfaccia della finestra StipendiWin
 * @version 1.0
 * @see StipendioCtrl StipendioCtrl
 * */
public class StipendioWinInterfaccia {
    @FXML
    private Label meseLabel;

    @FXML
    private Label annoLabel;

    @FXML
    private Label baseStipendialeLabel;

    @FXML
    private Label agevolazioniLabel;

    @FXML
    private Label straordinariLabel;

    @FXML
    private Label totaleLabel;

    @FXML
    private Label oreStraordinariLabel;

    @FXML
    private Label tassoStraordinariLabel;

    @FXML
    private Label tassoAgevolazioniLabel;

    @FXML
    private Label oreMensiliLabel;

    @FXML
    private void initialize(){
        prelevaInformazioni();
    }

    private void prelevaInformazioni() {

        double[] info = StipendioCtrl.prelevaInfoStipendioAttuale();
        meseLabel.setText(Calendario.italianMonth[LocalDate.now().getMonth().getValue() - 1]);
        annoLabel.setText(Integer.toString(LocalDate.now().getYear()));//RIVEDERE CONTI

        double baseOraria = info[0];
        double tassoStraordinari = info[1];
        double tassoAgevolazioni = info[2];
        int oreStraordinari = (int) info[3];
        int oreMensili = StipendioCtrl.prelevaOreEffettuate();


        double pagaStraordinari = (baseOraria * oreStraordinari) * tassoStraordinari;
        double pagaAgevolazioni = (baseOraria * oreMensili) * tassoAgevolazioni - (baseOraria * oreMensili);

        double totale = oreMensili*baseOraria + pagaStraordinari + pagaAgevolazioni;

        baseStipendialeLabel.setText(" €" + baseOraria);
        tassoAgevolazioniLabel.setText("Agevolazioni" + String.format(" (Tasso: +%.2f", tassoAgevolazioni) + ")");
        tassoStraordinariLabel.setText("Straordinari" + String.format(" (Tasso: +%.2f", tassoStraordinari) + ")");
        agevolazioniLabel.setText(" €" + String.format("%.2f", pagaAgevolazioni));
        straordinariLabel.setText(" €" + String.format("%.2f", pagaStraordinari));
        totaleLabel.setText(" €" + String.format("%.2f", totale));

        oreMensiliLabel.setText("" + oreMensili);
        oreStraordinariLabel.setText("" + oreStraordinari);

        // ESEMPIO

    }

    @FXML
    private void apriCronologiaStipendi() throws IOException {
        StipendioCtrl.mostraCronologiaWin();
    }
}
