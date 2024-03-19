package com.example.softwareamm.commons;

import com.example.softwareamm.Main;
import com.example.softwareamm.controls.TurnazioneCtrl;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;

/**Calendario con grafica e pulsanti di navigazione, contenente i turni
 * di un trimestre di uno specifico dipendente
 * @version 1.0
 * */
public class Calendario {

    /**Nome italiani dei mesi*/
    public static final String[] italianMonth = {"Gennaio", "Febbraio", "Marzo", "Aprile", "Maggio", "Giugno", "Luglio", "Agosto", "Settembre", "Ottobre", "Novembre", "Dicembre"};

    /**Nome italiani dei giorni settimanali*/
    public static final String[] italianDays = {"Lunedì", "Martedì", "Mercoledì", "Giovedì", "Venerdì", "Sabato", "Domenica"};

    private int mode = 0;

    /**Lista di tutti i turni del dipendente passato come parametro alla creazione del calendario*/
    public ResultSet turniDipendente;
    private VBox vBox;
    private HBox filter;
    private HBox header;
    private GridPane gridPane;
    private Button nextMonthButton;
    private Button prevMonthButton;
    private Label monthLabel;
    private LocalDate today;
    private LocalDate dateReference;
    private int monthIndex = 1;

    /**Permette la creazione del calendario grafico
     * @param data Data del giorno che si ritiene attuale
     * (oltre a questo dovrebbe anche essere restituito il Resulset dei turni)
     * @see TurnazioneCtrl TurnazioneCtrl
     * */

    public Calendario(LocalDate data, ResultSet turniDipendente){
        this.turniDipendente = turniDipendente;
        today = data;
        dateReference = data;
        initialize();
        buildCalendar();

        //Assegna al VBox principale tutti gli elmeenti
        vBox.getChildren().addAll(filter, header, gridPane);
    }

    private void initialize(){
        //Creazione delle componenti
        vBox = new VBox();
        header = new HBox();
        filter = new HBox();
        gridPane = new GridPane();
        nextMonthButton = new Button();
        prevMonthButton = new Button();
        monthLabel = new Label();

        //Impostazioni delle componenti

        //Vbox
        vBox.setAlignment(Pos.CENTER);
        vBox.setSpacing(5);
        vBox.getStylesheets().add(Main.class.getResource("styleSheet.css").toExternalForm());

        //Altri
        setFilter();
        setHeader();
        setGridPane();
    }

    /**Permette di ottenere il calendario creato
     * @return Ritorna il Pane contenente il calendario
     * */
    public VBox getCalendar(){
        return vBox;
    }

    private void setFilter(){
        ChoiceBox<String> choiceBox = new ChoiceBox<>();
        choiceBox.setStyle("-fx-border-color: #003db0");
        choiceBox.setMinWidth(120);
        //--------- Elementi di prova
        choiceBox.getItems().addAll("Turni", "Straordinari");
        //--------- Impostazioni choiceBox
        choiceBox.setValue("Turni");

        choiceBox.getSelectionModel().selectedIndexProperty().addListener((observableValue, number, number2) -> {
            mode = choiceBox.getSelectionModel().getSelectedIndex();
            buildCalendar();

        }
        );


        filter.getChildren().add(choiceBox);

        //Impostazioni filter
        filter.setAlignment(Pos.CENTER_RIGHT);

        filter.setMaxHeight(Region.USE_PREF_SIZE);
        filter.setMinHeight(Region.USE_PREF_SIZE);
        filter.setPrefHeight(40);

        filter.setMaxWidth(Region.USE_PREF_SIZE);
        filter.setMinWidth(Region.USE_PREF_SIZE);
        filter.setPrefWidth(730);
    }

