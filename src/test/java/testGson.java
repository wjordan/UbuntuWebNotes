import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.InstanceCreator;
import com.google.gson.reflect.TypeToken;
import net.tetromi.idocs.client.Note;
import org.scribe.oauth.Token;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @user will
 * @date May 25, 2010 4:58:57 AM
 */
public class testGson {
    static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
    public static final GsonBuilder builder = new GsonBuilder();
    static {
        builder.setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        builder.registerTypeAdapter(Token.class, new InstanceCreator<Token>() {
            public Token createInstance(Type type) {
                return new Token("", "");
            }
        });
    }

    public static final Gson g = builder.create();
    public static void main(String[] args) throws ParseException {
        String requestToken = "{\"token\":\"s7XLbQcFzJCPR6rjv5hR\",\"secret\":\"dmlrs5zzKpv9VcjftZZJV5N6lPgVZcnTMLGFvcjlj9kvXhLZPwjL6X37GpFK142knsRbTTWcF768LFLh\",\"rawString\":\"oauth_token_secret\\u003ddmlrs5zzKpv9VcjftZZJV5N6lPgVZcnTMLGFvcjlj9kvXhLZPwjL6X37GpFK142knsRbTTWcF768LFLh\\u0026oauth_token\\u003ds7XLbQcFzJCPR6rjv5hR\\u0026oauth_callback_confirmed\\u003dtrue\"}";
        Token token = g.fromJson(requestToken, Token.class);
        System.out.println("token = " + token);
        testArray();

        String date = "{\"create-date\":\"2010-04-10T19:42:11.027-07:00\"}";
        String date2 = "2010-04-10T19:42:11.027-07:00";
//        final Note note = g.fromJson(date,Note.class);
        final Date date1 = format.parse(date2);
        System.out.println("date1 = " + date1);
        final String s = format.format(date1)+"-07:00";
        System.out.println("s = " + s);
//        System.out.println("note = " + note);
    }

