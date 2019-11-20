package github.hadasbro.transport.domain.journey;

import github.hadasbro.transport.components.FinanceComponent;
import github.hadasbro.transport.components.SpringContext;
import github.hadasbro.transport.domain.EntityTag;
import github.hadasbro.transport.domain.location.Point;
import github.hadasbro.transport.exceptions.ApiException;
import github.hadasbro.transport.webDto.ApiRequestDto;
import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "passenger_action",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"identifier", "type"})}
        )
@ToString(exclude = {"point","journeyleg"})
@EqualsAndHashCode(exclude = {"point", "journeyleg"})
@NoArgsConstructor @AllArgsConstructor @Getter @Setter
@SuppressWarnings({"unused"})
public class Action implements EntityTag {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    public enum TYPE{
        TOUCH_IN, TOUCH_OUT, INIT_JOURNEY, REFUND
    }

    @Enumerated(EnumType.STRING)
    private TYPE type;

    private String identifier;

    private LocalDateTime date;

    private BigDecimal costAmont = BigDecimal.valueOf(0);

    @ManyToOne(
            fetch = FetchType.EAGER
    )
    @JoinColumn(name = "point_id")
    private Point point;

    @ManyToOne(
            fetch = FetchType.EAGER
    )
    @JoinColumn(name="journeyleg_id")
    private Journeyleg journeyleg;

    /**
     * onCreate
     */
    @PrePersist
    protected void onCreate() {
        date = LocalDateTime.now();
    }

    /**
     * Static Actions factory
     *
     * @param type -
     * @param request -
     * @param point -
     * @param journeyleg -
     * @return Action
     * @throws ApiException -
     */
    public static Action from(TYPE type, ApiRequestDto request, Point point, Journeyleg journeyleg) throws ApiException {

        FinanceComponent fc = SpringContext.getBean(FinanceComponent.class);

        Action action = new Action();
        action.setIdentifier(request.getActionIdentifier());
        action.setType(type);
        action.setPoint(point);
        action.setCostAmont(fc.calculcateJourneyCost(action, journeyleg));
        action.setJourneyleg(journeyleg);

        return action;
    }
}