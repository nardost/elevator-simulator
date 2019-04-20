package elevator;

public interface Controllable {
    void receiveControlSignal(Signal signal);
    void sendRequestToController(Request request) throws ElevatorSystemException;
}
