package elevator;

import java.io.IOException;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

class TextFileLogger implements Logger {

    private String logFile;

    TextFileLogger() throws ElevatorSystemException {
        logFile = SystemConfiguration.getConfig("log-file");
        try {
            File file = new File(logFile);
            file.createNewFile();
        } catch(IOException ioe) {
            throw new ElevatorSystemException("ERROR: while creating log file.");
        } catch(NullPointerException npe) {
            throw new ElevatorSystemException("ERROR: ELEVATOR_EVENTS_LOG_FILE (the log file environment variable) is not defined.");
        }
    }

    @Override
    public void log(String logMessage) throws ElevatorSystemException {
        try {
            Files.write(Paths.get(logFile), (logMessage + "\n").getBytes(), StandardOpenOption.APPEND);
        } catch(IOException ioe) {
            throw new ElevatorSystemException("ERROR: while writing to log file.");
        }
    }
}
