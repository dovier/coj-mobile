package cu.uci.coj.Exceptions;

/**
 * Created by osvel on 4/23/16.
 */
public class UnauthorizedException extends Exception {

    public UnauthorizedException() {
        super("Login failed exception");
    }
}
