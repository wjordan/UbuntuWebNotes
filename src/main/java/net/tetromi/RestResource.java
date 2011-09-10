package net.tetromi;

import org.restlet.data.Cookie;
import org.restlet.data.CookieSetting;
import org.restlet.data.Reference;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import org.restlet.util.Series;
import org.scribe.http.Request;
import org.scribe.http.Response;
import org.scribe.oauth.Scribe;
import org.scribe.oauth.Token;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Properties;

/**
 * Resource which has only one representation.
 */
public class RestResource extends ServerResource {

    private final String TOKEN_SECRET = "token_secret";
    private final String OAUTH_TOKEN = "oauth_token";
    private final String OAUTH_VERIFIER = "oauth_verifier";
    private final String ACCESS_TOKEN = "access_token";

    @Get("html")
    public String represent() {
        try {
            Properties props = new Properties();
            props.load(RestResource.class.getResourceAsStream("/ubuntuone.properties"));
            Scribe scribe = new Scribe(props);

            final Series<Cookie> cookies = getCookies();
            final String authToken = cookies.getFirstValue(OAUTH_TOKEN);
            final String accessString = cookies.getFirstValue(ACCESS_TOKEN);
            final String verifier = cookies.getFirstValue(OAUTH_VERIFIER);
            final String tokenSecret = cookies.getFirstValue(TOKEN_SECRET);

            Token accessToken;
            if (authToken == null) {
                Token token = scribe.getRequestToken();
                if(token.getToken() == null) {
                    // couldn't even get a request token for some reason?
                    throw new Exception(token.getRawString());
                }
                Reference ref = new Reference(props.getProperty("authorize.url"))
                        .addQueryParameter(OAUTH_TOKEN, token.getToken());
                getCookieSettings().add(new CookieSetting(TOKEN_SECRET, token.getSecret()));
                redirectTemporary(ref);
                return "redirect!";
            } else {
                if (accessString == null) {
                    accessToken = scribe.getAccessToken(new Token(authToken, tokenSecret), verifier);
                    if(accessToken.getToken() == null) {
                        throw new Exception(accessToken.getRawString());
                    }
                    getResponse().getCookieSettings().add(new CookieSetting(ACCESS_TOKEN,accessToken.getRawString()));
                } else {
                    accessToken = new UbuntuOneEqualizer().parseAccessTokens(accessString);
                }
            }

            // If you reach this point, you have a valid access token, so create the signed data request
            Request req = new Request(Request.Verb.GET, "https://one.ubuntu.com/notes/api/1.0/op/");
            scribe.signRequest(req, accessToken);
            final Response last = req.send();

            // produce the HTML output
            StringBuffer sb = new StringBuffer();
            sb.append("<html><head></head><body>");
//            sb.append(Note.getListHTML(last.getBody()));
            sb.append("</body></html>");
            return sb.toString();
        } catch (Exception e) {
            // Wipe all cookies if we have a problem, start from scratch.
            deleteCookie(OAUTH_TOKEN);
            deleteCookie(OAUTH_VERIFIER);
            deleteCookie(TOKEN_SECRET);
            deleteCookie(ACCESS_TOKEN);
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            return "error:" + sw.toString();
        }
    }

    private void deleteCookie(String cookieName) {
        final Series<CookieSetting> cookies = getResponse().getCookieSettings();
        final CookieSetting cookie = new CookieSetting(cookieName, "");
        cookie.setMaxAge(0);
        cookies.add(cookie);
    }
}