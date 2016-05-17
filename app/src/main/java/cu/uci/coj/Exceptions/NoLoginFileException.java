package cu.uci.coj.Exceptions;

/**
 * Created by osvel on 4/23/16.
 */
public class NoLoginFileException extends Exception {

    public NoLoginFileException() {
        super("No login file found");
    }
}
