package com.example.softwareazienda.commons;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class DBMSBound {
    private static Connection  connection;
    private static Statement stm;
    private ResultSet rs = null;

    public ResultSet acquisisciInfoUtente(int matricola) {
        try {
            stm = connection.createStatement();
            if (matricola != 27) {
                String query = "select * from dipendenti where matricola = " + "'" + matricola + "'";
                rs = stm.executeQuery(query);
                return rs;
            }else {
                String query = "select * from amministratori where matricola = " + "'" + matricola + "'";
                rs = stm.executeQuery(query);
                return rs;
            }
        }catch(SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public ResultSet myQuery(String query){
        try {
            stm = connection.createStatement();
            rs = stm.executeQuery(query);
            return rs;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public boolean creaConnessione(){
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3308/azienda", "root", "");
        } catch (SQLException | ClassNotFoundException e) {
            return false;
        }
        return true;
    }

    public void myUpdate(String query){
        try {


            stm = connection.createStatement();
            stm.executeUpdate(query);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public int aggiungiDipendente(String nome, String cognome, String codFiscale, String email, String indirizzo, String cellulare, LocalDate dataAssunzione, String note, int ruolo, int[] agevolazioni, String password) throws RuntimeException {
        try {
            stm = connection.createStatement();


            rs = stm.executeQuery("SELECT max(matricola) FROM utenti");
            rs.next();
            int matricola = rs.getInt(1)+1;
            insertUtente(matricola, password );

            String query = "INSERT INTO `dipendenti`(`matricola`, `nome`, `cognome`, `cod_fiscale`, `email`, `indirizzo`, `cellulare`, `data_assunzione`, `note`,  `ref_ruolo`) " +
                    "VALUES ('" + matricola + "','" + nome + "','" + cognome + "','" + codFiscale + "','" + email + "','" + indirizzo + "','" + cellulare + "','" + dataAssunzione + "','" + note + "','" + ruolo + "')";
            // errore nella query qua sopra.
            stm.executeUpdate(query);

            aggiornaAgevolazioni(matricola, agevolazioni);
            ResultSet dataInserimento = stm.executeQuery("SELECT MAX(data_odierna) FROM turni");
            dataInserimento.next();
            aggiungiTurno(matricola, ruolo, dataInserimento.getDate(1).toLocalDate(),-1, -1, 7);

            return matricola;

        } catch (SQLException  e) {
            throw new RuntimeException(e);
        }
    }

    public int[] acquisisciAgevolazioni(int matricola){
        try{
            stm = connection.createStatement();
            rs = stm.executeQuery("SELECT * FROM agevolazioni_lavorative WHERE ref_dipendente = " + matricola );

            rs.afterLast();
            rs.previous();
            int numAgevolazioni = rs.getRow();
            int[] agevolazioni = new int[numAgevolazioni];
            rs.beforeFirst();
            for(int i=0; i<numAgevolazioni;i++){
                rs.next();
                agevolazioni[i] = rs.getInt("ref_tipo_agevolazione_lavorativa");
            }

            return agevolazioni;

        }catch(SQLException e){
            throw new RuntimeException(e);
        }
    }

    public void aggiungiTurno(int matricola, int ruolo, LocalDate data , int inizioTurno, int fineTurno, int stato){
        try {
            stm = connection.createStatement();
            stm.executeUpdate("INSERT INTO turni(data_odierna, ref_dipendente, ora_inizio_turno, ora_fine_turno, stato_giornaliero ,ruolo_giornaliero) VALUES ('" + data + "','" + matricola + "','" + inizioTurno + "','" + fineTurno + "','" + stato + "','"+ruolo+"')");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public ArrayList<Integer> acquisisciMatricoleAzienda(){
        try {
            stm = connection.createStatement();
            rs = stm.executeQuery("SELECT matricola FROM utenti");
            ArrayList<Integer> listaMatricole = new ArrayList<>();
            while (rs.next()){
                listaMatricole.add(rs.getInt("matricola"));
            }

            return  listaMatricole;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String acquisisciIndirizzoEmail(int matricola){
        try {
            stm = connection.createStatement();
            if(matricola != 27) {
                rs = stm.executeQuery("SELECT email FROM utenti u, dipendenti d WHERE (u.matricola = d.matricola ) AND u.matricola = " + matricola);
            }else{
                rs = stm.executeQuery("SELECT email FROM utenti u, amministratori a WHERE (u.matricola = a.matricola ) AND u.matricola = " + matricola);
            }
            rs.next();
            return rs.getString("email");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void aggiornaPsw(int matricola, String password){
        try {
            stm = connection.createStatement();
            stm.executeUpdate("UPDATE utenti SET password = '"+password+"' WHERE matricola = "+matricola);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public ResultSet acquisisciNumColleghi(LocalDate dataTurno, int matricola){
        try{
            stm = connection.createStatement();
            rs = stm.executeQuery("SELECT ora_inizio_turno, ruolo_giornaliero, count(distinct ref_dipendente) as num_dipendenti " +
                    "FROM `turni` WHERE data_odierna = '"+dataTurno+"' AND ora_inizio_turno != -1 and ref_dipendente != "+matricola +" "+
                    "GROUP BY ora_inizio_turno, ruolo_giornaliero");
            return  rs;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public void aggiornaInfo(int matricola, String email, String indirizzo, String cellulare){
        try{
            stm = connection.createStatement();
            if(matricola != 27) {
                stm.executeUpdate("UPDATE dipendenti SET email = '"+email+"', indirizzo = '"+indirizzo+"', cellulare = '"+cellulare+"' ");
            }else{
                stm.executeUpdate("UPDATE amministratori SET email = '"+email+"', indirizzo = '"+indirizzo+"', cellulare = '"+cellulare+"' ");
            }
        }catch (SQLException e){
            throw new RuntimeException (e);
        }
    }

    public LocalDate acquisisciDataTurnazione(){
        try{
            stm = connection.createStatement();
            rs = stm.executeQuery("SELECT data_comunicazione FROM comunicazioni c, comunicazioni_proposta_variazione cpv WHERE c.id_comunicazione = cpv.ref_comunicazione and cpv.ref_tipo_proposta_variazione = 8 and cpv.ref_comunicazione >= ALL ( SELECT   cpv2.ref_comunicazione FROM comunicazioni_proposta_variazione cpv2 WHERE   cpv2.ref_tipo_proposta_variazione = 8 );  ");
            rs.next();
            return rs.getDate("data_comunicazione").toLocalDate();
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    public void insertUtente(int matricola, String password){
        try {
            stm = connection.createStatement();

            String query = "INSERT INTO `utenti`(`matricola`,`password`) VALUES ('" + matricola +"','"+password +"')";
            stm.executeUpdate(query);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int insertComunicazionePV(int mittente, int destinatario, LocalDate oggi, LocalDateTime inizioPV, LocalDateTime finePV, String tipoPV, String messaggio ){
        int idComunicazione;
        int idTipoPV;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3308/azienda", "root", "");
            stm = connection.createStatement();

            rs = stm.executeQuery("select max(id_comunicazione) from comunicazioni");
            rs.next();
            idComunicazione = rs.getInt("max(id_comunicazione)") +1;
            insertComunicazione(idComunicazione , mittente, destinatario, oggi, "PV");

            rs = stm.executeQuery("select id_tipo_proposta_variazione from tipo_proposta_variazione  where nome = '" + tipoPV + "'");
            rs.next();
            idTipoPV = rs.getInt("id_tipo_proposta_variazione");

            String query = "INSERT INTO `comunicazioni_proposta_variazione` (`ref_comunicazione`, `ref_tipo_proposta_variazione`, `data_inizio_pv`, `data_fine_pv`, `data_scadenza`, `messaggio` ) " +
                    "VALUES('" +  idComunicazione + "','" +  idTipoPV + "','" + inizioPV + "','" + finePV + "','" + inizioPV.minusDays(5) + "','"+  messaggio +  "')" ;

            stm.executeUpdate(query);

            return idComunicazione;
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }// se pv è ferie aggiornare giorni ferie presi


    }

    private void insertComunicazione(int idComunicazione, int mittente, int destinatario, LocalDate oggi, String tipo_comunicazione){
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3308/azienda", "root", "");
            stm = connection.createStatement();
            String query = "INSERT INTO `comunicazioni` (`id_comunicazione`,`ref_utente_mittente`, `ref_utente_destinatario`, `data_comunicazione`, `tipo_comunicazione` ) " +
                    "VALUES('" + idComunicazione + "','" +  mittente + "','" + destinatario + "','" + oggi + "','" + tipo_comunicazione+ "')" ;
            stm.executeUpdate(query);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void creaNuovaComunicazioneCD(int mittente, int destinatario, LocalDate oggi, String messaggio){
        try{

            stm = connection.createStatement();
            rs = stm.executeQuery("SELECT MAX(id_comunicazione)    FROM comunicazioni ");
            rs.next();
            int idComunicazione = rs.getInt("MAX(id_comunicazione)") +1;
            insertComunicazione( idComunicazione, mittente, destinatario, oggi, "CD");

            String query = "INSERT INTO `comunicazioni_ufficio`(`ref_comunicazione`, `messaggio`) VALUES ("+ idComunicazione +",'"+ messaggio +"')";
            stm.executeUpdate(query);

        }catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public ResultSet acquisisciListaComuniazioni(int matricola){
        try {
            stm = connection.createStatement();
            rs = stm.executeQuery("SELECT * FROM comunicazioni  WHERE ref_utente_destinatario = " + matricola + "  ORDER BY flag_visualizzazione ASC, data_comunicazione DESC");
            return rs;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public ResultSet acquisisciComunicazione(int id, String tipoComunicazione, int matricola){
        try {
            stm = connection.createStatement();
            if(tipoComunicazione.equals("CD")) {
                rs = stm.executeQuery("SELECT * " +
                        "FROM comunicazioni c, utenti u, comunicazioni_ufficio cu " +
                        "WHERE  u.matricola = " + matricola + " and u.matricola = c.ref_utente_destinatario and c.id_comunicazione = cu.ref_comunicazione and c.id_comunicazione = " + id);

            }else if(tipoComunicazione.equals("PV")){
                rs = stm.executeQuery("SELECT * " +
                        "FROM comunicazioni c, utenti u, comunicazioni_proposta_variazione cpv, tipo_proposta_variazione tpv, stati_comunicazione sc " +
                        "WHERE  u.matricola = " + matricola + " and u.matricola = c.ref_utente_destinatario and c.id_comunicazione = cpv.ref_comunicazione and cpv.ref_tipo_proposta_variazione = tpv.id_tipo_proposta_variazione and c.ref_stato_comunicazione = sc.id_stato_comunicazione and c.id_comunicazione = " + id);
            }else{
                System.out.println("ERRORE ");
            }

            return rs;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public ResultSet acquisisciListaDipendenti(){
        try {
            stm = connection.createStatement();
            rs = stm.executeQuery("SELECT * FROM dipendenti ");
            return rs;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String acquisisciPassword(int matricola) {
        try {
            stm = connection.createStatement();
            rs = stm.executeQuery("SELECT * FROM utenti WHERE matricola = " + matricola );
            if(rs.next()) {
                return rs.getString("password");
            }else{
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void aggiornaVisualizzazione(int id){
        try {
            stm = connection.createStatement();
            stm.executeUpdate("UPDATE `comunicazioni` SET `flag_visualizzazione`= 1 WHERE id_comunicazione = " + id);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void eliminaDipendente(int matricola){
        try {
            stm = connection.createStatement();
            stm.executeUpdate("DELETE FROM agevolazioni_lavorative WHERE ref_dipendente = " + matricola + "; ");
            stm.executeUpdate("DELETE FROM cronologie_stipendi WHERE ref_dipendente = " + matricola + "; ");
            stm.executeUpdate("DELETE FROM turni WHERE ref_dipendente = " + matricola + "; ");
            stm.executeUpdate("DELETE FROM comunicazioni_proposta_variazione WHERE ref_comunicazione IN (SELECT cpv2.ref_comunicazione " +
                    "FROM comunicazioni c, comunicazioni_proposta_variazione cpv2 " +
                    "WHERE c.id_comunicazione = cpv2.ref_comunicazione and (c.ref_utente_mittente = " + matricola + " or c.ref_utente_destinatario =" + matricola +")); ");
            stm.executeUpdate("DELETE FROM comunicazioni_ufficio WHERE ref_comunicazione IN (SELECT cu2.ref_comunicazione " +
                    "FROM comunicazioni c, comunicazioni_ufficio cu2 " +
                    "WHERE c.id_comunicazione = cu2.ref_comunicazione and (c.ref_utente_mittente = " + matricola + " or c.ref_utente_destinatario =" + matricola +")); ");
            stm.executeUpdate("DELETE FROM comunicazioni WHERE ref_utente_mittente = " + matricola + " or ref_utente_destinatario = "+ matricola + "; ");
            stm.executeUpdate("DELETE FROM dipendenti WHERE matricola = " + matricola + "; ");
            stm.executeUpdate("DELETE FROM utenti WHERE matricola = "+ matricola + "; ");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void aggiornaInfoDip(int matricola, String nome, String cognome, String codFiscale, String email, String indirizzo, String cellulare, String note, int ruolo, int agevolazioni[]){
        try {
            stm = connection.createStatement();
            stm.executeUpdate("UPDATE `dipendenti` SET `nome`= '"+ nome +"',`cognome`='"+cognome+"',`cod_fiscale`= '"+ codFiscale +"',`email`= '"+ email +"',`indirizzo`= '"+ indirizzo +"'," +
                    "`cellulare`='"+ cellulare +"',`note`='"+note+"',`ref_ruolo`='"+ruolo+"' WHERE matricola = " + matricola);

            aggiornaAgevolazioni(matricola, agevolazioni);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }

    public void aggiornaAgevolazioni(int refDipendente, int[] agevolazioni){
        try {
            stm = connection.createStatement();
            stm.executeUpdate("DELETE FROM agevolazioni_lavorative WHERE ref_dipendente = " + refDipendente);
            for(int i=0; i< agevolazioni.length; i++) {
                stm.executeUpdate("INSERT INTO `agevolazioni_lavorative`(`ref_dipendente`, `ref_tipo_agevolazione_lavorativa`) VALUES ("+ refDipendente +","+ agevolazioni[i] + ")" );
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public double[] acquisisciInfoStipendio( int matricola){
        try {
            stm = connection.createStatement();
            rs = stm.executeQuery("SELECT r.base_stipendiale_oraria, r.tasso_incremento_stipendo_straordinario,  tal.tasso_incremento_stipendio_agevolazione "+
                    "FROM Dipendenti d, ruoli r, agevolazioni_lavorative al, tipi_agevolazioni_lavorative tal " +
                    "WHERE d.ref_ruolo = r.id_ruolo AND d.matricola = al.ref_dipendente AND al.ref_tipo_agevolazione_lavorativa = tal.id_tipo_agevolazione_lavorativa AND d.matricola = "+ matricola );

            double[] datiStipendio = new double[4];
            double tassoAgevolazione = 1;
            rs.next();
            datiStipendio[0] = rs.getDouble("base_stipendiale_oraria");
            datiStipendio[1] = rs.getDouble("tasso_incremento_stipendo_straordinario");

            do{
                tassoAgevolazione *= rs.getDouble("tasso_incremento_stipendio_agevolazione");
            }while (rs.next());

            datiStipendio[2] = tassoAgevolazione;

            rs = stm.executeQuery("SELECT SUM(ore_straordinarie_odierne) as straordinari "+
                    "FROM turni " +
                    "WHERE ref_dipendente = "+ matricola +" and data_odierna <= '"+ LocalDate.now() + "'");
            rs.next();
            datiStipendio[3] = rs.getDouble("straordinari");

            return datiStipendio;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int acquisisciMatricolaAmm() {
        try {
            stm = connection.createStatement();
            String query = "select matricola from amministratori";
            rs = stm.executeQuery(query);
            rs.next();
            return rs.getInt("matricola");
        }catch(SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int acquisisciOreEffettuate(int matricola){

        ResultSet rs;
        int inizioTurno;
        int fineTurno;
        int oreEffettuate=0;
        try {
            stm = connection.createStatement();
            rs = stm.executeQuery("SELECT   tt.ref_dipendente, tt.ora_fine_turno, tt.ora_inizio_turno, ore_straordinarie_odierne " +
                    "FROM      turni tt " +
                    "WHERE    tt.ref_dipendente = " + matricola + " and tt.data_odierna <= '" + LocalDate.now() + "' and tt.stato_giornaliero != 7 AND tt.stato_giornaliero != 4");  //ore che dobbiamo pagare
            while (rs.next()) {
                if(rs.getInt("ora_inizio_turno") != -1) {
                    if (rs.getInt("ora_inizio_turno") >= 18) {
                        inizioTurno = rs.getInt("ora_inizio_turno") - 24;
                    } else {
                        inizioTurno = rs.getInt("ora_inizio_turno");
                    }
                    fineTurno = rs.getInt("ora_fine_turno");

                    oreEffettuate += fineTurno - inizioTurno;
                }else{
                    oreEffettuate += 8;
                }
            }
            return oreEffettuate;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public ResultSet getDataStipendi(int matricola){
        try {
            stm = connection.createStatement();
            rs = stm.executeQuery("SELECT data_stipendio FROM cronologie_stipendi WHERE ref_dipendente = " + matricola);
            return rs;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public ResultSet acquisisciStipendioPassatoDip(int matricola, LocalDate dataStipendio) {
        try {
            stm = connection.createStatement();
            rs = stm.executeQuery("SELECT * FROM cronologie_stipendi WHERE ref_dipendente = " + matricola + " AND data_stipendio = '" + dataStipendio + "'");
            return rs;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public ResultSet acquisisciCronologiaStipendiDip(int matricola) {
        try {
            stm = connection.createStatement();
            rs = stm.executeQuery("SELECT * FROM cronologie_stipendi WHERE ref_dipendente = " + matricola );
            return rs;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public ResultSet acquisisciStraordinariDip(int matricola, LocalDate data){
        try {
            stm = connection.createStatement();
            rs = stm.executeQuery("SELECT ore_straordinarie_odierne FROM turni WHERE ref_dipendente = " + matricola + " AND data_odierna = '" + data + "'");
            return rs;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public ResultSet acquisisciTurno(LocalDate data) {
        try {
            stm = connection.createStatement();
            rs = stm.executeQuery("SELECT d.matricola, d.nome, d.cognome, t.ora_inizio_turno, t.ora_fine_turno FROM turni t, dipendenti d WHERE d.matricola = t.ref_dipendente AND data_odierna = '" + data + "'  ORDER BY t.ora_inizio_turno");
        } catch (SQLException e) {
        }

        return rs;
    }

    public ResultSet acquisisciTurnoOra(LocalDate data, int oraInizioTurno, int ruolo, int matricola) {
        try {
            stm = connection.createStatement();
            rs = stm.executeQuery("SELECT ref_dipendente, ora_fine_turno FROM turni WHERE data_odierna = '" + data + "' " +
                    "AND ora_inizio_turno = "+oraInizioTurno+ " AND ruolo_giornaliero = "+ruolo +" and ref_dipendente != "+matricola);
            return rs;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public ResultSet acquisisciTurnoDip(int matricola, LocalDate data) {
        try {
            stm = connection.createStatement();
            rs = stm.executeQuery("SELECT t.ref_dipendente, d.nome, d.cognome, t.ora_inizio_turno, t.ora_fine_turno , t.ruolo_giornaliero FROM turni t, dipendenti d WHERE d.matricola = t.ref_dipendente AND ref_dipendente = "+matricola+" AND data_odierna = '" + data + "'  ORDER BY t.ora_inizio_turno");
            return rs;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public ResultSet acquisisciTurnazioneDip(int matricola) {
        try {
            stm = connection.createStatement();
            rs = stm.executeQuery("SELECT * FROM turni WHERE ref_dipendente = "+matricola+"  ORDER BY data_odierna, ora_inizio_turno");
            return rs;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public ResultSet acquisisciListaPia(){
        try {
            stm = connection.createStatement();
            rs = stm.executeQuery("SELECT * FROM PIA");
            return rs;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void creaPIA(LocalDate inizioPIA, LocalDate finePIA, String messaggio){
        try {
            stm = connection.createStatement();
            stm.executeUpdate("INSERT INTO `pia`(`data_inizio_pia`, `data_fine_pia`, `descrizione`) VALUES ('"+inizioPIA+"','"+finePIA+"','"+messaggio+"')");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public void rimuoviPIA(LocalDate inizioPIA){
        try {
            stm = connection.createStatement();
            stm.executeUpdate("DELETE FROM `pia` WHERE data_inizio_pia = '" + inizioPIA +"'");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void aggiornaInfoUtente(int matricola, String email, String indirizzo, String cellulare){
        try {
            stm = connection.createStatement();
            if(matricola != 27) {
                stm.executeUpdate("UPDATE `dipendenti` SET `email`= '" + email + "',`indirizzo`= '" + indirizzo + "'," +
                        "`cellulare`='" + cellulare + "' WHERE matricola = " + matricola);
            }else{
                stm.executeUpdate("UPDATE `amministratori` SET `email`= '" + email + "',`indirizzo`= '" + indirizzo + "'," +
                        "`cellulare`='" + cellulare + "' WHERE matricola = " + matricola);
            }


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void aggiornaStatoPV(int idComunicazione, int stato){
        try{
            stm = connection.createStatement();
            stm.executeUpdate("UPDATE comunicazioni SET ref_stato_comunicazione = "+stato+ " WHERE id_comunicazione = "+idComunicazione);
        }catch (SQLException e){
            throw new RuntimeException (e);
        }
    }

    public void shiftTurnazione(LocalDate dataInizio){
        try {
            stm = connection.createStatement();
            rs = stm.executeQuery("SELECT distinct data_odierna FROM turni WHERE data_odierna >= '" + dataInizio + "' ");

            ArrayList<LocalDate> date = new ArrayList<>();
            while (rs.next()){
                date.add(rs.getDate("data_odierna").toLocalDate());
            }
            for(int i = date.size()-1 ; i>=0 ; i--) {

                stm.executeUpdate("UPDATE turni SET data_odierna = '" + date.get(i).plusDays(1) + "' WHERE data_odierna = '" + date.get(i) + "' ");

            }
            stm.executeUpdate("UPDATE turni SET data_odierna = '"+ dataInizio+"' WHERE data_odierna = '"+ dataInizio.plusMonths(3)+"'");
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    public void inserisciStipendio(int matricola, int oreEffettuate, int baseOraria, double tassoAgevolazione, double tassoStraordinario, int oreStraordinari, double stipendio){
        try{
            stm = connection.createStatement();
            stm.executeUpdate("INSERT INTO cronologie_stipendi(ref_dipendente, ore_effettuate, base_stipendiale_oraria, tasso_incremento_agevolazione, ore_straordinari, tasso_incremento_straordinari, totale_stipendio, data_stipendio) " +
                    "VALUES ('"+matricola+"','"+oreEffettuate+"','"+baseOraria+"','"+tassoAgevolazione+"','"+oreStraordinari+"','"+tassoStraordinario+"','"+stipendio+"','"+LocalDate.now().minusMonths(1)+"')");
        }catch(SQLException e){
            throw new RuntimeException(e);
        }
    }

    //---------------------------------------------------------------------- MIE

    public LocalDate acquisisciDataFineTurnazione(){
        try{
            stm = connection.createStatement();
            rs = stm.executeQuery("SELECT MAX(data_odierna) AS data_odierna  FROM turni");
            rs.next();
            return rs.getDate("data_odierna").toLocalDate();
        }catch (SQLException e){
            throw new RuntimeException (e);
        }
    }

    public LocalDate acquisisciDataInizioTurnazione(){
        try{
            stm = connection.createStatement();
            rs = stm.executeQuery("SELECT MIN(data_odierna) AS data_odierna FROM turni");
            rs.next();
            return rs.getDate("data_odierna").toLocalDate();
        }catch (SQLException e){
            throw new RuntimeException (e);
        }
    }

    public ResultSet acquisisciRichieste(int matricola){
        try {
            stm = connection.createStatement();
            rs = stm.executeQuery("SELECT cpv.ref_comunicazione, tpv.nome, cpv.data_inizio_pv, cpv.data_fine_pv, cpv.messaggio, c.ref_stato_comunicazione " +
                    "FROM tipo_proposta_variazione tpv, comunicazioni_proposta_variazione cpv, comunicazioni c, stati_comunicazione sc " +
                    "WHERE (cpv.ref_tipo_proposta_variazione = tpv.id_tipo_proposta_variazione) && (cpv.ref_comunicazione = c.id_comunicazione) " +
                    "&& (c.ref_stato_comunicazione = sc.id_stato_comunicazione) && (tpv.id_tipo_proposta_variazione != 8) && c.ref_utente_mittente = " + matricola + "; ");
            return rs;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public ResultSet acquisisciRichieste(){
        try {
            DBMSBound db = new DBMSBound();
            db.creaConnessione();

            stm = connection.createStatement();
            rs = stm.executeQuery("SELECT cpv.ref_comunicazione, tpv.nome, cpv.data_inizio_pv, cpv.data_fine_pv, cpv.messaggio, c.ref_stato_comunicazione " +
                    "FROM tipo_proposta_variazione tpv, comunicazioni_proposta_variazione cpv, comunicazioni c, stati_comunicazione sc " +
                    "WHERE (cpv.ref_tipo_proposta_variazione = tpv.id_tipo_proposta_variazione) && (cpv.ref_comunicazione = c.id_comunicazione) " +
                    "&& (c.ref_stato_comunicazione = sc.id_stato_comunicazione) && (tpv.id_tipo_proposta_variazione != 8) && c.ref_utente_mittente != " + db.acquisisciMatricolaAmm() + "; ");
            return rs;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public ResultSet acquisisciInfoComunicazione(int idComunicazione){
        try {
            stm = connection.createStatement();
            rs = stm.executeQuery("SELECT * FROM comunicazioni c WHERE c.id_comunicazione =" + idComunicazione + "; ");
            return rs;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int acquisisciPIARimanenti(){
        try {
            stm = connection.createStatement();
            rs = stm.executeQuery("SELECT rimanentiPIA FROM amministratori; ");
            rs.next();
            return rs.getInt("rimanentiPIA");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void aggiornaPIARimanenti(int rimanentiPIA){
        try{
            stm = connection.createStatement();
            stm.executeUpdate("UPDATE amministratori SET rimanentiPIA = "+ rimanentiPIA);
        }catch (SQLException e){
            throw new RuntimeException (e);
        }
    }

    public ResultSet acquisisciInfoRuolo(int ruolo){
        try {
            return stm.executeQuery("SELECT * FROM ruoli WHERE id_ruolo = " + ruolo + "; ");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void aggiornaOrePermesso(int matricola, int orePermesso){
        try{
            stm = connection.createStatement();
            stm.executeUpdate("UPDATE dipendenti SET ore_permessi_prese = " + orePermesso +  " WHERE matricola = " + matricola);
        }catch (SQLException e){
            throw new RuntimeException (e);
        }
    }

    public void aggiornaGiorniFerie(int matricola, int giorniFerie){
        try{
            stm = connection.createStatement();
            stm.executeUpdate("UPDATE dipendenti SET giorni_ferie_presi = " + giorniFerie +  " WHERE matricola = " + matricola);
        }catch (SQLException e){
            throw new RuntimeException (e);
        }
    }

    public ResultSet acquisisciListaUtenti(){
        try {
            stm = connection.createStatement();
            rs = stm.executeQuery("SELECT * FROM utenti ");
            return rs;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void aggiornaFlagIngresso(int matricola, LocalDate data, int ora){
        try{
            stm = connection.createStatement();
            stm.executeUpdate("UPDATE turni SET firma_ingresso = 1 WHERE ref_dipendente = " + matricola + " && data_odierna = '" + data + "' && ora_inizio_turno = " + ora + "; ");
        }catch (SQLException e){
            throw new RuntimeException (e);
        }
    }

    public void aggiornaFlagUscita(int matricola, LocalDate data, int ora){
        try{
            stm = connection.createStatement();
            stm.executeUpdate("UPDATE turni SET firma_uscita = 1 WHERE ref_dipendente = " + matricola + " && data_odierna = '" + data + "' && ora_fine_turno = " + ora + "; ");
        }catch (SQLException e){
            throw new RuntimeException (e);
        }
    }


}

