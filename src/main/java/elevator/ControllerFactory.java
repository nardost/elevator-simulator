package elevator;

class ControllerFactory {

    private ControllerFactory() {
    }

    public static Controller createController() {
        switch(SystemConfiguration.getConfiguration("controller")) {
            case "beta":
                return new ControllerBeta();
            case "alpha":
            default:
                return new ControllerAlpha();
        }
    }
}
