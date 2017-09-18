package com.jackychu.app.tictactoe;

public class ErrorMessage {
	String message = "";
	
	public ErrorMessage() {
	}
	
	public ErrorMessage(String msg) {
		this.message = msg;
	}
	
	public void setMessage(String msg) {
		this.message = msg;
	}
	
	public String getMessage() {
		return this.message;
	}
	
	public String toString() {
		return this.message;
	}
}