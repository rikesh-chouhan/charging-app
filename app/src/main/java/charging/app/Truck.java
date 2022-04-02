package charging.app;

import com.opencsv.bean.CsvBindByPosition;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Truck {

    @CsvBindByPosition(required = true, position = 0)
    private String id;

    // in kwh
    @CsvBindByPosition(required = true, position = 1)
    private int batteryCapacity;

    // in kwh
    @CsvBindByPosition(required = false, position = 2)
    private int currentCharge;
}
