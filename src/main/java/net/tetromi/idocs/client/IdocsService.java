package net.tetromi.idocs.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import java.util.List;

@RemoteServiceRelativePath("IdocsService")
public interface IdocsService extends RemoteService {
    // Sample interface method of remote interface

    /**
     * Retrieves the user's list of notes from the server.
     */
    List<Note> getNotes(String accessToken, int sinceRevision, boolean getContent) throws OAuthException;

    Note getNote(String accessToken, String guid) throws OAuthException;

    String getToken();

    String getAccessToken(String requestToken, String verifier);

    Metadata getInfo(String accessToken) throws OAuthException;

    Metadata updateNotes(String accessToken, int syncRevision, List<Note> notes) throws Exception;
}
