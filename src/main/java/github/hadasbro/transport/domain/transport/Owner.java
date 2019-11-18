package github.hadasbro.transport.domain.transport;

import github.hadasbro.transport.domain.EntityTag;
import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "owner")
@SuppressWarnings({"unused"})
@EqualsAndHashCode(exclude = {"operators"})
@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
public class Owner implements EntityTag {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private String slug;

    @OneToMany(mappedBy="owner")
    private Set<Operator> operators;
}