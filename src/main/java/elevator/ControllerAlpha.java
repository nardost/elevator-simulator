package elevator;

class ControllerAlpha implements Controller {
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
                System.out.println("Controller ALPHA received an Elevator Request from " + request.getFloor() + " to go " + request.getDirection().toString());
                sendControlSignal(Signal.createGoToFloorSignal(2, request.getFloor(), request.getDirection()));
                break;
            case FLOOR:
                System.out.println("Controller ALPHA received a Floor Request to " + request.getFloor());
        }
    }

    @Override
    public void receiveNotification(Notification notification) throws ElevatorSystemException {
        //TODO: Process request and sendControlSignal()
        sendControlSignal(Signal.createAnnounceLocationOfElevatorSignal(notification.getNotifierId(), notification.getNotifierLocation(), notification.getNotifierDirection()));
    }
}
