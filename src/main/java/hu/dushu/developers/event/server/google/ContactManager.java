package hu.dushu.developers.event.server.google;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.gdata.client.contacts.ContactsService;
import com.google.gdata.data.DateTime;
import com.google.gdata.data.contacts.ContactEntry;
import com.google.gdata.data.contacts.Event;
import com.google.gdata.data.extensions.*;
import com.google.gdata.util.ServiceException;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import hu.dushu.developers.event.server.EventParticipant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.Date;

public class ContactManager extends ServiceManager<ContactsService> {

    private static final Logger logger = LoggerFactory
            .getLogger(ContactManager.class);

    private static final String CONTACTS_URL = "https://www.google.com/m8/feeds/contacts/default/full";

    @Inject
    public ContactManager(@Named("refreshToken") String refreshToken,
                          @Named("clientId") String clientId,
                          @Named("clientSecret") String clientSecret,
                          HttpTransport transport, JsonFactory jsonFactory) {

        super(refreshToken, clientId, clientSecret, transport, jsonFactory);

        ContactsService service = new ContactsService("GDG Event Management");
        service.setProtocolVersion(ContactsService.Versions.V3_1);
        setService(service);

        return;
    }

    public void importContact(EventParticipant p, String eventName, Date eventDate)
            throws IOException, ServiceException {

        URL url = new URL(CONTACTS_URL);

        String fullName = p.getFullName();
        String emailAddress = p.getEmailAddress();
        String phoneNumber = p.getPhoneNumber();
        logger.trace("row: " + p);

        ContactEntry contactEntry = createContact(url, fullName,
                emailAddress, phoneNumber);

            /*-
             * https://developers.google.com/google-apps/contacts/v3/reference#gcEvent
             */

        Event onSite = new Event(eventName, null);

        When when = new When();

        when.setStartTime(new DateTime(eventDate));

        onSite.setWhen(when);
        contactEntry.addEvent(onSite);

        try {
            getService().update(
                    new URL(contactEntry.getEditLink().getHref()),
                    contactEntry);
        } catch (Exception ex) {
            logger.error("failed to update: " + p, ex);
        }

        return;
    }

    private ContactEntry createContact(URL url, String fullName, String emailAddress, String phoneNumber)
            throws IOException, ServiceException {

        /*
         * create contact
         */
        ContactEntry entry = new ContactEntry();

        Name name = new Name();
        name.setFullName(new FullName(fullName, null));
        entry.setName(name);

        if (emailAddress != null) {
            addEmail(fullName, emailAddress, entry);
        }

        if (phoneNumber != null) {
            addPhone(phoneNumber, entry);
        }

        ContactEntry newEntry = getService().insert(url, entry);

        return newEntry;
    }

    private void addPhone(String phoneNumber, ContactEntry entry) {

        PhoneNumber number = new PhoneNumber();
        number.setPhoneNumber(phoneNumber);
        number.setRel("http://schemas.google.com/g/2005#home");
        // number.setPrimary(true);
        entry.addPhoneNumber(number);

        return;
    }

    private void addEmail(String fullName, String emailAddress,
                          ContactEntry entry) {

        Email email = new Email();
        email.setAddress(emailAddress);
        email.setDisplayName(fullName);

        if (emailAddress.endsWith("@gmail.com") //
                /*
                 * aol
                 */
                || emailAddress.endsWith("@aol.com")
                || emailAddress.endsWith("@love.com")
                || emailAddress.endsWith("@ygm.com")
                || emailAddress.endsWith("@games.com")
                || emailAddress.endsWith("@wow.com")
                /*
                 * yahoo
                 */
                || emailAddress.endsWith("@yahoo.com")
                /*
                 * microsoft
                 */
                || emailAddress.endsWith("@outlook.com")
                /*
                 * 163
                 */
                || emailAddress.endsWith("@163.com")
                || emailAddress.endsWith("@qq.com")) {
            email.setRel("http://schemas.google.com/g/2005#home");
        } else {
            email.setRel("http://schemas.google.com/g/2005#work");
        }
        // email.setPrimary(true);
        entry.addEmailAddress(email);

        return;
    }
}
