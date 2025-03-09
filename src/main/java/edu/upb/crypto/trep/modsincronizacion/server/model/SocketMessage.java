package edu.upb.crypto.trep.server.model;

public class SocketMessage {
    private String action;
    private Object data;
    private boolean success;
    private String message;

    // Getters and setters
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public Object getData() { return data; }
    public void setData(Object data) { this.data = data; }
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}