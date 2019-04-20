package elevator;

enum ElementType {CONTROLLER, ELEVATOR, FLOOR, PERSON}

public class Signal {
    private ElementType sender;
    private ElementType receiver;
    private int receiverId;
    private Payload payload;

    public Signal(ElementType sender, ElementType receiver, int receiverId, Payload payload) {
        setSender(sender);
        setReceiver(receiver);
        setReceiverId(receiverId);
        setPayload(payload);
    }

    public ElementType getSender() {
        return sender;
    }

    public ElementType getReceiver() {
        return receiver;
    }

    public int getReceiverId() {
        return receiverId;
    }

    public PayloadType getPayloadType() {
        return getPayload().getPayloadType();
    }

    public int getElevatorNumberFromPayload() {
        return  getPayload().getElevatorNumber();
    }

    public int getFloorNumberFromPayload() {
        return  getPayload().getFloorNumber();
    }

    public Direction getDirectionFromPayload() {
        return  getPayload().getDirection();
    }

    public boolean areDoorsClosedFromPayload() {
        return  getPayload().areDoorsClosed();
    }

    private Payload getPayload() {
        return this.payload;
    }

    private void setSender(ElementType sender) {
        this.sender = sender;
    }

    private void setReceiver(ElementType receiver) {
        this.receiver = receiver;
    }

    private void setReceiverId(int receiverId) {
        this.receiverId = receiverId;
    }

    private void setPayload(Payload payload) {
        this.payload = payload;
    }
}
