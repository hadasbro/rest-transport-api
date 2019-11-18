package github.hadasbro.transport.aspects;

import github.hadasbro.transport.aspects.loggerHandlers.DefaultLogger;
import github.hadasbro.transport.aspects.loggerHandlers.LoggerHandler;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Logger interface
 */
@SuppressWarnings({
        "unused",
        "WeakAccess"
})
@Retention(
        RetentionPolicy.RUNTIME
)
@Target({
        ElementType.CONSTRUCTOR,
        ElementType.METHOD
})
public @interface Logger {

    enum LOG{DB,LOG,DB_LOG}
    LOG value() default LOG.DB;
    Class<?> clazz() default Object.class;

    /**
     * logResponseDurationTime
     *
     * @return boolean
     */
    boolean logResponseDurationTime() default true;

    /**
     * includeStackTrace
     *
     * @return boolean
     */
    boolean includeStackTrace() default true;

    /**
     * filterStackTraceToPackage
     * @return String
     */
    String filterStackTraceToPackage() default "github.hadasbro.transport";

    /**
     * logRequestTime
     *
     * @return boolean
     */
    boolean logRequestTime() default true;

    /**
     * timeZone
     *
     * see: ZoneId.of("UTC")
     *
     * @return String
     */
    String timeZone() default "UTC";

    /**
     * log Only On Request Value
     *
     * request_field:request_value
     *
     * @return String
     */
    String logOnlyOnRequestValue() default "";

    /**
     * Log
     *
     * - all actions
     *
     * - request
     * - response
     * - error/exception
     * - trace on response/last action
     */
    enum TYPE{
        ALL,
        REQUEST,
        RESPONSE,
        ERROR,
        TRACE
    }

    /**
     * Log types
     *
     * @return TYPE[]
     */
    TYPE[] logTypes() default {TYPE.ALL};

    /**
     * Logger handler class
     * @return Class<? extends LoggerHandler>
     */
    Class<? extends LoggerHandler> loggerHandler() default DefaultLogger.class;

}