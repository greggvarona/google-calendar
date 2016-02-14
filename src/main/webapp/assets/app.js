/**
 * Created by gregg varona on 2/11/2016.
 */
(function() {
    var events = [];
    /* utility functions */
    var initEvents = function(events) {
        var arr = [];
        if(typeof events !== 'undefined' && events !== null) {
            events.forEach(function(element, index, array) {
                var isAllDay = false;
                if (element.start.hasOwnProperty('date')) {
                    isAllDay = true;
                }
                arr.push({
                    id: element.id,
                    title: element.summary,
                    description: element.hasOwnProperty('description') ? element.description : "",
                    start: isAllDay ? moment(element.start.date.value).utcOffset(element.start.date.tzShift) : moment(element.start.dateTime.value).utcOffset(element.start.dateTime.tzShift),
                    end: isAllDay ? moment(element.end.date.value).utcOffset(element.end.date.tzShift) :  moment(element.end.dateTime.value).utcOffset(element.end.dateTime.tzShift),
                    editable: false,
                    allDay: isAllDay,
                });
            });
        }
        return arr;
    };
    var renderCalendarCheckbox = function(container, checkboxId, label, isSelected) {
        container.append(
            "<div class='checkbox'>" +
            "<label><input id='" + checkboxId + "' type='checkbox' value='" + checkboxId + "' name='selectedCalendarIds'" + (isSelected ? "checked" : "" ) + " />" +
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
    var renderRecepientInputGroup = function(container) {
        var htmlString = '<div class="input-group">' +
        '<input type="email" name="recepient" class="form-control" placeholder="Email address">' +
        '<span class="input-group-btn"><button class="btn btn-default remove-recepient" type="button">x</button></span></div>';

        container.find('.input-group').last().after(htmlString);
    };
    var emailBody = function(events) {
        var body = "";

        events.forEach(function(event) {
            body += "\nDate: " + moment(event.start).format('MMMM Do YYYY, h:mm:ss a') + " - " + moment(event.end).format('MMMM Do YYYY, h:mm:ss a') + "\n";
            body += "Summary: " + event.title + "\n";
            body += "Description: " + event.description + "\n";
        });
        return body;
    };
    var openGmail = function(data) {
        var url = "https://mail.google.com/mail/?view=cm&fs=1&tf=1&";
        url += "&to=" + encodeURIComponent(data.to);
        url += "&su=" + encodeURIComponent(data.subject);
        url += "&body=" + encodeURIComponent(emailBody(data.events));

        window.open(url, "gmail_window");
    };
    /* initialize full calendar */
    $('#calendar').fullCalendar({
        header: { right: 'threeWeeksBasic,threeWeekAgenda'},
        defaultView: 'threeWeeksBasic',
        views: {
            threeWeeksBasic: {
                type: 'basic',
                duration: {weeks: 3},
                buttonText: 'normal'
            },
            threeWeekAgenda: {
                type: 'agenda',
                duration: {weeks: 3},
                buttonText: 'agenda'
            }
        },
        eventMouseover: function(event, jsEvent, view) {
            $(this).append('<div id=\"'+event.id+'\" class=\"hover-end\" >'
                + event.title + '<br/>' + event.description +
                '</div>');
        },
        eventMouseout: function(event, jsEvent, view) {
            $('#'+event.id).remove();
        },
        events: function(start, end, timezone, callback) {
            $.ajax({
                url: ctx + '/calendar-ajax',
                success: function(doc) {
                    //clearCelendarChoices();
                    if(typeof doc !== 'undefined' && doc !== null) {
                        events = initEvents(doc.events);
                        if ($("#calendar-selection .checkbox").length == 0) {
                            renderCalendarChoices(doc.calendars);
                        }
                    }
                    callback(events);
                    $('#calendar').fullCalendar('defaultDate', moment());
                },
                error: function() {
                    alert("An error was encountered while fetching your calendar data.");
                }
            });
        }
    });
    /* UI events */
    $('#add-recepient').on('click', function () {
        var container = $("#recepients");
        renderRecepientInputGroup(container);
    });
    $('#recepients').on('click', '.remove-recepient', function () {
        $(this).closest('.input-group').remove();
    });
    $('#send').on('click', function () {
        var data = {};
        var to = "";
        $("input[type='email']").each(function () {
            to += $(this).val() + ";";
        });
        data.to = to;
        data.subject = "Calendar";
        data.events = events;
        openGmail(data);
    });
    $('#calendar-selection-btn').on('click', function(e) {
        e.preventDefault();

        $.ajax({
            url: ctx + '/calendar-ajax',
            data: $("#calendar-selection").serializeArray(),
            method: 'post',
            beforeSend: function() {

            },
            success: function(doc) {
                $('#calendar').fullCalendar('refetchEvents');
            },
            error: function() {
                alert("An error was encountered while fetching your calendar data.");
            }
        });
    });
}());