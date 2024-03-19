module com.example.softwareamm {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.sql;
    requires mysql.connector.java;
    requires java.mail;


    opens com.example.softwareamm to javafx.fxml;
    exports com.example.softwareamm;

    exports com.example.softwareamm.gestioneaccount;
    opens com.example.softwareamm.gestioneaccount to javafx.fxml;

    exports com.example.softwareamm.gestionecomunicazioni;
    opens com.example.softwareamm.gestionecomunicazioni to javafx.fxml;

    exports com.example.softwareamm.gestionestipendi;
    opens com.example.softwareamm.gestionestipendi to javafx.fxml;

    exports com.example.softwareamm.gestioneturni;
    opens com.example.softwareamm.gestioneturni to javafx.fxml;

    exports com.example.softwareamm.commons;
    opens com.example.softwareamm.commons to javafx.fxml;

    opens com.example.softwareamm.gestionedipendenti to javafx.fxml;
    exports com.example.softwareamm.gestionedipendenti;

    exports com.example.softwareamm.entity;
    opens com.example.softwareamm.entity to javafx.fxml;
}