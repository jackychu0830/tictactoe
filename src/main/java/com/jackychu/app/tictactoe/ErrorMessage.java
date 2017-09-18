package com.jackychu.app.tictactoe;

/**
 * The class for http response to represent error message.
 * Will convert to json format automatically when response to client.
 */
public class ErrorMessage {
    /**
     * The error message
     */
    private String message = "";

    /**
     * Constructor without default message
     */
    public ErrorMessage() {
    }

    /**
     * Constructor with default message
     *
     * @param msg Error message
     */
    public ErrorMessage(String msg) {
        this.message = msg;
    }

    /**
     * Set error message
     *
     * @param msg Error message
     */
    public void setMessage(String msg) {
        this.message = msg;
    }

    /**
     * Get error message
     *
     * @return Error message
     */
    public String getMessage() {
        return this.message;
    }

    /**
     * Return error message
     *
     * @return Error message
     */
    public String toString() {
        return this.message;
    }
}