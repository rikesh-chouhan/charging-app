package charging.app;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class ChargingScheduler implements Scheduler {

    @Override
    public Map<String, List<String>> schedule(@NonNull final List<Charger> chargerList,
                                              @NonNull final List<Truck> truckList, int totalTime)
    throws CannotScheduleException {
        Map<String, List<String>> scheduled = new HashMap();
        if (chargerList.isEmpty() || truckList.isEmpty()) {
            throw new CannotScheduleException("There are no chargers or trucks to schedule");
        }

        List<Truck> sortedTrucks = truckList.stream()
                .sorted(Comparator.comparingInt(Truck::getCurrentCharge))
                .collect(Collectors.toList());

        log.info("Sorted truck: {}", sortedTrucks);

        return scheduled;
    }

}
