package charging.app;

import java.util.List;
import java.util.Map;

public interface Scheduler {

    Map<String, List<String>> schedule(List<Charger> chargerList, List<Truck> truckList, int totalTime)
            throws CannotScheduleException ;

}
