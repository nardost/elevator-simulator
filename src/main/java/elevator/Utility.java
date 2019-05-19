package elevator;

import java.util.List;

public class Utility {
    static int decodeFloorRequestFloor(String key) throws ElevatorSystemException {
        if(validFloorRequestKey(key)) {
            String d = key.substring(key.length() - 1);
            return Integer.parseInt(key.substring(0, key.length() - 1));
        }
        throw new ElevatorSystemException("Invalid FloorRequest key " + key);
    }

    static Direction decodeFloorRequestDirection(String key) throws ElevatorSystemException {
        if(validFloorRequestKey(key)) {
            return (key.substring(key.length() - 1).equals("U")) ? Direction.UP : Direction.DOWN;
        }
        throw new ElevatorSystemException("Invalid FloorRequest key " + key);
    }
    static String encodeFloorRequestKey(int floor, Direction direction) throws ElevatorSystemException {
        int numberOfFloors = Integer.parseInt(SystemConfiguration.getConfiguration("numberOfFloors"));
        if(floor < 1 || floor > numberOfFloors) {
            throw new ElevatorSystemException("Cannot encode key for invalid floor number: " + floor);
        }
        StringBuilder sb = new StringBuilder(Integer.toString(floor));
        sb.append((direction == Direction.UP) ? "U" : "D");
        return sb.toString();
    }

    static boolean validFloorRequestKey(String key) {
        String floorPart = key.substring(0, key.length() - 1);
        String directionPart = key.substring(key.length() - 1);
        try {
            int floor = Integer.parseInt(floorPart);
            SystemConfiguration.initializeSystemConfiguration();
            int numberOfFloors = Integer.parseInt(SystemConfiguration.getConfiguration("numberOfFloors"));
            if(floor < 1 || floor > numberOfFloors) {
                return false;
            }
            if(directionPart.equals("U") || directionPart.equals("D")) {
                if((floor == 1 && directionPart.equals("D") || (floor == numberOfFloors && directionPart.equals("U")))) {
                    return false;
                }
                return true;
            }
        } catch(NumberFormatException nfe) {
            return false;
        } catch(ElevatorSystemException ese) {
            ese.getMessage();
        }
        return false;
    }

    public static String listToString(List list, String prefix, String separator, String suffix) {
        StringBuilder sb = new StringBuilder();
        list.forEach(rider -> {
            sb.append(prefix);
            sb.append(rider.toString());
            sb.append(suffix);
            sb.append(separator);
        });
        int l = sb.length();
        int s = separator.length();
        if(l > s) {
            for(int i = 1; i <= s; i++) {
                sb.deleteCharAt(l - i);
            }
        }
        return sb.toString();
    }
}
