package github.hadasbro.transport.domain.transport;

import com.fasterxml.jackson.annotation.JsonBackReference;
import github.hadasbro.transport.domain.EntityTag;
import github.hadasbro.transport.domain.location.City;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import javax.persistence.*;
import java.util.Set;

@Data
@Entity
@SuppressWarnings({"unused"})
@Table(name = "operator")
@ToString(exclude = {"vehicles","owner","cities"})
@EqualsAndHashCode(exclude = {"vehicles","owner","cities"})
@NoArgsConstructor @AllArgsConstructor
public class Operator implements EntityTag {

    public final static int TYPE_PRIVATE = 1;
    public final static int TYPE_MIXED = 2;
    public final static int TYPE_NATIONAL = 3;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    @ManyToOne(
            fetch = FetchType.EAGER,
            optional = false
    )
    @JoinColumn(
            name = "owner_id",
            nullable = false
    )
    @OnDelete(
            action = OnDeleteAction.NO_ACTION
    )
    private Owner owner;

    private String operatorCode;

    private int licenceId;

    private int type;

    private int status;

    @JsonBackReference
    @ManyToMany
    @JoinTable(
            name = "operator_vehicle",
            joinColumns = @JoinColumn(name = "operator_id"),
            inverseJoinColumns = @JoinColumn(name = "vehicle_id")
    )
    private Set<Vehicle> vehicles;

    @JsonBackReference
    @ManyToMany
    @JoinTable(
            name = "operator_city",
            joinColumns = @JoinColumn(name = "operator_id"),
            inverseJoinColumns = @JoinColumn(name = "city_id")
    )
    private Set<City> cities;

}