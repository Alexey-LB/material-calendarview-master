package com.prolificinteractive.materialcalendarview.sample;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import java.io.File;
import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

/**
 * Show off setting min and max dates and disabling individual days
 */
public class DisableDaysActivity extends AppCompatActivity implements OnDateSelectedListener, OnMonthChangedListener {

    private static final DateFormat FORMATTER = SimpleDateFormat.getDateInstance();

    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'_'HHmmss'_'Z", Locale.ENGLISH);


    @Bind(R.id.calendarView)
    MaterialCalendarView widget;

    @Bind(R.id.textView)
    TextView textView;

    int year = 2017 - 1900;
    //  year    the year minus 1900,  month  0-11, date 1-31, hrs 0-23, min 0-59, sec 0-59.
    List <Date> listDates = Arrays.asList(
            new Date(year, 3, 3,0,0,0), new Date(year, 3, 6,0,0,0), new Date(year, 3, 9,0,0,0)
            ,new Date(year, 4, 4,0,0,0), new Date(year, 4, 8,0,0,0), new Date(year, 4, 12,0,0,0)
            ,new Date(year, 5, 5,0,0,0), new Date(year, 5, 10,0,0,0), new Date(year, 5, 15,0,0,0)
            ,new Date(year, 6, 6,0,0,0), new Date(year, 6, 12,0,0,0), new Date(year, 6, 18,0,0,0)
    );

    public DisableDaysActivity newInstance(List <Date> listDates_){
        DisableDaysActivity disableDaysActivity = new DisableDaysActivity();
        listDates = listDates_;
        return disableDaysActivity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic);
        ButterKnife.bind(this);

        widget.setOnDateChangedListener(this);
        widget.setOnMonthChangedListener(this);

        //Setup initial text
        textView.setText(getSelectedDatesString());
        // Add a decorator to disable prime numbered days
        widget.addDecorator(new MyPrimeDayDisableDecorator());

        //        // Add a decorator to disable prime numbered days
        //        widget.addDecorator(new PrimeDayDisableDecorator());
        //        // Add a second decorator that explicitly enables days <= 10. This will work because
        //        //  decorators are applied in order, and the system allows re-enabling
        //        widget.addDecorator(new EnableOneToTenDecorator());

        //  year    the year minus 1900,  month  0-11, date 1-31, hrs 0-23, min 0-59, sec 0-59.
        Calendar calendar = Calendar.getInstance();
        widget.setSelectedDate(calendar.getTime());
        calkcCalendar();

        Calendar instance1 = Calendar.getInstance();
        instance1.set(instance1.get(Calendar.YEAR), Calendar.JANUARY, 1);

        Calendar instance2 = Calendar.getInstance();
        instance2.set(instance2.get(Calendar.YEAR) + 2, Calendar.OCTOBER, 31);

        //устанавливает минимальную дату и максимальную дату выбора
//        widget.state().edit()
//                .setMinimumDate(instance1.getTime())
//                .setMaximumDate(instance2.getTime())
//                .commit();

        widget.state().edit()
                .setMinimumDate(getMinMaxCalendar(false))
                .setMaximumDate(getMinMaxCalendar(true))
                .commit();
//установка цвета указателей и выбра дня

