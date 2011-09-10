package net.tetromi.idocs.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.json.client.*;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import net.tetromi.idocs.client.data.Async;
import net.tetromi.idocs.client.util.DataDao;
import net.tetromi.idocs.client.util.Guid;
import net.tetromi.idocs.client.widget.SpinBox;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static net.tetromi.idocs.client.util.LOG.log;

public class Idocs implements EntryPoint {
    private static final String REQUEST_TOKEN = "request_token";
    private static final String OAUTH_VERIFIER = "oauth_verifier";
    private static final String ACCESS_TOKEN = "access_token";
    private static final String AUTHORIZE_URL = "https://one.ubuntu.com/oauth/authorize/";
    private static final String OAUTH_TOKEN = "oauth_token";

    static RootPanel content;
    static RootPanel metadata;
    static Image ajax;
    static String accessToken;
    static VerticalPanel titleList;
    static IdocsServiceAsync IdocsService;
    static DataDao dataDao;
    static Metadata localMetadata;
    static Timer t;
    static boolean updating = false;
    static Note currentNote;
    private boolean loggedIn = false;
    static VerticalPanel splitter;

    public void onModuleLoad() {
        metadata = RootPanel.get("metadata");
        metadata.setVisible(false);
        ajax = new Image("ajax.gif");

        content = RootPanel.get("content");
        IdocsService = IdocsServiceAsync.Util.getInstance();

        if (DataDao.DB.isSupported()) {
            log("using Database");
            dataDao = new DataDao.DB();
            log("used database");
        } else if (DataDao.Store.isSupported()) {
            log("using Store");
            dataDao = new DataDao.Store();
        } else {
            content.add(new Label("This application requires HTML5 Storage support in your browser."));
            return;
        }

        content.add(ajax);
        dataDao.init(new Async<Void>() {
            @Override public void onSuccess(Void v) {
                ajax.removeFromParent();
                load2();
            }
            @Override public void onFailure(Throwable error) {
                ajax.removeFromParent();
                content.add(new Label("Error initializing database."));
            }
        });
    }

    public void load2() {
        // Get OAuth token cookies to determine the signed-in state
        final String requestToken = Cookies.getCookie(REQUEST_TOKEN);
        accessToken = Cookies.getCookie(ACCESS_TOKEN);

        // Get query parameters passed from OAuth verification
        final String verifier = Window.Location.getParameter(OAUTH_VERIFIER);
        final String reqToken = Window.Location.getParameter(OAUTH_TOKEN);

        // OAuth verification logic
        if (accessToken == null) {
            dataDao.get(ACCESS_TOKEN, new Async<String>() {
                @Override public void onSuccess(String storageAccessToken) {
                    if(storageAccessToken != null) {
                        accessToken = storageAccessToken;
                        loggedIn();
                    } else if (verifier != null && requestToken != null && reqToken != null) {
                        final JSONObject tokenString = JSONParser.parse(requestToken).isObject();
                        String reqTokenValue = tokenString.get("token").isString().stringValue();
                        if (reqToken.equals(reqTokenValue)) {
                            content.add(ajax);
                            IdocsService.getAccessToken(requestToken, verifier, new Async<String>() {
                                public void onFailure(Throwable caught) {
                                    ajax.removeFromParent();
                                    Window.alert("Failure: " + caught);
                                    loggedOut();
                                }
                                public void onSuccess(String result) {
                                    ajax.removeFromParent();
                                    accessToken = result;
                                    Cookies.setCookie(ACCESS_TOKEN, result);
                                    dataDao.set(ACCESS_TOKEN,result,voidIgnore);
                                    Cookies.removeCookie(REQUEST_TOKEN);
                                    // Authentication complete, begin our first login sync
                                    firstLogin();
                                }
                            });
                        } else {
                            // Something went wrong; we should just let the user attempt to re-sign in
                            loggedOut();
                        }
                    } else {
                        loggedOut();
                    }
                }
            });
        } else {
            loggedIn();
        }
    }

