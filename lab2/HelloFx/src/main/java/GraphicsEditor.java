import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Arrays;
import java.util.Vector;
import java.util.concurrent.*;
import javax.swing.*;

// Класс для рисования графических объектов
public class GraphicsEditor extends JPanel implements NetworkEventListener, Runnable {
    private Vector<GraphicalObject> objects = new Vector<>(); // список графических объектов
    private final ExecutorService executor = Executors.newSingleThreadExecutor(); // пул потоков
    private final ScheduledExecutorService scheduledExecutor = Executors.newSingleThreadScheduledExecutor(); // пул потоков для таймера

    private boolean isServer; // режим сервера

    private NetworkTCPProtocol protocol;

    private EditorMode mode = EditorMode.ADD; // текущий режим редактора

    private MainPanel mainPanel; // панель для рисования

    private ModesPanel modesPanel; // панель с режимами редактора

    private NetworkPanel networkPanel; // панель с сетевыми функциями

    @Override
    public void onEvent(NetworkEvent event) {
        switch (event) {
            case NetworkEvent.ClearObjects __ -> {
                networkPanel.writeln("Объекты очищаются!");
                objects.clear();
                repaint();
            }
            case NetworkEvent.ResponseObject responseObject -> {
                networkPanel.writeln("Получен объект: " + responseObject.object());
                var object = switch (responseObject.type()) {
                    case "ImageObject" -> {
                        try {
                            yield new ImageObject(
                                    100, 100, 100, 100, Color.RED, "https://yt3.googleusercontent.com/sPt6fx7IJd61OcpQ63U2PGCgRyG33bHn8h3jVtW04Rd1A_3VVhrYZcyORZSRXG8aAwrea_ygKhg=s900-c-k-c0x00ffffff-no-rj"
                            );
                        } catch (IOException e) {
                            e.printStackTrace();
                            yield null;
                        }
                    }
                    default -> throw new IllegalStateException("Unexpected value: " + responseObject.object());
                };
                object.readFromJson(responseObject.object());
                objects.add(object);
            }
            case NetworkEvent.ResponseObjectByIndex responseObjectByIndex -> {
                networkPanel.writeln("Получен объект по индексу: " + responseObjectByIndex.index());
                var processedObj = switch (responseObjectByIndex.type()) {
                    case "ImageObject" -> {
                        try {
                            yield new ImageObject(
                                    100, 100, 100, 100, Color.RED, "https://yt3.googleusercontent.com/sPt6fx7IJd61OcpQ63U2PGCgRyG33bHn8h3jVtW04Rd1A_3VVhrYZcyORZSRXG8aAwrea_ygKhg=s900-c-k-c0x00ffffff-no-rj"
                            );
                        } catch (IOException e) {
                            e.printStackTrace();
                            yield null;
                        }
                    }
                    default -> throw new IllegalStateException("Unexpected value: " + responseObjectByIndex.object());
                };
                processedObj.readFromJson(responseObjectByIndex.object());
                objects.add(processedObj);
            }
            case NetworkEvent.ResponseObjectListSize responseObjectListSize ->
                    networkPanel.writeln("Получен размер списка объектов: " + responseObjectListSize.size());
            case NetworkEvent.ResponseObjectList responseObjectList ->
                    networkPanel.writeln("Получен список объектов: " + Arrays.toString(responseObjectList.objects()));
            case NetworkEvent.RequestObjectList __ -> {
                networkPanel.writeln("Запрошен список объектов");
                var objList = new GraphicalObject[objects.size()];
                protocol.sendObjectsList(objects.toArray(objList));
            }
            case NetworkEvent.RequestObjectListSize __ -> {
                networkPanel.writeln("Запрошен размер списка объектов");
                protocol.sendObjectsListSize(objects.size());
            }
            case NetworkEvent.RequestObjectByIndex requestObjectByIndex -> {
                networkPanel.writeln("Запрошен объект по индексу: " + requestObjectByIndex.index());
                protocol.sendObjectByIndex(requestObjectByIndex.index(), objects.get(requestObjectByIndex.index()));
            }
            default -> networkPanel.writeln("Unknown event: " + event);
        }
    }

