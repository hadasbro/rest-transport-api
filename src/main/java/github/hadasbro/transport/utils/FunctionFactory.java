package github.hadasbro.transport.utils;

import net.jodah.typetools.TypeResolver;
import java.util.*;
import java.util.stream.Collectors;


/**
 * FunctionFactory
 *
 * use it to register consumer or function with default parameters
 *
 * usage:

    - register function with default params:

    FunctionFactory.ConsumerDefaultParams2<Integer, Integer, String, Character> myConsumer = FunctionFactory.registerConsumer(
        (Integer a, Integer b, String c, Character e) -> { /CONSUMER BODY/  },
        FunctionFactory.REQUIRED.NO_DEFAULT,
        FunctionFactory.REQUIRED.NO_DEFAULT,
        "default string value",
        'x');

    OR [ Java >= 11 ]

    FunctionFactory.ConsumerDefaultParams4<Integer, Integer, String, Character> myConsumer2=FunctionFactory.registerConsumer(
     (var a,var b,var c,var e)->{ /CONSUMER BODY/  },
     1,
     2,
     "default string value",
     'x');

    - call function:

        myConsumer.apply(1,2);
        myConsumer.apply(1,2,"string value");
        myConsumer.apply(1,2,"string value",'c');

        myConsumer2.apply();
        myConsumer.apply(1);
        myConsumer.apply(1,2);
        myConsumer.apply(1,2,"string value");


    TODO
    - future [after bugfix in java 11]
    + default param values should be in lambda params annotations
    (var a, var bee, @def("def stroing hehe B") var ster, @def("def i B") var charhe) -> {}

    - add functions factory, not only consumer factory
 *
 */

@SuppressWarnings({"unused", "WeakerAccess", "SameParameterValue", "JavaDoc", "Convert2Diamond"})
final public class FunctionFactory {

    /**
     * Required parameter
     */
    enum REQUIRED{NO_DEFAULT}

    /**
     * Allowed parameters
     */
    private static Set<Class<?>> allowedParamTypes = new TreeSet<Class<?>>(new Comparator<Class<?>>(){

        /**
         * Class1 == Class2 if Class1 object instanceof Class2
         * e.g. Integer == Number, Double == Number etc.
         *
         * @param o1
         * @param o2
         * @return int
         */
        public int compare(Class<?> o1, Class<?> o2) {
            if(o2.isAssignableFrom(o1)){
                return 0;
            } else{
                return (o1.getName().compareTo(o2.getName()));
            }
        }
    })
    {{
        /*
         * allowed params
         */
        add(Number.class);
        add(Character.class);
        add(Date.class);
        add(Boolean.class);
        add(String.class);

    }};

    /**
     * checkIllegalArguments
     *
     * @param cls class
     * @param fun registered lambda
     * @throws IllegalArgumentException illegal args exception
     */
    private static void checkIllegalArguments(final Class<?> cls, final Class<?> fun) throws IllegalArgumentException {

        // throw an erros with expected function's signature

        Class<?>[] typeArgs = TypeResolver.resolveRawArguments(cls, fun);

        StringJoiner sj = new StringJoiner(",", "Illegal arguments in consumer factory (", ")");

        if(Arrays.stream(typeArgs).anyMatch(e -> !allowedParamTypes.contains(e))){

            sj.add(" Supported arguments must be an instance of: ");

            sj.add(
                    allowedParamTypes
                            .stream()
                            .map(Objects::toString)
                            .collect(Collectors.joining(", "))
            );

            throw new IllegalArgumentException(sj.toString());

        }

    }

    /**
     * Consumer interfces
     *
     * @param <T>
     * @param <S>
     * @param <U>
     * @param <W>
     */
    @FunctionalInterface private interface ConsumerParams4Multi<T, S, U, W> {void apply(T te, S s, U u, W w);}

    /*
     * todo: other consumers
     * other consumers
     */

    /**
     * common consumer interface
     */
    private interface ConsumerDefaultParamsCommon{}

    /**
     * Processed consumer interfaces
     *
     * @param <T>
     * @param <S>
     * @param <U>
     * @param <W>
     */
    @FunctionalInterface public interface ConsumerParams4Default4<T, S, U, W>  extends ConsumerParams4Multi<T, S, U, W> {}
    @FunctionalInterface public interface ConsumerParams4Default3<T, S, U, W>  extends ConsumerParams4Multi<T, S, U, W> {}
    @FunctionalInterface public interface ConsumerParams4Default2<T, S, U, W>  extends ConsumerParams4Multi<T, S, U, W> {}
    @FunctionalInterface public interface ConsumerParams4Default1<T, S, U, W>  extends ConsumerParams4Multi<T, S, U, W> {}

