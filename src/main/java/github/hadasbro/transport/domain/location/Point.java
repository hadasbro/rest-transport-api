package github.hadasbro.transport.domain.location;

import github.hadasbro.transport.domain.EntityTag;
import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "point")
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
@SuppressWarnings("unused")
public class Point implements EntityTag {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

//    @OneToMany(
//            mappedBy="point",
//            fetch = FetchType.LAZY
//    )
//    private Set<Action> actions = new HashSet<>();

}
