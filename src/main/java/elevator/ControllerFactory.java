package elevator;

class ControllerFactory {

    private ControllerFactory() {
    }

    public static Controller createController() {
        switch(SystemConfiguration.getConfiguration("controller")) {
            case "alpha":
                return new ControllerAlpha();
            case "beta":
                return new ControllerBeta();
            default:
                return new ControllerNull();
        }
    }
}
