package com.slam.outcomehealthvideos.utils;

import android.text.format.Time;

import org.junit.Test;


import static org.junit.Assert.*;

public class DateTimeUtilsTest {

    @Test
    public void convertMillieToHMmSs() {
//        Time now = new Time(Time.getCurrentTimezone());
//        now.setToNow();
//        long time = now.toMillis(true);

        long time = 38 * 1000;     // 00:38
        String nowFormatted1 = DateTimeUtils.convertMillieToHMmSs(time);
        assertEquals(nowFormatted1, "00:38");

        time = ((5* 60) + 45) * 1000;     // 05:45
        String nowFormatted2 = DateTimeUtils.convertMillieToHMmSs(time);
        assertEquals(nowFormatted2, "05:45");
    }
}