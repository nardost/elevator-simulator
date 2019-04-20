package elevator;

/**
 *
 * @author ntessema
 *
 */
public interface Controller {
    void sendControlSignal(Signal signal);
    void receiveRequest(Request request) throws ElevatorSystemException;
}