    private void setHeader(){

        //Etichetta del mese che si sta visualizzando sul calendario
        //monthLabel.setText(dateReference.getMonth().toString() + " " + dateReference.getYear());
        monthLabel.setAlignment(Pos.CENTER);
        monthLabel.getStyleClass().add("headerLabel");

        //Style della header
        header.getStyleClass().add("header");

        //Inserimento delle componenti nella header
        header.getChildren().addAll(prevMonthButton, monthLabel, nextMonthButton);

        //Impostazioni header
        header.setAlignment(Pos.CENTER);
        header.setSpacing(10);

        header.setMaxHeight(Region.USE_PREF_SIZE);
        header.setMinHeight(Region.USE_PREF_SIZE);
        header.setPrefHeight(40);

        header.setMaxWidth(Region.USE_PREF_SIZE);
        header.setMinWidth(Region.USE_PREF_SIZE);
        header.setPrefWidth(730);

        //Impostazioni dei pulsanti
        setKeys();
    }

    //Imposta le funziolanità dei pulsanti
    private void setKeys(){
        Image img1 = new Image(Main.class.getResource("Icone/freccia-destra.png").toString());
        ImageView view1 = new ImageView(img1);
        view1.setFitWidth(20);
        view1.setFitHeight(20);
        nextMonthButton.setGraphic(view1);

        nextMonthButton.getStyleClass().add("calendarBtn");

        nextMonthButton.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
            gridPane.getChildren().clear();
            dateReference = dateReference.plusMonths(1);
            buildCalendar();
            monthIndex++;

           /*prevMonthButton.setDisable(false);

            if(monthIndex == 2){
                nextMonthButton.setDisable(true);
            }*/
        });


        Image img2 = new Image(Main.class.getResource("Icone/freccia-sinistra.png").toString());
        ImageView view2 = new ImageView(img2);
        view2.setFitWidth(20);
        view2.setFitHeight(20);

        prevMonthButton.setGraphic(view2);

        prevMonthButton.getStyleClass().add("calendarBtn");

        prevMonthButton.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
            gridPane.getChildren().clear();
            dateReference = dateReference.minusMonths(1);
            buildCalendar();
            monthIndex--;
