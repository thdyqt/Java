module com.example.javaproject_102240130_102240141 {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    requires mysql.connector.j;
    requires java.desktop;
    requires jbcrypt;

    opens Application to javafx.graphics, javafx.fxml;
    exports Application;
    opens Controller to javafx.fxml;
    exports Controller;
}