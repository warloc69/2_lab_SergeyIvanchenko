package lab.exception;
/**
*    Class create exception BadIPException.
*    This exception calls, when ip is wrong.
*/
public class BadIPException  extends ConnectException {
    public static final long serialVersionUID = 12351233224l;
    public BadIPException() {
        super();
    }
    public BadIPException(String info) {
        super(info);
    }
    public BadIPException(String message, Throwable cause) {
        super (message, cause);
    }
    public BadIPException(Throwable cause) {
        super(cause);
    }
}