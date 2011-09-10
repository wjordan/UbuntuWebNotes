package net.tetromi;

import org.restlet.data.Form;
import org.restlet.data.Reference;
import org.scribe.eq.DefaultEqualizer;
import org.scribe.oauth.Token;

/**
 * @user will
 * @date May 21, 2010 6:15:33 PM
 */
public class UbuntuOneEqualizer extends DefaultEqualizer {
    @Override
    public Token parseRequestTokens(String response) {
        return parseTokens(response);
    }

    @Override
    public Token parseAccessTokens(String response) {
        return parseTokens(response);
    }

    private Token parseTokens(String response) {
        Form ref = new Reference(Reference.toString("", response, "")).getQueryAsForm();
        return new Token(ref.getFirstValue("oauth_token"),ref.getFirstValue("oauth_token_secret"),response);
    }
}
