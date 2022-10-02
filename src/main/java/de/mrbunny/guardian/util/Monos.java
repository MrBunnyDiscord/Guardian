package de.mrbunny.guardian.util;

import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

public class Monos {

    public static <A, B, C>Function<Mono<A>, Mono<C>> flatZipWith(Mono<? extends B> b, BiFunction<A, B, Mono<C>> combinator){
        return in -> in.zipWith (b, combinator).flatMap (Function.identity ());
    }

    public static <T> Function<Mono<T>, Mono<Optional<T>>> asOptional(){
        return in -> in.map (Optional::of).defaultIfEmpty (Optional.empty ());
    }
}
