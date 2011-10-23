package lab.exception;
/**
*    Class create exception ConnectException.
*    This exception calls, when we can't open socket.
*/
public class  ConnectException extends DataAccessException {
    public static final long serialVersionUID = 12351233224l;
    public ConnectException() {
        super();
    }
    public ConnectException(String info) {
        super(info);
    }
    public ConnectException(String message, Throwable cause) {
        super (message, cause);
    }
    public ConnectException(Throwable cause) {
        super(cause);
    }
}