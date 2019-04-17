package elevator;

import java.io.IOException;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

class TextFileLogger implements Logger {

    private String logFile;

    TextFileLogger() throws ElevatorSystemException {
        logFile = System.getenv("ELEVATOR_EVENTS_LOG_FILE");
        File file = new File(logFile);
        try {
            file.createNewFile();
        } catch(IOException ioe) {
            throw new ElevatorSystemException("ERROR: while opening log file.");//TODO: too revealing?
        }
    }

    @Override
    public void log(String logMessage) throws ElevatorSystemException {
        try {
            Files.write(Paths.get(logFile), (logMessage + "\n").getBytes(), StandardOpenOption.APPEND);
        } catch(IOException ioe) {
            throw new ElevatorSystemException("ERROR: while writing to log file."); //TODO: too revealing?
        }
    }
}
