package io.github.jas0nchen.horizontalscrollweek.sample;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

import io.github.jas0nchen.horizontalscrollweek.HorizontalScrollWeek;
import io.github.jas0nchen.horizontalscrollweek.Selectable;

/**
 * Author: jason
 * Time: 2017/9/28
 */
public class MainActivity extends AppCompatActivity {

    private static final int FRAGMENT_COUNT = 60;

    HorizontalScrollWeek horizontalScrollWeek;
    ViewPager pager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        horizontalScrollWeek = (HorizontalScrollWeek) findViewById(R.id.hsw_calendar);
        pager = (ViewPager) findViewById(R.id.vp_pager);

        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);

        List<CustomSource> sources = new ArrayList<>();
        DateTime start = DateTime.now().plusDays(-FRAGMENT_COUNT / 2);
        DateTime date;
        CustomSource source;
        int initSelectedIndex = FRAGMENT_COUNT / 2;
        for (int i = 0; i < FRAGMENT_COUNT; i++) {
            date = start.plusDays(i);

            source = new CustomSource();
            source.setDate(date);
            sources.add(source);
        }

        horizontalScrollWeek.withDates(sources)
                .initSelectAt(initSelectedIndex)
                .setup(pager);
    }

    private class PagerAdapter extends FragmentStatePagerAdapter {

        PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return new CustomFragment();
        }

        @Override
        public int getCount() {
            return FRAGMENT_COUNT;
        }
    }

    private class CustomSource implements Selectable {

        private DateTime date;

        @Override
        public int getBackgroundColor() {
            if (date.getDayOfMonth() % 2 == 0) {
                return ContextCompat.getColor(MainActivity.this, R.color.colorAccent);
            }
            return 0;
        }

        @Override
        public void setDate(DateTime date) {
            this.date = date;
        }

        @Override
        public DateTime getDate() {
            return date;
        }
    }
}
