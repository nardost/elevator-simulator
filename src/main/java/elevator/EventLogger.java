package elevator;

import java.util.concurrent.TimeUnit;

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

    /**
     * Strategy pattern. Delegates to a specific logging object.
     */
    public void logEvent(String logMessage) throws ElevatorSystemException {
        getLogger().log(logMessage);
    }

    public static String formatElapsedTime(long nanoTime) throws ElevatorSystemException  {
        long elapsedTime = nanoTime - Building.getInstance().getZeroTime();
        long elapsedSeconds = TimeUnit.SECONDS.convert(elapsedTime, TimeUnit.NANOSECONDS);
        long s = elapsedSeconds % 60;
        long m = ((elapsedSeconds - s) % 3600) / 60;
        long h = (elapsedSeconds - (elapsedSeconds - s) % 3600) / 60;
        return String.format("%02d:%02d:%02d", h, m, s);
    }

    public static void print(String msg) throws ElevatorSystemException {
        String eventString = formatElapsedTime(System.nanoTime()) + " " + msg;
        System.out.println(eventString);
        EventLogger.getInstance().logEvent(eventString);//TODO: change to logEvent()...
    }
}
