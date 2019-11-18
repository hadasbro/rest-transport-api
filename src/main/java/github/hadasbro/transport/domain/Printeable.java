package github.hadasbro.transport.domain;
import github.hadasbro.transport.utils.ObjectStringifer;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * interface Printeable
 */
public interface Printeable {

    /**
     * print
     *
     * Print object
     *
     * @return String
     */
    default String stringify() {
        return ToStringBuilder.reflectionToString(this, new ObjectStringifer.PrintToStringStyle());
    }

    /**
     * deepPrint
     *
     * Deep print object
     *
     * @return String
     */
    default String deepStringify() {
        return (new ObjectStringifer.DeepObjectPrinter()).toString(this);
    }


}
