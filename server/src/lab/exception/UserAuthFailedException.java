package lab.exception;
/**
*    Class create exception DataAccessException.
*    This exception calls, when we can't work with Data Base.
*/
public class UserAuthFailedException extends DataAccessException {
    public static final long serialVersionUID = 12331233224l;
    public UserAuthFailedException() {
        super();
    }
    public UserAuthFailedException(String info) {
        super(info);
    }
    public UserAuthFailedException(String message, Throwable cause) {
        super (message, cause);
    }
    public UserAuthFailedException(Throwable cause) {
        super(cause);
    }
}