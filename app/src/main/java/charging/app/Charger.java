package charging.app;

import com.opencsv.bean.CsvBindByPosition;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Charger {

    @CsvBindByPosition(required = true, position = 0)
    private String chargerId;

    // in kw
    @CsvBindByPosition(required = true, position = 1)
    private int chargingRate;

}
