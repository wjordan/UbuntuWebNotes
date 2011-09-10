package net.tetromi.idocs.client.util;

import com.google.gwt.core.client.GWT;

/** Console log function for both GWT-hosted and Chrome/Firebug. */
public class LOG {
    public static final boolean debug = false;

    public static void log(String log) {
        if(debug) {
            GWT.log(log);
            doLog(log);
        }
    }

    public static native void doLog(String log)/*-{
    if(typeof $wnd.console != "undefined" && console.log) {
            console.log(log);
        }
    }-*/;
}
