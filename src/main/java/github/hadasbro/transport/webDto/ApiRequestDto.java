package github.hadasbro.transport.webDto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import github.hadasbro.transport.aspects.tagInterfaces.LoggableRequest;
import github.hadasbro.transport.domain.EntityTag;
import github.hadasbro.transport.validators.api.ApiValidators;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Range;

import javax.persistence.Embeddable;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
@ToString @EqualsAndHashCode @Getter @Setter
@Embeddable
@SuppressWarnings({"unused"})
public class ApiRequestDto implements DtoObject, EntityTag, Serializable, LoggableRequest {

    private static final long serialVersionUID = 4865903039190150223L;

    final public static int CENTS = 100;

    final public static String URI_TOUCHIN = "touchin";
    final public static String URI_TOUCHOUT = "touchout";
    final public static String URI_REFUND = "refund";
    final public static String URI_BALANCE = "balance";
    final public static String URI_INIT_JOURNEY = "init";

    /**
     * journeyIdentifer
     */
    @NotNull(
            groups = {ApiValidators.ApiActionGroup.class, ApiValidators.ApiInitGroup.class},
            message = "journeyIdentifer {e.not_null}"
    )
    private String journeyIdentifer;

    /**
     * operator Licence Id
     */
    @NotNull(
            groups = {ApiValidators.ApiGeneralGroup.class},
            message = "licenceId {e.not_null}"
    )
    @Digits(
            groups = {ApiValidators.ApiGeneralGroup.class},
            integer = 6,
            fraction = 0
    )
    @Range(
            groups = {ApiValidators.ApiGeneralGroup.class},
            min=1,
            message = "LicenceId {label.form.range_min_max}"
    )
    private Integer licenceId;

    /**
     * passenger id
     */
    @NotNull(
            groups = {ApiValidators.ApiGeneralGroup.class},
            message = "passengerId {e.not_null}"
    )
    @Range(
            groups = {ApiValidators.ApiGeneralGroup.class},
            min=1,
            message = "passengerId {label.form.range_min_max}"
    )
    private Long passengerId;

    /**
     * action id - action unique token
     */
    @Pattern(
            regexp="^[a-zA-Z0-9_-]+$",
            message = "actionIdentifier {e.invalid_regex} ^[a-zA-Z0-9_-]+$",
            groups = {ApiValidators.ApiActionGroup.class, ApiValidators.ApiInitGroup.class}
    )
    @NotNull(
            groups = {ApiValidators.ApiActionGroup.class},
            message = "actionIdentifier {e.not_null}"
    )
    @Size(
            groups = {ApiValidators.ApiActionGroup.class},
            min = 2,
            max = 90,
            message = "actionIdentifier {label.form.size_min_max}"
    )
    private String actionIdentifier;

    /**
     * journeyleg id
     */
    @Pattern(
            regexp="^[a-zA-Z0-9_-]+$",
            message = "journeylegIdentifer {e.invalid_regex} ^[a-zA-Z0-9_-]+$",
            groups = {ApiValidators.ApiActionGroup.class}
    )
    @NotNull(
            groups = {ApiValidators.ApiActionGroup.class},
            message = "journeylegIdentifer {e.not_null}"
    )
    @Size(
            groups = {ApiValidators.ApiActionGroup.class},
            min = 2,
            max = 90,
            message = "journeylegIdentifer {label.form.size_min_max}"
    )
    private String journeylegIdentifer;

    /**
     * closed - true/false
     */
    @NotNull(
            groups = {ApiValidators.ApiActionGroup.class},
            message = "closed {e.not_null}"
    )
    private Boolean closed;

    /**
     * type
     */
    @NotNull(
            message = "type {e.not_null}"
    )
    @Range(
            groups = {ApiValidators.ApiInitGroup.class},
            min=1, max=3,
            message = "type {label.form.range_min_max}"
    )
    private Integer type;

    @NotNull(
            groups = {ApiValidators.ApiInitGroup.class, ApiValidators.ApiActionGroup.class},
            message = "pointId {e.not_null}"
    )
    private Long pointId;

}