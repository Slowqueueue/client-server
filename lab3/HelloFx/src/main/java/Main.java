import javax.swing.*;

class Server {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Graphics SERVER");
        GraphicsEditor editor = new GraphicsEditor(true, 0);
        frame.setContentPane(editor);
        frame.setSize(960, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}

class Client {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Graphics CLIENT");
        GraphicsEditor editor = new GraphicsEditor(false, 1);
        frame.setContentPane(editor);
        frame.setSize(960, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}

class Client2 {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Graphics CLIENT");
        GraphicsEditor editor = new GraphicsEditor(false, 2);
        frame.setContentPane(editor);
        frame.setSize(960, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}

