package roc.tarek.mobileappws.exceptions;

import java.util.Date;

public class UserServiceException extends RuntimeException {
    private static final long serialVersionUID = -5624181661285273363L;

    public UserServiceException(String message) {
        super(message);
    }
}
