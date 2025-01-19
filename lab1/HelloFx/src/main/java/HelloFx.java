import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Random;
import java.util.Vector;

public class HelloFx extends Application {
    final int WIDTH = 1280;
    final int HEIGHT = 720;
    Vector<AbstractClass> Vector;

    @Override
    public void start(Stage stage) throws IOException {
        Pane root = new Pane();
        Scene scene = new Scene(root, WIDTH, HEIGHT);
        Group view = new Group();
        HBox hbox = new HBox();

        this.Vector = new Vector<>();

        createButtons(view, hbox);
        root.getChildren().add(hbox);

        EventHandler<MouseEvent> eventHandler = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                double x = e.getSceneX();
                double y = e.getSceneY();

                for (AbstractClass item : Vector) {
                    if (item.contains(x, y)) {
                        item.selectItem();
                    }
                }
            }
        };

        view.addEventFilter(MouseEvent.MOUSE_CLICKED, eventHandler);

        root.getChildren().add(view);
        stage.setTitle("LR1");
        stage.setScene(scene);
        stage.show();
    }

    private void createButtons(Group view, HBox hbox) {
        Button btnImg = new Button("Create Image");
        Button btnText = new Button("Create Text");
        Button btnStartAll = new Button("Start All");
        Button btnStopAll = new Button("Stop All");
        Button btnDeleteAll = new Button("Delete All");
        Button btnStart = new Button("Start Selected");
        Button btnStop = new Button("Stop Selected");
        Button btnDelete = new Button("Delete Selected");
        hbox.getChildren().addAll(btnImg, btnText, btnStartAll, btnStopAll, btnDeleteAll, btnStart, btnStop, btnDelete);

        btnText.setOnAction(e -> {
            Random random = new Random();
            ClassText classText = new ClassText(random.nextInt(WIDTH - 400) + 75, random.nextInt(HEIGHT - 150) + 75, Color.web("#9400D3"));
            Vector.add(classText);
            view.getChildren().add(classText.getElement());

        });

        btnImg.setOnAction(e -> {
            try {
                Random random = new Random();
                ClassImg classImg = new ClassImg(random.nextInt(WIDTH - 300) + 75, random.nextInt(HEIGHT - 300) + 75);
                Vector.add(classImg);
                view.getChildren().add(classImg.getElement());
            } catch (FileNotFoundException err) {
                System.out.println(err);
            }
        });

        btnDeleteAll.setOnAction(e -> {
            for (AbstractClass item : Vector) {
                view.getChildren().remove(item.getElement());
            }
            Vector.clear();
        });

        btnStopAll.setOnAction(e -> {
            for (AbstractClass item : Vector) {
                item.stopAnimation();
            }
        });

        btnStartAll.setOnAction(e -> {
            for (AbstractClass item : Vector) {
                item.startAnimation();
            }
        });

        btnDelete.setOnAction(e -> {
            boolean clear = false;

            while (!clear) {
                boolean broken = false;

                for (AbstractClass item : Vector) {
                    if (item.isSelected()) {
                        view.getChildren().remove(item.getElement());
                        Vector.remove(item);
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
            for (AbstractClass item : Vector) {
                if (item.isSelected()) {
                    item.stopAnimation();
                }
            }
        });

        btnStart.setOnAction(e -> {
            for (AbstractClass item : Vector) {
                if (item.isSelected()) {
                    item.startAnimation();
                }
            }
        });
    }

    public static void main(String[] args) {
        launch();
    }
}