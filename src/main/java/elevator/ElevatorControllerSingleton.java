package elevator;

import java.util.List;


/**
 * @author ntessema
 *
 * Using the Strategy Pattern
 *
 */
public class ElevatorControllerSingleton implements Controller {

    //TODO: make this Strategy: 1.make ElevatorControllerSingleton theController below, 2. add member Controller delegate. in getInstance 3. theController = new ElevatorControllerSingleton(new ControllerAlpha()) ...

    private static Controller theController = null;

    private ElevatorControllerSingleton() {
    }

    public static Controller getInstance() {
        if(theController == null) {
            synchronized(ElevatorControllerSingleton.class) {
                if(theController == null) {
                    switch(ConfigurationManager.getConfig("controller")) {
                        case "b":
                            theController = new ControllerBeta();
                            break;
                        case "a":
                        default:
                            theController = new ControllerAlpha();
                    }
                }
            }
        }
        return theController;
    }

    @Override
    public void sendControlSignal(Signal signal) {

    }

    @Override
    public void receiveRequest(Request request) {

    }
}