    private class MainPanel extends JPanel { // панель для рисования
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            // отрисовка всех объектов
            for (GraphicalObject object : objects) {
                object.draw(g, getWidth(), getHeight());
            }
        }

    }

    public GraphicsEditor(boolean isServer) {
        registerModesPanel();
        start();
        this.isServer = isServer;
        protocol = new NetworkTCPProtocol(isServer, this);
        var thread = new Thread(protocol);
        thread.setDaemon(true);
        thread.start();

        var mainThread = new Thread(this);
        mainThread.setDaemon(true);
        mainThread.start();
    }

    @Override
    protected void paintComponent(Graphics g) { // отрисовка
        super.paintComponent(g);

        // отрисовка текущего режима редактора в заголовке окна
        final var frame = (JFrame) SwingUtilities.getWindowAncestor(this);

        final var mode = switch (this.mode) {
            case ADD -> "Добавление";
            case REMOVE -> "Удаление";
            case STOP_RESUME -> "Стоп/возобновить";
        };

        frame.setTitle("Графический редактор (" +
                (isServer ? "Сервер" : "Клиент")
                + "): " + mode);
    }

    public void saveToFile(String fileName) throws IOException { // сохранение в файл
        OutputStream output = new
                BufferedOutputStream(new FileOutputStream(fileName)); // буферизированный поток вывода
        ObjectOutputStream oos = new ObjectOutputStream(output); // поток вывода объектов
        oos.writeObject(objects); // запись списка объектов

    }

    public void loadFromFile(String fileName) throws IOException, ClassNotFoundException { // загрузка из файла
        try (InputStream input = new BufferedInputStream(new FileInputStream(fileName))) {
            ObjectInputStream ois = new ObjectInputStream(input); // поток ввода объектов
            objects = (Vector<GraphicalObject>) ois.readObject(); // чтение списка объектов
        }
        repaint(); // перерисовка
    }

    private void registerModesPanel() { // регистрация обработчиков событий для панели режимов
        final var layout = new GridBagLayout();

        setLayout(layout);

        ModesPanel modesPanel = new ModesPanel();
        networkPanel = new NetworkPanel();
        mainPanel = new MainPanel();
        mainPanel.setBackground(Color.LIGHT_GRAY);

        GridBagConstraints c = new GridBagConstraints();
        c.weightx = 0.01; // first column weight
        c.weighty = 1.0; // first row weight
        c.fill = GridBagConstraints.BOTH;
        add(modesPanel, c);

        c = new GridBagConstraints();
        c.weightx = 0.9; // center column weight
        c.weighty = 1.0; // first row weight
        c.fill = GridBagConstraints.BOTH;
        add(mainPanel, c);

        c = new GridBagConstraints();
        c.weightx = 0.05; // last column weight
        c.weighty = 1.0; // first row weight
        c.fill = GridBagConstraints.BOTH;
        add(networkPanel, c);

        modesPanel.onAddButtonClicked(__ -> { // регистрация обработчика события кнопки "Добавить"
            mode = EditorMode.ADD; // установка режима редактора
            objects.forEach(GraphicalObject::hideOutline); // скрытие контуров
        });

        modesPanel.onRemoveButtonClicked(__ -> { // регистрация обработчика события кнопки "Удалить"
            mode = EditorMode.REMOVE; // установка режима редактора
            objects.forEach(GraphicalObject::showOutline); // отображение контуров
        });

        modesPanel.onStopButtonClicked(__ -> { // регистрация обработчика события кнопки "Стоп/возобновить"
            mode = EditorMode.STOP_RESUME; // установка режима редактора
            objects.forEach(GraphicalObject::showOutline); // отображение контуров
        });

        modesPanel.onStopAllButtonClicked(__ -> { // регистрация обработчика события кнопки "Стоп всех"
            objects.forEach(GraphicalObject::stop); // остановка всех объектов
        });

        modesPanel.onResumeAllButtonClicked(__ -> { // регистрация обработчика события кнопки "Возобновить всех"
            objects.forEach(GraphicalObject::resume); // возобновление всех объектов
        });
    }

    private void start() {
        // Запуск таймера для обновления положения объектов и перерисовки
        scheduledExecutor.scheduleAtFixedRate(() -> {
            // Движение объектов в отдельном потоке
            executor.submit(() ->
                    objects.forEach(obj -> {
                        if (obj.isMoving()) {
                            obj.move(mainPanel.getWidth(), mainPanel.getHeight());
                        }
                    })
            );

            // Перерисовка в потоке диспетчера событий
            SwingUtilities.invokeLater(this::repaint);

            // Итого имеем два потока: один для движения объектов, другой для перерисовки
        }, 0, 16, TimeUnit.MILLISECONDS);

        mainPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) { // обработка щелчка мыши
                int x = e.getX(); // координаты курсора
                int y = e.getY();

                GraphicalObject object = null; // объект, который будет добавлен

                if (mode == EditorMode.REMOVE) {
                    objects.removeIf(obj -> obj.contains(x, y));  // удаление объекта, если он содержит точку (x, y)
                    return;
                }

                if (mode == EditorMode.STOP_RESUME) {
                    objects.stream() // поиск объекта, который содержит точку (x, y)
                            .filter(obj -> obj.contains(x, y))
                            .findFirst() // получение первого найденного объекта
                            .ifPresent(obj -> { // если объект найден, то останавливаем/возобновляем его
                                if (obj.isMoving()) { // если объект двигается, то останавливаем
                                    obj.stop();    // если объект стоит, то возобновляем движение
                                } else {           // если объект двигается, то останавливаем
                                    obj.resume();
                                }
                            });
                    return;
                }

                try {
                    if (e.getButton() == MouseEvent.BUTTON1) {
                        object = new ImageObject(x, y, 120, 120, Color.WHITE, "https://yt3.googleusercontent.com/sPt6fx7IJd61OcpQ63U2PGCgRyG33bHn8h3jVtW04Rd1A_3VVhrYZcyORZSRXG8aAwrea_ygKhg=s900-c-k-c0x00ffffff-no-rj");
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                if (object != null) {
                    objects.add(object);
                }
            }
        });
    }

    @Override
    public void run() {

        networkPanel.onEndConnButtonClicked(
                __ -> {
                    networkPanel.writeln("Закрываем соединение");
                    protocol.closeConnection();
                }
        );

        networkPanel.onClearObjectsButtonClicked(
                __ -> {
                    networkPanel.writeln("Очищаем объекты");
                    protocol.clearObjects();
                    objects.clear();
                }
        );

        networkPanel.onRequestObjectByIndexButtonClicked(
                __ -> {
                    networkPanel.writeln("Введите индекс объекта:");
                    networkPanel.onInput(
                            event ->
                                    protocol.requestObjectByIndex(
                                            Integer.parseInt((String) event.getSource())
                                    )

                    );
                }
        );

        networkPanel.onRequestObjectsListButtonClicked(
                __ -> {
                    networkPanel.writeln("Запрашиваем список объектов");
                    protocol.requestObjectsList();
                }
        );

        networkPanel.onRequestObjectsCountButtonClicked(
                __ -> {
                    networkPanel.writeln("Запрашиваем количество объектов");
                    protocol.requestObjectsListSize();
                }
        );

        networkPanel.onShowLocalListButtonClicked(
                __ -> {
                    networkPanel.writeln("Список локальных объектов:");
                    for (int i = 0; i < objects.size(); i++) {
                        networkPanel.writeln("#" + i + ": " + objects.get(i).toString());
                    }
                }
        );

        networkPanel.onSendObjectByIndexButtonClicked(
                __ -> {
                    networkPanel.writeln("Введите индекс объекта:");
                    networkPanel.onInput(
                            event -> {
                                int index = Integer.parseInt((String) event.getSource());
                                networkPanel.writeln("Отправляем объект по индексу " + index + ":");
                                protocol.sendObjectByIndex(index, objects.get(index));
                            }
                    );
                }
        );
    }

}
