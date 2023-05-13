package com.smallplayz.hackathon2023e;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.kordamp.bootstrapfx.BootstrapFX;

import java.io.IOException;

public class Code extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Code.class.getResource("Scene.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        //Image logo = new Image("");
        //stage.getIcons().add(logo);
        stage.setTitle("Infinite Storage");
        stage.setScene(scene);
        scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
        stage.show();

    }

    public static void main(String[] args) {
        launch();
    }
}