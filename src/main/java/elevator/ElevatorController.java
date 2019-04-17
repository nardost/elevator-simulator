package elevator;

import java.util.List;


/**
 * @author ntessema
 *
 */
public class ElevatorController implements Controller {

    private List<Floor> floors;
    private List<ElevatorBehavior> elevators;

    private EventLogger logger;

    public ElevatorController() {
    }

    @Override
    public void signalAll(Signal signal) {

    }

    @Override
    public void receiveNotification(Signal signal) {

    }

    @Override
    public void run(Signal signal) {
        //TODO: run signal
    }

    @Override
    public void stop(Signal signal) {
        //TODO: stop signal
    }
}
