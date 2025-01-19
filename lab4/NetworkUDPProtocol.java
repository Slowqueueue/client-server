import org.json.JSONArray;
import org.json.JSONObject;


import java.awt.*;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;


public class NetworkUDPProtocol implements Runnable {
    public static final int PORT_1 = 50001;
    public static final int PORT_2 = 50002;
    public static final String HOST = "localhost";


    private  int remotePort;
    private  int remotePort2;

    private DatagramSocket socket;
    private DatagramSocket socket2;
    private int usernum;


    byte[] receivingDataBuffer = new byte[1024];
    byte[] receivingDataBuffer2 = new byte[1024];
    byte[] sendingDataBuffer = new byte[1024];
    byte[] sendingDataBuffer2 = new byte[1024];


    private final NetworkEventListener listener;


    public NetworkUDPProtocol(boolean isServer, NetworkEventListener listener, int usernum) {
        this.listener = listener;
        this.usernum = usernum;
        int localPort = 0, localPort2 = 0;
        if (isServer) {
            localPort = PORT_1;
            localPort2 = PORT_1 + 10;
            remotePort = PORT_2;
            remotePort2 = PORT_2 + 10;
        } else {
            if (usernum == 1) {
                localPort = PORT_2;
                remotePort = PORT_1;
            }
            if (usernum == 2) {
                localPort2 = PORT_2 + 10;
                remotePort2 = PORT_1 + 10;
            }
        }
        try {
            socket = new DatagramSocket(localPort);
            socket2 = new DatagramSocket(localPort2);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }
    private void sendData(String data) {
        try {
            sendingDataBuffer = data.getBytes();
            sendingDataBuffer2 = data.getBytes();
            var packet = new DatagramPacket(sendingDataBuffer, sendingDataBuffer.length);
            var packet2 = new DatagramPacket(sendingDataBuffer2, sendingDataBuffer2.length);
            if (usernum == 0) {
                packet = new DatagramPacket(sendingDataBuffer, sendingDataBuffer.length, InetAddress.getByName(HOST), remotePort);
                packet2 = new DatagramPacket(sendingDataBuffer2, sendingDataBuffer2.length, InetAddress.getByName(HOST), remotePort2);
            }
            if (usernum == 1) {
                packet = new DatagramPacket(sendingDataBuffer, sendingDataBuffer.length, InetAddress.getByName(HOST), remotePort);
            }
            if (usernum == 2) {
                packet2 = new DatagramPacket(sendingDataBuffer2, sendingDataBuffer2.length, InetAddress.getByName(HOST), remotePort2);
            }
            if (usernum == 0) {
                socket.send(packet);
                socket2.send(packet2);
            }
            if (usernum == 1) {
                socket.send(packet);
            }
            if (usernum == 2) {
                socket2.send(packet2);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private final DatagramPacket inputPacket = new DatagramPacket(receivingDataBuffer, receivingDataBuffer.length);
    private final DatagramPacket inputPacket2 = new DatagramPacket(receivingDataBuffer2, receivingDataBuffer2.length);


    @Override
    public void run() {
        handleEvents();
    }


    public void closeConnection() {
        var jsonObject = new JSONObject();
        jsonObject.put("command", NetworkCommands.CLOSE_CONNECTION);
        var res = jsonObject.toString();
        sendData(res);
        if (socket != null) {
            socket.close();
        }
        if (socket2 != null) {
            socket2.close();
        }
    }


    public void clearObjects() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("command", NetworkCommands.CLEAR_OBJECTS);
        var res = jsonObject.toString();
        sendData(res);
    }


    public void sendObjectByIndex(int index, GraphicalObject object) {
        var jsonObject = new JSONObject();
        jsonObject.put("command", NetworkCommands.RESPONSE_OBJ_BY_INDEX);
        jsonObject.put("object", object.writeToJson());
        var type = object.getClass().getSimpleName();
        jsonObject.put("obj_type", type);
        jsonObject.put("index", index);
        var res = jsonObject.toString();
        sendData(res);
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
        sendData(res);
    }


    public void sendObjectsListSize(int size) {
        var jsonObject = new JSONObject();
        jsonObject.put("command", NetworkCommands.RESPONSE_OBJ_LIST_SIZE);
        jsonObject.put("obj_list_size", size);
        var res = jsonObject.toString();
        sendData(res);
    }


    public void requestObjectByIndex(int index) {
        var jsonObject = new JSONObject();
        jsonObject.put("command", NetworkCommands.REQUEST_OBJ_BY_INDEX);
        jsonObject.put("index", index);
        var res = jsonObject.toString();
        sendData(res);
    }


    public void requestObjectsList() {
        var jsonObject = new JSONObject();
        jsonObject.put("command", NetworkCommands.REQUEST_OBJ_LIST);
        var res = jsonObject.toString();
        sendData(res);
    }


    public void requestObjectsListSize() {
        var jsonObject = new JSONObject();
        jsonObject.put("command", NetworkCommands.REQUEST_OBJ_LIST_SIZE);
        var res = jsonObject.toString();
        sendData(res);
    }


    public void handleEvents() {
        eventLoop:
        while (true) {
            try {
                if (usernum == 0) {
                    socket.receive(inputPacket);
                    socket2.receive(inputPacket2);
                }
                if (usernum == 1) {
                    socket.receive(inputPacket);
                }
                if (usernum == 2) {
                    socket2.receive(inputPacket2);
                }
                String line = null, line2 = null;
                var jsonObject = new JSONObject();
                var jsonObject2 = new JSONObject();
                if (usernum == 0) {
                    line = new String(inputPacket.getData(), 0, inputPacket.getLength());
                    line2 = new String(inputPacket2.getData(), 0, inputPacket2.getLength());
                }
                if (usernum == 1) {
                    line = new String(inputPacket.getData(), 0, inputPacket.getLength());
                }
                if (usernum == 2) {
                    line2 = new String(inputPacket2.getData(), 0, inputPacket2.getLength());
                }
                if (usernum == 0) {
                    jsonObject = new JSONObject(line);
                    jsonObject2 = new JSONObject(line2);
                }
                if (usernum == 1) {
                    jsonObject = new JSONObject(line);
                }
                if (usernum == 2) {
                    jsonObject2 = new JSONObject(line2);
                }
                var command = new String();
                var command2 = new String();
                if (usernum == 0) {
                    command = jsonObject.getString("command");
                    command2 = jsonObject2.getString("command");
                }
                if (usernum == 1) {
                    command = jsonObject.getString("command");
                }
                if (usernum == 2) {
                    command2 = jsonObject2.getString("command");
                }
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
                if (usernum == 2){
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
