package elevator;


/**
 * @author ntessema
 *
 * Using the Strategy Pattern
 *
 */
class ElevatorController {

    private Controller controller;

    private static ElevatorController controlCenter;

    private ElevatorController() {
        setController(ControllerFactory.createController());
    }

    public static ElevatorController getInstance() {
        if(controlCenter == null) {
            synchronized(ElevatorController.class) {
                if(controlCenter == null) {
                    controlCenter = new ElevatorController();
                }
            }
        }
        return controlCenter;
    }

    public void sendControlSignal(ControlSignal signal) throws ElevatorSystemException {
        Building.getInstance().notifyObservers(signal);
    }

    public void receiveRequest(Request request) throws ElevatorSystemException {
        controller.receiveRequest(request);
    }

    public void receiveNotification(Notification notification) throws ElevatorSystemException {
        controller.receiveNotification(notification);
    }

    private void setController(Controller controller) {
        this.controller = controller;
    }
}
