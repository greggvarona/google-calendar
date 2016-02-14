package jp.co.isr.gvarona.calendar;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;

import java.io.IOException;
import java.util.*;

/**
 * Created by gregg varona on 2/14/2016.
 */
public class CalendarServiceHelper {


    /**
     * Fetches all calendar list entries.
     *
     * @param service
     * @return
     * @throws IOException
     */
    public List<CalendarListEntry> getCalendarListEntries(Calendar service) throws IOException {
        List<CalendarListEntry> items = new ArrayList<CalendarListEntry>();
        String pageToken = null;

        do {
            CalendarList calendarList = service.calendarList().list().setPageToken(pageToken).execute();
            items.addAll(calendarList.getItems());
            pageToken = calendarList.getNextPageToken();
        } while (pageToken != null);

        return items;
    }

    /**
     * Fetch all calendar events of specific calendar IDs.
     *
     * The timezone passed to Google's API is the default timezone of the JVM.
     *
     * @param service
     * @param selectedCalendarIds
     * @param start
     * @return
     * @throws IOException
     */
    public List<Event> getCalendarEventsByIds(Calendar service, String[] selectedCalendarIds, Date start)
            throws IOException {
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

    /**
     * Helper function that extracts the calendar ids and passing them to #getCalendarEventsByIds.
     * @param service
     * @param calendarListEntries
     * @param start
     * @return
     * @throws IOException
     */
    public List<Event> getCalendarEvents(Calendar service, List<CalendarListEntry> calendarListEntries, Date start)
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

    /**
     * Updates the <code>selected</code> attribute of a <code>CalendarListEntry</code>.
     *
     * Must have the necessary Calendar scope to do this, see calendar scopes.
     *
     * @param service
     * @param entries
     * @param calendarIds
     * @throws IOException
     */
    public void setSelectedCalendars(Calendar service, List<CalendarListEntry> entries, String[] calendarIds) throws IOException {
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
