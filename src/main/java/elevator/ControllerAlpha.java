package elevator;

class ControllerAlpha implements Controller {

    private static int requestNumber = 0;

    @Override
    public void sendControlSignal(ControlSignal signal) throws ElevatorSystemException {
        Building.getInstance().notifyObservers(signal);
    }
/**
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
    public void sendControlSignal(RiderOnBoardSignal signal) throws ElevatorSystemException {
        System.out.println("Controller ALPHA announcing location of elevator " + signal.getElevatorId());
        Building.getInstance().notifyObservers(signal);
    }

    @Override
    public void sendControlSignal(Signal signal) throws ElevatorSystemException {
        System.out.println("Controller ALPHA sending signal " + signal.getPayloadType());
        Building.getInstance().notifyObservers(signal);
    }
*/
    @Override
    public void receiveRequest(Request request) throws ElevatorSystemException  {
        //TODO: Process request and sendControlSignal()

        switch(request.getType()) {
            case ELEVATOR:
                int selectedElevatorId = 1 + (requestNumber++ % 4); //This is the elevator selected by Controller.
                int requestedFromFloorNumber = request.getFloor(); //This is the floor from which elevator request is made.
                Direction directionRequested = request.getDirection();

                //TODO: set status of elevator as busy. on next request pick another elevator....
                System.out.println("Controller ALPHA received an Elevator Request from " + request.getFloor() + " to go " + request.getDirection().toString());
                //sendControlSignal(Signal.createGoToFloorSignal(2, request.getFloor(), request.getDirection()));
                ControlSignal gotoSignal = new GotoSignal(selectedElevatorId, requestedFromFloorNumber, directionRequested);
                //System.out.println("receiveRequest - [GOTO FLOOR=" + gotoSignal.getGotoFloor() + ", ELEVATOR=" + gotoSignal.getElevatorId() + ", DIRECTION=" + gotoSignal.getDirection() + "]");
                sendControlSignal(gotoSignal);
                break;
            case FLOOR:
                int floorRequestedFromElevatorId = request.getElevatorId(); //This is the elevator from which a floor request is made.
                int requestedDestinationFloor = request.getFloor();
                Direction requestedDirection = request.getDirection();
                //TODO: Update queue of elevator. numberOfRiders++
                //EnterRiderSignal signal = new EnterRiderSignal(elevatorId, destination);
                ControlSignal robSignal = new RiderOnBoardSignal(floorRequestedFromElevatorId, requestedDestinationFloor);
                //System.out.println("Controller ALPHA received a Floor Request to " + request.getFloor());
                sendControlSignal(robSignal);
        }
    }

    @Override
    public void receiveNotification(Notification notification) throws ElevatorSystemException {
        //TODO: Process request and sendControlSignal()
        sendControlSignal(new ElevatorLocationSignal(notification.getNotifierId(), notification.getNotifierLocation(), notification.getNotifierDirection()));
        //sendControlSignal(Signal.createAnnounceLocationOfElevatorSignal(notification.getNotifierId(), notification.getNotifierLocation(), notification.getNotifierDirection()));
    }
}
