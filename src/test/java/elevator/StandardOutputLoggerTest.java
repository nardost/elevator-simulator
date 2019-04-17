package elevator;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class StandardOutputLoggerTest {

    private StandardOutputLogger consoleLogger;

    @Before
    public void setup() {
        this.consoleLogger = new StandardOutputLogger();
    }

    @Test
    public void logMethodLogsToSystemConsole(){
        ByteArrayOutputStream content = new ByteArrayOutputStream();
        System.setOut(new PrintStream(content));
        try {
            consoleLogger.log("Test Message");
            assertEquals("Test Message\n", content.toString());
        } catch(Exception e) {
            //
        }
    }
}
