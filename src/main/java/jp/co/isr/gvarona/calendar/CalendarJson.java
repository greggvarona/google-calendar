package jp.co.isr.gvarona.calendar;

import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.api.services.calendar.model.Event;
import com.google.gson.Gson;

import java.util.List;

/**
 * Created by gregg varona on 2/11/2016.
 */
public class CalendarJson {

    private String defaultDate;
    private List<Event> events;
    private List<CalendarListEntry> calendars;
    private transient Gson gson;

    public CalendarJson(String defaultDate, List<Event> events, List<CalendarListEntry> calendars) {
        this.defaultDate = defaultDate;
        this.events = events;
        this.calendars = calendars;
        gson = new Gson();
    }

    public String getDefaultDate() {
        return defaultDate;
    }

    public void setDefaultDate(String defaultDate) {
        this.defaultDate = defaultDate;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

    public List<CalendarListEntry> getCalendars() {
        return calendars;
    }

    public void setCalendars(List<CalendarListEntry> calendars) {
        this.calendars = calendars;
    }

    public String toJson() {
        return gson.toJson(this);
    }
}
