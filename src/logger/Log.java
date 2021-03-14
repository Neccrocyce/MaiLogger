package logger;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Log {

    /**
     * date in milliseconds, representing the time at which the log was created
     */
    private Date date;

    /**
     * The group the log is assigned to:
     * 0: Info, 1: Warning, 2: Error, 3: Critical
     */
    private final int group;

    /**
     * the description of the event that is logged
     */
    private final String message;

    Log (String date, String group, String message){
        try {
            this.date = new SimpleDateFormat("dd.MM.yy HH:mm:ss").parse(date);
        } catch (ParseException e) {
            this.date = new Date(0);
        }
        this.group = Group.getGroupFromString(group);
        this.message = message;
    }


    Log (int group, String message) {
        this.date = new Date();
        this.group = group;
        this.message = message;
    }

    int getGroup() {
        return group;
    }

    protected String getMessage() {
        return message;
    }

    protected long getTime () {
        return date.getTime();
    }

    protected String getDateAsString () {
        DateFormat df = new SimpleDateFormat("dd.MM.yy HH:mm:ss");
        return date.getTime() == 0 ? "--.--.-- --:--:--" : df.format(date);
    }

    /**
     *
     * @return the event
     */
    String getLog () {
        StringBuilder s = new StringBuilder();
        s.append(Group.getGroupAsString(group)).append(": ").append(message);
        return MaiLogger.isTime() ? getDateAsString() + " " + s.toString() : s.toString();
    }

    /**
     *
     * @return log with time (e.g. 01.01.01 01:01:01 INFO: msg)
     */
    public String toString() {
        return getLog();
    }
}
