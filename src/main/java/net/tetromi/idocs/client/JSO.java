package net.tetromi.idocs.client;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;

import java.util.*;

/** Enhanced Java overlay of a JavaScriptObject. */
public abstract class JSO extends JavaScriptObject {
    /** Wraps a JsArray into an iterable List. */
    public static class Array extends AbstractList<JSO> {
        private final JsArray<JSO> array;
        public Array(String js) {
            array = JSO.arrayFromJson(js);
        }

        public int size() {
            return array.length();
        }

        public JSO get(int index) {
            return array.get(index);
        }
    }

    public abstract static class Model {
        transient public static final DateTimeFormat format = DateTimeFormat.getFormat("yyyy-MM-dd'T'HH:mm:ss");
        protected JSO data;

        public Model(String data) {
            this(JSO.fromJson(data));
        }

        public Model(JSO data) {
            this.data = data;
        }

        public void set(String field, String value) {
            if (data == null) return;
            if (value == null) {
                data.delete(field);
            } else {
                data.set(field, value);
            }
        }

        public String get(String field) {
            if (data == null) return null;
            String val = this.data.get(field);
            if (val != null && "null".equals(val) || "undefined".equals(val)) {
                return null;
            } else {
                return val;
//                return escapeHtml(val);
            }
        }

        public Map<String, String> getFields() {
            Map<String, String> fieldMap = new HashMap<String, String>();

            if (data != null) {
                JsArrayString array = data.keys();

                for (int i = 0; i < array.length(); i++) {
                    fieldMap.put(array.get(i), data.get(array.get(i)));
                }
            }
            return fieldMap;
        }

        private static String escapeHtml(String maybeHtml) {
            final Element div = DOM.createDiv();
            DOM.setInnerText(div, maybeHtml);
            return DOM.getInnerHTML(div);
        }

        public String toString() {
            return new JSONObject(data).toString();
        }

        public static String format(Date date) {
            if (date == null) return null;
            return format.format(date);
        }

        public static Date parse(String date) {
            if (date == null) return null;
            return format.parse(date.substring(0, 19));
        }
    }

    // Overlay types always have protected, zero-arg constructors
    protected JSO() {
    }

    /**
     * Create an empty instance.
     *
     * @return new Object
     */
    public static native JSO create() /*-{
        return new Object();
    }-*/;

    /**
     * Convert a JSON encoded string into a JSO instance.
     * <p/>
     * Expects a JSON string structured like '{"foo":"bar","number":123}'
     *
     * @return a populated JSO object
     */
    public static native JSO fromJson(String jsonString) /*-{
        return eval('(' + jsonString + ')');
    }-*/;

    /**
     * Convert a JSON encoded string into an array of JSO instance.
     * <p/>
     * Expects a JSON string structured like '[{"foo":"bar","number":123}, {...}]'
     *
     * @return a populated JsArray
     */
    public static native JsArray<JSO> arrayFromJson(String jsonString) /*-{
        return eval('(' + jsonString + ')');
    }-*/;

    public final native boolean hasKey(String key) /*-{
        return this[key] != undefined;
    }-*/;

    public final native JsArrayString keys() /*-{
        var a = new Array();
        for (var p in this) { a.push(p); }
        return a;
    }-*/;

    @Deprecated
    public final Set<String> keySet() {
        JsArrayString array = keys();
        Set<String> set = new HashSet<String>();
        for (int i = 0; i < array.length(); i++) {
            set.add(array.get(i));
        }
        return set;
    }

    public final native String get(String key) /*-{
        return "" + this[key];
    }-*/;

    public final native String get(String key, String defaultValue) /*-{
        return this[key] ? ("" + this[key]) : defaultValue;
    }-*/;

    public final native void set(String key, String value) /*-{
        this[key] = value;
    }-*/;

    public final native void delete(String key) /*-{
        delete this[key];
    }-*/;

    public final int getInt(String key) {
        return Integer.parseInt(get(key));
    }

    public final boolean getBoolean(String key) {
        return Boolean.parseBoolean(get(key));
    }

    public final native JSO getObject(String key) /*-{
        return this[key];
    }-*/;

    public final native JsArray<JSO> getArray(String key) /*-{
        return this[key] ? this[key] : new Array();
    }-*/;
}
