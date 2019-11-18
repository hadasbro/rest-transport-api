package github.hadasbro.transport.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * TraceBuilder singleton
 *
 * this is an acumulator for requests, responses, exceptions etc.
 * you can use this class to store the request's data and then
 * save it to DB or to logs etc.
 *
 * usage:
 *
 * TraceBuilder.INSTANCE
 *      .exceptions(new Exception())
 *      .info("My info, log")
 *      .info("My log 2")
 *      .request("my request as a string")
 *      .response("my response");
 *
 *  then you can log in acumulated result to DB or do whatever
 *
 *  DB.insert((String)TraceBuilder.INSTANCE);
 *
 */
@SuppressWarnings({"unused"})
public enum TraceBuilder {

    INSTANCE;

    final private int TRACE_LIMIT = 30;

    private ArrayList<String> trancePackages = new ArrayList<>(){{
        add("java.lang");
    }};

    private String request;
    private String response;
    private String trace;
    private List<Exception> exceptions = new ArrayList<>();
    private Map<String, String> info = new HashMap<>();
    private AtomicInteger iCounter = new AtomicInteger(0);

    public void setTracePackages(ArrayList<String> trancePackages){
        this.trancePackages = trancePackages;
    }

    public String getCurrentTrace(){

        Predicate<StackWalker.StackFrame> packageFiler = f -> {

            if(trancePackages.isEmpty()) {
                return true;
            }

            for(String pckg : trancePackages) {
                if(f.getClassName().startsWith(pckg)){
                    return true;
                }
            }

            return false;
        };

        /*
        get trace, filer only needed packages and collect to string
         */
        return StackWalker
                .getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE)
                .walk(
                        s -> s
                                .filter(packageFiler)
                                .limit(TRACE_LIMIT)
                                .collect(Collectors.toList())
                )
                .stream()
                .map(Object::toString)
                .collect(Collectors.joining(" | "));


    }

    public TraceBuilder exceptions(Exception exception){
        this.exceptions.add(exception);
        return this;
    }

    public TraceBuilder info(String info){
        this.info.put("info#" + iCounter.incrementAndGet(), info);
        return this;
    }

    public TraceBuilder info(String key, String info){
        this.info.put(key, info);
        return this;
    }

    public TraceBuilder request(String request){
        this.request = request;
        return this;
    }

    public TraceBuilder response(String response){
        this.response = response;
        return this;
    }

    public TraceBuilder trace(String trace){
        this.trace = trace;
        return this;
    }

    @Override
    public String toString() {

        return "TraceBuilder{" +
                "request='" + request + '\'' +
                ", response='" + response + '\'' +
                ", all traces='" + trace + '\'' +
                ", trace='" + this.getCurrentTrace() + '\'' +
                ", exceptions=" + exceptions +
                ", info=" + info +
                '}';
    }
}
