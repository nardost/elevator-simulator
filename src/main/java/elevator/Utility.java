package elevator;

import java.text.DecimalFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
        list.forEach(x -> {
            sb.append(prefix);
            sb.append(x.toString());
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

    public static Direction evaluateDirection(int from, int to) {
        if(from == to) {
            return Direction.IDLE;
        }
        return (from > to) ? Direction.DOWN : Direction.UP;
    }

    public static String formatColumnString(String str, int cols) {
        StringBuilder sb = new StringBuilder();
        if(str.length() < cols) {
            for(int i = 0; i < cols - str.length(); i++) {
                sb.append(" ");
            }
            sb.append(str);
            return sb.toString();
        }
        return str.substring(0, cols);
    }

    public static String formatElapsedInstant() throws ElevatorSystemException  {//uses Instant
        long elapsed = Duration.between(Building.getInstance().getZeroInstant(), Instant.now()).toNanos();
        long elapsedSeconds = TimeUnit.SECONDS.convert(elapsed, TimeUnit.NANOSECONDS);
        long s = elapsedSeconds % 60;
        long m = ((elapsedSeconds - s) % 3600) / 60;
        long h = (elapsedSeconds - (elapsedSeconds - s) % 3600) / 60;
        return String.format("%02d:%02d:%02d", h, m, s);
    }

    public static String formatElapsedTime(long nanoTime) throws ElevatorSystemException  {//uses System.nanoTime()
        long elapsedTime = nanoTime - Building.getInstance().getZeroTime();
        long elapsedSeconds = TimeUnit.SECONDS.convert(elapsedTime, TimeUnit.NANOSECONDS);
        long s = elapsedSeconds % 60;
        long m = ((elapsedSeconds - s) % 3600) / 60;
        long h = (elapsedSeconds - (elapsedSeconds - s) % 3600) / 60;
        return String.format("%02d:%02d:%02d", h, m, s);
    }

    public static String nanoToRoundedSeconds(long nano, int decimalPlaces) {
        StringBuilder sb = new StringBuilder("0.");
        for(int i = 0; i < decimalPlaces; i++) {
            sb.append("0");
        }
        DecimalFormat df = new DecimalFormat(sb.toString());
        return df.format(new Long(TimeUnit.MILLISECONDS.convert(nano, TimeUnit.NANOSECONDS)).doubleValue() / 1000.0);
    }
}

