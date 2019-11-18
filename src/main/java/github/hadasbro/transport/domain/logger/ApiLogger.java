package github.hadasbro.transport.domain.logger;

import github.hadasbro.transport.domain.EntityTag;
import lombok.*;
import javax.persistence.*;
import java.util.Date;

@Entity
@SuppressWarnings({"unused"})
@Table(name = "api_logs")
@ToString @EqualsAndHashCode @Getter @Setter
public class ApiLogger implements EntityTag {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Version
    private Long version;

    @Embedded
    private ApiRequestLogger requestDetails;

    @Column(columnDefinition="TEXT")
    private String request;

    @Column(columnDefinition="TEXT")
    private String response;

    @Column(columnDefinition="TEXT")
    private String error;

    private Date date;

    @PrePersist
    protected void onCreate() {
        date = new Date();
    }

}