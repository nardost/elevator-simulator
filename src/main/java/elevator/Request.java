package elevator;

class Request {
    private Direction direction;
    private int floor;

    public Request(int floor, Direction direction) throws ElevatorSystemException {
        setDirection(direction);
        setFloor(floor);
    }

    public Direction getDirection() {
        return direction;
    }

    public int getFloor() {
        return floor;
    }

    private void setDirection(Direction direction) {
        this.direction = direction;
    }

    private void setFloor(int floor) throws ElevatorSystemException {
        if(floor < 1 || floor > Building.getInstance().getNumberOfFloors()) {
            throw new ElevatorSystemException("Floor must be between 1 and " + Building.getInstance().getNumberOfFloors());
        }
        this.floor = floor;
    }
}
