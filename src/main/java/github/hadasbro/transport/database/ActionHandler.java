package github.hadasbro.transport.database;

import github.hadasbro.transport.domain.passenger.Passenger;
import github.hadasbro.transport.domain.journey.Action;
import github.hadasbro.transport.exceptions.ApiException;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.ParameterMode;
import javax.persistence.StoredProcedureQuery;
import java.math.BigDecimal;

/**
 * STORED PROCEDURE
 *
 * ActionHandler
 *
 * this SP has TRANSACTION inside
 */
@Service
@SuppressWarnings({"unused", "WeakerAccess"})
public class ActionHandler {

    private EntityManager entityManager;

    public static final int STATUS_OK = 0;
    public static final int STATUS_WARNING = 1;
    public static final int STATUS_GENERAL_ERROR = 2;
    public static final int STATUS_INSUFFICIENT_FUNDS = 3;

    public ActionHandler(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /**
     * execute Stored Procedure
     *
     * Transactional Never to avoid double
     * wrap operations by action
     *
     * @param passenger -
     * @param action -
     * @throws ApiException -
     */
    public void execute(Passenger passenger, Action action) throws ApiException {

        StoredProcedureQuery spInsert = entityManager.createStoredProcedureQuery("ActionHandler");

        /*
        params
        */
        spInsert.registerStoredProcedureParameter("passenger_id", Long.class, ParameterMode.IN);
        spInsert.registerStoredProcedureParameter("amount", BigDecimal.class, ParameterMode.IN);
        spInsert.registerStoredProcedureParameter("action", String.class, ParameterMode.IN);
        spInsert.registerStoredProcedureParameter("error", String.class, ParameterMode.OUT);
        spInsert.registerStoredProcedureParameter("status", Integer.class, ParameterMode.OUT);
        spInsert.registerStoredProcedureParameter("inbalance", BigDecimal.class, ParameterMode.INOUT);

        /*
        values
        TODO - calc amount
        */
        spInsert.setParameter("passenger_id", passenger.getId());
        spInsert.setParameter("amount", action.getCostAmont());
        spInsert.setParameter("action", action.getType().name().toLowerCase());
        spInsert.setParameter("inbalance", passenger.getBalance());
        spInsert.setFlushMode(FlushModeType.AUTO);

        /*
        exec
        */
        spInsert.execute();

        /*
        get values
        */
        BigDecimal balance = (BigDecimal) spInsert.getOutputParameterValue("inbalance");
        Integer status = (Integer) spInsert.getOutputParameterValue("status");
        String error = (String) spInsert.getOutputParameterValue("error");

        if(status.equals(STATUS_INSUFFICIENT_FUNDS)) {
            throw new ApiException(ApiException.CODES.INSUFFICIENT_FUNDS);
        }

        if(status.equals(STATUS_GENERAL_ERROR)) {
            throw new ApiException(ApiException.CODES.GENERAL, new Exception(error));
        }

        if(status.equals(STATUS_WARNING)) {
            throw new ApiException(ApiException.CODES.GENERAL, new Exception(error));
        }

        passenger.setBalance(balance); //new BigDecimal(balance)

    }

}