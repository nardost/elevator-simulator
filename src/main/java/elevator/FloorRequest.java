package elevator;

public class FloorRequest implements FloorRequestFlyweight {

    private int floorOfOrigin;
    private Direction directionRequested;

    FloorRequest(int floorOfOrigin, Direction directionRequested) throws ElevatorSystemException {
        setFloorOfOrigin(floorOfOrigin);
        setDirectionRequested(directionRequested);
    }

    @Override
    public boolean equals(Object object) {
        if(object instanceof FloorRequest) {
            if(
                (getFloorOfOrigin() == ((FloorRequest) object).getFloorOfOrigin()) &&
                (getDirectionRequested() == ((FloorRequest) object).getDirectionRequested())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void relayFloorRequest(int personId, long time) throws ElevatorSystemException {
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(Integer.toString(getFloorOfOrigin()));
        sb.append(directionRequested.toString());
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
