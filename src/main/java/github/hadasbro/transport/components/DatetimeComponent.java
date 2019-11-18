package github.hadasbro.transport.components;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;


@Component
@Scope(value = "singleton")
@SuppressWarnings({"unused"})
public class DatetimeComponent {

    private final ThreadLocal<SimpleDateFormat> localSimpleDateFormat = SimpleDateFormatTS();

    private ThreadLocal<SimpleDateFormat> SimpleDateFormatTS() {

        TimeZone timeZone = TimeZone.getTimeZone("Etc/UTC");

        return ThreadLocal.withInitial(() -> {
            SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
            dateFormat1.setTimeZone(timeZone);
            return dateFormat1;
        });
    }

    public String getDate(Date date) {
        return localSimpleDateFormat.get().format(date);
    }

}
