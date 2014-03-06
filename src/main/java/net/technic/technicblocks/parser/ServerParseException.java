package net.technic.technicblocks.parser;

public class ServerParseException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ServerParseException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServerParseException(Throwable cause) {
        super(cause);
    }

    public ServerParseException(String message) {
        super(message);
    }

    public ServerParseException() {
        super();
    }
}
