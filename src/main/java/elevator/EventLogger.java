package elevator;

class EventLogger {

    private Logger logger; //For Strategy pattern

    private static EventLogger theLogger = null; //For Singleton pattern

    private EventLogger(Logger logger) {
        this.logger = logger;
    }

    //Singleton
    public static EventLogger getInstance() throws ElevatorSystemException {
        if(theLogger == null) {
            synchronized(EventLogger.class) {
                if(theLogger == null) {
                    theLogger = new EventLogger(LoggerFactory.createLogger());
                }
            }
        }
        return theLogger;
    }

    private Logger getLogger() {
        return logger;
    }

    public void logEvent(String logMessage) throws ElevatorSystemException {
        getLogger().log(logMessage);
    }

    public static void print(String msg) throws ElevatorSystemException {
        StringBuilder sb = new StringBuilder(Utility.formatElapsedTime(System.currentTimeMillis()));
        sb.append(" " + Thread.currentThread().getName());
        sb.append(" " + msg);
        System.out.println(sb.toString());
        EventLogger.getInstance().logEvent(sb.toString());
    }
}
