package elevator;

class ControllerBeta implements Controller {

    @Override
    public void sendControlSignal(ControlSignal signal) throws ElevatorSystemException {
        Building.getInstance().notifyObservers(signal);
    }
/**
    @Override
    public void sendControlSignal(GotoSignal signal) throws ElevatorSystemException {

    }

    @Override
    public void sendControlSignal(ElevatorLocationSignal signal) throws ElevatorSystemException {

    }

    @Override
    public void sendControlSignal(RiderOnBoardSignal signal) throws ElevatorSystemException {

    }

    @Override
    public void sendControlSignal(Signal signal) throws ElevatorSystemException {

    }
*/
    @Override
    public void receiveRequest(Request request) throws ElevatorSystemException  {

    }

    @Override
    public void receiveNotification(Notification notification) throws ElevatorSystemException {
    }
}
