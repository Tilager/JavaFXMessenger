package com.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

public class MainApplication extends Application {
    public static class CFG {
        static public final String MAIN_WINDOW = "/resources/gui/main.fxml";
        static public final String ROOM_WINDOW = "/resources/gui/room.fxml";
        static public final String DATABASE_URL = "jdbc:mysql://localhost:3306/javamessanger";
        static public final String DATABASE_LOGIN = "";
        static public final String DATABASE_PASSWORD = "";
    }

    @Override
    public void start(Stage stage) throws Exception {

        FXMLLoader root = new FXMLLoader(getClass().getResource(CFG.MAIN_WINDOW));
        Scene scene = new Scene(root.load());

        stage.setTitle("Messenger");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out), true, StandardCharsets.UTF_8));
        launch();
    }
}

