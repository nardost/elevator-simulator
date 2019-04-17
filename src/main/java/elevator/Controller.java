package elevator;

import java.util.Observable;

/**
 *
 * @author ntessema
 *
 * Strategy
 */
public interface Controller {
    void signalAll(Signal signal);
    void receiveNotification(Signal signal);
    void run(Signal signal) throws ElevatorSystemException;
    void stop(Signal signal) throws ElevatorSystemException;
}
