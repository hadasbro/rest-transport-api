package github.hadasbro.transport.domain.location;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import github.hadasbro.transport.domain.EntityTag;
import github.hadasbro.transport.domain.transport.Operator;
import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Entity
@SuppressWarnings({"unused"})
@Table(name = "city")
@ToString(exclude = {"operators", "country"})
@EqualsAndHashCode(exclude = {"operators", "country"})
@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
public class City implements EntityTag {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    private String cityCode;

    @JsonManagedReference
    @ManyToMany(mappedBy = "cities")
    private Set<Operator> operators;

    @ManyToOne(optional = false)
    @JoinColumn(name = "country_id", nullable = false)
    private Country country;

}
