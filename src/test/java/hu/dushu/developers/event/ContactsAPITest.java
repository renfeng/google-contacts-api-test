package hu.dushu.developers.event;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.gdata.util.ServiceException;
import com.google.inject.*;
import com.google.inject.name.Names;
import hu.dushu.developers.event.server.EventParticipant;
import hu.dushu.developers.event.server.google.ContactManager;
import org.junit.Test;

import java.io.IOException;
import java.util.Date;

public class ContactsAPITest {

    private final Injector injector = Guice.createInjector(Stage.DEVELOPMENT,
            new Module() {

                @Override
                public void configure(Binder binder) {

                    binder.bind(HttpTransport.class).toInstance(new NetHttpTransport());

                    binder.bind(JsonFactory.class).toInstance(
                            JacksonFactory.getDefaultInstance());

                    binder.bind(String.class)
                            .annotatedWith(Names.named("refreshToken"))
                            .toInstance(
                                    System.getProperty("refreshToken"));
                    binder.bind(String.class)
                            .annotatedWith(Names.named("clientId"))
                            .toInstance(
                                    System.getProperty("clientId"));
                    binder.bind(String.class)
                            .annotatedWith(Names.named("clientSecret"))
                            .toInstance(System.getProperty("clientSecret"));

                    return;
                }
            });

    @Test
    public void test() throws IOException, ServiceException {

        ContactManager contactManager = injector
                .getInstance(ContactManager.class);

        /*
         * use it as group name, and (internal) event name?
         */
        String eventName = "Form Responses 1";
        Date eventDate = new Date();

        EventParticipant participant = new EventParticipant();
        participant.setFullName("Frank R.");
        participant.setEmailAddress("renfeng.cn@gmail.com");
        participant.setPhoneNumber("+8613911103231");

        contactManager.importContact(participant, eventName,
                eventDate);

        return;
    }
}
