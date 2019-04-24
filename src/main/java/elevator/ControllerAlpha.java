package elevator;

class ControllerAlpha implements Controller {

    @Override
    public void sendControlSignal(GotoSignal signal) throws ElevatorSystemException {
        System.out.println("Controller ALPHA sending GotoSignal to " + signal.getElevatorId());
        System.out.println("[GOTO FLOOR=" + signal.getGotoFloor() + ", ELEVATOR=" + signal.getElevatorId() + ", DIRECTION=" + signal.getDirection() + "]");
        Building.getInstance().notifyObservers(signal);
    }

    @Override
    public void sendControlSignal(ElevatorLocationSignal signal) throws ElevatorSystemException {
        System.out.println("Controller ALPHA announcing location of elevator " + signal.getElevatorId());
        Building.getInstance().notifyObservers(signal);
    }

    @Override
    public void sendControlSignal(Signal signal) throws ElevatorSystemException {
        System.out.println("Controller ALPHA sending signal " + signal.getPayloadType());
        Building.getInstance().notifyObservers(signal);
    }

    @Override
    public void receiveRequest(Request request) throws ElevatorSystemException  {
        //TODO: Process request and sendControlSignal()
        //TODO: Alpha always picks elevator 1.
        switch(request.getType()) {
            case ELEVATOR:
                int elevatorId = 2; // elevator selected
                //TODO: set status of elevator as busy. on next request pick another elevator....
                System.out.println("Controller ALPHA received an Elevator Request from " + request.getFloor() + " to go " + request.getDirection().toString());
                //sendControlSignal(Signal.createGoToFloorSignal(2, request.getFloor(), request.getDirection()));
                GotoSignal signal = new GotoSignal(elevatorId, request.getFloor(), request.getDirection());
                System.out.println("receiveRequest - [GOTO FLOOR=" + signal.getGotoFloor() + ", ELEVATOR=" + signal.getElevatorId() + ", DIRECTION=" + signal.getDirection() + "]");
                sendControlSignal(signal);
                break;
            case FLOOR:
                elevatorId = request.getElevatorId();
                int destination = request.getFloor();
                Direction direction = request.getDirection();
                //TODO: Update queue of elevator. numberOfRiders++
                //EnterRiderSignal signal = new EnterRiderSignal(elevatorId, destination);
                System.out.println("Controller ALPHA received a Floor Request to " + request.getFloor());
        }
    }

    @Override
    public void receiveNotification(Notification notification) throws ElevatorSystemException {
        //TODO: Process request and sendControlSignal()
        sendControlSignal(new ElevatorLocationSignal(notification.getNotifierId(), notification.getNotifierLocation(), notification.getNotifierDirection()));
        //sendControlSignal(Signal.createAnnounceLocationOfElevatorSignal(notification.getNotifierId(), notification.getNotifierLocation(), notification.getNotifierDirection()));
    }
}
