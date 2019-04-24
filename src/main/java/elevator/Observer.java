package elevator;

interface Observer {
    void update(GotoSignal arg) throws ElevatorSystemException;
    void update(ElevatorLocationSignal arg) throws ElevatorSystemException;
    void update(Signal arg) throws ElevatorSystemException;
}
