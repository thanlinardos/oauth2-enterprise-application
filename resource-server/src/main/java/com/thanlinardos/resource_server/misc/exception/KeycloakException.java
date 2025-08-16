package com.thanlinardos.resource_server.misc.exception;

import com.thanlinardos.resource_server.model.info.OperationType;

public class KeycloakException extends Exception {

    public KeycloakException(String message) {
        super(message);
    }

    public KeycloakException(OperationType operationType, Class<?> inputClazz, Throwable cause) {
        super(getErrorMsg(operationType, inputClazz.getSimpleName()) + " and cause: ", cause);
    }

    public KeycloakException(OperationType operationType, Object input, Throwable cause) {
        super(getErrorMsg(operationType, input) + " and cause: ", cause);
    }

    public KeycloakException(OperationType operationType, Class<?> inputClazz) {
        super(getErrorMsg(operationType, inputClazz.getSimpleName()));
    }

    private static String getErrorMsg(OperationType operationType, String inputClassName) {
        return "Failed to execute Keycloak operationType: " + operationType + " with input type: " + inputClassName;
    }

    private static String getErrorMsg(OperationType operationType, Object input) {
        return "Failed to execute Keycloak operationType: " + operationType + " with input: " + input;
    }
}
