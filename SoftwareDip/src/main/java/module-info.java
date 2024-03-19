module com.example.softwaredip {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.sql;
    requires java.mail;


    opens com.example.softwaredip to javafx.fxml;
    exports com.example.softwaredip;
    exports com.example.softwaredip.gestioneaccount;
    opens com.example.softwaredip.gestioneaccount to javafx.fxml;
    exports com.example.softwaredip.gestionecomunicazioni;
    opens com.example.softwaredip.gestionecomunicazioni to javafx.fxml;
    exports com.example.softwaredip.gestionestipendi;
    opens com.example.softwaredip.gestionestipendi to javafx.fxml;
    exports com.example.softwaredip.gestioneturni;
    opens com.example.softwaredip.gestioneturni to javafx.fxml;
    exports com.example.softwaredip.gestionefirma;
    opens com.example.softwaredip.gestionefirma to javafx.fxml;
    exports com.example.softwaredip.commons;
    opens com.example.softwaredip.commons to javafx.fxml;
}