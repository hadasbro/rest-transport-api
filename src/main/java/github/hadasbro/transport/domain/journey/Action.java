package github.hadasbro.transport.domain.journey;

import github.hadasbro.transport.domain.EntityTag;
import github.hadasbro.transport.domain.location.Point;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "passenger_action")
@SuppressWarnings({"unused"})
@ToString(exclude = {"point","journeyleg"})
@EqualsAndHashCode(exclude = {"point", "journeyleg"})
@NoArgsConstructor @AllArgsConstructor @Getter @Setter
public class Action implements EntityTag {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    public enum TYPE{
        TOUCH_IN, TOUCH_OUT, INIT_JOURNEY
    }

    @Enumerated(EnumType.STRING)
    private TYPE type;

    private String identifier;

    private LocalDateTime date;

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

}