package github.hadasbro.transport.domain.journey;

import github.hadasbro.transport.domain.EntityTag;
import github.hadasbro.transport.domain.passenger.Passenger;
import github.hadasbro.transport.domain.transport.Operator;
import github.hadasbro.transport.utils.Utils;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "journey")
@SuppressWarnings({"unused"})
@ToString(exclude = {"operator","journeylegs","passenger"})
@EqualsAndHashCode(exclude = {"operator","journeylegs","passenger","lastAction"})
@NoArgsConstructor @AllArgsConstructor @Getter @Setter
public class Journey implements EntityTag {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String identifer;
    private LocalDateTime dateStart;
    private LocalDateTime lastAction;

    @ManyToOne(optional = false)
    @JoinColumn(name = "operator_id", nullable = false)
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    private Operator operator;

    @ManyToOne(optional = false)
    @JoinColumn(name = "passenger_id", nullable = false)
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    private Passenger passenger;

    @OneToMany(
            mappedBy="journey",
            fetch = FetchType.EAGER,
            cascade = {
                    CascadeType.PERSIST,
                    CascadeType.MERGE
            }
    )
    private Set<Journeyleg> journeylegs = new HashSet<>();

    /**
     * onCreate
     */
    @PrePersist
    protected void onCreate() {
        dateStart = lastAction = LocalDateTime.now();
    }

    /**
     * onUpdate
     */
    @PreUpdate
    protected void onUpdate() {
        lastAction = LocalDateTime.now();
    }

    /**
     * refreshIdentifer
     *
     * @return String
     */
    public String refreshIdentifer(){
        String nIdentifer = Utils.getRandString(32);
        this.setIdentifer(nIdentifer);
        return nIdentifer;
    }


}