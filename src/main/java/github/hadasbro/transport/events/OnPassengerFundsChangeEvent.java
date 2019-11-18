package github.hadasbro.transport.events;

import github.hadasbro.transport.domain.passenger.Passenger;
import org.springframework.context.ApplicationEvent;

@SuppressWarnings({"unused", "WeakerAccess"})
public class OnPassengerFundsChangeEvent extends ApplicationEvent {

    Passenger passenger;

    public OnPassengerFundsChangeEvent(Class<?> clazz, Passenger passenger) {
        super(clazz);
        this.setPassenger(passenger);
    }

    public Passenger getPassenger() {
        return passenger;
    }

    public void setPassenger(Passenger passenger) {
        this.passenger = passenger;
    }
}
