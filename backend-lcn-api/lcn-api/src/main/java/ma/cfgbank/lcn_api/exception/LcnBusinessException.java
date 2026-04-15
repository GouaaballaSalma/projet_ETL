package ma.cfgbank.lcn_api.exception;

public class LcnBusinessException extends RuntimeException {
    public LcnBusinessException(String message) {
        super(message);
    }
    
    public LcnBusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
