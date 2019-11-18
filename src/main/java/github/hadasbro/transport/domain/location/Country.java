package github.hadasbro.transport.domain.location;

import github.hadasbro.transport.domain.EntityTag;
import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@SuppressWarnings({"unused"})
@Table(name = "country")
@ToString(exclude = {"cities"})
@EqualsAndHashCode(exclude = {"cities"})
@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
public class Country implements EntityTag {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    private String isoCode;

    @OneToMany(
            mappedBy="country",
            fetch = FetchType.LAZY
    )
    private Set<City> cities = new HashSet<>();

}
