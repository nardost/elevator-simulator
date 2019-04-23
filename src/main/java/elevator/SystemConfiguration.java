package elevator;

public class SystemConfiguration {

    //TODO: this should be changed... read config from JSON ...
    private static final int NUMBER_OF_FLOORS = 20;
    private static final int NUMBER_OF_ELEVATORS = 4;
    private static final int FLOOR_TIME = 1;
    private static final int DOOR_TIME = 2;
    private static final int TIME_OUT = 10;
    private static final int DEFAULT_FLOOR = 1;
    private static final String LOGGER = "file";
    private static final String LOG_FILE = "logs/events-log.txt";
    private static final String CONTROLLER = "alpha";
    private static final String RIDER_GENERATOR = "test1";

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
            case "floor-time":
                return Integer.toString(FLOOR_TIME);
            case "door-time":
                return Integer.toString(DOOR_TIME);
            case "time-out":
                return Integer.toString(TIME_OUT);
            case "default-floor":
                return Integer.toString(DEFAULT_FLOOR);
            case "rider-generator":
                return RIDER_GENERATOR;
            default:
                return "";
        }
    }

}
