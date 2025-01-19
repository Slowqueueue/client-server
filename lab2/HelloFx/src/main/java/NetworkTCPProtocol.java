import java.awt.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import org.json.JSONObject;
import org.json.JSONArray;

public class NetworkTCPProtocol implements Runnable {
    private int port = 8080;
    private String host = "localhost";
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private final NetworkEventListener listener;

    private PrintWriter out;
    private BufferedReader in;
    private final boolean isServer;

    public NetworkTCPProtocol(boolean isServer, NetworkEventListener listener, int port, String hostname) {
        this(isServer, listener);
        this.port = port;
        this.host = hostname;
    }

    public NetworkTCPProtocol(boolean isServer, NetworkEventListener listener) {
        this.listener = listener;
        this.isServer = isServer;
    }

    public void run() {
        InputStream inputStream;
        OutputStream outputStream;
        try {
            if (isServer) {
                    serverSocket = new ServerSocket(port);
                    System.out.println("waiting for client...");
                    clientSocket = serverSocket.accept();
            } else {
                clientSocket = new Socket(host, port);
                }
            inputStream = clientSocket.getInputStream();
            outputStream = clientSocket.getOutputStream();
            out = new PrintWriter(outputStream, true);
            in = new BufferedReader(new InputStreamReader(inputStream));
            handleEvents();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeConnection() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
            if (clientSocket != null) {
                clientSocket.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public void clearObjects() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("command", NetworkCommands.CLEAR_OBJECTS);
        var res = jsonObject.toString();
        out.println(res);
    }


    public void sendObjectByIndex(int index, GraphicalObject object) {
        var jsonObject = new JSONObject();
        jsonObject.put("command", NetworkCommands.RESPONSE_OBJ_BY_INDEX);
        jsonObject.put("object", object.writeToJson());
        var type = object.getClass().getSimpleName();
        jsonObject.put("obj_type", type);
        jsonObject.put("index", index);
        var res = jsonObject.toString();
        out.println(res);
    }


    public void sendObjectsList(GraphicalObject[] objects) {
        var jsonObject = new JSONObject();
        jsonObject.put("command", NetworkCommands.RESPONSE_OBJ_LIST);
        var jsonArray = new JSONArray();
        for (int i = 0; i < objects.length; i++) {
            var object = objects[i];
            var obj = new JSONObject();
            obj.put("index", i);
            obj.put("obj_type", object.getClass().getSimpleName());
            obj.put("object", object.writeToJson());
            jsonArray.put(obj);
        }
        jsonObject.put("objects", jsonArray);
        var res = jsonObject.toString();
        out.println(res);
    }


    public void sendObjectsListSize(int size) {
        var jsonObject = new JSONObject();
        jsonObject.put("command", NetworkCommands.RESPONSE_OBJ_LIST_SIZE);
        jsonObject.put("obj_list_size", size);
        var res = jsonObject.toString();
        out.println(res);
    }


    public void requestObjectByIndex(int index) {
        var jsonObject = new JSONObject();
        jsonObject.put("command", NetworkCommands.REQUEST_OBJ_BY_INDEX);
        jsonObject.put("index", index);
        var res = jsonObject.toString();
        out.println(res);
    }


    public void requestObjectsList() {
        var jsonObject = new JSONObject();
        jsonObject.put("command", NetworkCommands.REQUEST_OBJ_LIST);
        var res = jsonObject.toString();
        out.println(res);
    }


    public void requestObjectsListSize() {
        var jsonObject = new JSONObject();
        jsonObject.put("command", NetworkCommands.REQUEST_OBJ_LIST_SIZE);
        var res = jsonObject.toString();
        out.println(res);
    }
    public void handleEvents() {
        while (true) {
            try {
                String line = in.readLine();
                var jsonObject = new JSONObject(line);
                var command = jsonObject.getString("command");
                switch (command) {
                    case NetworkCommands.CLOSE_CONNECTION -> {
                        listener.onEvent(new NetworkEvent.CloseConnection());
                        closeConnection();
                    }
                    case NetworkCommands.CLEAR_OBJECTS -> listener.onEvent(new NetworkEvent.ClearObjects());
                    case NetworkCommands.REQUEST_OBJ_BY_INDEX -> {
                        var index = jsonObject.getInt("index");
                        listener.onEvent(new NetworkEvent.RequestObjectByIndex(index));
                    }
                    case NetworkCommands.REQUEST_OBJ_LIST -> listener.onEvent(new NetworkEvent.RequestObjectList());
                    case NetworkCommands.REQUEST_OBJ_LIST_SIZE -> listener.onEvent(new NetworkEvent.RequestObjectListSize());
                    case NetworkCommands.RESPONSE_OBJ_BY_INDEX ->
                            listener.onEvent(new NetworkEvent.ResponseObjectByIndex(jsonObject.getInt("index"), jsonObject.getString("obj_type"), jsonObject.getString("object")));
                    case NetworkCommands.RESPONSE_OBJ_LIST -> {
                        var jsonArray = jsonObject.getJSONArray("objects");
                        var objects = new GraphicalObject[jsonArray.length()];
                        for (int i = 0; i < jsonArray.length(); i++) {
                            var obj = jsonArray.getJSONObject(i);
//                            objects[i] = new ObjectInfo(obj.getString("obj_type"), obj.getString("object"));
                            var object = switch (obj.getString("obj_type")) {
                                case "ImageObject" -> new ImageObject(
                                        100, 100, 100, 100, Color.RED, "https://yt3.googleusercontent.com/sPt6fx7IJd61OcpQ63U2PGCgRyG33bHn8h3jVtW04Rd1A_3VVhrYZcyORZSRXG8aAwrea_ygKhg=s900-c-k-c0x00ffffff-no-rj"
                                );
                                default -> throw new IllegalStateException("Unexpected value: " + obj.getString("obj_type"));
                            };
                            object.readFromJson(obj.getString("object"));
                            objects[i] = object;
                        }
                        listener.onEvent(new NetworkEvent.ResponseObjectList(objects));
                    }
                    case NetworkCommands.RESPONSE_OBJ_LIST_SIZE ->
                            listener.onEvent(new NetworkEvent.ResponseObjectListSize(jsonObject.getInt("obj_list_size")));
                    case NetworkCommands.RESPONSE_OBJ ->
                            listener.onEvent(new NetworkEvent.ResponseObject(jsonObject.getString("object"), jsonObject.getString("obj_type")));
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
