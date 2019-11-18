package github.hadasbro.transport.webDto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import de.sandkastenliga.tools.projector.core.Projection;
import de.sandkastenliga.tools.projector.core.ProjectionType;
import github.hadasbro.transport.domain.EntityTag;
import github.hadasbro.transport.domain.transport.Operator;
import github.hadasbro.transport.validators.operator.ValidateOperatorCode;
import github.hadasbro.transport.validators.operator.ValidateOperatorOwner;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

@JacksonXmlRootElement(localName = "operator")
@JsonRootName("operator")
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString @EqualsAndHashCode
@SuppressWarnings({"unused"})
public class OperatorDto implements DtoObject, EntityTag, Serializable {

    private static final long serialVersionUID = 4865903039190150223L;

    @JacksonXmlProperty(localName="operator-id", isAttribute = true)
    private Long id;

    @NotNull(message = "Name {e.not_null}")
    @Size(min = 2, max = 90, message = "Name {label.form.size_min_max}")
    private String name;

    @ValidateOperatorOwner
    @JacksonXmlProperty(localName="owner-id")
    private Long owner;

    @JacksonXmlProperty(localName="operator-code")
    @ValidateOperatorCode
    @NotNull(message = "Operator errorCode {e.not_null}")
    @Size(min = 2, max = 90, message = "Operator errorCode {label.form.size_min_max}")
    private String operatorCode;

    @Range(min=1, max=10000, message = "Licence id {label.form.range_min_max}")
    @NotNull(message = "Licence id {e.not_null}")
    private int licenceId;

    @Range(min=1, max=10000, message = "Type {label.form.range_min_max}")
    @NotNull(message = "Type {e.not_null}")
    private int type;

    @Range(min=1, max=3, message = "Status {label.form.range_min_max}")
    @NotNull(message = "Status {e.not_null}")
    private int status;

    private String ownerSlug;

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public Long getOwner() { return owner; }

    @Projection(
            value = ProjectionType.property,
            propertyName = "owner"
    )
    public void setOwner(Long owner) { this.owner = owner; }

    public String getOwnerSlug() { return ownerSlug; }

    @Projection(
            value = ProjectionType.property,
            propertyName = "owner",
            referencePropertyName = "slug"
    )
    public void setOwnerSlug(String ownerSlug) { this.ownerSlug = ownerSlug; }

    public String getOperatorCode() { return operatorCode; }

    public void setOperatorCode(String operatorCode) { this.operatorCode = operatorCode; }

    public int getLicenceId() { return licenceId; }

    public void setLicenceId(int licenceId) { this.licenceId = licenceId; }

    public int getType() { return type; }

    public void setType(int type) { this.type = type; }

    public int getStatus() { return status; }

    public void setStatus(int status) { this.status = status; }

    public String getTypeName() {

        switch (type) {

            case Operator.TYPE_PRIVATE:
                return "private";

            case Operator.TYPE_NATIONAL:
                return "national";

            case Operator.TYPE_MIXED:
            default:
                return "mixed";

        }

    }
}