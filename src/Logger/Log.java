package Logger;

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
    private int group;

    /**
     * the description of the event that is logged
     */
    private String message;

    public Log (String date, String group, String message){
        try {
            this.date = new SimpleDateFormat("dd.MM.yy HH:mm:ss").parse(date);
        } catch (ParseException e) {
            this.date = new Date(0);
        }
        switch (group) {
            case "INFO":
                this.group = 0;
                break;
            case "WARNING":
                this.group = 1;
                break;
            case "ERROR":
                this.group = 2;
                break;
            case "CRITICAL":
                this.group = 3;
                break;
            default:
                this.group = 4;
                break;
        }
        this.message = message;
    }

    /**
     * creates a new log representing the current time
     * @param group
     * @param message
     */
    public Log (int group, String message) {
        this.date = new Date();
        this.group = group;
        this.message = message;
    }

    public int getGroup() {
        return group;
    }

    public String getMessage() {
        return message;
    }

    public long getTime () {
        return date.getTime();
    }

    public String getGroupAsString () {
        switch (group) {
            case 0:
                return "INFO";
            case 1:
                return "WARNING";
            case 2:
                return "ERROR";
            case 3:
                return "CRITICAL";
            default:
                return "OTHER";
        }
    }

    public String getDateAsString () {
        DateFormat df = new SimpleDateFormat("dd.MM.yy HH:mm:ss");
        return date.getTime() == 0 ? "--.--.-- --:--:--" : df.format(date);
    }

    /**
     *
     * @param time returns the event with time if true (e.g. 01.01.01 01:01:01 INFO: msg) or without time if false (e.g. INFO: msg)
     * @return the event
     */
    public String getLog (boolean time) {
        StringBuilder s = new StringBuilder();
        s.append(getGroupAsString()).append(": ").append(message);
        return time ? getDateAsString() + " " + s.toString() : s.toString();
    }

    /**
     *
     * @return log with time (e.g. 01.01.01 01:01:01 INFO: msg)
     */
    public String toString() {
        return getLog(true);
    }
}
