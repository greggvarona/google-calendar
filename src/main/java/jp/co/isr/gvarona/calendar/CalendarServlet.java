package jp.co.isr.gvarona.calendar;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.servlet.auth.oauth2.AbstractAuthorizationCodeServlet;
import com.google.api.client.http.GenericUrl;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import jp.co.isr.gvarona.auth.AuthHelper;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CalendarServlet extends AbstractAuthorizationCodeServlet {

    private Logger logger = Logger.getLogger(CalendarServlet.class.toString());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        List<Event> events = null;
        Credential creds = getCredential();
        Calendar service = new Calendar.Builder(AuthHelper.HTTP_TRANSPORT, AuthHelper.JSON_FACTORY, creds)
                .setApplicationName(AuthHelper.APPLICATION_NAME).build();
        String[] selectedCalendars = request.getParameterValues("selectedCalendars");
        if (selectedCalendars != null && selectedCalendars.length > 0) {
            logger.log(Level.INFO, "Processing selected calendars.");
            events = this.getCalendarEventsBySelectedIds(service, selectedCalendars);
        } else {
            logger.log(Level.INFO, "Displaying all events in all calendars.");
            List<CalendarListEntry> entries = this.getCalendarListEntries(service);
            events = this.getCalendarEvents(service, entries);
        }
        logger.log(Level.INFO, (events == null) ? "no events here :(" : "events.size " + events.size());
        request.setAttribute("events", events);
        request.getRequestDispatcher("calendar.jsp").forward(request, response);
    }

    @Override
    protected String getRedirectUri(HttpServletRequest req) throws ServletException, IOException {
        GenericUrl url = new GenericUrl(req.getRequestURL().toString());
        url.setRawPath("/gvarona-exam/auth-callback");
        return url.build();
    }

    @Override
    protected AuthorizationCodeFlow initializeFlow() throws IOException {
        return AuthHelper.initializeFlow();
    }

    @Override
    protected String getUserId(HttpServletRequest req) throws ServletException, IOException {
        // return user ID
        return "";
    }

    private List<CalendarListEntry> getCalendarListEntries(Calendar service) throws IOException {
        List<CalendarListEntry> items = new ArrayList<CalendarListEntry>();
        // Iterate through entries in calendar list
        String pageToken = null;
        do {
            CalendarList calendarList = service.calendarList().list().setPageToken(pageToken).execute();
            items.addAll(calendarList.getItems());
            pageToken = calendarList.getNextPageToken();
        } while (pageToken != null);
        return items;
    }

    private List<Event> getCalendarEventsBySelectedIds(Calendar service, String[] selectedCalendarIds)
            throws IOException {
        // Iterate over the events in the specified calendar
        String pageToken = null;
        List<Event> eventsList = new ArrayList<Event>();
        for (String calendarId : selectedCalendarIds) {
            do {
                Events events = service.events().list(calendarId).setPageToken(pageToken).execute();
                eventsList.addAll(events.getItems());

                pageToken = events.getNextPageToken();
            } while (pageToken != null);
        }
        return eventsList;
    }

    private List<Event> getCalendarEvents(Calendar service, List<CalendarListEntry> calendarListEntries) throws IOException {
        // Iterate over the events in the specified calendar
        String pageToken = null;
        List<Event> eventsList = new ArrayList<Event>();
        for (CalendarListEntry calendarListEntry : calendarListEntries) {
            do {
                Events events = service.events().list(calendarListEntry.getId()).setPageToken(pageToken).execute();
                eventsList.addAll(events.getItems());

                pageToken = events.getNextPageToken();
            } while (pageToken != null);
        }
        return eventsList;
    }
}
