package util;

import java.util.Comparator;

public class TimeComparator implements Comparator<String> {


    @Override
    public int compare(String o1, String o2) {

        String[] first = o1.split(":");
        String[] second = o2.split(":");

        int hourFirst = Integer.parseInt(first[0]);
        int hourSecond = Integer.parseInt(second[0]);

        if(hourFirst > hourSecond)
            return 1;
        else if(hourFirst < hourSecond)
            return -1;

        int minuteFirst = Integer.parseInt(first[1]);
        int minuteSecond = Integer.parseInt(second[1]);
        if(minuteFirst > minuteSecond)
            return 1;
        else if(minuteFirst < minuteSecond)
            return -1;

        int secondFirst = Integer.parseInt(first[2]);
        int secondSecond = Integer.parseInt(second[2]);
        if(secondFirst > secondSecond)
            return 1;
        else if(secondFirst < secondSecond)
            return -1;

        return 0;


    }

}
