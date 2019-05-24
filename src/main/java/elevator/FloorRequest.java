package elevator;

import java.util.Objects;

public class FloorRequest implements FloorRequestFlyweight {

    private int floorOfOrigin;
    private Direction directionRequested;

    FloorRequest(int floorOfOrigin, Direction directionRequested) throws ElevatorSystemException {
        Validator.validateFloorNumber(floorOfOrigin);
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
    public int hashCode() {
        return Objects.hash(getFloorOfOrigin(), getDirectionRequested().toString());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(Integer.toString(getFloorOfOrigin()));
        sb.append(":");
        sb.append(getDirectionRequested().toString());
        return sb.toString();
    }

    int getFloorOfOrigin() {
        return floorOfOrigin;
    }

    private void setFloorOfOrigin(int floorOfOrigin) throws ElevatorSystemException {
        Validator.validateFloorNumber(floorOfOrigin);
        this.floorOfOrigin = floorOfOrigin;
    }

    Direction getDirectionRequested() {
        return directionRequested;
    }

    private void setDirectionRequested(Direction directionRequested) {
        this.directionRequested = directionRequested;
    }
}
