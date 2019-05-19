package elevator;

public class FloorRequest implements FloorRequestFlyweight {

    private int floorOfOrigin;
    private Direction directionRequested;

    FloorRequest(int floorOfOrigin, Direction directionRequested) throws ElevatorSystemException {
        setFloorOfOrigin(floorOfOrigin);
        setDirectionRequested(directionRequested);
    }

    @Override
    public void relayFloorRequest(int personId, long time) throws ElevatorSystemException {
        Building.getInstance().relayFloorRequestToControlCenter(this, personId, time);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(Integer.toString(getFloorOfOrigin()));
        sb.append((directionRequested == Direction.UP) ? "U" : "D");
        return sb.toString();
    }

    int getFloorOfOrigin() {
        return floorOfOrigin;
    }

    private void setFloorOfOrigin(int floorOfOrigin) throws ElevatorSystemException {
        if(floorOfOrigin > Building.getInstance().getNumberOfFloors()) {
            throw new ElevatorSystemException("Floor should be between 1 and " + Building.getInstance().getNumberOfFloors());
        }
        this.floorOfOrigin = floorOfOrigin;
    }

    Direction getDirectionRequested() {
        return directionRequested;
    }

    private void setDirectionRequested(Direction directionRequested) {
        this.directionRequested = directionRequested;
    }
}