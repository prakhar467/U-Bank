package com.company.ubank.exceptions;

public class AccountAlreadyRegisteredException extends Exception {
    public AccountAlreadyRegisteredException (String message) {
        super(message);
    }
}
