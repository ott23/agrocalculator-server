package net.tngroup.acserver.web.exceptions;

public class NodeException extends Exception {

    public NodeException(String message) {
        super(message);
    }

    public NodeException(Exception e) {
        super(e.getMessage());
        setStackTrace(e.getStackTrace());
    }
}
