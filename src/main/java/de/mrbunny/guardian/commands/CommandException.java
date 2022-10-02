package de.mrbunny.guardian.commands;

public class CommandException extends RuntimeException{

    private static final long serialVersionUID = 1025890998489976852L;

    public CommandException(String msg){
        super(msg);
    }

    public CommandException(Throwable cause){
        super(cause.getMessage (), cause);
    }

    public CommandException(String msg, Throwable cause){
        super(msg, cause);
    }
}
