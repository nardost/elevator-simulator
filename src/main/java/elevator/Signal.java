package elevator;

enum Receiver {ELEVATOR, FLOOR, BUILDING}

public class Signal {
    private Receiver receiverType;
    private int receiverId;
    private int gotoFloor;

    public Signal(Receiver receiverType, int receiverId, int gotoFloor) {
        this.receiverType = receiverType;
        this.receiverId = receiverId;
        this.gotoFloor = gotoFloor;
    }

    public Receiver getReceiverType() {
        return receiverType;
    }

    public int getReceiverId() {
        return receiverId;
    }

    public int getGotoFloor() {
        return gotoFloor;
    }
}
