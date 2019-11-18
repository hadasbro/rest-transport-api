package github.hadasbro.transport.aspects;

import github.hadasbro.transport.aspects.loggerHandlers.LoggerHandler;
import github.hadasbro.transport.aspects.classes.GetRequest;
import github.hadasbro.transport.aspects.classes.LoggerDetails;
import github.hadasbro.transport.aspects.tagInterfaces.LoggableRequest;
import github.hadasbro.transport.aspects.tagInterfaces.LoggableResponse;
import github.hadasbro.transport.services.GeneralService;
import lombok.extern.java.Log;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.function.Predicate;

@Log
@Aspect
@Component
@SuppressWarnings({
        "redundant",
        "cast",
        "unused",
        "WeakerAccess",
        "RedundantFieldInitialization",
        "unchecked",
        "ConstantConditions"
})
public class LoggerController {

    public static final String REQUEST_VALUE_SEPARATOR = ":";
    public static final String DEFAULT_TIMEZONE = "UTC";

    @Autowired
    GeneralService service;

    @Pointcut(
            "execution(* *(..)) && @annotation(Logger)"
    )
    public void loggerLog() {}

    @Around("loggerLog()")
    public Object around(ProceedingJoinPoint point) {

        LoggerHandler loggerHandlerInstance = null;

        Method method = ((MethodSignature) point.getSignature()).getMethod();

        Logger logParams = method.getAnnotation(Logger.class);

        // logging details

        LoggerDetails loggerDetails = new LoggerDetails();

        // log in response time/duration if settled

        boolean logDuration = logParams.logResponseDurationTime();
        long start = 0;
        long duration;
        if(logDuration){
            start = System.currentTimeMillis();
        }


        // log stack trace
        boolean logStackTrace = logParams.includeStackTrace();


        // log request time if settled

        boolean logRequestTime = logParams.logRequestTime();

        if(logRequestTime) {
            String filteredPackage = logParams.filterStackTraceToPackage();
            loggerDetails.setStackTrace(getStackTrace(filteredPackage));
        }

        // log request time

        LocalTime timeNow = null;

        if(logRequestTime) {
            String timeZone = logParams.timeZone();
            ZoneId zone = ZoneId.of(timeZone);
            loggerDetails.setRequestTime(LocalTime.now(zone));
        }

        // get log handler class

        Class<? extends LoggerHandler> loggerHandler = logParams.loggerHandler();
        try {
            loggerHandlerInstance = loggerHandler.getDeclaredConstructor().newInstance();
        } catch (
                InstantiationException
                | IllegalAccessException
                | InvocationTargetException
                | NoSuchMethodException e
        ) {
            log.warning(e.toString());
        }



        /*
            ----- parse RESPONSE -----
         */
        Object jpObj = null;

        /* LoggableResponse */
        LoggableResponse response = null;

        try {

            jpObj = point.proceed();

            if(jpObj instanceof LoggableResponse){
                response = (LoggableResponse) jpObj;
            }


            /*
                exceptionally check if it is Spring5:ResponseEntity class
                and if so, then allow logger for this class as well
             */
            Class responseEntityClazz = null;

            try {
                responseEntityClazz = Class.forName("org.springframework.http.ResponseEntity");
            } catch( ClassNotFoundException e ) {
                /* there is no ResponseEntity Class in the scope */
            }

            if(responseEntityClazz.isInstance(jpObj)) {

                Method getBodyMethod = responseEntityClazz.getMethod("getBody");

                Object reqBody = getBodyMethod.invoke(jpObj);

                if(reqBody instanceof LoggableResponse) {
                    response = (LoggableResponse) reqBody;
                }

            }

        } catch (Throwable throwable) {
            log.warning(throwable.toString());
        }


        /*
            ----- parse REQUEST -----
         */
        LoggableRequest request = null;

        try {

            /* LoggableRequest */
            request = Arrays
                    .stream(
                            point.getArgs()
                    )
                    .filter(
                            obj -> obj instanceof LoggableRequest
                    )
                    .map(
                            obj -> (LoggableRequest) obj
                    )
                    .findFirst()
                    .orElse(null);

        } catch (Throwable throwable) {
            log.warning(throwable.toString());
        }

        /*
            obtain GET params
         */
        HttpServletRequest httpRequest = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .getRequest();

        if(httpRequest.getMethod().equalsIgnoreCase("GET")) {
            request = new GetRequest(httpRequest.getRequestURI() + "?" + httpRequest.getQueryString());
        }


        // log only when Rqeuset containg value ...

        String logOnlyOnRequestValue = logParams.logOnlyOnRequestValue();

        String requiredRequestField;

        String requiredRequestValue;

        if(!logOnlyOnRequestValue.equals("") && request != null) {

            String[] splitted = logOnlyOnRequestValue.split(REQUEST_VALUE_SEPARATOR);

            if(splitted.length == 2){

                requiredRequestField = splitted[0];
                requiredRequestValue = splitted[1];

                try {

                    /*
                        check if request object contains required value and
                        if not, then just return null and stop proceed
                     */
                    Object value = FieldUtils.readField(request, requiredRequestField.trim(), true);

                    if(!value.toString().trim().equals(requiredRequestValue.trim())) {
                        return jpObj;
                    }

                } catch (IllegalAccessException | IllegalArgumentException e) {
                    log.warning(String.valueOf(e));
                }
            }
        }


        // checkm request duration if needed
        if(logDuration){
            duration = System.currentTimeMillis() - start;
            loggerDetails.setDuration(duration);
        }




        // get log types, log only request, response, both of them, etc.

        Logger.TYPE[] logTypes = logParams.logTypes();

        Predicate<Logger.TYPE> logAll = el -> el.equals(Logger.TYPE.ALL);
        Predicate<Logger.TYPE> logRequest = el -> el.equals(Logger.TYPE.REQUEST);
        Predicate<Logger.TYPE> logResponse = el -> el.equals(Logger.TYPE.RESPONSE);

        if(loggerHandlerInstance == null) {
            return jpObj;
        } else{
            loggerHandlerInstance.init();
        }

        // log request
        if(Arrays.stream(logTypes).anyMatch(logAll.or(logRequest)) && request != null){
            loggerHandlerInstance.logRequest(request, loggerDetails);
        }

        // log response
        if(Arrays.stream(logTypes).anyMatch(logAll.or(logResponse)) && response != null){
            loggerHandlerInstance.logResponse(response, loggerDetails);
        }

        loggerHandlerInstance.end();

        return jpObj;

    }

    /**
     * getStackTrace
     *
     * @return String
     */
    protected String getStackTrace() {
        return getStackTrace(null);
    }

    /**
     * getStackTrace
     *
     * @return String
     */
    protected String getStackTrace(String filterToPackage) {

        StackTraceElement[] elements = Thread.currentThread().getStackTrace();

        StringBuilder sb = new StringBuilder();

        for (int i = elements.length - 1; i >= 0; i--) {

            StackTraceElement s = elements[i];

            if(s.getClassName().trim().equalsIgnoreCase(getClass().getName().trim())){
                continue;
            }

            if(filterToPackage != null && !filterToPackage.trim().equals("") && !s.getClassName().contains(filterToPackage)){
                continue;
            }

            sb
                    .append(s.getClassName())
                    .append(".")
                    .append(s.getMethodName())
                    .append("(").append(s.getFileName())
                    .append(":")
                    .append(s.getLineNumber()).append(")")
                    .append(" -> ");
        }

        return StringUtils.strip(sb.toString(), " -> ");

    }

}