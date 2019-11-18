package github.hadasbro.transport.exceptions;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString @EqualsAndHashCode(callSuper = false)
@SuppressWarnings({"unused", "WeakerAccess"})
public class BlockerRuntimeException extends RuntimeException {

    public static BlockerRuntimeException wrap(Throwable t){
        return
                t instanceof BlockerRuntimeException
                        ? (BlockerRuntimeException) t
                        : new BlockerRuntimeException(t);
    }

    public BlockerRuntimeException(Throwable cause) {
        super(cause);
    }
}
