import java.awt.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class GraphicalObject {
    protected int x, y; // координаты центра
    protected int width, height; // размеры охватывающего прямоугольника
    protected Color color; // цвет

    private boolean isMoving = true; // двигается или нет

    private boolean isShowOutline = false; // показывать ли обводку

    public GraphicalObject(int x, int y, int width, int height, Color color) { // конструктор
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.color = color;
    }

    public void stop() {
        isMoving = false;
    } // остановить

    public void resume() {
        isMoving = true;
    } // возобновить

    public boolean isMoving() {
        return isMoving;
    } // двигается или нет

    public void showOutline() { // показать обводку
        isShowOutline = true;
    }

    public void hideOutline() { // скрыть обводку
        isShowOutline = false;
    }

    public boolean isShowOutline() { // показывать ли обводку
        return isShowOutline;
    }

    public void draw(Graphics g, int canvasWidth, int canvasHeight) { // отрисовка
        if (isShowOutline) {
            g.setColor(Color.GREEN); // обводка зеленого цвета
            g.drawRect(x - width / 2 - 1, y - height / 2 - 1, width + 2, height + 2); // прямоугольник
        }
    }

    public boolean contains(int x, int y) { // проверка попадания в объект
        return (x >= this.x - width/2 && x <= this.x + width/2 && y >= this.y - height/2 && y <= this.y + height/2);
    }

    public abstract void read(InputStream input) throws IOException; // чтение из потока

    public abstract void write(OutputStream output) throws IOException; // запись в поток

    public abstract void move( int canvasWidth, int canvasHeight); // перемещение объекта

    public abstract String writeToJson();

    public abstract void readFromJson(String json);
}
