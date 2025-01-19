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
    private boolean selected = false;
    private int x, y;
    private Color color;
    protected BorderPane bpane;
    protected Timeline timeline;
    public AbstractClass(int x, int y, Color color) {
        this.bpane = new BorderPane();
        this.x = x;
        this.y = y;
        this.color = color;

        this.bpane.setLayoutX(x);
        this.bpane.setLayoutY(y);
    }

    public BorderPane getElement() {
        return bpane;
    }

    public boolean isSelected() {
        return this.selected;
    }

    public boolean contains(double x, double y) {
        return this.bpane.getBoundsInParent().contains(x, y);
    }

    public Bounds getBoundsInParent() {
        return this.bpane.getBoundsInParent();
    }

    public void startAnimation() {
        this.timeline.play();
    }

    public void stopAnimation() {
        this.timeline.pause();
    }

    public void selectItem() {
        this.selected = !this.selected;

        if (this.selected) {
            this.bpane.setStyle("-fx-border-color: blue;");
        } else {
            this.bpane.setStyle("-fx-border-color: transparent;");
        }
    }

    public DataInputStream rStream(InputStream is) throws IOException {
        var data = new DataInputStream(is);

        this.x = data.readInt();
        this.y = data.readInt();

        int red = data.readInt();
        int green = data.readInt();
        int blue = data.readInt();

        this.color = Color.rgb(red, green, blue);
        return data;
    }

    public DataOutputStream wStream(OutputStream os) throws IOException {
        var data = new DataOutputStream(os);
        data.writeInt(x);
        data.writeInt(y);

        data.writeDouble(color.getRed());
        data.writeDouble(color.getGreen());
        data.writeDouble(color.getBlue());
        return data;
    }
}
