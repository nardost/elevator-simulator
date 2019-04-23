package elevator;

public interface Controllable {
    void receiveControlSignal(Signal signal) throws ElevatorSystemException;
    void sendRequestToController(Request request) throws ElevatorSystemException;
}
