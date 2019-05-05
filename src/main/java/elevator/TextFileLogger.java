package elevator;

import java.io.IOException;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

class TextFileLogger implements Logger {

    private String logFile;

    TextFileLogger() throws ElevatorSystemException {
        logFile = SystemConfiguration.getConfiguration("logFile");
        try {
            File file = new File(logFile);
            file.createNewFile();
        } catch(IOException ioe) {
            throw new ElevatorSystemException("I/O error while creating log file.");
        } catch(NullPointerException npe) {
            throw new ElevatorSystemException("ERROR: the log file pathname is null.");
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
