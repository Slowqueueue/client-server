import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

public class ClassText extends AbstractClass {
    private Label label;

    public ClassText(int x, int y, Color color) {
        super(x, y, color);

        this.label = new Label("Sample Text");
        this.label.setTextAlignment(TextAlignment.CENTER);
        this.label.setFont(new Font("Comic Sans MS", 32));
        this.label.setTextFill(color);

        this.bpane.setCenter(label);

        setupAnimation();
    }

    private void setupAnimation() {
        KeyValue initXValue = new KeyValue(this.bpane.translateXProperty(), 0);
        KeyValue initYValue = new KeyValue(this.bpane.translateYProperty(), 0);
        KeyFrame initFrame = new KeyFrame(Duration.ZERO, initXValue, initYValue);

        KeyValue topLeftXValue = new KeyValue(this.bpane.translateXProperty(), 50);
        KeyValue topLeftYValue = new KeyValue(this.bpane.translateYProperty(), 0);
        KeyFrame topLeftFrame = new KeyFrame(Duration.seconds(1), topLeftXValue, topLeftYValue);

        KeyValue bottomLeftXValue = new KeyValue(this.bpane.translateXProperty(), 50);
        KeyValue bottomLeftYValue = new KeyValue(this.bpane.translateYProperty(), 50);
        KeyFrame bottomLeftFrame = new KeyFrame(Duration.seconds(2), bottomLeftXValue, bottomLeftYValue);

        KeyValue bottomRightXValue = new KeyValue(this.bpane.translateXProperty(), 0);
        KeyValue bottomRightYValue = new KeyValue(this.bpane.translateYProperty(), 50);
        KeyFrame bottomRightFrame = new KeyFrame(Duration.seconds(3), bottomRightXValue, bottomRightYValue);
        KeyFrame endFrame = new KeyFrame(Duration.seconds(4), initXValue, initYValue);

        this.timeline = new Timeline(initFrame, topLeftFrame, bottomLeftFrame, bottomRightFrame, endFrame);
        this.timeline.setCycleCount(Timeline.INDEFINITE);
        startAnimation();
    }
}
