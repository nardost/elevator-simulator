package elevator;

/**
 *
 * @author ntessema
 *
 */
public interface Controller {
    void sendControlSignal(GotoSignal signal) throws ElevatorSystemException;
    void sendControlSignal(ElevatorLocationSignal signal) throws ElevatorSystemException;
    void sendControlSignal(Signal signal) throws ElevatorSystemException;
    void receiveRequest(Request request) throws ElevatorSystemException;
    void receiveNotification(Notification notification) throws ElevatorSystemException;
}
