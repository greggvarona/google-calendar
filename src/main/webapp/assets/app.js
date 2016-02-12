/**
 * Created by gregg varona on 2/11/2016.
 */
(function() {
    var initEvents = function(events) {
        var arr = [];
        if(typeof events !== 'undefined' && events !== null) {
            events.forEach(function(element, index, array) {
                console.log('iterating through events ' + JSON.stringify(element.start));
                var isAllDay = false;
                if (element.start.hasOwnProperty('date')) {
                    isAllDay = true;
                }
                arr.push({
                    id: element.id,
                    title: element.summary,
                    start: isAllDay ? element.start.date.value : moment(element.start.dateTime.value).utcOffset(element.start.dateTime.tzShift),
                    end: isAllDay ? element.end.date.value :  moment(element.end.dateTime.value).utcOffset(element.end.dateTime.tzShift),
                    editable: false,
                    allDay: isAllDay
                });
            });
        }
        return arr;
    };
    var renderCalendarCheckbox = function(container, checkboxId, label, isSelected) {
        container.append(
            "<div class='checkbox'>" +
            "<label><input type='checkbox' name='selectedCalendarIds'" + (isSelected ? "checked" : "" ) + " />" +
            label +
            "</label></div>"
        );
    };
    var renderCalendarChoices = function(calendars) {
        if(typeof calendars !== 'undefined' && calendars != null) {
            calendars.forEach(function(element, index, array) {
                renderCalendarCheckbox($('#calendar-selection'), element.id, element.summary, element.selected);
            });
        }
    };
    var initGmail = function() {
        var url = "https://mail.google.com/mail/?view=cm&fs=1&tf=1&to=TO&cc=CC&su=SUBJECT&body=BODY";
    };
    $('#calendar').fullCalendar({
        buttonIcons: false,
        header: { right: 'basicTwoWeeks,twoWeekAgenda'},
        defaultView: 'basicTwoWeeks',
        views: {
            basicTwoWeeks: {
                type: 'basic',
                duration: {weeks: 2},
                buttonText: 'two weeks'
            },
            twoWeekAgenda: {
                type: 'agenda',
                duration: {weeks: 2},
                buttonText: 'agenda'
            }
        },
        events: function(start, end, timezone, callback) {
            $.ajax({
                url: ctx + '/calendar-ajax',
                dataType: 'json',
                success: function(doc) {
                    var events = [];
                    if(typeof doc !== 'undefined' && doc !== null) {
                        events = initEvents(doc.events);
                        renderCalendarChoices(doc.calendars);
                    }
                    callback(events);
                },
                error: function() {
                    alert("An error was encountered while fetching your calendar data.");
                }
            });
        }
    });

}());