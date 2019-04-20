package elevator;

class LoggerFactory {

    private LoggerFactory() {
    }

    public static Logger createLogger() throws ElevatorSystemException {
        switch(ConfigurationManager.getConfig("logger")) {
            case "stdout":
                return new StandardOutputLogger();
            case "file":
            default:
                return new TextFileLogger();
        }
    }
}
