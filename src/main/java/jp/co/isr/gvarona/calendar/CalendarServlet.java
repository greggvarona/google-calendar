package jp.co.isr.gvarona.calendar;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.extensions.servlet.auth.oauth2.AbstractAuthorizationCodeServlet;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.util.DateTime;
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
import java.util.*;
import java.util.logging.Logger;

/**
 * Redirects to the calendar view when authorization and consent is granted.
 */
public class CalendarServlet extends AbstractAuthorizationCodeServlet {

    private Logger logger = Logger.getLogger(CalendarServlet.class.toString());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        doRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        doRequest(request, response);
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

    protected void doRequest(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        request.getRequestDispatcher("calendar.jsp").forward(request, response);
    }

    protected List<CalendarListEntry> getCalendarListEntries(Calendar service) throws IOException {
        List<CalendarListEntry> items = new ArrayList<CalendarListEntry>();
        String pageToken = null;

        do {
            CalendarList calendarList = service.calendarList().list().setPageToken(pageToken).execute();
            items.addAll(calendarList.getItems());
            pageToken = calendarList.getNextPageToken();
        } while (pageToken != null);

        return items;
    }

    protected List<Event> getCalendarEventsByIds(Calendar service, String[] selectedCalendarIds, Date start)
            throws IOException {
        // Iterate over the events in the specified calendar
        String pageToken = null;
        List<Event> eventsList = new ArrayList<Event>();
        Date today = start;
        Date twoWeeksAfter = DateUtils.add(today, 14);
        DateTime minDateTime = new DateTime(today, TimeZone.getDefault());
        DateTime maxDateTime = new DateTime(twoWeeksAfter, TimeZone.getDefault());

        for (String calendarId : selectedCalendarIds) {
            do {
                Events events = service.events().list(calendarId)
                        .setTimeMin(minDateTime)
                        .setTimeMax(maxDateTime)
                        .setPageToken(pageToken).execute();
                eventsList.addAll(events.getItems());
                pageToken = events.getNextPageToken();
            } while (pageToken != null);
        }
        return eventsList;
    }

    protected List<Event> getCalendarEvents(Calendar service, List<CalendarListEntry> calendarListEntries, Date start)
            throws IOException {
        // Iterate over the events in the specified calendar
        List<String> ids = new ArrayList<String>();

        for (CalendarListEntry calendarListEntry : calendarListEntries) {
            if(calendarListEntry.isSelected()) {
                ids.add(calendarListEntry.getId());
            }
        }

        return getCalendarEventsByIds(service, ids.toArray(new String[0]), start);
    }

    protected void setSelectedCalendars(Calendar service, List<CalendarListEntry> entries, String[] calendarIds) throws IOException {
        List<String> calIds = Arrays.asList(calendarIds);

        for(CalendarListEntry entry : entries) {
            entry.setSelected(false);
            if(calIds.contains(entry.getId())) {
                entry.setSelected(true);
            }
            service.calendarList().update(entry.getId(), entry).execute();
        }

    }
}
