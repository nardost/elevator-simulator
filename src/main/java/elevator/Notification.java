package elevator;

public class Notification {
    private int notifierId;
    private int notifierLocation;
    private Direction notifierDirection;

    public Notification(int notifierId, int notifierLocation, Direction notifierDirection) throws ElevatorSystemException {
        setNotifierId(notifierId);
        setNotifierLocation(notifierLocation);
        setNotifierDirection(notifierDirection);
    }

    public int getNotifierId() {
        return notifierId;
    }

    public int getNotifierLocation() {
        return notifierLocation;
    }

    public Direction getNotifierDirection() {
        return notifierDirection;
    }

    private void setNotifierId(int notifierId) throws ElevatorSystemException {
        if(notifierId < 1 || notifierId > Building.getInstance().getNumberOfElevators()) {
            throw new ElevatorSystemException("Elevator number is only 1 through " + Building.getInstance().getNumberOfElevators());
        }
        this.notifierId = notifierId;
    }

    private void setNotifierLocation(int notifierLocation) throws ElevatorSystemException {
        if(notifierLocation < 1 || notifierLocation > Building.getInstance().getNumberOfFloors()) {
            throw new ElevatorSystemException("Elevator number is only 1 through " + Building.getInstance().getNumberOfElevators());
        }
        this.notifierLocation = notifierLocation;
    }

    private void setNotifierDirection(Direction notifierDirection) {
        this.notifierDirection = notifierDirection;
    }
}
