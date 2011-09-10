package net.tetromi.idocs.server;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import net.tetromi.idocs.client.IdocsService;
import net.tetromi.idocs.client.Metadata;
import net.tetromi.idocs.client.Note;
import net.tetromi.idocs.client.OAuthException;
import org.scribe.http.Request;
import org.scribe.http.Response;
import org.scribe.oauth.Scribe;
import org.scribe.oauth.Token;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Properties;

public class IdocsServiceImpl extends RemoteServiceServlet implements IdocsService {
    public static final Logger log = LoggerFactory.getLogger(IdocsServiceImpl.class);

    public static final String NOTES_API_URL = "https://one.ubuntu.com/notes/api/1.0/op/";
    public static final String NOTES_USER_INFO = "https://one.ubuntu.com/notes/api/1.0/user/";
    public static final Type noteListType = new TypeToken<List<Note>>() {
    }.getType();
    public static final JsonParser parser = new JsonParser();
    public static final GsonBuilder builder = new GsonBuilder();

    static {
        builder.setDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        builder.registerTypeAdapter(Token.class, new InstanceCreator<Token>() {
            public Token createInstance(Type type) {
                return new Token("", "");
            }
        });
    }

    public static final Gson g = builder.create();

    public String getAccessToken(String requestToken, String verifier) {
        return g.toJson(getScribe().getAccessToken(g.fromJson(requestToken, Token.class), verifier));
    }

    public Metadata getInfo(String accessToken) throws OAuthException {
        return g.fromJson(oAuthGet(accessToken,NOTES_USER_INFO),Metadata.class);
    }

    public Metadata updateNotes(String accessToken, int syncRevision, List<Note> notes) throws Exception {
        JsonObject json = new JsonObject();
        json.addProperty("latest-sync-revision",syncRevision+1);
        json.add("note-changes",g.toJsonTree(notes,noteListType));
        // append the required "note-content-version: 0.1" constant parameter to the json string
        for(JsonElement j : json.getAsJsonArray("note-changes")) {
            j.getAsJsonObject().add("note-content-version",new JsonPrimitive(0.1));
        }
        oAuthPut(accessToken,NOTES_API_URL,json.toString());
        return getInfo(accessToken);
    }

    /** Returns a new request token fetched from the API server. */
    public String getToken() {
        return g.toJson(getScribe().getRequestToken());
    }

    private Scribe getScribe() {
        try {
            final Properties props = new Properties();
            props.load(IdocsServiceImpl.class.getResourceAsStream("/ubuntuone.properties"));
            final HttpServletRequest req = getThreadLocalRequest();
            final StringBuffer redirectUrl = req.getRequestURL();
            final String redir2 = redirectUrl.substring(0, redirectUrl.indexOf("/Idocs/IdocsService"))+"/index.html";
            props.setProperty(Scribe.Properties.CALLBACK_URL, redir2);
            return new Scribe(props);
        } catch (IOException e) {
            return null;
        }
    }

    /** Gets a list of notes from the server. */
    public List<Note> getNotes(String accessToken, int sinceRevision, boolean getContent) throws OAuthException {
        final StringBuffer url = new StringBuffer(NOTES_API_URL)
                .append("?since=")
                .append(sinceRevision);
        if(getContent) url.append("&include_notes=true");
        String json = oAuthGet(accessToken, url.toString());
        final JsonElement noteArray = parser.parse(json).getAsJsonObject().get("notes");
        return g.fromJson(noteArray, noteListType);
    }

    /** Delivers an oAuth-signed GET request. */
    private String oAuthGet(String accessToken, String apiUrl) throws OAuthException {
        return oAuthRequest(accessToken, apiUrl, Request.Verb.GET, null);
    }

    /** Delivers an oAuth-signed PUT request. */
    private String oAuthPut(String accessToken, String apiUrl, String data) throws OAuthException {
        return oAuthRequest(accessToken, apiUrl, Request.Verb.PUT, data);
    }

    private String oAuthRequest(String accessToken, String apiUrl, Request.Verb verb, String data) throws OAuthException {
        final Scribe scribe = getScribe();
        final Request req = new Request(verb, apiUrl);
        if(data != null) req.addPayload(data);
        scribe.signRequest(req, g.fromJson(accessToken, Token.class));
        final Response response = req.send();
        if (response.getCode() != 200) throw new OAuthException();
        return response.getBody();
    }

    /** Gets the contents of a single note from the server. */
    public Note getNote(String accessToken, String guid) throws OAuthException {
        String json = oAuthGet(accessToken, NOTES_API_URL + guid + "?include_notes=true");
        final JsonElement noteArray = parser.parse(json).getAsJsonObject().get("notes");
        return g.fromJson(noteArray.getAsJsonArray().get(0), Note.class);
    }

}