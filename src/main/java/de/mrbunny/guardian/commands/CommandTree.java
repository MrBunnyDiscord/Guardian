package de.mrbunny.guardian.commands;

import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CommandTree implements Command{

    private final Map<String, Command> subCommands = new HashMap<> ();

    public CommandTree withNode(String name, Command command){
        if(getSubcommand(name) == null){
            this.subCommands.put (name.toLowerCase(Locale.ROOT), command);
        } else {
            throw new IllegalArgumentException ( "This subcommand already exists: " + name );
        }
        return this;
    }

    @Override
    public Mono<?> invoke(Context ctx) throws CommandException {
        if(ctx.getArgs ().length == 0){
            return ctx.error ("Not enough arguments.");
        }
        Command subCommand = getSubcommand(ctx.getArgs ()[0]);
        if(subCommand == null){
            return ctx.error ("No knox sub-command: " + ctx.getArgs ()[0]);
        }
        return subCommand.invoke (ctx.stripArgs (1));
    }

    public Command getSubcommand(String name){
        return subCommands.get (name.toLowerCase(Locale.ROOT));
    }

    public Map<String, Command> getSubCommands() {
        return (Map<String, Command>) Collections.unmodifiableSet(subCommands.keySet());
    }
}
