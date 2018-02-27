package test;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TestDeleteAfter {
    public static void main (String[] args) {
        Date time = Calendar.getInstance().getTime();
        Date time2 = new Date(System.currentTimeMillis());

        DateFormat formatter = new SimpleDateFormat();
        System.out.println(formatter.format(time));
        System.out.println(formatter.format(time2));
        try {
            System.out.println(new SimpleDateFormat("dd.MM.yy HH:mm:ss").parse("26.02.18 22:41:03").toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        System.out.println(new Date(0).toString());
        System.out.println(new Date(Long.MAX_VALUE).toString());
        String[] temp = "lala".split("\n");



        System.out.println("end");
    }
}
