package elevator;

enum RequestType {ELEVATOR, FLOOR}

class Request {

    private RequestType type;
    private int floor;
    private Direction direction;

    public Request(RequestType type, int floor, Direction direction) throws ElevatorSystemException {
        setType(type);
        setFloor(floor);
        setDirection(direction);
    }

    public RequestType getType() {
        return type;
    }

    public int getFloor() {
        return floor;
    }

    public Direction getDirection() {
        return direction;
    }

    private void setType(RequestType type) {
        this.type = type;
    }

    private void setFloor(int floor) throws ElevatorSystemException {
        if(floor < 1 || floor > Building.getInstance().getNumberOfFloors()) {
            throw new ElevatorSystemException("Floor must be between 1 and " + Building.getInstance().getNumberOfFloors());
        }
        this.floor = floor;
    }

    private void setDirection(Direction direction) {
        this.direction = direction;
    }
}
