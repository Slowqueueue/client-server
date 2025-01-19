package com.labscs.elements;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

public class CatImg extends AbstractClass {
    private Image img_;
    private ImageView imgView_;

    public CatImg(int x, int y) throws FileNotFoundException {
        super(x, y, null);
        this.img_ = new Image(new FileInputStream("C:/image.png"));
        this.imgView_ = new ImageView(this.img_);
        this.imgView_.setFitWidth(80);
        this.imgView_.setPreserveRatio(true);
        this.bpane_.setCenter(imgView_);

        setupAnimation();
    }

    private void setupAnimation() {
        KeyValue initRotate = new KeyValue(this.bpane_.rotateProperty(), 0);
        KeyFrame initFrame = new KeyFrame(Duration.ZERO, initRotate);
        KeyValue endRotate = new KeyValue(this.bpane_.rotateProperty(), 360);
        KeyFrame endFrame = new KeyFrame(Duration.seconds(5), endRotate);

        this.timeline_ = new Timeline(initFrame, endFrame);
        this.timeline_.setCycleCount(Timeline.INDEFINITE);

        startAnimation();
    }
}
