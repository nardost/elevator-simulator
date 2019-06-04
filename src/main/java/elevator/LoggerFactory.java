package elevator;

class LoggerFactory {

    private LoggerFactory() {
    }

    static Logger createLogger() throws ElevatorSystemException {
        switch(SystemConfiguration.getConfiguration("logger")) {
            case "stdout":
                return new StandardOutputLogger();
            case "file":
            default:
                return new TextFileLogger();
        }
    }
}
