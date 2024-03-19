package com.example.softwaredip.controls;

import com.example.softwaredip.commons.Calendario;
import com.example.softwaredip.commons.DBMSBound;
import com.example.softwaredip.commons.Utils;
import com.example.softwaredip.entity.Utente;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

/**Control dei casi d'uso CalendarioAmm, RegistroGiornalieroAmm, VisualizzaTurniDip, VisualizzaTurniAmm,
 * VisualizzaTurniTendinaMesi, VisualizzaCalendarioProposta e VisualizzaPropostaTurnazioneTrimestrale
 * @version 1.0*/

public class TurnazioneCtrl {

    /**Preleva i turni dell'utente loggato e li inserisce all'interno di un calendario
     * @return Ritorna un calendario con i turni dell'utente loggato*/

    /*--------------------------------------------- TURNI_WIN ---------------------------------------------*/

    /**Preleva i turni del dipendente selezionato del trimestre corrente
     * @return Ritorna il calendario con i turni prelevati*/
    public static Calendario aquisisciTurni(){

        /*PRELEVARE TUTTI I TURNI DEL TRIMESTRE DEL DIPENDENTE LOGGATO*/

        /*QUERY - prelevaTurniDipendente(matricola) = listaTurniDipendente*/

        DBMSBound db = new DBMSBound();

        ResultSet rs = null;

        if(db.creaConnessione()){
            rs = db.acquisisciTurnazioneDip(Utente.utenteLoggato.getMatricola());

            try {
                rs.next();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else {
            Utils.mostraPopUpErroreDB();
        }

        Calendario calendario = new Calendario(LocalDate.now(), rs);
        Utils.messaggioConsole("Calendario generato", Utils.Tipo_messaggio.INFO);
        return calendario;
    }
}
