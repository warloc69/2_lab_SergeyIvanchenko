package lab.exception;
/**
*    Class create exception BadTaskExecException.
*    This exception calls, when the info in the task is wronge.
*/
public class BadTaskException extends Exception {
    public static final long serialVersionUID = 1233123322l;
    public BadTaskException() {
        super();
    }
    public BadTaskException(String info) {
        super(info);
    }
    public BadTaskException(String message, Throwable cause) {
        super (message, cause);
    }
    public BadTaskException(Throwable cause) {
        super(cause);
    }
}