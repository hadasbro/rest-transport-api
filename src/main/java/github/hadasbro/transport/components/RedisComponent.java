package github.hadasbro.transport.components;

import github.hadasbro.transport.domain.passenger.Passenger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;


@Component
@Scope(value = "singleton")
@SuppressWarnings({"unused"})
public class RedisComponent {

    /**
     * Redis Keys
     */
    final public static String KEY_OPERATOR_LIST = "all-operators";

    @Value("${redis.timeout:10}")
    private String timeout;

    RedisComponent() {}

    public void flush(String key) {}

    public void publish(Passenger passenger) {}

    @PostConstruct
    public void pConstruct() {}

    @PreDestroy
    public void pDestruct() {}

}
