package de.mrbunny.guardian.commands;

import reactor.core.publisher.Mono;

@FunctionalInterface
public interface Command {

    Mono<?> invoke(Context ctx) throws CommandException;

    default String description(){
        return "None";
    }

    default String usage(){
        return "None";
    }
}
