package logger;

class Group {
    final static int INFO = 0;
    final static int WARNING = 1;
    final static int ERROR = 2;
    final static int CRITICAL = 3;
    final static int OTHER = 4;

    static String getGroupAsString (int group) {
        switch (group) {
            case INFO:
                return "INFO";
            case WARNING:
                return "WARNING";
            case ERROR:
                return "ERROR";
            case CRITICAL:
                return "CRITICAL";
            default:
                return "OTHER";
        }
    }
}
