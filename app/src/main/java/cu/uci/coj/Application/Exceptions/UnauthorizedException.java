package cu.uci.coj.Application.Exceptions;

/**
 * Created by osvel on 4/23/16.
 */
public class UnauthorizedException extends Exception {

    public UnauthorizedException() {
        super("Login failed exception");
    }
}
