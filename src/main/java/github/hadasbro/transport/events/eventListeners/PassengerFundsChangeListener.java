package github.hadasbro.transport.events.eventListeners;

import github.hadasbro.transport.events.OnPassengerFundsChangeEvent;
import github.hadasbro.transport.services.TransportService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Log
@Component
@SuppressWarnings({"unused"})
public class PassengerFundsChangeListener implements ApplicationListener<OnPassengerFundsChangeEvent> {

    @Autowired
    TransportService transportService;

    @Override
    public void onApplicationEvent(@NonNull final OnPassengerFundsChangeEvent event) {
        transportService.logPassengerChange(event.getPassenger());
    }

}
