package github.hadasbro.transport.controllers;

import github.hadasbro.transport.exceptions.ValidationException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@SuppressWarnings(value = {"unused", "WeakerAccess"})
abstract public class BaseController {

    private static Map<String, String> getParams = new HashMap<>();

    /**
     * parseValidationErrors
     *
     * @param result -
     * @throws ValidationException -
     */
    protected void parseValidationErrors(BindingResult result) throws ValidationException {

        if (result.hasErrors()) {

            List<ObjectError> errors = result.getAllErrors();

            String allErrrors = errors
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.joining(", "));

            throw new ValidationException(allErrrors);

        }

    }

    /**
     * parseValidationErrorsWithFilter
     *
     * @param result -
     * @param filter -
     * @throws ValidationException -
     */
    protected void parseValidationErrorsWithFilter(BindingResult result, Predicate<ObjectError> filter) throws ValidationException {

        if (result.hasErrors()) {

            List<ObjectError> errors = result.getAllErrors();

            String allErrrors = errors
                    .stream()
                    .filter(filter)
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.joining(", "));

            if (allErrrors.length() > 0)
                throw new ValidationException(allErrrors);

        }

    }

    /**
     * getGet
     *
     * @param key -
     * @return String
     * @throws NullPointerException -
     */
    public static String getGet(String key) throws NullPointerException{

        if(!issetGet(key)) {
            throw new NullPointerException();
        }

        return getParams.get(key);

    }

    /**
     * issetGet
     *
     * @param key -
     * @return boolean
     */
    public static boolean issetGet(String key){
        return getParams.containsKey(key);
    }

    /**
     * loadGetParams
     *
     * use this method and also this::issetGet
     * if you want to save and read/use params from GET
     *
     * e.g. for test purposes you can do
     * loadGetParams()
     * and then
     * if(issetGet(myParam)){ do something }
     *
     * @param req -
     */
    public static void loadGetParams(HttpServletRequest req){

        getParams = Collections.list(req.getParameterNames())
                .stream()
                .collect(
                        Collectors.toMap(
                                parameterName -> parameterName, el -> {
                                    String[] vals = req.getParameterValues(el);
                                    return vals.length > 0 ? vals[0] : "";
                                }
                        )
                );

    }
}
