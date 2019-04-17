package elevator;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Floor implements Observer, Observable, Controllable {

    private int id;
    private boolean upButtonActive;
    private boolean downButtonActive;
    private PersonsGenerator generator;
    private Controller controller;
    private EventLogger logger;
    
    private List<Observer> observers;
    
    private List<Rider> ridersDoneWithWaitingForElevator;

    private boolean isActive;

    public Floor(int id, boolean upButtonActive, boolean downButtonActive, Controller theBuilding, EventLogger logger) {
        this.id = id;
        this.isActive = false;
        this.upButtonActive = upButtonActive;
        this.downButtonActive = downButtonActive;
        this.controller = theBuilding;
        this.logger = logger;

        this.generator = new PersonsGenerator(new Type1RiderGenerator());//TODO: make factory
        this.observers = new ArrayList<>();
        this.ridersDoneWithWaitingForElevator = new ArrayList<>();
    }
/** replaced by other Observable methods down below
    @Override
    public void register(Rider observer) throws ElevatorSystemException {
        observers.add(observer);
        //TODO: log event
    }

    @Override
    public void unregister(Rider observer) {
        int index = observers.indexOf(observer);
        observers.remove(index);
        ridersDoneWithWaitingForElevator.add(observer);
        //TODO: log event (not required)
    }

    @Override
    public void notifyRiders() {
        for(Rider observer : observers) {
            observer.update();
            //TODO: log event (not required)
        }
    }
*/
    @Override
    public void receiveSignal(Signal signal) {
        if(signal.getReceiverType() == Receiver.FLOOR && signal.getReceiverId() == this.getId()) {
            //TODO: receive signal from Controller and act if destined for self.
        }
    }

    @Override
    public void notifyController(Signal signal) {
        controller.receiveNotification(signal);
    }

    @Override
    public void run() throws ElevatorSystemException {
        this.isActive = true;
        //TODO: run Floor
        generateRiders();
    }

    @Override
    public void stop() {
        this.isActive = false;
        //TODO: stop Floor activity
    }

    /**
     *
     * ************ Observable methods ************************
     */

    @Override
    public void addObserver(Observer o) {
        observers.add(o);
    }

    @Override
    public void deleteObserver(Observer o) {

    }

    @Override
    public void notifyObservers(Signal signal) {
        for(Observer observer : observers) {
            observer.update(signal);
        }
    }

    @Override
    public void deleteObservers() {

    }

    @Override
    public void setChanged() {

    }

    @Override
    public void clearChanged() {

    }

    @Override
    public boolean hasChanged() {
        return false;
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
    public void update(Signal signal) { //TODO: Floor observes Building

    }


    public int getId() {
        return id;
    }

    public boolean isUpButtonActive() {
        return upButtonActive;
    }

    public boolean isDownButtonActive() {
        return downButtonActive;
    }

    public void  setUpButtonActive(boolean b) {
        upButtonActive = b;
    }

    public void  setDownButtonActive(boolean b) {
        downButtonActive = b;
    }

    public void generateRiders() throws ElevatorSystemException {
        int counter = 0;
        do {
            String id = UUID.randomUUID().toString();
            List<Rider> newRiders = generator.generateRiders(id, this);
            for(Rider rider: newRiders) {
                addObserver((Observer) rider);
                logger.logEvent("Rider " + rider.getId() + " generated on floor " + getId());
            }
            counter++;
        } while(counter < 10);
    }

}
