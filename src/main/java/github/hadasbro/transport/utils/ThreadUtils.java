package github.hadasbro.transport.utils;


import java.util.concurrent.CompletionException;


@SuppressWarnings({"unused"})
final public class ThreadUtils {

    /**
     * CompletionException
     *
     * @param tex -
     * @return CompletionException
     */
    public static CompletionException excToCompletableExc(Throwable tex) {
        return  tex instanceof CompletionException
                ? (CompletionException)tex
                : new CompletionException(tex);
    }


}

