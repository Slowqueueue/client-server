package com.labscs.elements;
import java.io.DataInputStream;
import java.io.DataOutputStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javafx.animation.Timeline;
import javafx.geometry.Bounds;

import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;

public abstract class AbstractClass {
    private boolean selected_ = false;
    private int x_, y_;
    private Color color_;

    protected BorderPane bpane_;
    protected Timeline timeline_;

    public AbstractClass(int x, int y, Color color) {
        this.bpane_ = new BorderPane();
        this.x_ = x;
        this.y_ = y;
        this.color_ = color;

        this.bpane_.setLayoutX(x);
        this.bpane_.setLayoutY(y);
    }

    public BorderPane getElement() {
        return bpane_;
    }

    public boolean isSelected() {
        return this.selected_;
    }

    public boolean contains(double x, double y) {
        return this.bpane_.getBoundsInParent().contains(x, y);
    }

    public Bounds getBoundsInParent() {
        return this.bpane_.getBoundsInParent();
    }

    public int getX() {
        return this.x_;
    }

    public int getY() {
        return this.y_;
    }

    public void startAnimation() {
        this.timeline_.play();
    }

    public void stopAnimation() {
        this.timeline_.pause();
    }

    public void selectItem() {
        this.selected_ = !this.selected_;

        if (this.selected_) {
            this.bpane_.setStyle("-fx-border-color: black;");
        } else {
            this.bpane_.setStyle("-fx-border-color: transparent;");
        }
    }

    public DataInputStream rStream(InputStream is) throws IOException {
        DataInputStream data = new DataInputStream(is);

        this.x_ = data.readInt();
        this.y_ = data.readInt();

        int red = data.readInt();
        int green = data.readInt();
        int blue = data.readInt();

        this.color_ = Color.rgb(red, green, blue);
        return data;
    }

    public DataOutputStream wStream(OutputStream os) throws IOException {
        DataOutputStream data = new DataOutputStream(os);
        data.writeInt(x_);
        data.writeInt(y_);

        data.writeDouble(color_.getRed());
        data.writeDouble(color_.getGreen());
        data.writeDouble(color_.getBlue());
        return data;
    }
}
