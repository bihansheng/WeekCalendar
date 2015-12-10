package com.hansheng.weekcalendar.view;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.hansheng.weekcalendar.R;
import com.hansheng.weekcalendar.base.SimpleBaseAdapter;
import com.hansheng.weekcalendar.entity.CalendarData;
import com.hansheng.weekcalendar.utils.SpecialCalendarutil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author 万坤
 * @version 1.0.0
 * @date 2015-04-29
 * @history 创建
 */
public class MyCalendar extends LinearLayout {
    RelativeLayout mIvPrevious;
    TextView mTvYearMouth;
    RelativeLayout mIvNext;
    ViewFlipper mRvDay;

    private Context context;
    private GridView mGridView = null;
    private OnItemClickLitener mOnItemClickLitener;

    private List<CalendarData> calendarDatas;
    private Map<Integer, List> weeks;
    private int weekPosition;//星期在月份中的位置


    private CalendarData today;
    private CalendarData theDayOfSelected;//被选中的日期
    private CalendarData theDayForShow;//用于展示数据的中间变量

    public MyCalendar(Context context) {
        super(context);
        init(context, null);
    }

    public MyCalendar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    /**
     * 初始化View
     */
    private void init(Context context, AttributeSet attrs) {
        this.context = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_calender, this, true);
        mIvPrevious = (RelativeLayout) findViewById(R.id.iv_previous);
        mTvYearMouth = (TextView) findViewById(R.id.tv_year_mouth);
        mIvNext = (RelativeLayout) findViewById(R.id.iv_next);
        mRvDay = (ViewFlipper) findViewById(R.id.rv_day);
        initDatas();
        initView();
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ViewCalender);
        array.recycle();
    }

    /**
     * 初始化数据
     */
    private void initDatas() {
        calendarDatas = new ArrayList<>();
        getToday();//获取当天的数据
        theDayOfSelected = today;
        theDayForShow = today;
        getWholeMonthDatas(theDayOfSelected);
        weekPosition = SpecialCalendarutil.getTheWeekPosition(weeks, theDayOfSelected);
    }

    /**
     * 设置整个月的数据
     */
    private void getWholeMonthDatas(CalendarData data) {
        calendarDatas = SpecialCalendarutil.getWholeMonthDay(data);//获取某天所在的整个月的数据（包含用于显示的上个月的天数和下个月的天数）
        weeks = SpecialCalendarutil.getWholeWeeks(calendarDatas);//获取当月有几个星期，以及每一星期对应的数据星期数据
        mTvYearMouth.setText(String.format("%s年%s月", String.valueOf(data.year), String.valueOf(data.month)));
    }

    /**
     * 初始化月控件
     */
    private void initView() {

        mIvPrevious.setOnClickListener(new OnClickListener() {//跳到上一个月
            @Override
            public void onClick(View v) {
                showLastView(false);//显示上一个星期/月
            }
        });
        mIvNext.setOnClickListener(new OnClickListener() {//跳到下一个月
            @Override
            public void onClick(View v) {
                showNextView(false);//显示下一个星期/月
            }
        });
        mGridView = addDayView();
        mGridView.setAdapter(new CalendarAdapter(context, weeks.get(weekPosition)));
        mRvDay.addView(mGridView, 0);
    }

    /**
     * 初始化日期
     */
    private GridView addDayView() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                AbsListView.LayoutParams.FILL_PARENT, AbsListView.LayoutParams.WRAP_CONTENT);
        final GridView gridView = new GridView(context);
        gridView.setNumColumns(7);
        gridView.setGravity(Gravity.CENTER_VERTICAL);
        gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        gridView.setVerticalSpacing(1);
        gridView.setHorizontalSpacing(1);
