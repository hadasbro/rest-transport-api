package github.hadasbro.transport.components;

import github.hadasbro.transport.domain.journey.Action;
import github.hadasbro.transport.domain.journey.Journeyleg;
import github.hadasbro.transport.exceptions.ApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.math.BigDecimal;
import java.util.Objects;


@Component
@Scope(value = "singleton")
@SuppressWarnings({"unused"})
public class FinanceComponent {

    @Value("${data.min_journey_cost:3.00}")
    private BigDecimal minimunJourneyCost;

    FinanceComponent() {}

    @PostConstruct
    public void pConstruct() {
    }

    @PreDestroy
    public void pDestruct() {
    }

    /**
     * getMinimunJourneyCost
     *
     * @return BigDecimal
     */
    private BigDecimal getMinimunJourneyCost() {
        return minimunJourneyCost;
    }

    /**
     * setMinimunJourneyCost
     *
     * @param minimunJourneyCost -
     */
    public void setMinimunJourneyCost(BigDecimal minimunJourneyCost) {
        this.minimunJourneyCost = minimunJourneyCost;
    }

    /**
     * calculcateJourneyCost
     *
     * @param currentAction -
     * @param journeyleg -
     * @return BigDecimal
     * @throws ApiException -
     */
    public BigDecimal calculcateJourneyCost(Action currentAction, Journeyleg journeyleg) throws ApiException {

        if (currentAction.getType() == Action.TYPE.INIT_JOURNEY) {
            return BigDecimal.valueOf(0);
        }

        if (journeyleg == null && currentAction.getType() != Action.TYPE.TOUCH_IN) {
            throw new ApiException("Journeyleg required to calculate journey`s cost", ApiException.CODES.GENERAL);
        }

        if (currentAction.getType() == Action.TYPE.TOUCH_IN) {
            return this.getMinimunJourneyCost();
        } else if (currentAction.getType() == Action.TYPE.REFUND) {
            Action correspondingAction = Objects
                    .requireNonNull(journeyleg)
                    .getActions()
                    .stream()
                    .filter(
                            a -> a.getIdentifier().equals(currentAction.getIdentifier())
                    )
                    .limit(1)
                    .findFirst()
                    .orElseThrow(() -> new ApiException("Corresponding action not found", ApiException.CODES.GENERAL));

            return correspondingAction.getCostAmont();

        } else {

            // TODO - calculate cost of journey between 2 points and for operator
            // substract prepaided amount (in case of touchin action before)

            double radDouble;
            radDouble = Math.random();
            radDouble = radDouble * 30;
            int randomInt = (int) radDouble;

            return BigDecimal.valueOf(randomInt);
        }

    }
}
