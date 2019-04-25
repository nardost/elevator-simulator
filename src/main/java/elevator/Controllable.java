package elevator;

public interface Controllable {
    void receiveControlSignal(ControlSignal signal) throws ElevatorSystemException;
    /**void receiveControlSignal(GotoSignal signal) throws ElevatorSystemException;
    void receiveControlSignal(Signal signal) throws ElevatorSystemException;*/
    void sendRequestToController(Request request) throws ElevatorSystemException;
}
