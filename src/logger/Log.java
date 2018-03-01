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
                this.group = Group.INFO;
                break;
            case "WARNING":
                this.group = Group.WARNING;
                break;
            case "ERROR":
                this.group = Group.ERROR;
                break;
            case "CRITICAL":
                this.group = Group.CRITICAL;
                break;
            default:
                this.group = Group.OTHER;
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
        s.append(Group.getGroupAsString(group)).append(": ").append(message);
        return time ? getDateAsString() + " " + s.toString() : s.toString();
    }

    /**
     *
     * @return log with time (e.g. 01.01.01 01:01:01 INFO: msg)
     */
    public String toString() {
        return getLog(true);
    }

    private long getAgeInMilliSeconds () {
        return new Date().getTime() - getTime();
    }

    public String getAge () {
        long age = getAgeInMilliSeconds();
        StringBuilder s = new StringBuilder();
        if (age > 86400000) {
            return "> 1d";
        }
        else if (age > 3600000) {
            int hours = (int) age % 3600000;
            s.append(hours).append("h ");
            age = age - hours * 3600000;
        }
        if (age > 60000) {
            int min = (int) age % 60000;
            s.append(min).append("m ");
            age = age - min * 60000;
        }
        if (age > 1000) {
            int sec = (int) age % 1000;
            s.append(sec).append("s");
            return s.toString();
        }
        if (age > 100) {
            return "0." + (age / 100) + "s";
        }
        else {
            return "" + age + "ms";
        }
    }
}
