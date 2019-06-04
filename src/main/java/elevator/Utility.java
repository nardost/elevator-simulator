package elevator;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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
        Validator.validateFloorNumber(floor);
        StringBuilder sb = new StringBuilder(Integer.toString(floor));
        sb.append((direction == Direction.UP) ? "U" : "D");
        return sb.toString();
    }

    static boolean validFloorRequestKey(String key) {
        String floorPart = key.substring(0, key.length() - 1);
        String directionPart = key.substring(key.length() - 1);
        try {

            final int NUMBER_OF_FLOORS = Integer.parseInt(SystemConfiguration.getConfiguration("numberOfFloors"));
            int floor = Integer.parseInt(floorPart);
            if(floor < 1 || floor > NUMBER_OF_FLOORS) {
                return false;
            }
            if(directionPart.equals("U") || directionPart.equals("D")) {
                if((floor == 1 && directionPart.equals("D") || (floor == NUMBER_OF_FLOORS && directionPart.equals("U")))) {
                    return false;
                }
                return true;
            }
        } catch(NumberFormatException nfe) {
            return false;
        }
        return false;
    }

    public static String listToString(List list, String prefix, String separator, String suffix) throws ElevatorSystemException {
        try {
            Validator.validateNotNull(list);
        } catch (ElevatorSystemException ese) {
            throw new ElevatorSystemException("cannot stringify null list.");
        }
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

    public static Direction evaluateDirection(int from, int to) throws ElevatorSystemException {
        Validator.validateFloorNumber(from);
        Validator.validateFloorNumber(to);
        if(from == to) {
            return Direction.IDLE;
        }
        return (from > to) ? Direction.DOWN : Direction.UP;
    }

    public static String formatColumnString(String str, int cols) {
        try {
            //any exception must be caught here because method is used in a lambda expression.
            Validator.validateGreaterThanZero(cols);
        } catch(ElevatorSystemException ese) {
            System.out.printf(ese.getMessage());
        }
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

    public static String formatElapsedTime(long milliTime) throws ElevatorSystemException  {//uses System.currentTimeMillis()
        long elapsedTime = milliTime - Building.getInstance().getZeroTime();
        long elapsedSeconds = TimeUnit.SECONDS.convert(elapsedTime, TimeUnit.MILLISECONDS);
        long s = elapsedSeconds % 60;
        long m = ((elapsedSeconds - s) % 3600) / 60;
        long h = (elapsedSeconds - (elapsedSeconds - s) % 3600) / 60;
        return String.format("%02d:%02d:%02d", h, m, s);
    }

    public static String millisToRoundedSeconds(double millis, int decimalPlaces) {
        StringBuilder sb = new StringBuilder("0.");
        for(int i = 0; i < decimalPlaces; i++) {
            sb.append("0");
        }
        DecimalFormat df = new DecimalFormat(sb.toString());
        return df.format(millis / 1000.0);
    }

    public static String generateReport(List<Person> list) throws ElevatorSystemException {
        final int totalNumberOfRiders = list.size();
        double waitTimes[] = new double[totalNumberOfRiders];
        double rideTimes[] = new double[totalNumberOfRiders];
        double minWaitTime = Double.MAX_VALUE;
        double maxWaitTime = Double.MIN_VALUE;
        double totalWaitTime;
        double averageWaitTime;
        double minRideTime = Double.MAX_VALUE;;
        double maxRideTime = Double.MIN_VALUE;
        double totalRideTime;
        double averageRideTime;
        int personWithMinWaitTime;
        int personWithMaxWaitTime;
        int personWithMinRideTime;
        int personWithMaxRideTime;
        StringBuilder sb = new StringBuilder();
        StringBuilder undone = new StringBuilder();
        sb.append("\n");
        sb.append("Person\tStart Floor\tEnd Floor\tDirection\tWait Time\tRide Time\tTotal Time\n");
        sb.append("------\t-----------\t---------\t---------\t---------\t---------\t----------\n");
        list.forEach(p -> {
            if(p.getStatus() != RiderStatus.DONE) {
                if(undone.length() > 0) {
                    undone.append(", ");
                }
                undone.append("P" + p.getId());
            } else {
                double waitTime = new Long(p.getBoardingTime() - p.getCreatedTime()).doubleValue();
                double rideTime = new Long(p.getExitTime() - p.getBoardingTime()).doubleValue();
                double totalTime = new Long(p.getExitTime() - p.getCreatedTime()).doubleValue();

                waitTimes[p.getId() - 1] = waitTime;
                rideTimes[p.getId() - 1] = rideTime;

                String id = "P" + p.getId();
                String origin = Integer.toString(p.getOriginFloor());
                String destination = Integer.toString(p.getDestinationFloor());
                String direction = ((p.getOriginFloor() < p.getDestinationFloor()) ? Direction.UP : Direction.DOWN).toString();
                String waitTimeStr = Utility.millisToRoundedSeconds(waitTime, 1);
                String rideTimeStr = Utility.millisToRoundedSeconds(rideTime, 1);
                String totalTimeStr = Utility.millisToRoundedSeconds(totalTime, 1);
                sb.append(
                        Utility.formatColumnString(id, 6) + "\t" +
                                Utility.formatColumnString(origin, 11) + "\t" +
                                Utility.formatColumnString(destination, 9) + "\t" +
                                Utility.formatColumnString(direction, 9) + "\t" +
                                Utility.formatColumnString(waitTimeStr, 9) + "\t" +
                                Utility.formatColumnString(rideTimeStr, 9) + "\t" +
                                Utility.formatColumnString(totalTimeStr, 10) + "\n");
            }
        });

        minWaitTime = Arrays.stream(waitTimes).summaryStatistics().getMin();
        maxWaitTime = Arrays.stream(waitTimes).summaryStatistics().getMax();
        totalWaitTime = Arrays.stream(waitTimes).summaryStatistics().getSum();
        averageWaitTime = totalWaitTime / new Integer(totalNumberOfRiders).doubleValue();
        minRideTime = Arrays.stream(rideTimes).min().getAsDouble();
        maxRideTime = Arrays.stream(rideTimes).summaryStatistics().getMax();
        totalRideTime = Arrays.stream(rideTimes).sum();
        averageRideTime = totalRideTime / new Integer(totalNumberOfRiders).doubleValue();

        sb.append("\n");
        sb.append("Average Wait Time: " + Utility.formatColumnString(Utility.millisToRoundedSeconds(averageWaitTime, 1), 6) + " sec\n");
        sb.append("Average Ride Time: " + Utility.formatColumnString(Utility.millisToRoundedSeconds(averageRideTime, 1), 6) + " sec\n");
        sb.append("\n");
        personWithMinWaitTime = (1 + indexOf(minWaitTime, waitTimes));
        sb.append("Minimum Wait Time: " + Utility.formatColumnString(Utility.millisToRoundedSeconds(minWaitTime, 1), 6) + " sec (P" + personWithMinWaitTime + ")\n");
        personWithMinRideTime = (1 + indexOf(minRideTime, rideTimes));
        sb.append("Minimum Ride Time: " + Utility.formatColumnString(Utility.millisToRoundedSeconds(minRideTime, 1), 6) + " sec (P" + personWithMinRideTime + ")\n");
        sb.append("\n");
        personWithMaxWaitTime = (1 + indexOf(maxWaitTime, waitTimes));
        sb.append("Maximum Wait Time: " + Utility.formatColumnString(Utility.millisToRoundedSeconds(maxWaitTime, 1), 6) + " sec (P" + personWithMaxWaitTime + ")\n");
        personWithMaxRideTime = (1 + indexOf(maxRideTime, rideTimes));
        sb.append("Maximum Ride Time: " + Utility.formatColumnString(Utility.millisToRoundedSeconds(maxRideTime, 1), 6) + " sec (P" + personWithMaxRideTime + ")\n");
        sb.append("\n");
        sb.append("Unhandled floor requests: " + ElevatorController.getInstance().unhandledFloorRequests());
        sb.append("\n");
        sb.append("Undone Riders: " + undone.toString());

        return sb.toString();
    }
    static int indexOf(double value, double[] array) {
        return Arrays.stream(array).boxed().collect(Collectors.toList()).indexOf(value);
    }
}