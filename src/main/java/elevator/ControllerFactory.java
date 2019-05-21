package elevator;

class ControllerFactory {

    private ControllerFactory() {
    }

    public static Controller createController() throws ElevatorSystemException {
        switch(SystemConfiguration.getConfiguration("controller")) {
            case "beta":
                return new ControllerBeta();
            default:
                return new ControllerNull();
        }
    }
}
