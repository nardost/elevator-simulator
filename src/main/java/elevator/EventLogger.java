package elevator;

public class EventLogger {

    private Logger logger;

    private static EventLogger theLogger = null;

    private EventLogger(Logger logger) {
        this.logger = logger;
    }

    //Singleton
    public static EventLogger getInstance(String typeOfLogger) throws ElevatorSystemException {
        if(theLogger == null) {
            synchronized(EventLogger.class) {
                if(theLogger == null) {
                    switch(typeOfLogger) {
                        case "STDOUT":
                            theLogger = new EventLogger(new StandardOutputLogger());
                            break;
                        case "FILE":
                            theLogger = new EventLogger(new TextFileLogger());
                            break;
                        default:
                            throw new ElevatorSystemException("Event logger options are: FILE, STDOUT");
                    }
                }
            }
        }
        return theLogger;
    }

    public void logEvent(String logMessage) throws ElevatorSystemException {
        logger.log(logMessage);
    }
}
