package elevator;

class ControllerBeta implements Controller {
    @Override
    public void sendControlSignal(Signal signal) throws ElevatorSystemException {
        System.out.println("Controller BETA sending signal " + signal.getPayloadType());
        Building.getInstance().notifyObservers(signal);
    }

    @Override
    public void receiveRequest(Request request) throws ElevatorSystemException  {
        //TODO: Process request and sendControlSignal()
        //TODO: Beta always picks elevator 2.
        switch(request.getType()) {
            case ELEVATOR:
                System.out.println("Controller BETA received an Elevator Request from " + request.getFloor() + " to go " + request.getDirection().toString());
                sendControlSignal(Signal.createGoToFloorSignal(2, request.getFloor(), request.getDirection()));
                break;
            case FLOOR:
                System.out.println("Controller BETA received a Floor Request to " + request.getFloor());
        }
    }

    @Override
    public void receiveNotification(Notification notification) throws ElevatorSystemException {
        //TODO: Process request and sendControlSignal()
        sendControlSignal(Signal.createAnnounceLocationOfElevatorSignal(notification.getNotifierId(), notification.getNotifierLocation(), notification.getNotifierDirection()));
    }
}
