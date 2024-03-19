package com.example.softwareamm;

import com.example.softwareamm.commons.DBMSBound;
import com.example.softwareamm.commons.Utils;
import com.example.softwareamm.controls.TimeCtrl;
import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**Classe principale di avvio del software
 * @version 1.0*/
public class Main extends Application {
    private final static int SOGLIA_MINUTI = 10;
    private static ScheduledExecutorService executorService;

    /**Stage principale del software*/
    public static Stage mainStage;

    /**Crea e mostra lo stage principale del software con la schermata di accesso*/
    @Override
    public void start(Stage stage) throws IOException {
        Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("Icone/logo/iconaLogoBlu2.png")));
        stage.getIcons().add(icon);
        Main.mainStage = stage;

        Utils.mostraScena(Main.mainStage, "Accesso", "gestioneaccount/AccessoWin.fxml");

        stage.show();
    }

    /**Avvia il software e il clock delle azioniPeriodiche*/
    public static void main(String[] args) {
        System.out.println("\033[0;97m" + "[AVVIO]\n");

        System.out.println(Utils.formattaData(LocalDate.now()));

        avviaClock();
        launch();
        interrompiClock();

        System.out.println("[CHIUSURA]");
    }

    private static void avviaClock(){
        LocalTime ora = LocalTime.now();
        int delay
                ;
        if(ora.getMinute() < SOGLIA_MINUTI){
            delay = SOGLIA_MINUTI - ora.getMinute();
        } else {
            delay = 60 + SOGLIA_MINUTI - ora.getMinute();
        }

        delay = (delay*60) - ora.getSecond();

        System.out.println("\n----------------------------" +
                "\nClock: AVVIATO" +
                "\nData: " + LocalDate.now() +
                "\nOra: " + ora.getHour() + ":" + ora.getMinute() + ":" + ora.getSecond() +
                "\nDelay: " + delay + " secondi" +
                "\n----------------------------\n"
        );

        executorService  = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(Main::azioniPeriodiche, delay, 3600, TimeUnit.SECONDS);
    }

    private static void interrompiClock(){
        executorService.close();

        LocalTime ora = LocalTime.now();

        System.out.println("\n----------------------------" +
                "\nClock: INTERROTTO" +
                "\nData: " + LocalDate.now() +
                "\nOra: " + ora.getHour() + ":" + ora.getMinute() + ":" + ora.getSecond() +
                "\n----------------------------\n"
        );
    }

    private static void azioniPeriodiche()  {
        LocalTime ora = LocalTime.now();
        System.out.println("\n----------------------------" +
                "\nControllo periodico del tempo" +
                "\nData: " + LocalDate.now() +
                "\nOra: " + ora.getHour() + ":" + ora.getMinute() + ":" + ora.getSecond() +
                "\n----------------------------\n"
        );

        /*Inserire funzioni periodiche da eseguire*/
        TimeCtrl tc = new TimeCtrl();
        DBMSBound db = new DBMSBound();
        tc.turnazioneTriemstrale();
        ResultSet dip = db.acquisisciListaDipendenti();
        try {
            while (dip.next()) {

                tc.calcoloStipendio(dip.getInt("matricola"));
            }
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
        tc.comunicazioneRitardo();
        tc.ComunicazioneDimenticanza();
        tc.scadenzaPV();
        tc.ripristinoSoglie();
        tc.eliminaTurnazionePrecedente();
    }
}