    /**
     * Registered consumer interfaces
     * Matrix of consumer methods
     *
     * @param <T>
     * @param <S>
     * @param <U>
     * @param <W>
     */
    public interface ConsumerDefaultParams4<T, S, U, W> extends ConsumerDefaultParams3<T, S, U, W> {
        default void apply(){apply(null, null,null,null);}
    }

    public interface ConsumerDefaultParams3<T, S, U, W> extends ConsumerDefaultParams2<T, S, U, W> {
        default void apply(T a){apply(a, null,null,null);}
    }

    public interface ConsumerDefaultParams2<T, S, U, W> extends ConsumerDefaultParams1<T, S, U, W> {
        default void apply(T a, S b){apply(a, b, null,null);}
    }

    public interface ConsumerDefaultParams1<T, S, U, W> extends ConsumerDefaultParams0<T, S, U, W> {
        default void apply(T a, S b, U c){apply(a, b, c,null);}
    }

    public interface ConsumerDefaultParams0<T, S, U, W> extends ConsumerDefaultParamsCommon {
        void apply(T a, S b, U c, W d);
    }

    /**
     * Register consumer
     *
     * 4 params 4 default
     *
     * @param fun - lambda
     * @param def1 - default param
     * @param def2 - default param
     * @param def3 - default param
     * @param def4 - default param
     * @param <T> - type
     * @param <S> - type
     * @param <U> - type
     * @param <W> - type
     * @return ConsumerDefaultParams4
     */
    public static <T, S, U, W> ConsumerDefaultParams4<T, S, U, W> registerConsumer(ConsumerParams4Default4<T, S, U, W> fun, T def1, S def2, U def3, W def4) {

        checkIllegalArguments(ConsumerParams4Default4.class, fun.getClass());

        return (a, b, c, d) -> {
            T ag = (a == null ? def1 : a);
            S bg = (b == null ? def2 : b);
            U cg = (c == null ? def3 : c);
            W dg = (d == null ? def4 : d);
            fun.apply(ag, bg, cg, dg);
        };

    }

    /**
     * Register consumer
     *
     * @param fun - lambda
     * @param def1 - default param
     * @param def2 - default param
     * @param def3 - default param
     * @param def4 - default param
     * @param <T> - type
     * @param <S> - type
     * @param <U> - type
     * @param <W> - type
     * @return ConsumerDefaultParams3
     */
    public static <T, S, U, W> ConsumerDefaultParams3<T, S, U, W> registerConsumer(ConsumerParams4Default3<T, S, U, W> fun, REQUIRED def1, S def2, U def3, W def4) {

        checkIllegalArguments(ConsumerParams4Default3.class, fun.getClass());

        return (a, b, c, d) -> {
            S bg = (b == null ? def2 : b);
            U cg = (c == null ? def3 : c);
            W dg = (d == null ? def4 : d);
            fun.apply(a, bg, cg, dg);
        };

    }

    /**
     * Register consumer
     *
     * @param fun - lambda
     * @param def1 - default param
     * @param def2 - default param
     * @param def3 - default param
     * @param def4 - default param
     * @param <T> - type
     * @param <S> - type
     * @param <U> - type
     * @param <W> - type
     * @return ConsumerDefaultParams2
     */
    static <T, S, U, W> ConsumerDefaultParams2<T, S, U, W> registerConsumer(ConsumerParams4Default2<T, S, U, W> fun, REQUIRED def1, REQUIRED def2, U def3, W def4) {

        checkIllegalArguments(ConsumerParams4Default2.class, fun.getClass());

        return (a, b, c, d) -> {
            U cg = (c == null ? def3 : c);
            W dg = (d == null ? def4 : d);
            fun.apply(a, b, cg, dg);
        };

    }

    /**
     * Register consumer
     *
     * @param fun - lambda
     * @param def1 - default param
     * @param def2 - default param
     * @param def3 - default param
     * @param def4 - default param
     * @param <T> - type
     * @param <S> - type
     * @param <U> - type
     * @param <W> - type
     * @return ConsumerDefaultParams1
     */
    public static <T, S, U, W> ConsumerDefaultParams1<T, S, U, W> registerConsumer(ConsumerParams4Default1<T, S, U, W> fun, REQUIRED def1, REQUIRED def2, REQUIRED def3, W def4) {

        checkIllegalArguments(ConsumerParams4Default1.class, fun.getClass());

        return (a, b, c, d) -> {
            W dg = (d == null ? def4 : d);
            fun.apply(a, b, c, dg);
        };
    }

}
