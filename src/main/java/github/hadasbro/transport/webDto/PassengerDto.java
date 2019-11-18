package github.hadasbro.transport.webDto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import github.hadasbro.transport.domain.EntityTag;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;

@JacksonXmlRootElement(localName = "passenger")
@JsonRootName("passenger")
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString @EqualsAndHashCode
@SuppressWarnings({"unused"})
public class PassengerDto implements DtoObject, EntityTag, Serializable {

    private static final long serialVersionUID = 4865903039190150223L;

    @NotNull(message = "First name {e.not_null}")
    private String firstName;

    @NotNull(message = "Last name {e.not_null}")
    @NotEmpty(message = "{label.form.correctLastName}")
    private String lastName;

    @NotNull(message = "Balance {e.not_null}")
    private BigDecimal balance;

    @NotNull(message = "Email {e.not_null}")
    @Size(min = 4, max = 90, message = "Email {label.form.size_min_max}")
    private String email;

    @JacksonXmlProperty(localName="fist-name")
    public String getFirstName() { return firstName; }

    public void setFirstName(String firstName) { this.firstName = firstName; }

    @JacksonXmlProperty(localName="last-name")
    public String getLastName() { return lastName; }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() { return email; }

    public void setEmail(String email) {
        this.email = email;
    }

    public BigDecimal getBalance() { return balance; }

    public void setBalance(BigDecimal balance) { this.balance = balance; }

}