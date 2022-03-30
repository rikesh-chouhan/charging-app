package charging.app;

public class CannotScheduleException extends Exception {

    public CannotScheduleException() {
        super();
    }

    public CannotScheduleException(String message) {
        super(message);
    }

    public CannotScheduleException(Exception e) {
        super(e);
    }
}
