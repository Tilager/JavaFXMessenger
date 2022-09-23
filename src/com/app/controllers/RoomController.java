package com.app.controllers;

import java.io.OutputStream;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;

import com.app.utils.Messages;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;

public class RoomController {
    OutputStream out = null;
    String add_message = null;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private ScrollPane chatScrollPane;

    @FXML
    private VBox chat;

    @FXML
    private TextField message;

    @FXML
    void initialize() {

    }

    @FXML
    void onSendButtonClick(ActionEvent action) {
        String msg = message.getText();
        if (msg.strip().equals("")) return;
        Label labelMsg = new Label(msg);
        labelMsg.setFont(Font.font("Cascadia Mono", FontPosture.REGULAR, 18));
        labelMsg.setTextFill(Paint.valueOf("WHITE"));
        labelMsg.getStyleClass().add("rounded-send-label");

        HBox messageBox = new HBox();
        messageBox.setAlignment(Pos.CENTER_RIGHT);
        messageBox.getChildren().add(labelMsg);

        chat.getChildren().add(messageBox);

        chatScrollPane.applyCss();
        chatScrollPane.layout();
        chatScrollPane.setVvalue(1.0);

        message.setText("");
        Messages.sendMsg(out, msg);
    }

    public void setOut (OutputStream output) { out = output; }

    public void setMessage (String msg) { add_message = msg; }

    public void addMessage() {
        if (add_message.strip().equals("")) return;

        Label labelMsg = new Label(add_message);
        labelMsg.setFont(Font.font("Cascadia Mono", FontPosture.REGULAR, 18));
        labelMsg.setTextFill(Paint.valueOf("WHITE"));
        labelMsg.getStyleClass().add("rounded-add-label");

        HBox messageBox = new HBox();
        messageBox.getChildren().add(labelMsg);

        chat.getChildren().add(messageBox);

        chatScrollPane.applyCss();
        chatScrollPane.layout();
        chatScrollPane.setVvalue(1.0);
    }
}
