package lab.exception;
/**
*    Class create exception DataAccessException.
*    This exception calls, when we can't work with Data Base.
*/
public class DataAccessException extends Exception {
    public static final long serialVersionUID = 12331233224l;
    public DataAccessException() {
        super();
    }
    public DataAccessException(String info) {
        super(info);
    }
    public DataAccessException(String message, Throwable cause) {
        super (message, cause);
    }
    public DataAccessException(Throwable cause) {
        super(cause);
    }
}