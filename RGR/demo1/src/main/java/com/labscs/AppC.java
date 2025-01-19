package com.labscs;
import javafx.application.Application;
import javafx.stage.Stage;

public class AppC extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage root) {
            System.out.println("Starting client...");
            new AppClient().start(new Stage());
        }
    }