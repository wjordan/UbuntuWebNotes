package net.tetromi.idocs.client;

import com.google.gson.annotations.SerializedName;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.rpc.IsSerializable;

/** Metadata returned from the Tomboy API. */
public class Metadata implements IsSerializable {
    /** Gson-serializable fields */
    @SerializedName("user-name")public String username;
    @SerializedName("first-name")public String firstname;
    @SerializedName("last-name")public String lastname;
    @SerializedName("latest-sync-revision")public int syncRevision;

    /** Client-side JSON model using GWT overlay type */
    public static final class Json extends JSO.Model {
        public Json(String data) {super(data);}
        public final String getUsername(){return get("user-name");}
        public final String getFirstname(){return get("first-name");}
        public final String getLastname(){return get("last-name");}
        public final String getLatestSyncRevision(){return get("latest-sync-revision");}
        public Json(Metadata data) {
            super(JSO.create());
            set("user-name",data.username);
            set("first-name",data.firstname);
            set("last-name",data.lastname);
            set("latest-sync-revision",Integer.toString(data.syncRevision));
        }
    }

    public Json toJson() { return new Json(this); }
    public JSONObject toJsonObject() { return new JSONObject(toJson().data); }
    public String toJsonString() { return toJsonObject().toString(); }

    public static Metadata fromJson(Json j) {
        final Metadata data = new Metadata();
        data.firstname = j.getFirstname();
        data.username = j.getUsername();
        data.lastname = j.getLastname();
        data.syncRevision = j.getLatestSyncRevision() == null ?
                -1 :
                Integer.parseInt(j.getLatestSyncRevision());
        return data;
    }
}
