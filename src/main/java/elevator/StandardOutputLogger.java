package elevator;

class StandardOutputLogger implements Logger {

    @Override
    public void log(String logMessage) {
        System.out.println(logMessage);
    }
}
