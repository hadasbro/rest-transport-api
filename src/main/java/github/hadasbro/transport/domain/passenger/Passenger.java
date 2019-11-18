package github.hadasbro.transport.domain.passenger;

import github.hadasbro.transport.domain.EntityTag;
import github.hadasbro.transport.domain.location.City;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "passenger")
@SuppressWarnings({"unused"})
@NoArgsConstructor @Getter @Setter @EqualsAndHashCode
@ToString(exclude = {"city"})
public class Passenger implements EntityTag {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private boolean active = true;
    private boolean blocked = false;
    private String firstName;
    private String lastName;
    private String email;
    private BigDecimal balance;

    @Transient
    private String fullName;

    @ManyToOne(
            fetch = FetchType.EAGER,
            optional = false
    )
    @JoinColumn(
            name = "city_id",
            nullable = false
    )
    @OnDelete(
            action = OnDeleteAction.NO_ACTION
    )
    private City city;

    @Embedded
    private Accesscard card;

}