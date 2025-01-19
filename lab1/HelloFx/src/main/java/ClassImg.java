import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

public class ClassImg extends AbstractClass {
    private Image img;
    private ImageView imgView;

    public ClassImg(int x, int y) throws FileNotFoundException {
        super(x, y, null);
        this.img = new Image(new FileInputStream("C:/image.png"));
        this.imgView = new ImageView(this.img);
        this.imgView.setFitWidth(200);
        this.imgView.setPreserveRatio(true);

        this.bpane.setCenter(imgView);

        setupAnimation();
    }

    private void setupAnimation() {
        KeyValue initRotate = new KeyValue(this.bpane.rotateProperty(), 0);
        KeyFrame initFrame = new KeyFrame(Duration.ZERO, initRotate);
        KeyValue endRotate = new KeyValue(this.bpane.rotateProperty(), 360);
        KeyFrame endFrame = new KeyFrame(Duration.seconds(5), endRotate);

        this.timeline = new Timeline(initFrame, endFrame);
        this.timeline.setCycleCount(Timeline.INDEFINITE);

        startAnimation();
    }
}
