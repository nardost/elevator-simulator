package elevator;

class ControllerFactory {

    private ControllerFactory() {
    }

    public static Controller createController() {
        switch(SystemConfiguration.getConfiguration("controller")) {
            case "alpha":
                return new ControllerAlpha();
            default:
                return new ControllerNull();
        }
    }
}
