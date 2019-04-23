package elevator;

enum ElementType {CONTROLLER, ELEVATOR, FLOOR, PERSON, ALL, ALL_FLOORS, ALL_ELEVATORS}

class Signal {
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

    /**
     * Utility methods
     */

    public static Signal createGoToFloorSignal(int elevatorId, int floorId, Direction direction)
            throws ElevatorSystemException {
        Payload payload = new Payload(PayloadType.CONTROLLER_TO_ELEVATOR__GOTO_FLOOR_DIRECTION, elevatorId, floorId, 1, direction);
        return new Signal(ElementType.CONTROLLER, ElementType.ELEVATOR, elevatorId, payload);
    }


    public static Signal createRunAllSignal() throws ElevatorSystemException {
        Payload payload = new Payload(PayloadType.CONTROLLER_TO_ALL__RUN, 1, 1, 1, Direction.IDLE);//1 and IDLE are arbitrary
        return new Signal(ElementType.CONTROLLER, ElementType.ALL, 1, payload);
    }

    public static Signal createAnnounceLocationOfElevatorSignal(int elevatorNumber, int floorNumber, Direction direction) throws ElevatorSystemException {
        Payload payload = new Payload(
                PayloadType.CONTROLLER_TO_FLOORS__LOCATION_OF_ALL_ELEVATORS,
                elevatorNumber,
                floorNumber,
                1, //dont care
                direction);
        return new Signal(ElementType.CONTROLLER, ElementType.ALL_FLOORS, 1, payload);
    }
    public static Signal createGeneratePersonSignal(int floorNumber, int destination) throws ElevatorSystemException {
        Payload payload = new Payload(PayloadType.CONTROLLER_TO_FLOOR__GENERATE_PERSON, 1, floorNumber, destination, Direction.IDLE);
        return new Signal(ElementType.CONTROLLER, ElementType.FLOOR, floorNumber, payload);
    }
    public static Signal createElevatorArrivedSignal(int elevatorNumber, int floorNumber, Direction direction) throws ElevatorSystemException {
        Payload payload = new Payload(
                PayloadType.FLOOR_TO_WAITING_PERSONS__ELEVATOR_ARRIVAL,
                elevatorNumber,
                floorNumber,
                1,
                direction);
        return new Signal(ElementType.FLOOR, ElementType.PERSON, 1, payload);
    }

    /**
     * Getters and Setters
     */

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

    public int getField1() {
        return  getPayload().getField1();
    }

    public int getField2() {
        return  getPayload().getField2();
    }

    public int getField3() {
        return getPayload().getField3();
    }

    public Direction getField4() {
        return  getPayload().getField4();
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

enum PayloadType {
    CONTROLLER_TO_FLOORS__LOCATION_OF_ALL_ELEVATORS,
    CONTROLLER_TO_FLOOR__GENERATE_PERSON,
    CONTROLLER_TO_ELEVATOR__GOTO_FLOOR_DIRECTION,
    CONTROLLER_TO_ALL__RUN,
    FLOOR_TO_WAITING_PERSONS__ELEVATOR_ARRIVAL,
    ELEVATOR_TO_RIDING_PERSONS__ARRIVAL_TO_FLOOR,
}

class Payload {
    private PayloadType payloadType;
    private int field1;
    private int field2;
    private int field3;
    private Direction field4;

    public Payload(PayloadType payloadType, int field1, int field2, int field3, Direction field4)
            throws ElevatorSystemException {
        setPayloadType(payloadType);
        setField1(field1);
        setField2(field2);
        setField3(field3);
        setField4(field4);
    }

    public PayloadType getPayloadType() {
        return payloadType;
    }

    public int getField1() {
        return field1;
    }

    public int getField2() {
        return field2;
    }

    public int getField3() {
        return field3;
    }

    public Direction getField4() {
        return field4;
    }

    private void setPayloadType(PayloadType payloadType) {
        this.payloadType = payloadType;
    }

    private void setField1(int field1) throws ElevatorSystemException {
        if(field1 < 1 || field1 > Building.getInstance().getNumberOfElevators()) {
            throw new ElevatorSystemException("Elevator number is only 1 through " + Building.getInstance().getNumberOfElevators());
        }
        this.field1 = field1;
    }

    private void setField2(int field2) throws ElevatorSystemException {
        if(field2 < 1 || field2 > Building.getInstance().getNumberOfFloors()) {
            throw new ElevatorSystemException("Floor number is only 1 through " + Building.getInstance().getNumberOfFloors());
        }
        this.field2 = field2;
    }

    private void setField3(int field3) throws ElevatorSystemException {
        if(field3 < 1 || field3 > Building.getInstance().getNumberOfFloors()) {
            throw new ElevatorSystemException("Floor number is only 1 through " + Building.getInstance().getNumberOfFloors());
        }
        this.field3 = field3;
    }

    private void setField4(Direction field4) {
        this.field4 = field4;
    }
}

