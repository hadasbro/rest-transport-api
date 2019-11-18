package github.hadasbro.transport.components;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;


@Component
@Scope(value = "singleton")
@SuppressWarnings({"unused"})
public class ApiComponent {

    ApiComponent() {}

    @PostConstruct
    public void pConstruct() {
    }

    @PreDestroy
    public void pDestruct() {
    }

}
