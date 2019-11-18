package github.hadasbro.transport.aspects.classes;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalTime;

@Getter
@Setter
public class LoggerDetails {

    /**
     * duration
     */
    private long duration = 0;

    /**
     * requestTime
     */
    private LocalTime requestTime = null;

    /**
     * stackTrace
     */
    private String stackTrace = "";

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();

        if(requestTime != null){
            sb.append(requestTime).append(", ");
        }

        if(duration > 0){
            sb
                    .append(" duration: ")
                    .append(duration)
                    .append(" ms, ");
        }

        if(!stackTrace.equals("")){
            sb
                    .append(" stackTrace: ")
                    .append(stackTrace)
                    .append(", ");
        }

        return StringUtils.strip(sb.toString(), ", ");

    }
}
