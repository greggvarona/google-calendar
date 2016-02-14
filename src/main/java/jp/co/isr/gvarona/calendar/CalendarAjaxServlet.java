package jp.co.isr.gvarona.calendar;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.api.services.calendar.model.Event;
import jp.co.isr.gvarona.auth.AuthHelper;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by gregg varona on 2/11/2016.
 * Renders calendar data as JSON.
 */
public class CalendarAjaxServlet extends CalendarServlet {

    private Logger logger = Logger.getLogger(CalendarAjaxServlet.class.toString());

    private CalendarServiceHelper calendarServiceHelper = new CalendarServiceHelper();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        List<Event> events = null;
        Credential creds = getCredential();
        Calendar service = new Calendar.Builder(AuthHelper.HTTP_TRANSPORT, AuthHelper.JSON_FACTORY, creds)
                .setApplicationName(AuthHelper.APPLICATION_NAME).build();
        List<CalendarListEntry> entries = calendarServiceHelper.getCalendarListEntries(service);
        Date today = new Date();

        logger.log(Level.INFO, "Processing all events in all calendars.");
        events = calendarServiceHelper.getCalendarEvents(service, entries, today);

        renderJson(response, today, events, entries);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        List<Event> events = null;
        Credential creds = getCredential();
        Calendar service = new Calendar.Builder(AuthHelper.HTTP_TRANSPORT, AuthHelper.JSON_FACTORY, creds)
                .setApplicationName(AuthHelper.APPLICATION_NAME).build();
        String[] selectedCalendarIds = request.getParameterValues("selectedCalendarIds");
        List<CalendarListEntry> entries = calendarServiceHelper.getCalendarListEntries(service);
        Date today = new Date();
        if (selectedCalendarIds != null && selectedCalendarIds.length > 0) {
            logger.log(Level.INFO, "Processing events for selected calendars.");
            events = calendarServiceHelper.getCalendarEventsByIds(service, selectedCalendarIds, today);
            calendarServiceHelper.setSelectedCalendars(service, entries, selectedCalendarIds);
        }
        renderJson(response, today, events, entries);
    }

    protected void renderJson(HttpServletResponse response, Date today, List<Event> events,
                              List<CalendarListEntry> entries) throws IOException {
        CalendarJson calendarJson = new CalendarJson(DateUtils.format(today, "yyyy-MM-dd"), events, entries);
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        out.write(calendarJson.toJson());
        out.flush();
    }
}
