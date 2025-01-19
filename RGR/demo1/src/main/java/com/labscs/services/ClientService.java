package com.labscs.services;

import com.labscs.AppClient;
import com.labscs.elements.*;

import java.util.Random;
import java.util.Vector;

import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class ClientService {
    final int WIDTH_;
    final int HEIGHT_;
    private AppClient app_;
    Vector<CatText> texts_;
    Vector<CatImg> images_;

    public ClientService(int w, int h, AppClient app, Vector<CatText> texts, Vector<CatImg> images) {
        this.WIDTH_ = w;
        this.HEIGHT_ = h;
        this.app_ = app;

        this.texts_ = texts;
        this.images_ = images;
    }

    public VBox createServerButtons() {
        VBox vbox = new VBox();
        vbox.setSpacing(4);

        Button objectList = new Button("Object List");
        Button sendObject = new Button("Send object");
        Button requestImage = new Button("Request Img");
        Button requestText = new Button("Request Text");
        Button requestAll = new Button("Request all");
        Button deleteImg = new Button("Delete Img");
        Button deleteText = new Button("Delete Text");

        objectList.setMinSize(150, 25);
        sendObject.setMinSize(150, 25);
        requestAll.setMinSize(150, 25);
        requestImage.setMinSize(150, 25);
        requestText.setMinSize(150, 25);
        deleteImg.setMinSize(150, 25);
        deleteText.setMinSize(150, 25);

        objectList.setOnAction(e -> this.app_.handleCommand("objectList"));
        sendObject.setOnAction(e -> this.app_.handleCommand("sendObject"));
        requestAll.setOnAction(e -> this.app_.handleCommand("requestAll"));
        requestImage.setOnAction(e -> this.app_.handleCommand("indexI"));
        requestText.setOnAction(e -> this.app_.handleCommand("indexT"));
        deleteImg.setOnAction(e -> this.app_.handleCommand("deleteI"));
        deleteText.setOnAction(e -> this.app_.handleCommand("deleteT"));

        vbox.getChildren().addAll(objectList, sendObject, requestAll, requestImage, requestText, deleteImg, deleteText);
        return vbox;
    }

    public VBox createClientButtons(Group view) {
        VBox vbox = new VBox();
        vbox.setSpacing(4);

        Button btnText = new Button("Create Text");
        Button btnImg = new Button("Create Image");
        Button btnDeleteAll = new Button("Delete All");
        Button btnStopAll = new Button("Stop All");
        Button btnStartAll = new Button("Start All");
        Button btnStart = new Button("Start");
        Button btnDelete = new Button("Delete");
        Button btnStop = new Button("Stop");

        btnText.setMinSize(150, 25);
        btnImg.setMinSize(150, 25);
        btnDeleteAll.setMinSize(150, 25);
        btnStopAll.setMinSize(150, 25);
        btnStartAll.setMinSize(150, 25);
        btnStart.setMinSize(150, 25);
        btnStop.setMinSize(150, 25);
        btnDelete.setMinSize(150, 25);

        vbox.getChildren().addAll(btnImg, btnText, btnDeleteAll, btnStopAll, btnStartAll, btnDelete, btnStop, btnStart);

        btnText.setOnAction(e -> createText(view, this.texts_, -1, -1));
        btnImg.setOnAction(e -> createImg(view, this.images_, -1, -1));

        btnDeleteAll.setOnAction(e -> deleteAll(view, this.images_, this.texts_));

        btnStopAll.setOnAction(e -> {
            for (CatImg item : images_)
                item.stopAnimation();

            for (CatText item : texts_)
                item.stopAnimation();
        });

        btnStartAll.setOnAction(e -> {
            for (CatImg item : images_)
                item.startAnimation();

            for (CatText item : texts_)
                item.startAnimation();
        });

        btnDelete.setOnAction(e -> {
            boolean clear = false;

            while (!clear) {
                boolean broken = false;

                for (CatImg item : images_) {
                    if (item.isSelected()) {
                        view.getChildren().remove(item.getElement());
                        images_.remove(item);
                        broken = true;
                        break;
                    }
                }

                if (!broken) {
                    clear = true;
                }
            }

            clear = false;

            while (!clear) {
                boolean broken = false;

                for (CatText item : texts_) {
                    if (item.isSelected()) {
                        view.getChildren().remove(item.getElement());
                        texts_.remove(item);
                        broken = true;
                        break;
                    }
                }

                if (!broken) {
                    clear = true;
                }
            }

        });

        btnStop.setOnAction(e -> {
            for (CatImg item : images_) {
                if (item.isSelected()) {
                    item.stopAnimation();
                }
            }

            for (CatText item : texts_) {
                if (item.isSelected()) {
                    item.stopAnimation();
                }
            }
        });

        btnStart.setOnAction(e -> {
            for (CatImg item : images_) {
                if (item.isSelected()) {
                    item.startAnimation();
                }
            }

            for (CatText item : texts_) {
                if (item.isSelected()) {
                    item.startAnimation();
                }
            }
        });

        return vbox;
    }

    public void createText(Group view, Vector<CatText> texts, int x, int y) {
        CatText catText;

        if (x == y && x == -1) {
            Random random = new Random();
            catText = new CatText(random.nextInt(WIDTH_ - 450) + 320, random.nextInt(HEIGHT_ - 175) + 50,
                    Color.CRIMSON);
        } else {
            catText = new CatText(x, y, Color.DEEPPINK);
        }

        texts.add(catText);
        view.getChildren().add(catText.getElement());
    }

    public void createImg(Group view, Vector<CatImg> images, int x, int y) {
        try {
            CatImg catImg;

            if (x == y && x == -1) {
                Random random = new Random();
                catImg = new CatImg(random.nextInt(WIDTH_ - 450) + 320, random.nextInt(HEIGHT_ - 175) + 50);
            } else {
                catImg = new CatImg(x, y);
            }

            images.add(catImg);
            view.getChildren().add(catImg.getElement());

        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    public void deleteAll(Group view, Vector<CatImg> images, Vector<CatText> texts) {
        for (CatImg item : images) {
            view.getChildren().remove(item.getElement());
        }
        images_.clear();

        for (CatText item : texts) {
            view.getChildren().remove(item.getElement());
        }
        texts_.clear();
    }

}
