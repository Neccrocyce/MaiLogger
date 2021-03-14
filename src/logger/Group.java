package logger;

public class Group {
    public final static int DEBUG = 0;
    public final static int INFO = 1;
    public final static int WARNING = 2;
    public final static int ERROR = 3;
    public final static int CRITICAL = 4;
    public final static int TASK = 5;
    public final static int OTHER = 6;
    public final static int CUSTOM = 7;

    static String getGroupAsString (int group) {
        switch (group) {
            case DEBUG:
                return "DEBUG";
            case INFO:
                return "INFO";
            case WARNING:
                return "WARNING";
            case ERROR:
                return "ERROR";
            case CRITICAL:
                return "CRITICAL";
            case TASK:
                return "TASK";
            default:
                return "OTHER";
        }
    }

    static int getGroupFromString (String group) {
        switch (group) {
            case "DEBUG":
                return DEBUG;
            case "INFO":
                return INFO;
            case "WARNING":
                return WARNING;
            case "ERROR":
                return ERROR;
            case "CRITICAL":
                return CRITICAL;
            case "TASK":
                return TASK;
            default:
                return OTHER;
        }
    }
}
