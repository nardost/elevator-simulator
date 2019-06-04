package elevator;

class EventLogger {

    private Logger logger;

    private static EventLogger theLogger = null;

    private EventLogger(Logger logger) {
        this.logger = logger;
    }

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

    void logEvent(String logMessage) throws ElevatorSystemException {
        getLogger().log(logMessage);
    }

    static void print(String msg) throws ElevatorSystemException {
        StringBuilder sb = new StringBuilder(Utility.formatElapsedTime(System.currentTimeMillis()));
        //sb.append(" (" + Thread.currentThread().getName() + ")");
        sb.append(" " + msg);
        System.out.println(sb.toString());
        EventLogger.getInstance().logEvent(sb.toString());
    }
}
