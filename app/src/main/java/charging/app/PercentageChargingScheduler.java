package charging.app;

import java.util.*;

public class PercentageChargingScheduler extends ChargingScheduler {

    int percentage;

    public PercentageChargingScheduler(int percentage) {
        this.percentage = percentage;
    }

    // find candidate trucks which when charged would total up
    // close to a chargers capacity
    @Override
    List<String> candidateTrucksForCharger(List<Map.Entry<String, Integer>> toBeChargedList, int maxCharge) {

        // Container to store candidate trucks
        Map.Entry<Integer, List<String>> optimum = Map.entry(0, new ArrayList<>());

        HashSet<Map.Entry<Integer, List<String>>> candidates = new HashSet<>();
        candidates.add(optimum);

        for (Map.Entry<String, Integer> chargeRequired : toBeChargedList) {
            Set<Map.Entry<Integer, List<String>>> newEntries = new HashSet<>();

            for (Map.Entry<Integer, List<String>> currentCandidateList : candidates) {
                List<String> newSublist = new ArrayList<>(currentCandidateList.getValue());
                newSublist.add(chargeRequired.getKey());
                Map.Entry<Integer, List<String>> newTotal =
                        Map.entry(currentCandidateList.getKey() +
                                        ((int)(chargeRequired.getValue() * percentage/100)),
                                newSublist);

                if (newTotal.getKey() <= maxCharge) {
                    newEntries.add(newTotal);

                    // the `=` allows replacement of the older
                    if (newTotal.getValue().size() > optimum.getValue().size()) {
                        optimum = newTotal;
                    }
                }
            }

            candidates.addAll(newEntries);
        }

        // placeholder
        return optimum.getValue();
    }

}
