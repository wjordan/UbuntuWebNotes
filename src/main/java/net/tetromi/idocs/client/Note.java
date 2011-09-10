package net.tetromi.idocs.client;

import com.google.gson.annotations.SerializedName;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.rpc.IsSerializable;

import java.util.Date;

/** Note object that serializes into JSON values used by the Tomboy API. */
public class Note implements IsSerializable, Comparable<Note> {
    public String guid;
    public String title;
    @SerializedName("note-content") public String content;
    @SerializedName("last-metadata-change-date") public Date lastMetadataChanged;
    @SerializedName("last-change-date") public Date lastChanged;
    @SerializedName("create-date") public Date created;

    public int compareTo(Note o) {
        if (o == null || o.lastChanged == null || lastChanged == null) return 0;
        return lastChanged.compareTo(o.lastChanged);
    }

    /** Client-side JSON model using GWT overlay type */
    public static final class Json extends JSO.Model {
        public Json(String data) {super(data);}
        public Json(JSO data) {super(data);}
        public final String getGuid() {return get("guid");}
        public final String getTitle() {return get("title");}
        public final String getContent() {return get("note-content");}
        public final String getLastMetadataChanged() {return get("last-metadata-change-date");}
        public final String getLastChangeDate() {return get("last-change-date");}
        public final String getCreateDate() {return get("create-date");}
        public Json(Note note) {
            super(JSO.create());
            set("guid", note.guid);
            set("title", note.title);
            set("note-content", note.content);
            set("last-metadata-change-date", format(note.lastMetadataChanged));
            set("last-change-date", format(note.lastChanged));
            set("create-date", format(note.created));
        }
    }

    public Json toJson() { return new Json(this); }
    public JSONObject toJsonObject() { return new JSONObject(toJson().data); }
    public String toJsonString() { return toJsonObject().toString(); }

    public static Note fromJson(Json j) {
        if(j.getGuid() == null) return null;
        final Note note = new Note();
        note.guid = j.getGuid();
        note.title = j.getTitle();
        note.content = j.getContent();
        note.lastChanged = Json.parse(j.getLastChangeDate());
        note.lastMetadataChanged = Json.parse(j.getLastMetadataChanged());
        note.created = Json.parse(j.getCreateDate());
        return note;
    }
}
