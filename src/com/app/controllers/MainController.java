package com.app.controllers;

import com.app.MainApplication.CFG;
import com.app.utils.DBControl;
import com.app.utils.Messages;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;
import java.sql.*;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class MainController {
    RoomController roomControl;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private VBox mainBox;

    @FXML
    private Button createRoomButton;

    @FXML
    private TextField inIpAddress;

    @FXML
    private TextField loginTextField;

    @FXML
    private PasswordField passwordTextField;

    @FXML
    private VBox roomMenu;

    @FXML
    private VBox authMenu;

    @FXML
    void onCreateButtonClick(ActionEvent event) throws IOException {
        // create socket
        final InetSocketAddress IP;
        try {
            IP = toInetSocketAddress(inIpAddress.getText());
        } catch (UnknownHostException e) {
            return;
        }

        ServerSocket serSocket = new ServerSocket(IP.getPort(), 50, IP.getAddress());
        Socket socket = serSocket.accept();
        InputStream in = socket.getInputStream(); OutputStream out = socket.getOutputStream();
        Stage stage = createRoom(out);
        Thread readThrd = createReadThrd(in);
        readThrd.setDaemon(true);
        readThrd.start();

        createRoomButton.getScene().getWindow().hide();
        stage.showAndWait();

        out.close(); in.close(); serSocket.close();
        readThrd.interrupt();
    }

    @FXML
    void onJoinButtonClick(ActionEvent event) throws IOException {
        final InetSocketAddress IP;
        try {
            IP = toInetSocketAddress(inIpAddress.getText());
        } catch (UnknownHostException e) {
            return;
        }

        // connect socket
        Socket socket;
        try {
            socket = new Socket(IP.getAddress(), IP.getPort());
        } catch (ConnectException e) {
            Alert error = new Alert(Alert.AlertType.ERROR, "Connection error!", ButtonType.OK);
            error.showAndWait();
            return;
        }

        InputStream in = socket.getInputStream(); OutputStream out = socket.getOutputStream();
        Stage stage = createRoom(out);
        Thread readThrd = createReadThrd(in);
        readThrd.setDaemon(true);
        readThrd.start();

        createRoomButton.getScene().getWindow().hide();
        stage.showAndWait();

        out.close(); in.close(); socket.close();
        readThrd.interrupt();
    }

    @FXML
    void onLogButtonClick(ActionEvent event) throws SQLException {
        String login = loginTextField.getText();
        String password = passwordTextField.getText();

        try (Connection connection = DriverManager.getConnection(CFG.DATABASE_URL,
                                                                CFG.DATABASE_LOGIN,
                                                                CFG.DATABASE_PASSWORD)) {
            Statement stm = connection.createStatement();

            if (DBControl.logUser(stm, login, password)) {
                authMenu.setDisable(true);
                roomMenu.setDisable(false);
            } else {
                showErrorAlert("Invalid username or password!");
                passwordTextField.setText("");
            }
        }
    }

    @FXML
    void onRegButtonClick(ActionEvent event) throws SQLException {
        String login = loginTextField.getText();
        String password = passwordTextField.getText();

        if (checkLogin(login) && checkPassword(password)) {
            try (Connection connection = DriverManager.getConnection(CFG.DATABASE_URL,
                                                                     CFG.DATABASE_LOGIN,
                                                                     CFG.DATABASE_PASSWORD)) {
                Statement stm = connection.createStatement();

                if (DBControl.addUser(stm, login, password)) {
                    System.out.println("Все ок");
                } else {
                    showErrorAlert("Invalid username or password!");
                }
            }
        } else {
            showErrorAlert("Enter a stronger password!");
        }
    }

    @FXML
    void initialize() {
        assert createRoomButton != null : "fx:id=\"createRoomButton\" was not injected: check your FXML file 'main.fxml'.";
        assert inIpAddress != null : "fx:id=\"inIpAddress\" was not injected: check your FXML file 'main.fxml'.";
    }

    Stage createRoom(OutputStream out) throws IOException {
        // create room window
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource(CFG.ROOM_WINDOW));
        loader.load();

        Stage stage = new Stage();
        stage.setTitle("Room");
        stage.setResizable(false);
        stage.setScene(new Scene(loader.getRoot()));
        this.roomControl = loader.getController();
        this.roomControl.setOut(out);
        return stage;
    }

    Thread createReadThrd(InputStream in) {
        return new Thread(() -> {
            Runnable updater = () -> roomControl.addMessage();

            while (true) {
                // UI update is run on the Application thread
                roomControl.setMessage(Messages.readMsg(in));
                Platform.runLater(updater);
            }
        });
    }

    InetSocketAddress toInetSocketAddress(String ipV4) throws UnknownHostException {
        if (ipV4.equals("CANCEL")) throw new UnknownHostException();
        String[] ip = ipV4.split(":");
        final InetSocketAddress IP;
        try {
            if(ip.length != 2)
                throw new UnknownHostException();

            int port = Integer.parseInt(ip[1]);
            IP = new InetSocketAddress(ip[0], port);
            if (IP.isUnresolved())
                throw new UnknownHostException();
        } catch (NumberFormatException | UnknownHostException | ArrayIndexOutOfBoundsException e) {
            Alert error = new Alert(Alert.AlertType.ERROR, "Invalid ip!", ButtonType.OK);
            error.showAndWait();
            throw new UnknownHostException();
        }
        return IP;
    }

    boolean checkLogin(String login) {
        return login.matches("^[\\dA-Za-z._\\-]*$");
    }

    void showErrorAlert(String contentText) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(contentText);
        alert.showAndWait();
    }

    boolean checkPassword(String password) {
        if (password.length() > 6)
            return password.matches("^[\\dA-Za-z._\\-]*$");
        return false;
    }
}