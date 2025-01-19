package com.labscs;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Vector;

import com.labscs.elements.*;
import com.labscs.services.*;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class AppClient extends Application {
    private final int WIDTH_ = 900;
    private final int HEIGHT_ = 600;

    private Vector<CatImg> images_;
    private Vector<CatText> texts_;

    private AppService appService_;
    private ClientService clientService_;

    private TextArea logs_;
    private TextField textField_;
    private Pane ui_;
    private Group view_;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage root) {
        this.images_ = new Vector<CatImg>();
        this.texts_ = new Vector<CatText>();
        this.ui_ = new Pane();
        this.view_ = new Group();
        this.appService_ = new AppService();
        this.clientService_ = new ClientService(WIDTH_, HEIGHT_, this, this.texts_, this.images_);

        this.logs_ = new TextArea();
        this.logs_.setMaxWidth(300);
        this.logs_.setEditable(false);

        this.textField_ = new TextField();
        this.textField_.setMaxWidth(200);

        setupUI();
        setupHandlers();
        runScene(root);
    }

    private void setupUI() {
        Button exit = new Button("Exit");
        exit.setOnAction(e -> Platform.exit());
        exit.setMinSize(300, 40);

        VBox clientButtons = this.clientService_.createClientButtons(this.view_);
        VBox serverButtons = this.clientService_.createServerButtons();
        HBox userButtons = new HBox(clientButtons, serverButtons);
        userButtons.setSpacing(2);

        VBox vbox = new VBox(exit, userButtons, this.logs_, this.textField_);
        vbox.setSpacing(10);
        vbox.setPadding(new Insets(5));
        vbox.setMinHeight(HEIGHT_);
        vbox.setStyle("-fx-background-color: rgba(64, 0, 64, 0.5);");

        this.ui_.getChildren().addAll(vbox, this.view_);

        try {
            this.ui_.setBackground(createBackground());
        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    private void setupHandlers() {
        EventHandler<MouseEvent> eventHandler = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                double x = e.getX();
                double y = e.getY();

                for (CatImg item : images_) {
                    if (item.contains(x, y)) {
                        item.selectItem();
                    }
                }

                for (CatText item : texts_) {
                    if (item.contains(x, y)) {
                        item.selectItem();
                    }
                }
            }
        };

        this.view_.addEventFilter(MouseEvent.MOUSE_CLICKED, eventHandler);
    }

    public void handleCommand(String command) {
        if (command == "objectList") {
            getObjectList();
        } else if (command == "sendObject") {
            sendObject();
        } else if (command == "indexI") {
            requestImage();
        } else if (command == "indexT") {
            requestText();
        } else if (command == "requestAll") {
            requestAll();
        } else if (command == "deleteT") {
            deleteObject(1);
        } else if (command == "deleteI") {
            deleteObject(0);
        }
    }

    private void appendLog(String msg) {
        this.logs_.appendText(msg);
    }

    private void runScene(Stage root) {
        Scene scene = new Scene(this.ui_, WIDTH_, HEIGHT_);
        root.setTitle("Client");
        root.setScene(scene);
        root.show();
    }

    private void deleteObject(int x) {
        String msg = "";
        String idx = this.textField_.getText();

        if (x == 1) {
            msg += "text_" + idx;
        }
        if (x == 0) {
            msg += "img_" + idx;
        }

        String obj = this.appService_.deleteObject(msg);
        appendLog("Server: " + obj + "\n");
    }

    private void requestAll() {
        String objects = getObjectList();
        String[] objArr = objects.split(":");

        for (String item : objArr) {
            String[] obj = item.split("_");
            int x = Integer.parseInt(obj[2]);
            int y = Integer.parseInt(obj[3]);

            if ("img".equals(obj[1])) {
                try {
                    CatImg img = new CatImg(x, y);
                    this.images_.add(img);
                    this.view_.getChildren().add(img.getElement());
                } catch (Exception err) {
                    err.printStackTrace();
                }
            }

            if ("text".equals(obj[1])) {
                CatText text = new CatText(x, y, Color.BLACK);
                this.texts_.add(text);
                this.view_.getChildren().add(text.getElement());
            }
        }
    }

    private void requestImage() {
        String idx = this.textField_.getText();
        String obj = this.appService_.requestImage(idx);
        String[] obj_split = obj.split("_");
        appendLog("Server: " + obj + "\n");

        int x = Integer.parseInt(obj_split[1]);
        int y = Integer.parseInt(obj_split[2]);

        try {
            CatImg img = new CatImg(x, y);
            this.images_.add(img);
            this.view_.getChildren().add(img.getElement());
        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    private void requestText() {
        String idx = this.textField_.getText();
        String obj = this.appService_.requestText(idx);
        String[] obj_split = obj.split("_");
        appendLog("Server: " + obj + "\n");

        int x = Integer.parseInt(obj_split[1]);
        int y = Integer.parseInt(obj_split[2]);

        CatText text = new CatText(x, y, Color.BLACK);
        this.texts_.add(text);
        this.view_.getChildren().add(text.getElement());
    }

    private String getObjectList() {
        String res = this.appService_.getObjectList();
        String[] objects = res.split(":");
        appendLog("Server: Objects count - " + objects.length + "\n");

        for (String item : objects) {
            appendLog("Server: obj " + item + "\n");
        }

        return res;
    }

    private void sendObject() {
        for (CatImg item : images_) {
            if (item.isSelected()) {
                String obj = "img_" + item.getX() + "_" + item.getY();
                String res = this.appService_.sendObject(obj);
                appendLog("Server: " + res + "\n");
            }
        }

        for (CatText item : texts_) {
            if (item.isSelected()) {
                String obj = "text_" + item.getX() + "_" + item.getY();
                String res = this.appService_.sendObject(obj);
                appendLog("Server: " + res + "\n");
            }
        }
    }

    private Background createBackground() throws FileNotFoundException {
        Image bgImg = new Image(new FileInputStream("C:/image2.png"));
        BackgroundSize bgSize = new BackgroundSize(this.WIDTH_, this.HEIGHT_, false, false, false, true);
        BackgroundImage bgImgView = new BackgroundImage(bgImg, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER, bgSize);
        Background bg = new Background(bgImgView);

        return bg;
    }
}
