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

    protected void doRequest(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        List<Event> events = null;
        Credential creds = getCredential();
        Calendar service = new Calendar.Builder(AuthHelper.HTTP_TRANSPORT, AuthHelper.JSON_FACTORY, creds)
                .setApplicationName(AuthHelper.APPLICATION_NAME).build();
        String[] selectedCalendarIds = request.getParameterValues("selectedCalendarIds");
        List<CalendarListEntry> entries = this.getCalendarListEntries(service);
        Date today = new Date();
        if (selectedCalendarIds != null && selectedCalendarIds.length > 0) {
            logger.log(Level.INFO, "Processing events for selected calendars.");
            events = this.getCalendarEventsByIds(service, selectedCalendarIds, today);
            this.setSelectedCalendars(entries, selectedCalendarIds);
        } else {
            logger.log(Level.INFO, "Processing all events in all calendars.");
            events = this.getCalendarEvents(service, entries, today);
        }
        CalendarJson calendarJson = new CalendarJson(DateUtils.format(today, "yyyy-MM-dd"),
                events, selectedCalendarIds, entries);

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        out.write(calendarJson.toJson());
        out.flush();
    }
}
