package github.hadasbro.transport.domain.journey;

import github.hadasbro.transport.domain.EntityTag;
import github.hadasbro.transport.domain.passenger.Passenger;
import github.hadasbro.transport.domain.transport.Operator;
import github.hadasbro.transport.webDto.ApiRequestDto;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "journeyleg")
@SuppressWarnings({"unused"})
@ToString(exclude = {"operator","passenger","journey","actions"})
@EqualsAndHashCode(exclude = {"operator", "passenger", "journey", "actions"})
@NoArgsConstructor @AllArgsConstructor @Getter @Setter
public class Journeyleg implements EntityTag {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String identifer;
    private boolean closed;
    private LocalDateTime firstAction;
    private LocalDateTime lastAction;

    /**
     * Operator
     */
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "operator_id", nullable = false)
    private Operator operator;

    /**
     * Passenger
     */
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "passenger_id", nullable = false)
    private Passenger passenger;

    /**
     * Journey
     */
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "journey_id", nullable = false)
    private Journey journey;

    /**
     * Actions
     */
    @OneToMany(
            mappedBy="journeyleg",
            fetch = FetchType.EAGER,
            cascade = {
                    CascadeType.PERSIST,
                    CascadeType.MERGE
            }
    )
    private Set<Action> actions = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        firstAction = lastAction = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        lastAction = LocalDateTime.now();
    }

    /**
     * addAction
     * @param action -
     */
    public void addAction(Action action) {
        this.actions.add(action);
    }

    /**
     * Journeyleg static factory
     *
     * @param journey -
     * @param passenger -
     * @param operator -
     * @param request -
     * @return Journeyleg
     */
    public static Journeyleg from(Journey journey, Passenger passenger, Operator operator, ApiRequestDto request) {
        Journeyleg journeyleg = new Journeyleg();
        journeyleg.setJourney(journey);
        journeyleg.setIdentifer(request.getJourneylegIdentifer());
        journeyleg.setPassenger(passenger);
        journeyleg.setOperator(operator);
        return journeyleg;
    }
}