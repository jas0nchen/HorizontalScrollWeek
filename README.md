# HorizontalScrollWeek
An Android library about simple horizontal scrollable week calendar.

## ScreenShots
![](https://raw.githubusercontent.com/jas0nchen/horizontalscrollweek/master/art/screen_record.gif)

## Get started

In your `build.gradle`:

```groovy
dependencies {
    compile 'io.github.jas0nchen:horizontalscrollweek:0.1.0'
}
```
If you are using `com.android.tools.build:gradle:3.+`, use this instead:

```groovy
dependencies {
    implementation 'io.github.jas0nchen:horizontalscrollweek:0.1.0'
}
```
## Usage
### Step 1. Make sure your model class implements Selectable and override the methods completely, for example:
```java
public class CustomSource implements Selectable {
             
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
```
_Note: you must have to override the method setDate(DateTime) and getDate()!_
### Step 2. Setup your ViewPager and Horizontal Scroll Week, that's all.
```java
private void setupCalendar(HorizontalScrollWeek horizontalScrollWeek, ViewPager pager, List<Selectable> sources, int initSelectedIndex){
    horizontalScrollWeek.withDates(sources)
                        .initSelectAt(initSelectedIndex)
                        .setup(pager);
}
```
Thanks
-------
[JodaTime](https://github.com/JodaOrg/joda-time): Joda-Time is the widely used replacement for the Java date and time classes prior to Java SE 8.

License
-------

    Copyright 2017 jas0nchen.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
