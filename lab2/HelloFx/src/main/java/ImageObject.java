import org.json.JSONObject;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import javax.imageio.ImageIO;
public class ImageObject extends GraphicalObject {
    private Image image;
    private double angle = 0.0;

    private String imageUrl;

    public ImageObject(int x, int y, int width, int height, Color color, String imageUrl) throws IOException {
        super(x, y, width, height, color);
        URL url = new URL(imageUrl);
        this.imageUrl = imageUrl;
        image = ImageIO.read(url);
    }

    public void draw(Graphics g, int canvasWidth, int canvasHeight) {
        super.draw(g, canvasWidth, canvasHeight);
        Graphics2D g2d = (Graphics2D) g;
        AffineTransform originalTransform = g2d.getTransform(); ;
        AffineTransform rotatedTransform = AffineTransform.getRotateInstance(angle, x, y);
        g2d.transform(rotatedTransform);
        g.drawImage(image, x - width/2, y - height/2, width, height, null);
        g2d.setTransform(originalTransform);
    }

    @Override
    public boolean contains(int x, int y) {
        return (x >= this.x - width/2 && x <= this.x + width/2 && y >= this.y - height/2 && y <= this.y + height/2);
    }

    @Override
    public void read(InputStream input) throws IOException {
        DataInputStream dis = new DataInputStream(input);
        x = dis.readInt();
        y = dis.readInt();
        width = dis.readInt();
        height = dis.readInt();
        int r = dis.readInt();
        int g = dis.readInt();
        int b = dis.readInt();
        color = new Color(r, g, b);
        String imageUrl = dis.readUTF();
        URL url = new URL(imageUrl);
        image = ImageIO.read(url);
    }

    @Override
    public void write(OutputStream output) throws IOException {
        DataOutputStream dos = new DataOutputStream(output);
        dos.writeInt(x);
        dos.writeInt(y);
        dos.writeInt(width);
        dos.writeInt(height);
        dos.writeInt(color.getRed());
        dos.writeInt(color.getGreen());
        dos.writeInt(color.getBlue());
        dos.writeUTF(image.toString());
    }

    @Override
    public void move(int canvasWidth, int canvasHeight)
    {
        angle += 0.05;
    }
    @Override
    public String writeToJson() {
        var jsonObject = new JSONObject();

        jsonObject.put("x", x);
        jsonObject.put("y", y);
        jsonObject.put("width", width);
        jsonObject.put("height", height);
        jsonObject.put("color", color.getRGB());
        jsonObject.put("image", imageUrl);

        return jsonObject.toString();
    }

    @Override
    public void readFromJson(String json) {
        var jsonObject = new JSONObject(json);

        x = jsonObject.getInt("x");
        y = jsonObject.getInt("y");
        width = jsonObject.getInt("width");
        height = jsonObject.getInt("height");
        color = new Color(jsonObject.getInt("color"));

        String imageUrl = jsonObject.getString("image");
        URL url = null;
        try {
            url = new URL(imageUrl);
            image = ImageIO.read(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.imageUrl = imageUrl;

    }

    @Override
    public String toString() {
        return "ImageObject(" +
                ", x=" + x +
                ", y=" + y +
                ", imageUrl='" + imageUrl + '\'' +
                ')';
    }
}
