package net.tetromi;

import org.restlet.data.Cookie;
import org.restlet.data.Reference;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import org.restlet.util.Series;
import org.scribe.http.Request;
import org.scribe.oauth.Scribe;
import org.scribe.oauth.Token;

import java.util.Properties;

/**
 * @user will
 * @date May 22, 2010 1:11:43 AM
 */
public class NoteResource extends ServerResource {
    private final String ACCESS_TOKEN = "access_token";

    @Get("html")
    public String represent() {
        try {
/*
            final NoteModel note = Note.getNotes(getNote()).get(0);
            // produce the HTML output
            StringBuffer sb = new StringBuffer();
            sb.append("<html><head></head><body>");
            sb.append(note.getNoteHTML());
            sb.append("</body></html>");
            return sb.toString();
*/
            return "";
        } catch (Exception e) {
            e.printStackTrace();
            Reference ref = new Reference("/api/");
            redirectSeeOther(ref);
            return "redirect!";
        }
    }

    @Get("json")
    public String getJSON() {
        try {
            return getNote();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private String getNote() throws Exception {
        Properties props = new Properties();
        props.load(RestResource.class.getResourceAsStream("/ubuntuone.properties"));
        Scribe scribe = new Scribe(props);

        final Series<Cookie> cookies = getCookies();
        final String accessString = cookies.getFirstValue(ACCESS_TOKEN);
        Token accessToken = new UbuntuOneEqualizer().parseAccessTokens(accessString);
        if(accessToken.getToken() == null) {
            // we don't have an auth token stored yet, so this edit request isn't valid.
            throw new Exception();
        }

        final String id = getQuery().getFirstValue("id");
        Request req = new Request(Request.Verb.GET, "https://one.ubuntu.com/notes/api/1.0/op/"+id+"?include_notes=true");
        scribe.signRequest(req, accessToken);
        return req.send().getBody();
    }
}
