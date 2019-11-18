package github.hadasbro.transport.domain.passenger;

import github.hadasbro.transport.domain.EntityTag;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Embeddable
@SuppressWarnings({"unused"})
@Data @AllArgsConstructor @NoArgsConstructor
public class Accesscard implements EntityTag {

    /**
     * Card ID
     */
    private int acId;

    /**
     * Validity date
     */
    private LocalDateTime validityDate;

    /**
     * Activation date
     */
    private LocalDateTime activationDate;

}