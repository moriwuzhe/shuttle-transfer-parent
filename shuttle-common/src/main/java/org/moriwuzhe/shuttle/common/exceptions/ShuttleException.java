package org.moriwuzhe.shuttle.common.exceptions;

/**
  * @Description:
  * @Author: moriwuzhe
  * @Date: 2021-03-17 00:55
  * @Version: 1.0
  */
public class ShuttleException extends RuntimeException {
    static final long serialVersionUID = 1L;

    public ShuttleException() {
        super();
    }

    public ShuttleException(String message) {
        super(message);
    }

    public ShuttleException(String message, Throwable cause) {
        super(message, cause);
    }

    public ShuttleException(Throwable cause) {
        super(cause);
    }

    protected ShuttleException(String message, Throwable cause,
                               boolean enableSuppression,
                               boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
