package github.hadasbro.transport.domain.transport;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import github.hadasbro.transport.domain.EntityTag;
import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Entity
@SuppressWarnings({"unused"})
@Table(name = "vehicle")
@ToString(exclude = {"operators"})
@EqualsAndHashCode(exclude = {"operators"})
@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
public class Vehicle implements EntityTag {

    public enum TYPE {
        TRAIN, UNDERGROUND, OVERGROUND, CABLECAR, DLR
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    private String slug;

    @Enumerated(EnumType.STRING)
    private TYPE type;

    @JsonManagedReference
    @ManyToMany(mappedBy = "vehicles")
    private Set<Operator> operators;

}