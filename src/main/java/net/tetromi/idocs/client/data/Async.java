package net.tetromi.idocs.client.data;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

/** Extends GWT's AsyncCallback with a default failure case. */
public abstract class Async<T> implements AsyncCallback<T> {
    public void onFailure(Throwable caught) {
        GWT.log("FAILURE:", caught);
    }
    public abstract void onSuccess(T result);
}
