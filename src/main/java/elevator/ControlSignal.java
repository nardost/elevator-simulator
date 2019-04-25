package elevator;

enum ControlSignalType {GOTO, ELEVATOR_LOCATION, RIDER_ON_BOARD}

public interface ControlSignal {
    ControlSignalType getSignalType();
}
