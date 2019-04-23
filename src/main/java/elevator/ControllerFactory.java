package elevator;

class ControllerFactory {

    private ControllerFactory() {
    }

    public static Controller createController() {
        switch(SystemConfiguration.getConfig("controller")) {
            case "beta":
                return new ControllerBeta();
            case "alpha":
            default:
                return new ControllerAlpha();
        }
    }
}