/*
            nextMonthButton.setDisable(false);

            if(monthIndex == 0){
                prevMonthButton.setDisable(true);
            }*/
        });
    }

    private void setGridPane(){
        //Impostazioni gridPane
        gridPane.setMaxWidth(Region.USE_PREF_SIZE);
        gridPane.setMinWidth(Region.USE_PREF_SIZE);
        gridPane.setPrefWidth(Region.USE_COMPUTED_SIZE);

        gridPane.setMaxHeight(Region.USE_PREF_SIZE);
        gridPane.setMinHeight(Region.USE_PREF_SIZE);
        gridPane.setPrefHeight(510);

        gridPane.setGridLinesVisible(false);

        //Style gridPane
        gridPane.getStyleClass().add("grid");
    }

    //Crea un elemento giorno per il calendario
    private VBox createDay(String numberOfDay, String turn, boolean today , boolean none){
        VBox day = new VBox();
        day.getStyleClass().add("day");

        Label numberOfDayLabel = new Label(numberOfDay);

        //Se none = true allora i turni del giorno che si vuole creare non devono essere visualizzati
        //Se today = true allora il numero e il bordo del giorno devono essere blu
        //essere visualizzati

        if(none){
            numberOfDayLabel.getStyleClass().add("numberDayLabelNone");
            day.setStyle("-fx-background-color: #c9c9c9");
            day.getChildren().addAll(numberOfDayLabel);

        } else {
            Label turnLabel = new Label(turn);
            numberOfDayLabel.getStyleClass().add("numberDayLabel");
            if(today){
                day.setStyle("-fx-border-color: #003db0");
                numberOfDayLabel.setStyle("-fx-text-fill: #003db0");
            }
            turnLabel.getStyleClass().add("turnLabel");
            day.getChildren().addAll(numberOfDayLabel, turnLabel);
        }

        return day;
    }

    private void buildCalendar(){

        //Modifichiamo l'etichetta nell'header con il mese attuale
        monthLabel.setText(italianMonth[dateReference.getMonth().getValue()-1] + " " + dateReference.getYear());

        //Prelevamento di informazioni utili nella gestione del calendario
        int dayOfMouth = dateReference.getDayOfMonth();
        int mouthLenght = dateReference.lengthOfMonth();
        LocalDate firstDayOfMouth = dateReference.minusDays(dayOfMouth-1);
        LocalDate lastDayOfMouth = firstDayOfMouth.plusDays(mouthLenght-1);

        //Primo lunedi del calendario
        LocalDate firstMondayOfCalendar;

        if(firstDayOfMouth.getDayOfWeek() != DayOfWeek.MONDAY){
            firstMondayOfCalendar = firstDayOfMouth.minusDays(firstDayOfMouth.getDayOfWeek().getValue() - 1);
        } else {
            firstMondayOfCalendar = firstDayOfMouth;
        }

        //Ultima domenica del calendario
        LocalDate lastSundayOfCalendar = lastDayOfMouth;

        if(lastDayOfMouth.getDayOfWeek() != DayOfWeek.SUNDAY){
            while(lastSundayOfCalendar.getDayOfWeek() != DayOfWeek.SUNDAY){
                lastSundayOfCalendar = lastSundayOfCalendar.plusDays(1);
            }
        }

        //Numero di giorni nel calendario
        LocalDate temp = firstMondayOfCalendar;
        int daysInOurCalendar = 1;
        while(!temp.equals(lastSundayOfCalendar)){
            temp = temp.plusDays(1);
            daysInOurCalendar++;
        }

        // Creazione del calendario
        int weekCount = daysInOurCalendar / 7;

        LocalDate dateIterator = firstMondayOfCalendar;

        for(int j = 0; j<weekCount + 1; j++){
            for(int i = 0; i<7; i++){
                if(j==0){
                    //Nome del giorno settimanale
                    Label dayLabel = new Label();
                    dayLabel.setText(getDayName(i));
                    dayLabel.getStyleClass().add("nameDayLabel");
                    gridPane.add(dayLabel,i, j);
                } else {
                    //Inserimento dati giorno
                    boolean none = false;
                    if(dateIterator.getMonth() == dateReference.getMonth()){
                        none = false;
                    } else {
                        none = true;
                    }

                    String numberOfDay = Integer.toString(dateIterator.getDayOfMonth());
                    VBox day = createDay(numberOfDay, prelevaInfoTurno(dateIterator, mode), dateIterator.equals(today), none);

                    gridPane.add(day,i, j);
                    dateIterator = dateIterator.plusDays(1);
                }
            }
        }
    }

    private String prelevaInfoTurno(LocalDate data, int tipoInfo){

        //STRING BUILDER
        StringBuilder strBr = new StringBuilder();
        try {
            turniDipendente.beforeFirst();
            while(turniDipendente.next()){
                LocalDate dataTurno = turniDipendente.getDate("data_odierna").toLocalDate();
                if(dataTurno.equals(data)){

                    switch(tipoInfo){
                        case 0 ->{
                            int oraIngresso = turniDipendente.getInt("ora_inizio_turno");
                            int oraUscita = turniDipendente.getInt("ora_fine_turno");
                            if(oraIngresso != -1 && oraUscita != -1){
                                strBr.append(oraIngresso + ":00 - " + oraUscita + ":00\n");
                            }
                        }

                        case 1 ->{
                            int oreStraordinari = turniDipendente.getInt("ore_straordinarie_odierne");
                            if(oreStraordinari != 0){
                                strBr.append(oreStraordinari + " ore\n");
                            }
                        }
                    }
                }
            }

            return strBr.toString();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static String getDayName(int i){

        String dayName;

        switch (i) {
            case 0 -> {
                dayName = "Lunedì";
            }
            case 1 -> {
                dayName = "Martedì";
            }
            case 2 -> {
                dayName = "Mercoledì";
            }
            case 3 -> {
                dayName = "Giovedì";
            }
            case 4 -> {
                dayName = "Venerdì";
            }
            case 5 -> {
                dayName = "Sabato";
            }
            case 6 -> {
                dayName = "Domenica";
            }
            default -> {
                System.out.println("Errore prelevamento nome giorno");
                dayName = "Errore";
            }
        }

        return dayName;
    }
}
