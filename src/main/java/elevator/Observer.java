package elevator;

interface Observer {
    void update(Signal arg) throws ElevatorSystemException;
}
