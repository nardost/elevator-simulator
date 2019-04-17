package elevator;

public interface Controllable {
    void receiveSignal(Signal signal); //TODO: parameters to be decided.
    void notifyController(Signal signal);//TODO: parameters to be decided
    void run() throws ElevatorSystemException;
    void stop() throws ElevatorSystemException;
}
