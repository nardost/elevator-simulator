package elevator;

interface Observer {
    void update(Message message) throws ElevatorSystemException;
}
