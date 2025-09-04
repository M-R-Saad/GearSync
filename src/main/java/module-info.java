module org.example.vehicle {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires java.sql;
    requires kernel;
    requires layout;
    requires itextpdf;

    opens org.example.vehicle to javafx.fxml;
    exports org.example.vehicle;
}