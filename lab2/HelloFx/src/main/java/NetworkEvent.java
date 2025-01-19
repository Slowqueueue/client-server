public interface NetworkEvent {
    record ClearObjects() implements NetworkEvent {
    }

    record CloseConnection() implements NetworkEvent {
    }

    record RequestObjectByIndex(int index) implements NetworkEvent {
    }

    record RequestObjectList() implements NetworkEvent {
    }

    record RequestObjectListSize() implements NetworkEvent {
    }

    record ResponseObject(String object, String type) implements NetworkEvent {
    }

    record ResponseObjectByIndex(int index, String type, String object) implements NetworkEvent {
    }

    record ResponseObjectList(GraphicalObject[] objects) implements NetworkEvent {
    }

    record ResponseObjectListSize(int size) implements NetworkEvent {
    }
}
