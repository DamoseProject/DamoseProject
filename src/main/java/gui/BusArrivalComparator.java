package gui;

import model.BusInUnaFermataRecord;

import java.time.LocalTime;
import java.util.Comparator;

public class BusArrivalComparator implements Comparator<BusInUnaFermataRecord> {

    @Override
    public int compare(BusInUnaFermataRecord b1, BusInUnaFermataRecord b2) {
        int priority1 = getPriority(b1);
        int priority2 = getPriority(b2);

        if (priority1 != priority2) {
            return Integer.compare(priority1, priority2);
        }

        LocalTime time1 = getEffectiveTime(b1);
        LocalTime time2 = getEffectiveTime(b2);

        return time1.compareTo(time2);
    }

    private int getPriority(BusInUnaFermataRecord bus) {
        if (bus.isRealTime()) return 1;
        if (bus.getIsSmartPredicted()) return 2;
        return 3;
    }

    private LocalTime getEffectiveTime(BusInUnaFermataRecord bus) {
        return (bus.getOrarioEffettivo() != null) ? bus.getOrarioEffettivo() : bus.getOrarioStatico();
    }
}