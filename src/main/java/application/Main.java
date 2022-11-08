package application;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.scene.text.*;
import javafx.util.Duration;

import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Scanner;

public class Main extends Application {
    static Client client;
    static Stage primaryStage;
    private HashMap<String, Pane> screens = new HashMap<>();
    int width = 600, height = 400;

    public void setupSceneWithText(String name, String content) {
        StackPane layout = new StackPane();
        Text text = new Text();
        text.setFont(new Font(20));
        text.setText(content);
        layout.getChildren().add(text);
        screens.put(name, layout);
    }

    public Scene getScene(String name) {
        return new Scene(screens.get(name), width, height);
    }

    public void setupScenes() {
        setupSceneWithText("connectServerScene", "Connecting to server...");
        setupSceneWithText("connectPlayerScene", "Connecting to player...");
        setupSceneWithText("errorScene", "Sorry, error occurred. \n Please restart game.");
    }

    public void serverConnected() {
        primaryStage.setScene(getScene("connectPlayerScene"));
        client.connectPlayer();
    }

    public void playerConnected() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getClassLoader().getResource("mainUI.fxml"));
            Pane root = fxmlLoader.load();
            primaryStage.setScene(new Scene(root));
            primaryStage.setResizable(false);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void closeAll() {
        primaryStage.setScene(getScene("errorScene"));
    }

    @Override
    public void start(Stage primaryStage) throws InterruptedException {
        setupScenes();
        primaryStage.setTitle("Tic Tac Toe");
        primaryStage.setResizable(false);
        primaryStage.setScene(getScene("connectServerScene"));
        this.primaryStage = primaryStage;
        primaryStage.show();
        client = new Client(this, 9999);
        client.connect();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
