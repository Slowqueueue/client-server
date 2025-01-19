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
    private ServerSocket serverSocket2;
    private Socket clientSocket;
    private Socket clientSocket2;
    private int usernum;
    private final NetworkEventListener listener;

    private PrintWriter out;
    private BufferedReader in;
    private PrintWriter out2;
    private BufferedReader in2;
    private final boolean isServer;

    public NetworkTCPProtocol(boolean isServer, NetworkEventListener listener, int port, String hostname, int usernum) {
        this(isServer, listener, usernum);
        this.port = port;
        this.host = hostname;
    }

    public NetworkTCPProtocol(boolean isServer, NetworkEventListener listener, int usernum) {
        this.usernum = usernum;
        this.listener = listener;
        this.isServer = isServer;
    }

    public void run() {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        InputStream inputStream2 = null;
        OutputStream outputStream2 = null;
        try {
            if (isServer) {
                serverSocket = new ServerSocket(port);
                serverSocket2 = new ServerSocket(port + 10);
                System.out.println("waiting for client...");
                clientSocket = serverSocket.accept();
                clientSocket2 = serverSocket2.accept();
            } else {
                System.out.println(usernum);
                if (usernum == 1) clientSocket = new Socket(host, port);
                if (usernum == 2) clientSocket2 = new Socket(host, port + 10);
            }
            if (usernum == 0) {
                inputStream = clientSocket.getInputStream();
                outputStream = clientSocket.getOutputStream();
                inputStream2 = clientSocket2.getInputStream();
                outputStream2 = clientSocket2.getOutputStream();
            }
            if (usernum == 0) {
                inputStream = clientSocket.getInputStream();
                outputStream = clientSocket.getOutputStream();
                inputStream2 = clientSocket2.getInputStream();
                outputStream2 = clientSocket2.getOutputStream();
            }
            if (usernum == 1) {
                inputStream = clientSocket.getInputStream();
                outputStream = clientSocket.getOutputStream();
            }
            if (usernum == 2) {
                inputStream2 = clientSocket2.getInputStream();
                outputStream2 = clientSocket2.getOutputStream();
            }
            if (usernum == 0) {
                out = new PrintWriter(outputStream, true);
                in = new BufferedReader(new InputStreamReader(inputStream));
                out2 = new PrintWriter(outputStream2, true);
                in2 = new BufferedReader(new InputStreamReader(inputStream2));
            }
            if (usernum == 1) {
                out = new PrintWriter(outputStream, true);
                in = new BufferedReader(new InputStreamReader(inputStream));
            }
            if (usernum == 2) {
                out2 = new PrintWriter(outputStream2, true);
                in2 = new BufferedReader(new InputStreamReader(inputStream2));
            }
            handleEvents();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeConnection() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
                serverSocket2.close();
            }
            if (clientSocket != null) {
                clientSocket.close();
                serverSocket2.close();
            }
            if (clientSocket2 != null) {
                clientSocket2.close();
                serverSocket.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public void clearObjects() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("command", NetworkCommands.CLEAR_OBJECTS);
        var res = jsonObject.toString();

        if (usernum == 0) {
            out.println(res);
            out2.println(res);
        }
        if (usernum == 1) out.println(res);
        if (usernum == 2) out2.println(res);
    }


    public void sendObjectByIndex(int index, GraphicalObject object) {
        var jsonObject = new JSONObject();
        jsonObject.put("command", NetworkCommands.RESPONSE_OBJ_BY_INDEX);
        jsonObject.put("object", object.writeToJson());
        var type = object.getClass().getSimpleName();
        jsonObject.put("obj_type", type);
        jsonObject.put("index", index);
        var res = jsonObject.toString();
        if (usernum == 0) {
            out.println(res);
            out2.println(res);
        }
        if (usernum == 1) out.println(res);
        if (usernum == 2) out2.println(res);
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
        if (usernum == 0) {
            out.println(res);
            out2.println(res);
        }
        if (usernum == 1) out.println(res);
        if (usernum == 2) out2.println(res);
    }


    public void sendObjectsListSize(int size) {
        var jsonObject = new JSONObject();
        jsonObject.put("command", NetworkCommands.RESPONSE_OBJ_LIST_SIZE);
        jsonObject.put("obj_list_size", size);
        var res = jsonObject.toString();
        if (usernum == 0) {
            out.println(res);
            out2.println(res);
        }
        if (usernum == 1) out.println(res);
        if (usernum == 2) out2.println(res);
    }


    public void requestObjectByIndex(int index) {
        var jsonObject = new JSONObject();
        jsonObject.put("command", NetworkCommands.REQUEST_OBJ_BY_INDEX);
        jsonObject.put("index", index);
        var res = jsonObject.toString();
        if (usernum == 0) {
            out.println(res);
            out2.println(res);
        }
        if (usernum == 1) out.println(res);
        if (usernum == 2) out2.println(res);
    }


    public void requestObjectsList() {
        var jsonObject = new JSONObject();
        jsonObject.put("command", NetworkCommands.REQUEST_OBJ_LIST);
        var res = jsonObject.toString();
        if (usernum == 0) {
            out.println(res);
            out2.println(res);
        }
        if (usernum == 1) out.println(res);
        if (usernum == 2) out2.println(res);
    }


    public void requestObjectsListSize() {
        var jsonObject = new JSONObject();
        jsonObject.put("command", NetworkCommands.REQUEST_OBJ_LIST_SIZE);
        var res = jsonObject.toString();
        if (usernum == 0) {
            out.println(res);
            out2.println(res);
        }
        if (usernum == 1) out.println(res);
        if (usernum == 2) out2.println(res);
    }

    public void handleEvents() {
        eventLoop:
        while (true) {
            try {
                String line = null, line2 = null;
                var jsonObject = new JSONObject();
                var jsonObject2 = new JSONObject();
                if (usernum == 0) {
                    line = in.readLine();
                    line2 = in2.readLine();
                }
                if (usernum == 1) line = in.readLine();
                if (usernum == 2) line2 = in2.readLine();
                if (usernum == 0) {
                    jsonObject = new JSONObject(line);
                    jsonObject2 = new JSONObject(line2);
                }
                if (usernum == 0) {
                    jsonObject = new JSONObject(line);
                    jsonObject2 = new JSONObject(line2);
                }
                if (usernum == 1) jsonObject = new JSONObject(line);
                if (usernum == 2) jsonObject2 = new JSONObject(line2);

                var command = new String();
                var command2 = new String();
                if (usernum == 0) {
                    command = jsonObject.getString("command");
                    command2 = jsonObject2.getString("command");
                }
                if (usernum == 1) command = jsonObject.getString("command");
                if (usernum == 2) command2 = jsonObject2.getString("command");

                if (usernum == 1) {
                    switch (command) {
                        case NetworkCommands.CLOSE_CONNECTION -> {
                            listener.onEvent(new NetworkEvent.CloseConnection());
                            closeConnection();
                            break eventLoop;
                        }
                        case NetworkCommands.CLEAR_OBJECTS -> listener.onEvent(new NetworkEvent.ClearObjects());
                        case NetworkCommands.REQUEST_OBJ_BY_INDEX -> {
                            var index = jsonObject.getInt("index");
                            listener.onEvent(new NetworkEvent.RequestObjectByIndex(index));
                        }
                        case NetworkCommands.REQUEST_OBJ_LIST -> listener.onEvent(new NetworkEvent.RequestObjectList());
                        case NetworkCommands.REQUEST_OBJ_LIST_SIZE ->
                                listener.onEvent(new NetworkEvent.RequestObjectListSize());
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
                                    default ->
                                            throw new IllegalStateException("Unexpected value: " + obj.getString("obj_type"));
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
                }
                if (usernum == 2) {
                    switch (command2) {
                        case NetworkCommands.CLOSE_CONNECTION -> {
                            listener.onEvent(new NetworkEvent.CloseConnection());
                            closeConnection();
                            break eventLoop;
                        }
                        case NetworkCommands.CLEAR_OBJECTS -> listener.onEvent(new NetworkEvent.ClearObjects());
                        case NetworkCommands.REQUEST_OBJ_BY_INDEX -> {
                            var index = jsonObject2.getInt("index");
                            listener.onEvent(new NetworkEvent.RequestObjectByIndex(index));
                        }
                        case NetworkCommands.REQUEST_OBJ_LIST -> listener.onEvent(new NetworkEvent.RequestObjectList());
                        case NetworkCommands.REQUEST_OBJ_LIST_SIZE ->
                                listener.onEvent(new NetworkEvent.RequestObjectListSize());
                        case NetworkCommands.RESPONSE_OBJ_BY_INDEX ->
                                listener.onEvent(new NetworkEvent.ResponseObjectByIndex(jsonObject2.getInt("index"), jsonObject2.getString("obj_type"), jsonObject2.getString("object")));
                        case NetworkCommands.RESPONSE_OBJ_LIST -> {
                            var jsonArray = jsonObject2.getJSONArray("objects");
                            var objects = new GraphicalObject[jsonArray.length()];
                            for (int i = 0; i < jsonArray.length(); i++) {
                                var obj = jsonArray.getJSONObject(i);
//                            objects[i] = new ObjectInfo(obj.getString("obj_type"), obj.getString("object"));
                                var object = switch (obj.getString("obj_type")) {
                                    case "ImageObject" -> new ImageObject(
                                            100, 100, 100, 100, Color.RED, "https://yt3.googleusercontent.com/sPt6fx7IJd61OcpQ63U2PGCgRyG33bHn8h3jVtW04Rd1A_3VVhrYZcyORZSRXG8aAwrea_ygKhg=s900-c-k-c0x00ffffff-no-rj"
                                    );
                                    default ->
                                            throw new IllegalStateException("Unexpected value: " + obj.getString("obj_type"));
                                };
                                object.readFromJson(obj.getString("object"));
                                objects[i] = object;
                            }
                            listener.onEvent(new NetworkEvent.ResponseObjectList(objects));
                        }
                        case NetworkCommands.RESPONSE_OBJ_LIST_SIZE ->
                                listener.onEvent(new NetworkEvent.ResponseObjectListSize(jsonObject2.getInt("obj_list_size")));
                        case NetworkCommands.RESPONSE_OBJ ->
                                listener.onEvent(new NetworkEvent.ResponseObject(jsonObject2.getString("object"), jsonObject2.getString("obj_type")));
                    }
                }
                if (usernum == 0 && command != null) {
                    switch (command) {
                        case NetworkCommands.CLOSE_CONNECTION -> {
                            listener.onEvent(new NetworkEvent.CloseConnection());
                            closeConnection();
                            break eventLoop;
                        }
                        case NetworkCommands.CLEAR_OBJECTS -> listener.onEvent(new NetworkEvent.ClearObjects());
                        case NetworkCommands.REQUEST_OBJ_BY_INDEX -> {
                            var index = jsonObject.getInt("index");
                            listener.onEvent(new NetworkEvent.RequestObjectByIndex(index));
                        }
                        case NetworkCommands.REQUEST_OBJ_LIST -> listener.onEvent(new NetworkEvent.RequestObjectList());
                        case NetworkCommands.REQUEST_OBJ_LIST_SIZE ->
                                listener.onEvent(new NetworkEvent.RequestObjectListSize());
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
                                    default ->
                                            throw new IllegalStateException("Unexpected value: " + obj.getString("obj_type"));
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
                }
                if (usernum == 0 && command2 != null) {
                    switch (command2) {
                        case NetworkCommands.CLOSE_CONNECTION -> {
                            listener.onEvent(new NetworkEvent.CloseConnection());
                            closeConnection();
                            break eventLoop;
                        }
                        case NetworkCommands.CLEAR_OBJECTS -> listener.onEvent(new NetworkEvent.ClearObjects());
                        case NetworkCommands.REQUEST_OBJ_BY_INDEX -> {
                            var index = jsonObject2.getInt("index");
                            listener.onEvent(new NetworkEvent.RequestObjectByIndex(index));
                        }
                        case NetworkCommands.REQUEST_OBJ_LIST -> listener.onEvent(new NetworkEvent.RequestObjectList());
                        case NetworkCommands.REQUEST_OBJ_LIST_SIZE ->
                                listener.onEvent(new NetworkEvent.RequestObjectListSize());
                        case NetworkCommands.RESPONSE_OBJ_BY_INDEX ->
                                listener.onEvent(new NetworkEvent.ResponseObjectByIndex(jsonObject2.getInt("index"), jsonObject2.getString("obj_type"), jsonObject2.getString("object")));
                        case NetworkCommands.RESPONSE_OBJ_LIST -> {
                            var jsonArray = jsonObject2.getJSONArray("objects");
                            var objects = new GraphicalObject[jsonArray.length()];
                            for (int i = 0; i < jsonArray.length(); i++) {
                                var obj = jsonArray.getJSONObject(i);
//                            objects[i] = new ObjectInfo(obj.getString("obj_type"), obj.getString("object"));
                                var object = switch (obj.getString("obj_type")) {
                                    case "ImageObject" -> new ImageObject(
                                            100, 100, 100, 100, Color.RED, "https://yt3.googleusercontent.com/sPt6fx7IJd61OcpQ63U2PGCgRyG33bHn8h3jVtW04Rd1A_3VVhrYZcyORZSRXG8aAwrea_ygKhg=s900-c-k-c0x00ffffff-no-rj"
                                    );
                                    default ->
                                            throw new IllegalStateException("Unexpected value: " + obj.getString("obj_type"));
                                };
                                object.readFromJson(obj.getString("object"));
                                objects[i] = object;
                            }
                            listener.onEvent(new NetworkEvent.ResponseObjectList(objects));
                        }
                        case NetworkCommands.RESPONSE_OBJ_LIST_SIZE ->
                                listener.onEvent(new NetworkEvent.ResponseObjectListSize(jsonObject2.getInt("obj_list_size")));
                        case NetworkCommands.RESPONSE_OBJ ->
                                listener.onEvent(new NetworkEvent.ResponseObject(jsonObject2.getString("object"), jsonObject2.getString("obj_type")));
                    }
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
