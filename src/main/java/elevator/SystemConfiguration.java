package elevator;

import java.util.Hashtable;

public class SystemConfiguration {

    private static Hashtable<String, String> configurationTable = null;

    private SystemConfiguration() throws ElevatorSystemException {
        configurationTable = new Hashtable<>();
        setConfig("logger");
        setConfig("log-file");
        setConfig("controller");
        setConfig("number-of-floors");
        setConfig("number-of-elevators");
        setConfig("floor-time");
        setConfig("door-time");
        setConfig("time-out");
        setConfig("default-floor");
        setConfig("rider-generator");
    }

    public static void initializeSystemConfiguration() throws ElevatorSystemException {
        if(configurationTable == null) {
            synchronized (SystemConfiguration.class) {
                if(configurationTable == null) {
                    new SystemConfiguration();
                }
            }
        }
    }

    private void setConfig(String config) throws ElevatorSystemException {

        //TODO: get config values from a JSON / XML file here.

        final int NUMBER_OF_FLOORS = 20;
        final int NUMBER_OF_ELEVATORS = 4;
        final int FLOOR_TIME = 1;
        final int DOOR_TIME = 2;
        final int TIME_OUT = 10;
        final int DEFAULT_FLOOR = 1;
        final String LOGGER = "file";
        final String LOG_FILE = "logs/events-log.txt";
        final String CONTROLLER = "alpha";
        final String RIDER_GENERATOR = "test1";

        String value;

        switch (config) {
            case "logger":
                value = LOGGER;
                break;
            case "log-file":
                value = LOG_FILE;
                break;
            case "controller":
                value = CONTROLLER;
                break;
            case "number-of-floors":
                value = Integer.toString(NUMBER_OF_FLOORS);
                break;
            case "number-of-elevators":
                value = Integer.toString(NUMBER_OF_ELEVATORS);
                break;
            case "floor-time":
                value = Integer.toString(FLOOR_TIME);
                break;
            case "door-time":
                value = Integer.toString(DOOR_TIME);
                break;
            case "time-out":
                value = Integer.toString(TIME_OUT);
                break;
            case "default-floor":
                value = Integer.toString(DEFAULT_FLOOR);
                break;
            case "rider-generator":
                value = RIDER_GENERATOR;
                break;
            default:
                throw new ElevatorSystemException("No configuration found for " + config);
        }
        configurationTable.put(config, value);
    }

    public static String getConfiguration(String config) {
        return configurationTable.get(config);
    }

}
