package elevator;

/**
 *
 * @author ntessema
 *
 */
public interface Controller {
    void sendControlSignal(Signal signal) throws ElevatorSystemException;
    void receiveRequest(Request request) throws ElevatorSystemException;
    void receiveNotification(Notification notification) throws ElevatorSystemException;
}
