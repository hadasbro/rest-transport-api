package github.hadasbro.transport.webDto;

import github.hadasbro.transport.domain.EntityTag;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@ToString
@EqualsAndHashCode
@SuppressWarnings({"unused"})
public class OperatorByVehicleOwner implements DtoObject, EntityTag, Serializable {

    @NotNull(message = "Vehicles {e.not_null}")
    public String[] vehicleTypes;

    @NotNull(message = "Owners {e.not_null}")
    public String[] ownerSlugs;
}
