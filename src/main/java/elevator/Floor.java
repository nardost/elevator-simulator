package elevator;

import java.util.ArrayList;
import java.util.List;

class Floor implements Observer, Observable, Controllable {

    private int floorNumber;
    private boolean upButtonActive;
    private boolean downButtonActive;
    private PersonsGenerator generator;
    
    private List<Observer> waitingRiders;
    private List<Rider> doneRiders;

    private static int instanceCounter = 0;

    public Floor(boolean upButtonActive, boolean downButtonActive) {
        setFloorNumber(++instanceCounter);
        setUpButtonActive(upButtonActive);
        setDownButtonActive(downButtonActive);

        setGenerator(new PersonsGenerator(new Type1RiderGenerator()));//TODO: make factory
        setWaitingRiders(new ArrayList<>());
        setDoneRiders(new ArrayList<>());
    }

    @Override
    public void receiveControlSignal(Signal signal) {

    }

    @Override
    public void sendRequestToController(Request request) throws ElevatorSystemException {
        Building.getInstance().relayRequestToControlCenter(request);
    }

    public void start() throws ElevatorSystemException {
        //TODO: run Floor
        generateRiders();
    }

    /**
     *
     * ************ Observable methods ************************
     */

    @Override
    public void addObserver(Observer o) {
        getWaitingRiders().add(o);
    }

    @Override
    public void deleteObserver(Observer o) {

    }

    @Override
    public void notifyObservers(Signal signal) {
        for(Observer observer : getWaitingRiders()) {
            observer.update(signal);
        }
    }

    @Override
    public int countObservers() {
        return 0;
    }


    /**
     *
     * ******** Observer methods *******************
     */

    @Override
    public void update(Signal signal) { //TODO: Building updates Floor with signal. Floor acts on the signal
        if(signal.getReceiver() == ElementType.FLOOR && signal.getReceiverId() == getFloorNumber()) {
            receiveControlSignal(signal);
        }
    }



    public void generateRiders() throws ElevatorSystemException {
        int counter = 0;
        do {
            List<Rider> newRiders = getGenerator().generateRiders(getFloorNumber(), Building.getInstance().getNumberOfFloors());
            for(Rider rider: newRiders) {
                addObserver((Observer) rider);
                EventLogger.getInstance().logEvent("Rider " + rider.getId() + " generated on floor " + getFloorNumber());
            }
            counter++;
        } while(counter < 10);
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


    private PersonsGenerator getGenerator() {
        return generator;
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

    private void setGenerator(PersonsGenerator gen) {
        this.generator = generator;
    }

    private void setWaitingRiders(List<Observer> waiting) {
        this.waitingRiders = waitingRiders;
    }

    private void setDoneRiders(List<Rider> done) {
        this.doneRiders = doneRiders;
    }
}
