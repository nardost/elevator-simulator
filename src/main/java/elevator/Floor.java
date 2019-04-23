package elevator;

import java.util.*;

class Floor implements Observer, Observable, Controllable {

    private int floorNumber;
    private boolean upButtonActive;
    private boolean downButtonActive;
    
    private List<Observer> waitingRiders;
    private List<Rider> doneRiders;

    private static int instanceCounter = 0;

    public Floor(boolean upButtonActive, boolean downButtonActive) {
        setFloorNumber(++instanceCounter);
        setUpButtonActive(upButtonActive);
        setDownButtonActive(downButtonActive);

        setWaitingRiders(Collections.synchronizedList(new ArrayList<>()));
        setDoneRiders(Collections.synchronizedList(new ArrayList<>()));
    }

    private void run() throws ElevatorSystemException {
        Iterator<Observer> iterator = getWaitingRiders().iterator();
        while(iterator.hasNext()) {
            Person person = (Person) iterator.next();
            person.requestElevator();
        }
    }

    @Override
    public void receiveControlSignal(Signal signal) throws ElevatorSystemException {
        PayloadType payloadType = signal.getPayloadType();
        if(payloadType == PayloadType.CONTROLLER_TO_FLOOR__GENERATE_PERSON) {

            int destination = signal.getField3();

            Person person = new Person(getFloorNumber(), destination);
            addToWaitingRidersList(person);
            System.out.println("Floor[" + getFloorNumber() + "] created Person [" + person.getId() + "] destined to Floor[" + destination +"]... ");
            run();
        }
        if(payloadType == PayloadType.CONTROLLER_TO_FLOORS__LOCATION_OF_ALL_ELEVATORS) {

            int elevatorNumber = signal.getField1();
            int floorNumber = signal.getField2();
            Direction directionOfElevator = signal.getField4();

            if(floorNumber == getFloorNumber()) {
                //TODO: Elevator is here. Notify waiting Persons.
                System.out.println("Elevator[" + elevatorNumber + "] arrived here (Floor[" + getFloorNumber() + "]).");
                notifyObservers(Signal.createElevatorArrivedSignal(elevatorNumber, floorNumber, directionOfElevator));
                //TODO: delete from waitingRidersList and add to done list
                //TODO: remove all whose intended direction is same as elevator direction
                //TODO concurrent modification exception prevents the next line.
                removeFromWaitingRidersList(directionOfElevator);

            }
        }
        if(payloadType == PayloadType.CONTROLLER_TO_ALL__RUN) {
            //
        }
    }

    public void addToDoneList(Rider rider) throws ElevatorSystemException {
        try {
            getDoneRiders().listIterator().add(rider);
            System.out.println("Person[" + rider.getId() + "] added to done list.");
        } catch(NullPointerException npe) {
            throw new ElevatorSystemException("INTERNAL ERROR: done riders list is null.");
        }
    }

    @Override
    public void sendRequestToController(Request request) throws ElevatorSystemException {
        Building.getInstance().relayRequestToControlCenter(request);
    }

    /**
     *
     * ************ Observable methods ************************
     */

    void addToWaitingRidersList(Person person) throws ElevatorSystemException {
        try {
            getWaitingRiders().listIterator().add(person);
            System.out.println(countObservers() + " : number of people waiting for elevator on floor " + getFloorNumber());
        } catch(NullPointerException npe) {
            throw new ElevatorSystemException("INTERNAL ERROR: waitingRidersList is null.");
        }
    }


    //TODO: I keep getting ConcurrentModificationException when I try to remove from the list.
    //TODO: Instead of removing, I introduced a status member in Person - DONE, WAITING.
    void removeFromWaitingRidersList(Direction directionOfElevator) throws ElevatorSystemException {

        List<Observer> waitingList = getWaitingRiders();
        ListIterator<Observer> li = waitingList.listIterator();
        while(li.hasNext()) {
            Person p = (Person) li.next();
            Direction directionOfPerson = (p.getDestinationFloor() > getFloorNumber()) ? Direction.UP : Direction.DOWN;
            if(directionOfElevator == directionOfPerson) {
                p.setStatus(RiderStatus.RIDING);
            }
        }
    }

    @Override
    public void addObserver(Observer o) throws ElevatorSystemException {
        addToWaitingRidersList((Person) o);
    }

    @Override
    public void deleteObserver(Observer o) throws ElevatorSystemException {
        //deleteFromWaitingRidersList((Person) o);
    }

    @Override
    public void notifyObservers(Signal signal) throws ElevatorSystemException {
        for(Observer observer : getWaitingRiders()) {
            observer.update(signal);
        }
    }

    @Override
    public int countObservers() throws ElevatorSystemException {

        if(getWaitingRiders() == null) {
            throw new ElevatorSystemException("INTERNAL ERROR: waiting riders list is null for Floor " + getFloorNumber());
        }
        return getWaitingRiders().size();
    }


    /**
     *
     * ******** Observer methods *******************
     */

    @Override
    public void update(Signal signal) throws ElevatorSystemException { //TODO: Building updates Floor with signal. Floor acts on the signal
        if(signal.getReceiver() == ElementType.ALL || signal.getReceiver() == ElementType.ALL_FLOORS ||
                (signal.getReceiver() == ElementType.FLOOR && signal.getReceiverId() == getFloorNumber())) {
            //System.out.println("Floor[" + getFloorNumber() + "] : " + signal.getPayloadType().toString());
            receiveControlSignal(signal);
        }
    }


    public int getFloorNumber() {
        return floorNumber;
    }

    public boolean isUpButtonActive() {
        return upButtonActive;
    }

    public boolean isDownButtonActive() {
        return downButtonActive;
    }

    private List<Observer> getWaitingRiders() {
        return waitingRiders;
    }

    private List<Rider> getDoneRiders() {
        return doneRiders;
    }

    private void setFloorNumber(int floorNumber) {
        this.floorNumber = floorNumber;
    }

    public void  setUpButtonActive(boolean upButtonActive) {
        this.upButtonActive = upButtonActive;
    }

    public void  setDownButtonActive(boolean downButtonActive) {
        this.downButtonActive = downButtonActive;
    }

    private void setWaitingRiders(List<Observer> waitingRiders) {
        this.waitingRiders = waitingRiders;
    }

    private void setDoneRiders(List<Rider> doneRiders) {
        this.doneRiders = doneRiders;
    }
}
