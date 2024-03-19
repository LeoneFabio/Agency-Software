module com.example.softwareazienda {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    exports com.example.softwareazienda;
    opens com.example.softwareazienda to javafx.fxml;

    exports com.example.softwareazienda.gestionefirma;
    opens com.example.softwareazienda.gestionefirma to javafx.fxml;
}