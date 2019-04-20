package elevator;

class ControllerFactory {

    private ControllerFactory() {
    }

    public static Controller createController() {
        switch(ConfigurationManager.getConfig("controller")) {
            case "b":
                return new ControllerBeta();
            case "a":
            default:
                return new ControllerAlpha();
        }
    }
}
