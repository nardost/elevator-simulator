package elevator;

public class ConfigurationManager {

    //TODO: this should be changed... read config from JSON ...
    private static final int NUMBER_OF_FLOORS = 5;
    private static final int NUMBER_OF_ELEVATORS = 2;
    private static final double DEFAULT_SPEED = 1.0;
    private static final int DEFAULT_FLOOR = 1;
    private static final String LOGGER = "file";
    private static final String LOG_FILE = "logs/events-log.txt";
    private static final String CONTROLLER = "a";

    public static String getConfig(String config) {
        switch (config) {
            case "logger":
                return LOGGER;
            case "log-file":
                return LOG_FILE;
            case "controller":
                return CONTROLLER;
            case "number-of-floors":
                return Integer.toString(NUMBER_OF_FLOORS);
            case "number-of-elevators":
                return Integer.toString(NUMBER_OF_ELEVATORS);
            case "speed":
                return Double.toString(DEFAULT_SPEED);
            case "default-floor":
                return Integer.toString(DEFAULT_FLOOR);
            default:
                return "";
        }
    }

}
