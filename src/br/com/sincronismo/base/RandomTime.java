package br.com.sincronismo.base;

import java.util.Random;

public class RandomTime {

    public int year() {
        return 2018;
    }

    public int month() {
        Random random = new Random();
        int month = random.nextInt(12);
        if (month == 0) {
            return month();
        }
        return month;
    }

    public int day() {
        Random random = new Random();
        int day = random.nextInt(30);
        if (day == 0) {
            return day();
        }
        return day;
    }

    public int hour() {
        Random random = new Random();
        int hour = random.nextInt(24);
        if (hour == 0) {
            return hour();
        }
        return hour;
    }

    public int minute() {
        Random random = new Random();
        int minute = random.nextInt(59);
        if (minute == 0) {
            return minute();
        }
        return minute;
    }

    public int second() {
        Random random = new Random();
        int second = random.nextInt(59);
        if (second == 0) {
            return second();
        }
        return second;
    }
}
