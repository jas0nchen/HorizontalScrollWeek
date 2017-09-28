/*
 * Copyright 2017 jason. https://github.com/jas0nchen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.jas0nchen.horizontalscrollweek.utils;

import android.content.Context;

import org.joda.time.DateTime;

import java.util.Calendar;

import io.github.jas0nchen.horizontalscrollweek.R;

/**
 * Author: jason
 * Time: 2017/9/28
 */
public class Utils {

    public static boolean isSameDay(DateTime src, DateTime dst) {
        return isSameDay(src.getYear(), src.getMonthOfYear(), src.getDayOfMonth(), dst.getYear(), dst.getMonthOfYear(), dst.getDayOfMonth());
    }

    private static boolean isSameDay(int lastYear, int lastMonth, int lastDay, int year, int month, int day) {
        return lastYear == year && lastMonth == month && lastDay == day;
    }

    public static String getChineseWeekOfTheDay(Context context, int week) {
        String result;
        switch (week) {
            case 1:
                result = context.getString(R.string.mon);
                break;
            case 2:
                result = context.getString(R.string.tue);
                break;
            case 3:
                result = context.getString(R.string.wen);
                break;
            case 4:
                result = context.getString(R.string.thu);
                break;
            case 5:
                result = context.getString(R.string.fri);
                break;
            case 6:
                result = context.getString(R.string.sat);
                break;
            case 7:
                result = context.getString(R.string.sun);
                break;
            default:
                result = context.getString(R.string.today);
                break;
        }
        return result;
    }
}
