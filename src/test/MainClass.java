package test;

import logger.MaiLog;

import java.util.ArrayList;
import java.util.List;

public class MainClass implements MaiLog {
    public List<String> log;
    public List<String> errlog;
    public boolean stopped;

    public MainClass () {
        log = new ArrayList<>();
        errlog = new ArrayList<>();
        stopped = false;
    }

    @Override
    public void stop() {
        stopped = true;
    }

    @Override
    public void sendErrMsg(String msg) {
        errlog.add(msg);
    }

    @Override
    public void sendLog(String msg) {
        log.add(msg);
    }

    public String getLog () {
        StringBuilder out = new StringBuilder();
        for (String s: log) {
            out.append(s + "\n");
        }
        return out.toString();
    }

    public String getErrLog () {
        StringBuilder out = new StringBuilder();
        for (String s: errlog) {
            out.append(s + "\n");
        }
        if (out.length() > 0) {
            out.deleteCharAt(out.length() - 1);
        }
        return out.toString();
    }

    public void clearLog () {
        log = new ArrayList<>();
        errlog = new ArrayList<>();
    }


}
