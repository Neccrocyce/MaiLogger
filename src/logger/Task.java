package logger;

import java.util.Date;

public class Task extends Log {
    private boolean finished = false;
    private int group;
    private String messageSuffix;

    public Task(String message) {
        super(Group.OTHER, message);
        group = Group.OTHER;
    }

    public void setSuceeded () {
        if (finished) return;
        messageSuffix = " ... SUCCESS " + "(" + getAge() + ")";
        group = Group.INFO;
        finished = true;
    }

    public void setFailed () {
        if (finished) return;
        messageSuffix = " ... FAILED " + "(" + getAge() + ")";
        group = Group.ERROR;
        finished = true;
    }

    public void setAbort (String reason) {
        if (finished) return;
        messageSuffix = " ... ABORTED BY " + reason;
        group = Group.ERROR;
        finished = true;
    }

    @Override
    public int getGroup() {
        return group;
    }

    @Override
    public String getLog (boolean time) {
        StringBuilder s = new StringBuilder();
        s.append(Group.getGroupAsString(group)).append(": ").append(super.getMessage()).append(messageSuffix);
        return time ? getDateAsString() + " " + s.toString() : s.toString();
    }

    @Override
    public String toString () {
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
