package net.tetromi;

import org.restlet.Application;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.data.CookieSetting;
import org.restlet.data.Form;
import org.restlet.routing.Filter;
import org.restlet.routing.Router;

/**
 * Creates a root Restlet that will receive all incoming calls.
 *
 * @user will
 * @date May 20, 2010 10:05:44 PM
 */
public class RestApplication extends Application {
    @Override
    public Restlet createInboundRoot() {
        // Create a router Restlet that routes each call to a
        // new instance of HelloWorldResource.
        final Router router = new Router(getContext());
        final Filter filter = new Filter() {
            @Override
            protected int beforeHandle(Request request, Response response) {
                // If these values are passed as query parameters,
                // add a corresponding cookie.
                setCookieFromParam(request, response, "oauth_verifier");
                setCookieFromParam(request, response, "oauth_token");
                return Filter.CONTINUE;
            }
        };
        filter.setNext(RestResource.class);
        router.attachDefault(filter);
        router.attach("/notes",filter);
        router.attach("/note", NoteResource.class);
        return router;
    }

    private void setCookieFromParam(Request request, Response response, String cookieParam) {
        final Form form = request.getOriginalRef().getQueryAsForm();
        String token = form.getFirstValue(cookieParam);
        if(token != null && !token.isEmpty()) {
            final CookieSetting cookie = new CookieSetting(cookieParam, token);
            request.getCookies().add(cookie);
            response.getCookieSettings().add(cookie);
        }
    }
}