    public static void testArray() {
        String noteArray = "[{\"guid\":\"ada31537-83ac-412c-98ee-d2771426e826\",\"ref\":{\"href\":\"https://one.ubuntu.com/notes/edit/ada31537-83ac-412c-98ee-d2771426e826\",\"api-ref\":\"https://one.ubuntu.com/notes/api/1.0/op/ada31537-83ac-412c-98ee-d2771426e826\"},\"title\":\"New Note 93\"},{\"guid\":\"57614a88-7af8-456f-93cb-ddfb3d1b8b5c\",\"ref\":{\"href\":\"https://one.ubuntu.com/notes/edit/57614a88-7af8-456f-93cb-ddfb3d1b8b5c\",\"api-ref\":\"https://one.ubuntu.com/notes/api/1.0/op/57614a88-7af8-456f-93cb-ddfb3d1b8b5c\"},\"title\":\"Saab service\"},{\"guid\":\"c66235d2-5d3d-44b9-ab88-442de6b55130\",\"ref\":{\"href\":\"https://one.ubuntu.com/notes/edit/c66235d2-5d3d-44b9-ab88-442de6b55130\",\"api-ref\":\"https://one.ubuntu.com/notes/api/1.0/op/c66235d2-5d3d-44b9-ab88-442de6b55130\"},\"title\":\"Atheism and agnosticism\"},{\"guid\":\"502adf59-511f-453f-81bd-52f238804099\",\"ref\":{\"href\":\"https://one.ubuntu.com/notes/edit/502adf59-511f-453f-81bd-52f238804099\",\"api-ref\":\"https://one.ubuntu.com/notes/api/1.0/op/502adf59-511f-453f-81bd-52f238804099\"},\"title\":\"Badiou, Number and Numbers\"},{\"guid\":\"223ad19e-daf2-45bc-a769-70da0cd56619\",\"ref\":{\"href\":\"https://one.ubuntu.com/notes/edit/223ad19e-daf2-45bc-a769-70da0cd56619\",\"api-ref\":\"https://one.ubuntu.com/notes/api/1.0/op/223ad19e-daf2-45bc-a769-70da0cd56619\"},\"title\":\"Linksys Router Config\"},{\"guid\":\"29a0f8cb-0bdf-4de1-a76f-7cd9ba79a2ba\",\"ref\":{\"href\":\"https://one.ubuntu.com/notes/edit/29a0f8cb-0bdf-4de1-a76f-7cd9ba79a2ba\",\"api-ref\":\"https://one.ubuntu.com/notes/api/1.0/op/29a0f8cb-0bdf-4de1-a76f-7cd9ba79a2ba\"},\"title\":\"ZipZapPlay install\"},{\"guid\":\"6c6e51bd-5c90-43dc-950d-32146698c8be\",\"ref\":{\"href\":\"https://one.ubuntu.com/notes/edit/6c6e51bd-5c90-43dc-950d-32146698c8be\",\"api-ref\":\"https://one.ubuntu.com/notes/api/1.0/op/6c6e51bd-5c90-43dc-950d-32146698c8be\"},\"title\":\"Aji reader feedback\"},{\"guid\":\"15ece241-d2ec-4512-880f-7f43fb7757b9\",\"ref\":{\"href\":\"https://one.ubuntu.com/notes/edit/15ece241-d2ec-4512-880f-7f43fb7757b9\",\"api-ref\":\"https://one.ubuntu.com/notes/api/1.0/op/15ece241-d2ec-4512-880f-7f43fb7757b9\"},\"title\":\"ZZP notes\"},{\"guid\":\"6cb8c62e-d74a-464b-9d7f-589605120296\",\"ref\":{\"href\":\"https://one.ubuntu.com/notes/edit/6cb8c62e-d74a-464b-9d7f-589605120296\",\"api-ref\":\"https://one.ubuntu.com/notes/api/1.0/op/6cb8c62e-d74a-464b-9d7f-589605120296\"},\"title\":\"PDF file-serving webapp\"},{\"guid\":\"aa6d8ec2-0613-4e88-8569-3ddbb06f2f1d\",\"ref\":{\"href\":\"https://one.ubuntu.com/notes/edit/aa6d8ec2-0613-4e88-8569-3ddbb06f2f1d\",\"api-ref\":\"https://one.ubuntu.com/notes/api/1.0/op/aa6d8ec2-0613-4e88-8569-3ddbb06f2f1d\"},\"title\":\"New Note 101\"},{\"guid\":\"5198303d-ad8e-48ba-8369-7cb10512b7c5\",\"ref\":{\"href\":\"https://one.ubuntu.com/notes/edit/5198303d-ad8e-48ba-8369-7cb10512b7c5\",\"api-ref\":\"https://one.ubuntu.com/notes/api/1.0/op/5198303d-ad8e-48ba-8369-7cb10512b7c5\"},\"title\":\"New Note 15 (old)\"},{\"guid\":\"d3c64f62-4e98-440f-b043-58ff86ec709c\",\"ref\":{\"href\":\"https://one.ubuntu.com/notes/edit/d3c64f62-4e98-440f-b043-58ff86ec709c\",\"api-ref\":\"https://one.ubuntu.com/notes/api/1.0/op/d3c64f62-4e98-440f-b043-58ff86ec709c\"},\"title\":\"Physics world format (old)\"},{\"guid\":\"620db96d-096a-4a26-a5ce-dd352d604f7a\",\"ref\":{\"href\":\"https://one.ubuntu.com/notes/edit/620db96d-096a-4a26-a5ce-dd352d604f7a\",\"api-ref\":\"https://one.ubuntu.com/notes/api/1.0/op/620db96d-096a-4a26-a5ce-dd352d604f7a\"},\"title\":\"New Note 58 (old)\"},{\"guid\":\"7ac58c40-fc32-470c-9508-720ba3243e52\",\"ref\":{\"href\":\"https://one.ubuntu.com/notes/edit/7ac58c40-fc32-470c-9508-720ba3243e52\",\"api-ref\":\"https://one.ubuntu.com/notes/api/1.0/op/7ac58c40-fc32-470c-9508-720ba3243e52\"},\"title\":\"Hi GDC (old)\"},{\"guid\":\"16dbc9fc-94d7-4033-97dc-bcd95acac60d\",\"ref\":{\"href\":\"https://one.ubuntu.com/notes/edit/16dbc9fc-94d7-4033-97dc-bcd95acac60d\",\"api-ref\":\"https://one.ubuntu.com/notes/api/1.0/op/16dbc9fc-94d7-4033-97dc-bcd95acac60d\"},\"title\":\"New Note 56 (old)\"},{\"guid\":\"9a36069d-8691-447b-b523-926a062ef0be\",\"ref\":{\"href\":\"https://one.ubuntu.com/notes/edit/9a36069d-8691-447b-b523-926a062ef0be\",\"api-ref\":\"https://one.ubuntu.com/notes/api/1.0/op/9a36069d-8691-447b-b523-926a062ef0be\"},\"title\":\"New Note 39 (old)\"},{\"guid\":\"f4571403-9b4c-4400-a7cf-0c121e011b9e\",\"ref\":{\"href\":\"https://one.ubuntu.com/notes/edit/f4571403-9b4c-4400-a7cf-0c121e011b9e\",\"api-ref\":\"https://one.ubuntu.com/notes/api/1.0/op/f4571403-9b4c-4400-a7cf-0c121e011b9e\"},\"title\":\"New Note 70\"},{\"guid\":\"f14e0f0e-fa87-477d-b2a6-02fd01bcbeac\",\"ref\":{\"href\":\"https://one.ubuntu.com/notes/edit/f14e0f0e-fa87-477d-b2a6-02fd01bcbeac\",\"api-ref\":\"https://one.ubuntu.com/notes/api/1.0/op/f14e0f0e-fa87-477d-b2a6-02fd01bcbeac\"},\"title\":\"New Note 69\"},{\"guid\":\"9cdb1d8a-2987-4fb6-ae55-6c6223d020f1\",\"ref\":{\"href\":\"https://one.ubuntu.com/notes/edit/9cdb1d8a-2987-4fb6-ae55-6c6223d020f1\",\"api-ref\":\"https://one.ubuntu.com/notes/api/1.0/op/9cdb1d8a-2987-4fb6-ae55-6c6223d020f1\"},\"title\":\"stickynotes\"},{\"guid\":\"1319a397-d57e-4161-8ff4-ec8e0646e0a7\",\"ref\":{\"href\":\"https://one.ubuntu.com/notes/edit/1319a397-d57e-4161-8ff4-ec8e0646e0a7\",\"api-ref\":\"https://one.ubuntu.com/notes/api/1.0/op/1319a397-d57e-4161-8ff4-ec8e0646e0a7\"},\"title\":\"New Note 66\"},{\"guid\":\"39662ea4-27b6-4de6-b529-041512932f9f\",\"ref\":{\"href\":\"https://one.ubuntu.com/notes/edit/39662ea4-27b6-4de6-b529-041512932f9f\",\"api-ref\":\"https://one.ubuntu.com/notes/api/1.0/op/39662ea4-27b6-4de6-b529-041512932f9f\"},\"title\":\"Using Links in Tomboy (old)\"},{\"guid\":\"eee218ad-4dd3-49c9-920f-b9efc020197e\",\"ref\":{\"href\":\"https://one.ubuntu.com/notes/edit/eee218ad-4dd3-49c9-920f-b9efc020197e\",\"api-ref\":\"https://one.ubuntu.com/notes/api/1.0/op/eee218ad-4dd3-49c9-920f-b9efc020197e\"},\"title\":\"Sticky Note: 12/14/2009\"},{\"guid\":\"93d8faee-b469-4a08-95f0-377f841a7113\",\"ref\":{\"href\":\"https://one.ubuntu.com/notes/edit/93d8faee-b469-4a08-95f0-377f841a7113\",\"api-ref\":\"https://one.ubuntu.com/notes/api/1.0/op/93d8faee-b469-4a08-95f0-377f841a7113\"},\"title\":\"Sticky Note: 11/25/2009\"},{\"guid\":\"73e00ad1-968e-4c93-917b-b1f094909b36\",\"ref\":{\"href\":\"https://one.ubuntu.com/notes/edit/73e00ad1-968e-4c93-917b-b1f094909b36\",\"api-ref\":\"https://one.ubuntu.com/notes/api/1.0/op/73e00ad1-968e-4c93-917b-b1f094909b36\"},\"title\":\"New Note Template\"},{\"guid\":\"04efb233-3722-4342-8c46-1f5fb9870ea8\",\"ref\":{\"href\":\"https://one.ubuntu.com/notes/edit/04efb233-3722-4342-8c46-1f5fb9870ea8\",\"api-ref\":\"https://one.ubuntu.com/notes/api/1.0/op/04efb233-3722-4342-8c46-1f5fb9870ea8\"},\"title\":\"Foucault, The Hermeneutics of the Subject\"},{\"guid\":\"70d73909-46e9-4b34-8021-7a5b13f57fcb\",\"ref\":{\"href\":\"https://one.ubuntu.com/notes/edit/70d73909-46e9-4b34-8021-7a5b13f57fcb\",\"api-ref\":\"https://one.ubuntu.com/notes/api/1.0/op/70d73909-46e9-4b34-8021-7a5b13f57fcb\"},\"title\":\"Kafka metamorphosis quote\"},{\"guid\":\"40570746-1948-4f9c-81fc-b46b77a80af7\",\"ref\":{\"href\":\"https://one.ubuntu.com/notes/edit/40570746-1948-4f9c-81fc-b46b77a80af7\",\"api-ref\":\"https://one.ubuntu.com/notes/api/1.0/op/40570746-1948-4f9c-81fc-b46b77a80af7\"},\"title\":\"Message to YCombinator on SocialFog\"},{\"guid\":\"11f6e177-40cb-492a-b1a2-ea18106dbea8\",\"ref\":{\"href\":\"https://one.ubuntu.com/notes/edit/11f6e177-40cb-492a-b1a2-ea18106dbea8\",\"api-ref\":\"https://one.ubuntu.com/notes/api/1.0/op/11f6e177-40cb-492a-b1a2-ea18106dbea8\"},\"title\":\"Aimee email\"},{\"guid\":\"4297c16e-dce6-4427-bdd8-8da6da074a58\",\"ref\":{\"href\":\"https://one.ubuntu.com/notes/edit/4297c16e-dce6-4427-bdd8-8da6da074a58\",\"api-ref\":\"https://one.ubuntu.com/notes/api/1.0/op/4297c16e-dce6-4427-bdd8-8da6da074a58\"},\"title\":\"Geert email\"},{\"guid\":\"1f786783-cbda-4f47-b680-1ce84518e2c0\",\"ref\":{\"href\":\"https://one.ubuntu.com/notes/edit/1f786783-cbda-4f47-b680-1ce84518e2c0\",\"api-ref\":\"https://one.ubuntu.com/notes/api/1.0/op/1f786783-cbda-4f47-b680-1ce84518e2c0\"},\"title\":\"Dont ask Don&amp;apos;t Tell response\"},{\"guid\":\"a1c2f5a5-5712-41b8-ad49-195a4933e7b7\",\"ref\":{\"href\":\"https://one.ubuntu.com/notes/edit/a1c2f5a5-5712-41b8-ad49-195a4933e7b7\",\"api-ref\":\"https://one.ubuntu.com/notes/api/1.0/op/a1c2f5a5-5712-41b8-ad49-195a4933e7b7\"},\"title\":\"Rotman, Semiotics of Mathematics\"},{\"guid\":\"81d5c784-b8b2-4b2f-94cb-b360127cf3cf\",\"ref\":{\"href\":\"https://one.ubuntu.com/notes/edit/81d5c784-b8b2-4b2f-94cb-b360127cf3cf\",\"api-ref\":\"https://one.ubuntu.com/notes/api/1.0/op/81d5c784-b8b2-4b2f-94cb-b360127cf3cf\"},\"title\":\"Rotman, Mathematics as Sign (2000)\"},{\"guid\":\"b4468e93-b671-4f85-94aa-b8d475d13d76\",\"ref\":{\"href\":\"https://one.ubuntu.com/notes/edit/b4468e93-b671-4f85-94aa-b8d475d13d76\",\"api-ref\":\"https://one.ubuntu.com/notes/api/1.0/op/b4468e93-b671-4f85-94aa-b8d475d13d76\"},\"title\":\"Foucault, Society Must Be Defended\"},{\"guid\":\"65c3c2d0-d403-4195-b9a8-bcef11224b74\",\"ref\":{\"href\":\"https://one.ubuntu.com/notes/edit/65c3c2d0-d403-4195-b9a8-bcef11224b74\",\"api-ref\":\"https://one.ubuntu.com/notes/api/1.0/op/65c3c2d0-d403-4195-b9a8-bcef11224b74\"},\"title\":\"Google Docs from iPad webapp\"},{\"guid\":\"67ee80cc-b4f5-48aa-9d95-1bb32a3f540c\",\"ref\":{\"href\":\"https://one.ubuntu.com/notes/edit/67ee80cc-b4f5-48aa-9d95-1bb32a3f540c\",\"api-ref\":\"https://one.ubuntu.com/notes/api/1.0/op/67ee80cc-b4f5-48aa-9d95-1bb32a3f540c\"},\"title\":\"Sara notes\"},{\"guid\":\"6bad09f6-0547-4086-a47d-55d5d93fb42a\",\"ref\":{\"href\":\"https://one.ubuntu.com/notes/edit/6bad09f6-0547-4086-a47d-55d5d93fb42a\",\"api-ref\":\"https://one.ubuntu.com/notes/api/1.0/op/6bad09f6-0547-4086-a47d-55d5d93fb42a\"},\"title\":\"Constrained programming (old)\"},{\"guid\":\"3d18e4d7-c04c-4481-b512-2f4dbadb70da\",\"ref\":{\"href\":\"https://one.ubuntu.com/notes/edit/3d18e4d7-c04c-4481-b512-2f4dbadb70da\",\"api-ref\":\"https://one.ubuntu.com/notes/api/1.0/op/3d18e4d7-c04c-4481-b512-2f4dbadb70da\"},\"title\":\"Constrained programming\"},{\"guid\":\"7e7fd1f7-4c80-4f6a-bc97-6806dff1b37a\",\"ref\":{\"href\":\"https://one.ubuntu.com/notes/edit/7e7fd1f7-4c80-4f6a-bc97-6806dff1b37a\",\"api-ref\":\"https://one.ubuntu.com/notes/api/1.0/op/7e7fd1f7-4c80-4f6a-bc97-6806dff1b37a\"},\"title\":\"Reading List\"},{\"guid\":\"237c3a33-7a07-4413-a4b1-a032272a03f0\",\"ref\":{\"href\":\"https://one.ubuntu.com/notes/edit/237c3a33-7a07-4413-a4b1-a032272a03f0\",\"api-ref\":\"https://one.ubuntu.com/notes/api/1.0/op/237c3a33-7a07-4413-a4b1-a032272a03f0\"},\"title\":\"Reading list thoughts\"},{\"guid\":\"d130ed6a-155b-411a-ba2d-11cc7d56a6c5\",\"ref\":{\"href\":\"https://one.ubuntu.com/notes/edit/d130ed6a-155b-411a-ba2d-11cc7d56a6c5\",\"api-ref\":\"https://one.ubuntu.com/notes/api/1.0/op/d130ed6a-155b-411a-ba2d-11cc7d56a6c5\"},\"title\":\"Start Here\"},{\"guid\":\"2944c9e7-1972-4643-9be3-2006856a7f4a\",\"ref\":{\"href\":\"https://one.ubuntu.com/notes/edit/2944c9e7-1972-4643-9be3-2006856a7f4a\",\"api-ref\":\"https://one.ubuntu.com/notes/api/1.0/op/2944c9e7-1972-4643-9be3-2006856a7f4a\"},\"title\":\"Reading List Notebook Template\"},{\"guid\":\"68afc554-d7f0-4ec2-ba9a-153386aec0ac\",\"ref\":{\"href\":\"https://one.ubuntu.com/notes/edit/68afc554-d7f0-4ec2-ba9a-153386aec0ac\",\"api-ref\":\"https://one.ubuntu.com/notes/api/1.0/op/68afc554-d7f0-4ec2-ba9a-153386aec0ac\"},\"title\":\"New Note 68\"},{\"guid\":\"aac3aebe-d2f3-4023-8f55-09834bff23ee\",\"ref\":{\"href\":\"https://one.ubuntu.com/notes/edit/aac3aebe-d2f3-4023-8f55-09834bff23ee\",\"api-ref\":\"https://one.ubuntu.com/notes/api/1.0/op/aac3aebe-d2f3-4023-8f55-09834bff23ee\"},\"title\":\"Notes 5/24/2010\"}]";
        Type noteListType = new TypeToken<List<Note>>() {
        }.getType();
        final List<Note> o = g.fromJson(noteArray, noteListType);
        System.out.println("o = " + o);
    }
}