        widget.setArrowColor(0xFF00FF80);
        widget.setSelectionColor(0xFF00FF0F);
        // установка текущего дня
        CalendarDay calendarDay = CalendarDay.today();
        //выбор-очистка дня
        widget.clearSelection();//очистка всех выделенных дней (ввиде круга)
        widget.setDateSelected(calendarDay,true);//установка(true) и сброс (false) ВЫБОРА дня(ввиде круга)
//        // указание какие дни нам доступны
//        for(int i = 0; i <MyPrimeDayDisableDecorator.PRIME_TABLE.length;i++){
//            if ((i & 1) == 0) MyPrimeDayDisableDecorator.PRIME_TABLE[i] = true;
//            else MyPrimeDayDisableDecorator.PRIME_TABLE[i] = false;
//        }

//        for (CalendarDay calendarDay : ss.selectedDates) {
//            setDateSelected(calendarDay, true);
 //       }
      //
    }

    private Calendar getCalendar(long millis){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        return calendar;
    }

    private Calendar getCalendar(int year, int month, int date){
        Calendar calendar = Calendar.getInstance();
        calendar.set(year,month,date,0,0,0);
        return calendar;
    }
    //определение минимальной и максимальной даты за которую есть данные
    // если список пуст, показываем текущий месяц просто
    private Calendar getMinMaxCalendar(boolean max){
        long time = System.currentTimeMillis();
        if(listDates != null) {
            for (int i = 0; i < listDates.size(); i++) {
                if (max) {
                    if (listDates.get(i).getTime() > time) time = listDates.get(i).getTime();
                } else {
                    if (listDates.get(i).getTime() < time) time = listDates.get(i).getTime();
                }
            }
        }
        //расширяем список на ВЕСЬ месяц от 1 до ПОСЛЕДНЕГО числа месяца
        Calendar calendar = getCalendar(time);
        if(max) {
            return getCalendar(calendar.get(Calendar.YEAR)
                    ,calendar.get(Calendar.MONTH) + 1, 0);//1 число следующего МЕСЯЦА не ВКЛЮЧАЕМ!!
        }
        return getCalendar(calendar.get(Calendar.YEAR)
                ,calendar.get(Calendar.MONTH), 1);
    }

    private void calkcCalendar(){
        if(listDates == null) return;
        Calendar widgetCalendar = widget.getCurrentDate().getCalendar();
        //установили мин и максимальную дату
        Calendar minCalendar = getCalendar(widgetCalendar.get(Calendar.YEAR), widgetCalendar.get(Calendar.MONTH), 1);
        Calendar maxCalendar = getCalendar(widgetCalendar.get(Calendar.YEAR), widgetCalendar.get(Calendar.MONTH)+1, 1);
        MyPrimeDayDisableDecorator.clearDecorate();
        //здесь сравнение дат и установка дней, в которых есть записи
        for(int i = 0; i < listDates.size();i++){
            Calendar calendar = getCalendar(listDates.get(i).getTime());

            if((maxCalendar.getTime().compareTo(calendar.getTime()) > 0)
                    && (minCalendar.getTime().compareTo(calendar.getTime()) <= 0)){
                //выделяем дату в календаре
                MyPrimeDayDisableDecorator.setrDecorate(calendar.get(Calendar.DATE));
                Log.v("Cal ", "["+calendar.get(Calendar.DATE)+"]  !! true");
            }

            Log.e("Cal MinMax", "min= " + dateFormat.format(minCalendar.getTime())
                    + " -- max= " + dateFormat.format(maxCalendar.getTime())
                    + " -- carent= " + dateFormat.format(calendar.getTime().getTime())
            );

        }
        widget.invalidateDecorators();//обновить Выделенные дни в которых есть записи
    }

    private void calkcCalendar_(){
        if(listDates == null) return;
        Date widgetDate = widget.getCurrentDate().getDate();

//        Calendar minCalendar = Calendar.getInstance();
//        Calendar maxCalendar = Calendar.getInstance();
//        //установили мин и максимальную дату
//        minCalendar.set(widgetDate.getYear(), widgetDate.getMonth(), 1,0,0,0);
//        maxCalendar.set(widgetDate.getYear(), widgetDate.getMonth() + 1, 1,0,0,0);
//        Log.e("Cal MinMax", "min= " + dateFormat.format(minCalendar.getTime())
//                + " -- max= " + dateFormat.format(maxCalendar.getTime()));
        Date minDate = new Date(widgetDate.getYear(), widgetDate.getMonth(), 1,0,0,0);
        Date maxDate = new Date(widgetDate.getYear(), widgetDate.getMonth()+1, 1,0,0,0);

        MyPrimeDayDisableDecorator.clearDecorate();
        //здесь сравнение дат и установка дней, в которых есть записи
        for(int i = 0; i < listDates.size();i++){
            Date d = listDates.get(i);

            if((minDate.compareTo(d) <= 0) && (maxDate.compareTo(d) > 0)){
                //выделяем дату в календаре
                MyPrimeDayDisableDecorator.setrDecorate(d.getDay());
                Log.v("Cal ", "["+d.getDay()+"]  !! true");
            }

            Log.e("Cal MinMax", "min= " + dateFormat.format(minDate.getTime())
                    + " -- max= " + dateFormat.format(maxDate.getTime())
                    + " -- carent= " + dateFormat.format(d.getTime())
            );

        }

    }

    private static class MyPrimeDayDisableDecorator implements DayViewDecorator {

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            //до 127 раз shouldDecorate, (вначале вызывается decorate, 2)
            // (исходный апрель 04- НО здесь от 0/11, тоесть апрель 3 номер)
            // вызывается от февраля (дата с 26по 28 февраля, номер месяца 1) и до
            // июня(вплоть до 10 июня, номер месяца 5)
            Log.i("Cal"," shouldDecorate" + "  m= " +day.getMonth() + "  d="+day.getDay() + "  decor= "+ PRIME_TABLE[day.getDay()]);
            return !PRIME_TABLE[day.getDay()];
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.setDaysDisabled(true);
            //вызывается в начале раза 2, потом до 127 раз shouldDecorate
            Log.v("Cal"," decorate");
        }
        // по умолчанию все false
        private static boolean[] PRIME_TABLE = new boolean[36]; //  1/31 + PADDING

        public  static void clearDecorate(){ for(int i = 0;i < PRIME_TABLE.length;i++)PRIME_TABLE[i] = false;}
        public  static void setrDecorate(int day){if((day >= 0) && (day < PRIME_TABLE.length))PRIME_TABLE[day] = true;}
    }

    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @Nullable CalendarDay date, boolean selected) {
        textView.setText(getSelectedDatesString());
        Log.v("Cal"," onDateSelected");
        getSelectedDatesString();
    }

    @Override
    public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
        //noinspection ConstantConditions
        getSupportActionBar().setTitle(FORMATTER.format(date.getDate()));
        Log.v("Cal"," onMonthChanged");
        calkcCalendar();
    }

    private String getSelectedDatesString() {
        CalendarDay date = widget.getSelectedDate();
        if (date == null) {
            return "No Selection";
        }
        onTextAppearanceChecked((date.getDay() & 1) == 0);

        if(0 == (date.getDay() % 10))onSetWeekMode();
        else onSetMonthMode();

        Log.v("Cal"," SelectedDates= " + FORMATTER.format(date.getDate()));
        calkcCalendar();

        return FORMATTER.format(date.getDate());
    }

    //@OnCheckedChanged(R.id.check_text_appearance)
    void onTextAppearanceChecked(boolean checked) {
        if (checked) {
            widget.setHeaderTextAppearance(R.style.TextAppearance_AppCompat_Large);
            widget.setDateTextAppearance(R.style.TextAppearance_AppCompat_Medium);
            widget.setWeekDayTextAppearance(R.style.TextAppearance_AppCompat_Medium);
        } else {
            widget.setHeaderTextAppearance(R.style.TextAppearance_MaterialCalendarWidget_Header);
            widget.setDateTextAppearance(R.style.TextAppearance_MaterialCalendarWidget_Date);
            widget.setWeekDayTextAppearance(R.style.TextAppearance_MaterialCalendarWidget_WeekDay);
        }
        widget.setShowOtherDates(checked ? MaterialCalendarView.SHOW_ALL : MaterialCalendarView.SHOW_NONE);
    }

    ///@OnClick(R.id.button_selection_mode)
    void onChangeSelectionMode() {
        CharSequence[] items = {
                "No Selection",
                "Single Date",
                "Multiple Dates",
                "Range of Dates"
        };
        new AlertDialog.Builder(this)
                .setTitle("Selection Mode")
                .setSingleChoiceItems(items, widget.getSelectionMode(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        widget.setSelectionMode(which);
                        dialog.dismiss();
                    }
                })
                .show();
    }
    private static final int[] DAYS_OF_WEEK = {
            Calendar.SUNDAY,
            Calendar.MONDAY,
            Calendar.TUESDAY,
            Calendar.WEDNESDAY,
            Calendar.THURSDAY,
            Calendar.FRIDAY,
            Calendar.SATURDAY,
    };

    Random random = new Random();
    //@OnClick(R.id.button_set_first_day)
    void onFirstDayOfWeekClicked() {
        int index = random.nextInt(DAYS_OF_WEEK.length);
        widget.state().edit()
                .setFirstDayOfWeek(DAYS_OF_WEEK[index])
                .commit();

    }

    //@OnClick(R.id.button_weeks)
    public void onSetWeekMode() {
        CalendarMode calendarMode = CalendarMode.WEEKS;

       // calendarMode.visibleWeeksCount = 1;

        widget.state().edit()
                //.setCalendarDisplayMode(CalendarMode.WEEKS)
                .setCalendarDisplayMode(calendarMode)
                .commit();
    }

    //@OnClick(R.id.button_months)
    public void onSetMonthMode() {
        widget.state().edit()
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit();
    }

   // @OnClick(R.id.button_previous)
    void onPreviousClicked() {
        widget.goToPrevious();
    }

   // @OnClick(R.id.button_next)
    void onNextClicked() {
        widget.goToNext();
    }



    private static class PrimeDayDisableDecorator implements DayViewDecorator {

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            return PRIME_TABLE[day.getDay()];
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.setDaysDisabled(true);
        }

        private static boolean[] PRIME_TABLE = {
                false,  // 0?
                false,
                true, // 2
                true, // 3
                false,
                true, // 5
                false,
                true, // 7
                false,
                false,
                false,
                true, // 11
                false,
                true, // 13
                false,
                false,
                false,
                true, // 17
                false,
                true, // 19
                false,
                false,
                false,
                true, // 23
                false,
                false,
                false,
                false,
                false,
                true, // 29
                false,
                true, // 31
                false,
                false,
                false, //PADDING
        };
    }

    private static class EnableOneToTenDecorator implements DayViewDecorator {

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            return day.getDay() <= 10;
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.setDaysDisabled(false);
        }
    }
}
