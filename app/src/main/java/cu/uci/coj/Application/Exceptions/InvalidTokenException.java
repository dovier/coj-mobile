package cu.uci.coj.Application.Exceptions;

import java.io.IOException;

/**
 * Created by osvel on 5/6/16.
 */
public class InvalidTokenException extends IOException {

    public InvalidTokenException() {
        super("Invalid token");
    }
}
