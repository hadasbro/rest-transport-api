package github.hadasbro.transport.domain.logger;

import lombok.*;

import javax.persistence.*;

@Embeddable @Data
@SuppressWarnings({"unused", "WeakerAccess"})
public class ApiRequestLogger {

    @Column(name = "req_journeyidentifer")
    private String journeyIdentifer;

    @Column(name = "req_licenceId")
    private Integer licenceId;

    @Column(name = "req_passengerId")
    private Long passengerId;

    @Column(name = "req_actionIdentifier")
    private String actionIdentifier;

    @Column(name = "req_journeylegIdentifer")
    private String journeylegIdentifer;

    @Column(name = "req_type")
    private Integer type;

    @Column(name = "req_closed")
    private Boolean closed;

}