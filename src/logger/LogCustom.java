package logger;


public class LogCustom extends Log {
    private final String group;

    LogCustom (String date, String group, String message){
        super(date, group, message);
        this.group = group;
    }

    LogCustom(String group, String message) {
        super(Group.CUSTOM, message);
        this.group = group;
    }

    @Override
    int getGroup() {
        return Group.CUSTOM;
    }

    @Override
    String getLog () {
        StringBuilder s = new StringBuilder();
        s.append(group).append(": ").append(super.getMessage());
        return MaiLogger.isTime() ? getDateAsString() + " " + s.toString() : s.toString();
    }
}
