package net.tetromi.idocs.client;

public class OAuthException extends Exception {
    public String authToken;

    public OAuthException() {
        this.authToken = null;
    }

    public OAuthException(String authToken) {
        this.authToken = authToken;
    }
}
