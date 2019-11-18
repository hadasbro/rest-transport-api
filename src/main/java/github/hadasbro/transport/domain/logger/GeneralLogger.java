package github.hadasbro.transport.domain.logger;

import github.hadasbro.transport.domain.EntityTag;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;

@Entity
@SuppressWarnings({"unused"})
@Table(name = "general_logs")
@ToString @EqualsAndHashCode @Getter @Setter
public class GeneralLogger implements EntityTag {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(length=1000)
    private String request;

    @Column(length=1000)
    private String response;

    private Date date;

    @PrePersist
    protected void onCreate() {
        date = new Date();
    }
}