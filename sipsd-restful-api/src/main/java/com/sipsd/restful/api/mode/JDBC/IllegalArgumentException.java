package com.sipsd.restful.api.mode.JDBC;

/**
 * Created by Administrator on 2016/8/31.
 */
public class IllegalArgumentException extends Exception {

    public IllegalArgumentException() {
    }

    public IllegalArgumentException(String message) {
        super(message);
    }

    public IllegalArgumentException(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalArgumentException(Throwable cause) {
        super(cause);
    }


}
