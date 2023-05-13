module com.smallplayz.hackathon2023e {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    requires java.desktop;

    opens com.smallplayz.hackathon2023e to javafx.fxml;
    exports com.smallplayz.hackathon2023e;
}