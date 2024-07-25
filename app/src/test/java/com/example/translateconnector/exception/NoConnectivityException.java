package com.example.translateconnector.exception;

import java.io.IOException;

/**
 * Created by ton on 4/5/18.
 */
public class NoConnectivityException extends IOException {

    @Override
    public String getMessage() {
        return "No connectivity exception";
    }

}