//        gridView.setOnTouchListener(new OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//
//                return false;
//            }
//        });
        gridView.setLayoutParams(params);
        return gridView;
    }


    /**
     * 获取今天的参数
     */
    private void getToday() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");
        String currentDate = sdf.format(date);
        int year = Integer.parseInt(currentDate.split("-")[0]);
        int month = Integer.parseInt(currentDate.split("-")[1]);
        int day = Integer.parseInt(currentDate.split("-")[2]);
        today = new CalendarData(year, month, day);

    }


    private float mLastX = -1;
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        float x = 0;
//        switch (event.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                x = event.getRawX();
//                HLog.e(Constant.TAG, ">>>>>>>>>>>>点击" + x);
//                break;
//        }
//        return super.onTouchEvent(event);
//    }

    /**
     * scrollview的ontouchEvent和其他的ViewGroup的方法还是很大不同， 该方法一般和ontouchEvent 一起用
     * onInterceptTouchEvent()主要功能是控制触摸事件的分发，例如是子视图的点击事件还是滑动事件。
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mLastX = event.getRawX();
                break;
            case MotionEvent.ACTION_MOVE:
                float dx = mLastX - event.getRawX(); //X移动坐标
                if (dx > 80) { // 向左滑
                    showNextView(true);//显示下一个星期/月
                    return true;
                } else if (dx < -80) {
                    showLastView(true);//显示上一个星期/月
                    return true;
                }
                break;
        }
        return super.onInterceptTouchEvent(event);
    }


    /**
     * 显示下一个星期/月的数据
     */
    public void showNextView(boolean isShowNextWeek) {
        GridView mGridView = addDayView();
        mGridView.setAdapter(new CalendarAdapter(context, getNextWeekDatas(isShowNextWeek)));
        mRvDay.addView(mGridView, 1);
        mRvDay.setInAnimation(AnimationUtils.loadAnimation(context, R.anim.push_left_in));
        mRvDay.setOutAnimation(AnimationUtils.loadAnimation(context, R.anim.push_left_out));
        mRvDay.showNext();
        mRvDay.removeViewAt(0);
    }

    /**
     * 显示上一个星期/月的数据
     */
    public void showLastView(boolean isShowLastWeek) {
        GridView mGridView = addDayView();
        mGridView.setAdapter(new CalendarAdapter(context, getLastWeekDatas(isShowLastWeek)));
        mRvDay.addView(mGridView, 1);
        mRvDay.setInAnimation(AnimationUtils.loadAnimation(context, R.anim.push_right_in));
        mRvDay.setOutAnimation(AnimationUtils.loadAnimation(context, R.anim.push_right_out));
        mRvDay.showNext();
        mRvDay.removeViewAt(0);
    }

    /**
     * 获取下个星期的数据
     *
     * @param isShowNextWeek true 获取下各星期的数据，如果false 直接获取下各月第一个星期的数据
     * @return
     */
    public List<CalendarData> getNextWeekDatas(boolean isShowNextWeek) {
        if (weekPosition == weeks.size() - 1 || !isShowNextWeek) {//最后一个星期，加载下一个月的数据，或者直接获取下个月
            theDayForShow = (theDayForShow.isNextMonthDay) ? theDayForShow : SpecialCalendarutil.getTheDayOfNextMonth(theDayForShow);
            getWholeMonthDatas(theDayForShow);
            // 为了让数据连贯，直接跳到第二个星期，这里可能没有数据交叉的情况，不跳到第二个星期 判断这个月的第一天是否是星期天
            weekPosition = (SpecialCalendarutil.getWeekdayOfFirstDayInMonth(theDayForShow) == 0 || !isShowNextWeek) ? 0 : 1;
        } else {
            weekPosition++;
        }

        return (List<CalendarData>) weeks.get(weekPosition);
    }

    public List<CalendarData> getLastWeekDatas(boolean isShowLastWeek) {
        if (weekPosition == 0 || !isShowLastWeek) {//第一个星期，加载上一个月的数据,或者直接获取上个月
            theDayForShow = (theDayForShow.isLastMonthDay) ? theDayForShow : SpecialCalendarutil.getTheDayOfLastMonth(theDayForShow);
            getWholeMonthDatas(theDayForShow);
            if (isShowLastWeek) {
                // 为了让数据连贯，直接跳到倒数第二个星期，这里可能没有数据交叉的情况，不跳倒数到第二个星期，判断这个月的最后是否是星期六
                weekPosition = weeks.size() - ((SpecialCalendarutil.getWeekdayOfEndDayInMonth(theDayForShow) == 6) ? 1 : 2);
            } else {//直接获取上个月
                weekPosition = 0;
            }
        } else {
            weekPosition--;
        }
        return (List<CalendarData>) weeks.get(weekPosition);
    }


    /**
     * 日期列表适配器
     */
    public class CalendarAdapter extends SimpleBaseAdapter {
        List<CalendarData> datas;

        public CalendarAdapter(Context context, List<CalendarData> datas) {
            super(context, datas);
            this.datas = datas;
        }

        @Override
        public int getItemResource() {
            return R.layout.item_calendar;
        }

        @Override
        public View getItemView(final int position, View convertView, final ViewHolder viewHolder) {
            final CalendarData calendar = (CalendarData) getItem(position);
            final TextView dayView = (TextView) viewHolder.getView(R.id.tv_calendar_day);
            final TextView weekView = (TextView) viewHolder.getView(R.id.tv_calendar_week);
            weekView.setText(SpecialCalendarutil.getWeekString(mContext).get(position));
            dayView.setText(String.valueOf(calendar.day));
            dayView.setSelected(false);
            if (calendar.isSameDay(theDayOfSelected)) {//被选中的日期是白的
                dayView.setTextColor(getResources().getColor(R.color.color_white));
                dayView.setSelected(true);//设置选中背景
            } else if (calendar.isLastMonthDay || calendar.isNextMonthDay) {//上一个月、下一个月的日期是灰色的
                dayView.setTextColor(getResources().getColor(R.color.color_d1));
            } else if (calendar.isSameDay(today)) {//当天的日期是橘黄色的
                dayView.setTextColor(getResources().getColor(R.color.color_orange));
            } else {
                dayView.setTextColor(getResources().getColor(R.color.color_88));
            }
            //如果设置了回调，则设置点击事件
            dayView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    theDayOfSelected = datas.get(position);
                    theDayForShow = datas.get(position);
                    notifyDataSetChanged();
                   Log.e("myCalender", calendar.year + "年" + calendar.month + "月" + calendar.day);
                    if (mOnItemClickLitener != null) {
                        mOnItemClickLitener.onItemClick(dayView, position);
                    }
                }
            });
            return convertView;
        }
    }


    /**
     * ItemClick的回调接口
     */
    public interface OnItemClickLitener {
        void onItemClick(View view, int position);
    }

    /**
     * 设置回调接口
     */
    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener) {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }

    /**
     * 获取被选中日期的日期字符串
     *
     * @return
     */
    public String getTheDayOfSelected() {
        if (theDayOfSelected != null) {
            String sYear = String.valueOf(theDayOfSelected.year);
            String sMonth = String.valueOf(theDayOfSelected.month);
            String sDay = String.valueOf(theDayOfSelected.day);
            return String.format("%s-%s-%s", sYear, (2 > sMonth.length()) ? "0" + sMonth : "" + sMonth, (2 > sDay.length()) ? "0" + sDay : "" + sDay);
        }
        return "";
    }

    /**
     * 判断当前选中日期是否是今天
     *
     * @return
     */
    public boolean isTodayIsSelectedDay() {
        return today.isSameDay(theDayForShow)&&today.isSameDay(theDayOfSelected)  ;
    }



    /**
     * 判断显示的是否是今天，如果不是，跳转到今天
     * @return 是否是今天
     */
    public boolean showToday() {
        if(!isTodayIsSelectedDay()||weekPosition !=SpecialCalendarutil.getTheWeekPosition(weeks, today)){//如果显示的不是当天
            int mode =0;//动画模式（0 没有动画，1 向左动 ， 2 向右动）
            if(theDayForShow.year>today.year ||theDayForShow.month>today.month){//下一个月
                getWholeMonthDatas(today);
                weekPosition = SpecialCalendarutil.getTheWeekPosition(weeks, today);
                mode =2;
            }else if(theDayForShow.year<today.year ||theDayForShow.month<today.month){//上一个月
                getWholeMonthDatas(today);
                weekPosition = SpecialCalendarutil.getTheWeekPosition(weeks, today);
                mode =1;
            }else{//本月
                int position =  SpecialCalendarutil.getTheWeekPosition(weeks, today);
                if(weekPosition<position){//上个星期
                    mode =1;
                }else if(weekPosition>position){//下个星期
                    mode =2;
                }
                weekPosition = position;
            }

            theDayOfSelected = today;
            theDayForShow = today;
            GridView mGridView = addDayView();
            mGridView.setAdapter(new CalendarAdapter(context, weeks.get(weekPosition)));
            mRvDay.addView(mGridView, 1);
            if(mode ==2){
                mRvDay.setInAnimation(AnimationUtils.loadAnimation(context, R.anim.push_right_in));
                mRvDay.setOutAnimation(AnimationUtils.loadAnimation(context, R.anim.push_right_out));
            }else if(mode ==1){
                mRvDay.setInAnimation(AnimationUtils.loadAnimation(context, R.anim.push_left_in));
                mRvDay.setOutAnimation(AnimationUtils.loadAnimation(context, R.anim.push_left_out));
            }else{
                mRvDay.setInAnimation(null);
                mRvDay.setOutAnimation(null);
            }
            mRvDay.showNext();
            mRvDay.removeViewAt(0);
            return false;
        }
        return true;
    }

}