    private void firstLogin() {
        loggedIn();
        syncButton.click();
    }

    private void loggedOut() {
        addEditor();

        loggedIn = false;
        RootPanel.get("login").add(new Label("Sign into Ubuntu and add this connection to your Ubuntu One account " +
                "in order to synchronize your notes with your other machines."));
        final Button signInButton = new Button("Sign in to Ubuntu One");
        signInButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                // Redirect to Ubuntu One login to get oauth access token
                signInButton.setText("Redirecting...");
                signInButton.setEnabled(false);
                oauthRedirect();
            }
        });
        options.add(signInButton);

        final Button clearButton = new Button("Clear data");
        clearButton.addClickHandler(new ClickHandler() {
            @Override public void onClick(ClickEvent event) {
                if(Window.confirm("Warning: This will delete ALL local data.")) {
                    Cookies.removeCookie(ACCESS_TOKEN);
                    Cookies.removeCookie(REQUEST_TOKEN);
                    dataDao.clear(new Async<Void>() {
                        @Override public void onSuccess(Void result) {
                            log("Local Data cleared");
                            Window.Location.reload();
                        }
                    });
                }
            }
        });
        options.add(clearButton);

        refreshNoteList();
        addHandlers();
    }

    static VerticalPanel noteEditorPanel;
    static TextArea noteContent;
    static TextBox noteTitle;
    static RootPanel slot3;
    static Button syncButton;

    private void loggedIn() {
        addEditor();
        loggedIn = true;
        final Button signOutButton = new Button("Sign out");
        signOutButton.addClickHandler(new ClickHandler() {
            @Override public void onClick(ClickEvent event) {
                if(Window.confirm("Warning: This will delete ALL local unsaved, unsynchronized data.")) {
                    Cookies.removeCookie("access_token");
                    dataDao.clear(new Async<Void>() {
                        @Override public void onSuccess(Void result) {
                            log("Local Data cleared");
                            Window.Location.reload();
                        }
                    });
                }
            }
        });
        options.add(signOutButton);

        syncButton = new Button("Sync");
        syncButton.addClickHandler(new ClickHandler() {
            @Override public void onClick(ClickEvent event) {
                syncButton.setEnabled(false);
                syncButton.setText("Syncing...");
                update(new Async<Void>() {
                    @Override public void onFailure(Throwable caught) {
                        syncButton.setText("Sync Failed");
                        syncButton.setEnabled(true);
                    }
                    @Override public void onSuccess(Void result) {
                        syncButton.setText("Sync Complete");
                        syncButton.setEnabled(true);
                    }
                });
            }
        });
        options.add(syncButton);
        refreshNoteList();
        addHandlers();
    }

    private static FlowPanel options;

    private void addEditor() {
        options = new FlowPanel();

        // init UI stuff
        titleList = new VerticalPanel();
        noteEditorPanel = new VerticalPanel();
        noteEditorPanel.add(options);
        noteContent = new TextArea();
        noteEditorPanel.setWidth("100%");
        noteContent.getElement().setId("noteContent");
        noteContent.setWidth("100%");
        noteTitle = new TextBox();
        noteTitle.setWidth("100%");
        noteTitle.getElement().setId("noteTitle");
        noteEditorPanel.add(noteTitle);
        noteEditorPanel.add(noteContent);

        noteContent.getElement().getStyle().setFontSize(12, Style.Unit.PT);


        final Grid grid = new Grid(1,3);

        final SpinBox fontSize = new SpinBox(5,40,1,12);
        fontSize.setWidth("24pt");
        fontSize.addListener(new SpinBox.Listener() {
            @Override public void valueChanged(int newValue) {
                noteContent.getElement().getStyle().setFontSize(newValue, Style.Unit.PT);
            }
        });

        splitter = new VerticalPanel();
        splitter.setWidth("24pt");
        // Add a 'splitter' toggle cell that collapses the left column.
        final Button collapse = new Button("<<");
        collapse.setWidth("24pt");
        collapse.addClickHandler(new ClickHandler() {
            @Override public void onClick(ClickEvent event) {
                if(titleList.isVisible()) {
                    collapse.setText(">>");
                    titleList.setVisible(false);
                    grid.getColumnFormatter().setWidth(0,"0%");
                    grid.getColumnFormatter().setWidth(2,"100%");
                } else {
                    collapse.setText("<<");
                    titleList.setVisible(true);
                    grid.getColumnFormatter().setWidth(0,"20%");
                    grid.getColumnFormatter().setWidth(2,"80%");
                }
            }
        });

        splitter.add(collapse);
        splitter.add(fontSize);

        grid.setWidget(0,0,titleList);
        grid.setWidget(0,1, splitter);
        grid.setWidget(0,2, noteEditorPanel);
        grid.setWidth("100%");
        final HTMLTable.ColumnFormatter col = grid.getColumnFormatter();
        col.setWidth(0,"20%");
        col.setWidth(1,"20px");
        col.setWidth(2,"80%");
        grid.getRowFormatter().setVerticalAlign(0, HasVerticalAlignment.ALIGN_TOP);
        RootPanel.get("slot2").add(grid);

        final Button newNoteButton = new Button("New Note");
        newNoteButton.addClickHandler(new ClickHandler() {
            @Override public void onClick(ClickEvent event) {
                final Note note = new Note();
                note.title = "New Note " + (titleList.getWidgetCount()+1);
                note.guid = Guid.getRandom();
                final Date now = new Date();
                note.created = now;
                note.lastChanged = now;
                note.lastMetadataChanged = now;
                note.content = "";
                updateLocalNote(note);
                final Anchor link = createLink(note);
                titleList.insert(link,0);
                followLink(link,note);
                event.stopPropagation();
            }
        });
        options.add(newNoteButton);
    }

    // Get the combined sync + updated notelist from local storage for display
    private void refreshNoteList() {
        titleList.insert(ajax,0);
        getStored("note-list", new Async<JSONValue>() {
            @Override public void onSuccess(JSONValue result) {
                final List<Note> noteList = getNoteList(result.isArray());
                getUpdatedNotes(new Async<List<Note>>() {
                    @Override public void onSuccess(List<Note> updatedNotes) {
                        merge(noteList,updatedNotes);
                        titleList.clear();
                        for (final Note note : noteList) {
                            final Anchor link = createLink(note);
                            titleList.add(link);
                            setLocalNote(note);
                        }
                    }
                });
            }
        });
    }

    private Anchor createLink(final Note note) {
        final Anchor link = new Anchor(note.title);
        link.addClickHandler(new ClickHandler() {
            @Override public void onClick(ClickEvent event) {
                followLink(link, note);
                event.stopPropagation();
            }
        });
        return link;
    }

    private void followLink(Anchor link, Note note) {
        final int top = link.getParent().getAbsoluteTop();
        Style style = noteEditorPanel.getElement().getStyle();
        style.setPosition(Style.Position.RELATIVE);
        style.setTop(link.getAbsoluteTop() - top, Style.Unit.PX);

        style = splitter.getElement().getStyle();
        style.setPosition(Style.Position.RELATIVE);
        style.setTop(link.getAbsoluteTop() - top, Style.Unit.PX);

        displayNote(note);
        History.newItem(note.guid,false);
        ((FocusWidget)noteEditorPanel.getWidget(2)).setFocus(true);
    }

    private void addHandlers() {
        // Hyperlink handler
        History.addValueChangeHandler(new ValueChangeHandler<String>() {
            public void onValueChange(ValueChangeEvent<String> event) {
                displayNewNote(event.getValue());
            }
        });
        updating = true;
        displayNote(History.getToken());
        updating = false;

        // note handlers
        setTextHandler(Idocs.noteContent);
        setTitleHandler(Idocs.noteTitle);
    }

    private void displayNewNote(String guid) {
        updating = true;
        if(t != null) {
            t.cancel();
            if(currentNote != null) flushText(currentNote, noteContent);
        }
        displayNote(guid);
        updating = false;
    }

    private void setTextHandler(final TextArea noteContent) {
        noteContent.addKeyPressHandler(new KeyPressHandler() {
            @Override public void onKeyPress(KeyPressEvent event) {
                if (!updating) {
                    if (t != null) t.cancel();
                    final Note changeNote = currentNote;
                    t = new Timer() {
                        @Override public void run() {
                            log("keypress flushing");
                            flushText(changeNote, noteContent);
                            adjustTextArea();
                        }
                    };
                    t.schedule(1000);
                }
            }
        });

        noteContent.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override public void onValueChange(ValueChangeEvent<String> stringValueChangeEvent) {
                log("valuechange = " + stringValueChangeEvent.getValue());
                if(!updating) {
                    if (t != null) t.cancel();
                    flushText(currentNote, noteContent);
                    adjustTextArea();
                }
            }
        });
    }

    private void flushText(Note changeNote, TextArea noteContent) {
        if(!noteContent.getValue().equals(changeNote.content)) {
            changeNote.content = noteContent.getValue();
            updateLocalNote(changeNote);
        }
    }

    private void setTitleHandler(final TextBox noteTitle) {
        noteTitle.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override public void onValueChange(ValueChangeEvent<String> stringValueChangeEvent) {
                if(!updating && !History.getToken().isEmpty()) {
                    if(!currentNote.title.equals(noteTitle.getValue())) {
                        currentNote.title = noteTitle.getValue();
                        updateLocalNote(currentNote);
                        for (Widget w : titleList) {
                            if(w instanceof Hyperlink) {
                                final Hyperlink link = (Hyperlink) w;
                                if(link.getTargetHistoryToken().equals(currentNote.guid)) {
                                    link.setText(currentNote.title);
                                }
                            }
                        }
                    }
                }
            }
        });
    }

    private void displayNote(final String guid) {
        log("Display note: " + guid);
        if (guid == null || guid.isEmpty()) {
            displayNote((Note) null);
            return;
        }

        getLocalNote(guid, new Async<Note>() {
            @Override public void onSuccess(Note note) {
                if (note != null) {
                    displayNote(note);
                } else if(loggedIn) {
                    log("WARN: attempting to fetch individual note from server");
                    // If note content doesn't exist locally, we might be able to fetch it from the server?
                    IdocsService.getNote(accessToken, guid, new Async<Note>() {
                        @Override public void onSuccess(Note result) {
                            if (result != null) {
                                setLocalNote(result);
                            }
                            displayNote(result);
                        }
                    });
                } else {
                    updating = true;
                    displayNote((Note)null);
                    History.newItem(null,false);
                    updating = false;
                }
            }
        });
    }

    /** Display note contents in the right pane */
    private void displayNote(Note note) {
        log("Displaying note");
        currentNote = note;

        if (note == null) {
            noteTitle.setText("");
            noteContent.setText("");
            noteTitle.setReadOnly(true);
            noteContent.setReadOnly(true);
            return;
        }
        noteTitle.setReadOnly(false);
        noteContent.setReadOnly(false);
        log("Note: " + note.title);
        noteTitle.setText(note.title);
        noteContent.setText(note.content);
        adjustTextArea();
    }

    private void getNewContent(final Metadata data, final AsyncCallback<Void> on) {
        titleList.insert(ajax, 0);
        IdocsService.getNotes(accessToken, localMetadata.syncRevision, true, new AsyncCallback<List<Note>>() {
            @Override public void onFailure(Throwable caught) {on.onFailure(caught);}
            @Override public void onSuccess(List<Note> result) {
                addNotes(result, new Async<Void>() {
                    @Override public void onSuccess(Void result) {
                        setLocalMetadata(data);
                        titleList.remove(ajax);
                        putNotes(on);
                    }
                });
            }
        });
    }

    /** Adds/merges the specified notes with the local note list. */
    private void addNotes(final List<Note> newNotes, final Async<Void> done) {
        getStored("note-list", new Async<JSONValue>() {
            @Override public void onSuccess(JSONValue result) {
                final List<Note> oldNotes = getNoteList(result.isArray());
                log("got " + newNotes.size() + " new/updated notes from server.");
                merge(oldNotes, newNotes);
                setLocalNotes(oldNotes);
                refreshNoteList();
                done.onSuccess(null);
            }
        });
    }

    /** Merges one list of notes into another. */
    private void merge(List<Note> oldNotes, List<Note> newNotes) {
        final List<Note> toRemove = new ArrayList<Note>();
        for (Note newNote : newNotes) {
            for (Note oldNote : oldNotes) {
                if (newNote.guid.equals(oldNote.guid)) {
                    toRemove.add(oldNote);
                }
            }
            setLocalNote(newNote);
        }
        oldNotes.removeAll(toRemove);
        oldNotes.addAll(newNotes);
    }

    /** Automatically size the textarea widget to match the text content. */
    private native void adjustTextArea()/*-{
        var textarea = $doc.getElementById('noteContent');
//        textarea.style.height = '400px';
        var times = 0;
        while(textarea.scrollHeight <= (textarea.clientHeight) && times < 1000) {
            textarea.rows -= 1; times += 1;
        }
        while(textarea.scrollHeight > (textarea.clientHeight) && times < 1000) {
            textarea.rows += 1; times += 1;
        }
    textarea.rows += 2;
    }-*/;

    /** ignore asynchronous callback. */
    private static final Async<Void> voidIgnore = new Async<Void>() {
        @Override public void onSuccess(Void result) {}
    };

    private void getLocalMetadata(final Async<Metadata> returnResult) {
        dataDao.get("metadata", new Async<String>() {
            @Override public void onSuccess(String result) {
                Metadata data;
                try {
                    data = Metadata.fromJson(new Metadata.Json(result));
                } catch (Exception e) {
                    data = new Metadata();
                    data.syncRevision = -1;
                    setLocalMetadata(data);
                }
                returnResult.onSuccess(data);
            }
        });
    }

    private void setLocalMetadata(Metadata data) {
        dataDao.set("metadata", data.toJsonString(), voidIgnore);
    }

    /** Attempt to sync with server's revision. */
    private void update(final AsyncCallback<Void> on) {
        getLocalMetadata(new Async<Metadata>() {
            @Override public void onSuccess(Metadata result) {
                localMetadata = result;
                IdocsService.getInfo(accessToken, new AsyncCallback<Metadata>() {
                    public void onFailure(Throwable caught) {on.onFailure(caught);}
                    public void onSuccess(Metadata result) {
                        if (result.syncRevision > localMetadata.syncRevision) {
                            // Server has updated data.
                            getNewContent(result, on);
                        } else {
                            // No updated data from server; we can safely send our local changes
                            putNotes(on);
                        }
                    }
                });
            }
        });
    }

    private void getUpdatedNotes(final Async<List<Note>> result) {
        getStored("updated-notes", new Async<JSONValue>() {
            @Override public void onSuccess(JSONValue data) {
                final JSONObject notesObject = data.isObject();
                if (notesObject == null) {
                    result.onSuccess(new ArrayList<Note>());
                    return;
                }
                final List<Note> notes = getNotesFromMap(notesObject);
                result.onSuccess(notes);
            }
        });
    }

    /** Send all modified notes as a PUT to the server. */
    private void putNotes(final AsyncCallback<Void> on) {
        getUpdatedNotes(new Async<List<Note>>() {
            @Override public void onSuccess(final List<Note> notes) {
                if(notes == null || notes.isEmpty()) on.onSuccess(null);
                IdocsService.updateNotes(accessToken, localMetadata.syncRevision, notes, new AsyncCallback<Metadata>() {
                    public void onFailure(Throwable caught) {on.onFailure(caught);}
                    public void onSuccess(Metadata result) {
                        if (result.syncRevision > localMetadata.syncRevision) {
                            setLocalMetadata(result);
                            localMetadata = result;
                            addNotes(notes, voidIgnore);
                            setStored("updated-notes", new JSONObject()); // clear updated notes list on a successful syncRevision update
                        }
                        on.onSuccess(null);
                    }
                });
            }
        });
    }

    private List<Note> getNotesFromMap(JSONObject notesObject) {
        final JSONArray array = new JSONArray();
        int i = 0;
        for (String key : notesObject.keySet()) {
            array.set(i++, notesObject.get(key));
        }
        return getNoteList(array);
    }

    /** Adds the specified Note to the locally stored updated notes list. */
    private void updateLocalNote(final Note updatedNote) {
        if (updatedNote == null) return;
        log("Updating local note: " + updatedNote.title);
        getStored("updated-notes", new Async<JSONValue>() {
            @Override public void onSuccess(JSONValue result) {
                JSONObject hash = result.isObject();
                if (hash == null) {
                    log("First updated note");
                    hash = new JSONObject();
                }
                log("Adding note to update list");
                hash.put(updatedNote.guid, updatedNote.toJsonObject());
                setStored("updated-notes", hash);
                setLocalNote(updatedNote);
            }
        });
    }

    /** Gets a JSON value from the local storage. */
    private void getStored(final String key, final Async<JSONValue> result) {
        dataDao.get(key, new Async<String>() {
            @Override public void onSuccess(String json) {
                if (json == null || json.isEmpty()) {
                    result.onSuccess(JSONNull.getInstance());
                } else {
                    result.onSuccess(JSONParser.parse(json));
                }
            }
        });
    }

    /** Sets a JSON value in the local storage. */
    private void setStored(String key, JSONValue data) {
        dataDao.set(key, data.toString(), voidIgnore);
    }

    private List<Note> getNoteList(JSONArray data) {
        List<Note> notes = new ArrayList<Note>();
        if (data == null) return notes;
        for (JSO json : new JSO.Array(data.toString())) {
            notes.add(Note.fromJson(new Note.Json(json)));
        }
        return notes;
    }

    private void setLocalNotes(List<Note> notes) {
        JsArray<JSO> array = JsArray.createArray().cast();
        for (Note note : notes) {
            array.push(note.toJson().data);
        }
        setStored("note-list", new JSONArray(array));
    }

    private void getLocalNote(String guid, final Async<Note> result) {
        getStored(guid, new Async<JSONValue>() {
            @Override public void onSuccess(JSONValue data) {
                final JSONObject object = data.isObject();
                if (object == null) {
                    result.onSuccess(null);
                } else {
                    result.onSuccess(Note.fromJson(new Note.Json(object.toString())));
                }
            }
        });
    }

    private void setLocalNote(Note note) {
        if (note == null) return;
        setStored(note.guid, note.toJsonObject());
    }

    // Retrieves the initial request token and redirects to the OAuth provider.
    private void oauthRedirect() {
        IdocsService.getToken(new AsyncCallback<String>() {
            public void onFailure(Throwable caught) {
                Window.alert("Failure: " + caught);
                Window.Location.reload();
            }
            public void onSuccess(String result) {
                final JSONObject tokenString = JSONParser.parse(result).isObject();
                String token = tokenString.get("token").isString().stringValue();
                setCookie(REQUEST_TOKEN, result);
                final String url = AUTHORIZE_URL + "?oauth_token=" + token;
                Window.Location.assign(url);
            }
        });
    }

    private static final long DURATION = 1000L * 60 * 60 * 24 * 365 * 5; // 5 years

    /** Sets a very long cookie. */
    private static void setCookie(String name, String value) {
        // save request token on browser for later
        Cookies.setCookie(name, value, new Date(System.currentTimeMillis() + DURATION));
    }
}
