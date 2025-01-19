import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class NetworkPanel extends JPanel {

    private final JButton
            endConnButton = new JButton("Закрыть соединение"),
            clearObjectsButton = new JButton("Очистить объекты"),
            requestObjectByIndexButton = new JButton("Запросить объект по индексу"),
            requestObjectsListButton = new JButton("Запросить список объектов"),
            requestObjectsCountButton = new JButton("Запросить количество объектов"),
            showLocalListButton = new JButton("Показать локальный список"),
            sendObjectByIndexButton = new JButton("Отправить объект по индексу");


    private final JTextArea
            logArea = new JTextArea();

    private final JTextField
            inputField = new JTextField();

    private final JScrollPane
            scrollPane = new JScrollPane(logArea);

    public NetworkPanel(
    ) {
        inputField.setEnabled(false);

        setLayout(new GridBagLayout());

        final var constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 1;
        constraints.weighty = 0.01;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 2;
        constraints.gridheight = 1;

        add(endConnButton, constraints);
        constraints.gridy++;
        add(clearObjectsButton, constraints);
        constraints.gridy++;
        add(requestObjectByIndexButton, constraints);
        constraints.gridy++;
        add(requestObjectsListButton, constraints);
        constraints.gridy++;
        add(requestObjectsCountButton, constraints);
        constraints.gridy++;
        add(showLocalListButton, constraints);
        constraints.gridy++;
        add(sendObjectByIndexButton, constraints);
        constraints.gridy++;
        constraints.gridheight = 2;
        constraints.weighty = 0.7;
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        add(scrollPane, constraints);
        constraints.gridy += 2;
        constraints.gridheight = 1;
        constraints.weighty = 0.01;
        add(inputField, constraints);

        // fix height of logArea
        logArea.setEditable(false);
        logArea.setLineWrap(true);
        scrollPane.setPreferredSize(new Dimension(0, 0));

    }

    public void onEndConnButtonClicked(ActionListener listener) {
        endConnButton.addActionListener(listener);
    }

    public void onClearObjectsButtonClicked(ActionListener listener) {
        clearObjectsButton.addActionListener(listener);
    }

    public void onRequestObjectByIndexButtonClicked(ActionListener listener) {
        requestObjectByIndexButton.addActionListener(listener);
    }

    public void onRequestObjectsListButtonClicked(ActionListener listener) {
        requestObjectsListButton.addActionListener(listener);
    }

    public void onRequestObjectsCountButtonClicked(ActionListener listener) {
        requestObjectsCountButton.addActionListener(listener);
    }

    public void onShowLocalListButtonClicked(ActionListener listener) {
            showLocalListButton.addActionListener(listener);
    }

    public void onSendObjectByIndexButtonClicked(ActionListener listener) {
        sendObjectByIndexButton.addActionListener(listener);
    }

    public void onInput(ActionListener listener) {
        inputField.setEnabled(true);
        inputField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    listener.actionPerformed(
                            new ActionEvent(
                                    inputField.getText(),
                                    ActionEvent.ACTION_PERFORMED,
                                    inputField.getText()
                            )
                    );
                    inputField.removeKeyListener(this);
                    inputField.setText("");
                    inputField.setEnabled(false);
                }
            }
        });
    }

    public void writeln(String text) {
        logArea.append(text + "\n");
        SwingUtilities.invokeLater(() -> {
            JScrollBar vertical = scrollPane.getVerticalScrollBar();
            BoundedRangeModel brm = vertical.getModel();
            int extent = brm.getExtent();
            int max = brm.getMaximum();
            vertical.setValue(max - extent);
        });
    }
}
