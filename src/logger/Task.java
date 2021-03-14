package logger;

import java.util.Date;

public class Task extends Log {
    private boolean finished = false;
    private int group;
    private String messageSuffix;

    Task(String message) {
        super(Group.TASK, message);
        group = Group.TASK;
    }

    void setSucceeded() {
        if (finished) return;
        messageSuffix = "SUCCESS " + "(" + getAge() + ")";
        group = Group.INFO;
        finished = true;
    }

    void setFailed () {
        if (finished) return;
        messageSuffix = "FAILED " + "(" + getAge() + ")";
        group = Group.ERROR;
        finished = true;
    }

    void setAbort (String reason) {
        if (finished) return;
        messageSuffix = "ABORTED BY " + reason;
        group = Group.ERROR;
        finished = true;
    }

    @Override
    int getGroup() {
        return group;
    }

    String getLogImmediate () {
        StringBuilder s = new StringBuilder();
        if (!finished) {
            s.append(Group.getGroupAsString(group)).append(": ").append(super.getMessage()).append(" ... ");
        } else {
            if (MaiLogger.equalsActiveTask(this)) {
                s.append(messageSuffix);
            } else {
                s.append(Group.getGroupAsString(group)).append(": ").append(super.getMessage()).append(" ... ").append(messageSuffix);
            }
        }
        return MaiLogger.isTime() ? super.getDateAsString() + " " + s.toString() : s.toString();
    }

    @Override
    String getLog () {
        StringBuilder s = new StringBuilder();
        s.append(Group.getGroupAsString(group)).append(": ").append(super.getMessage()).append(" ... ").append(messageSuffix);
        return MaiLogger.isTime() ? super.getDateAsString() + " " + s.toString() : s.toString();
    }

    boolean isFinished() {
        return finished;
    }

    @Override
    public String toString () {
        return getLog();
    }

    private long getAgeInMilliSeconds () {
        return new Date().getTime() - getTime();
    }

    private String getAge () {
        long age = getAgeInMilliSeconds();
        StringBuilder s = new StringBuilder();
        if (age > 86400000) {
            return "> 1d";
        }
        else if (age > 3600000) {
            int hours = (int) age % 3600000;
            s.append(hours).append("h ");
            age = age - hours * 3600000L;
        }
        if (age > 60000) {
            int min = (int) age % 60000;
            s.append(min).append("m ");
            age = age - min * 60000L;
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
