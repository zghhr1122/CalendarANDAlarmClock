package com.kyle.calendarprovider;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.kyle.calendarprovider.calendar.CalendarEvent;
import com.kyle.calendarprovider.calendar.CalendarProviderManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.btn_main_add)
    Button btnMainAdd;
    @BindView(R.id.btn_main_delete)
    Button btnMainDelete;
    @BindView(R.id.btn_main_update)
    Button btnMainUpdate;
    @BindView(R.id.btn_main_query)
    Button btnMainQuery;
    @BindView(R.id.tv_event)
    TextView tvEvent;
    @BindView(R.id.btn_edit)
    Button btnEdit;
    @BindView(R.id.btn_search)
    Button btnSearch;
    @BindView(R.id.btn_alart)
    Button bta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);


        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_CALENDAR,
                            Manifest.permission.READ_CALENDAR}, 1);
        }
    }

    @OnClick({R.id.btn_main_add, R.id.btn_main_delete, R.id.btn_edit,
            R.id.btn_main_update, R.id.btn_main_query, R.id.btn_search,R.id.btn_alart})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_main_add:
                //不要忘记申请权限
                CalendarEvent calendarEvent = new CalendarEvent(
                        "马上吃饭",
                        "吃好吃的",
                        "南信院二食堂",
                        System.currentTimeMillis(),
                        System.currentTimeMillis() + 60000,
                        0, null
                );

                // 添加事件
                int result = CalendarProviderManager.addCalendarEvent(this, calendarEvent);
                if (result == 0) {

                    // 时间戳转为小时和分钟
                    long time=System.currentTimeMillis();
                    final Calendar mCalendar=Calendar.getInstance();
                    mCalendar.setTimeInMillis(time);
                    // 取得小时：mHour=mCalendar.get(Calendar.HOUR);
                    // 取得分钟：mMinuts=mCalendar.get(Calendar.MINUTE);
                    createAlarm("马上吃饭", (long) mCalendar.get(Calendar.HOUR),(long) mCalendar.get(Calendar.MINUTE),1);
                    Toast.makeText(this, "插入成功", Toast.LENGTH_SHORT).show();
                } else if (result == -1) {
                    Toast.makeText(this, "插入失败", Toast.LENGTH_SHORT).show();
                } else if (result == -2) {
                    Toast.makeText(this, "没有权限", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_main_delete:
                // 删除事件
                long calID2 = CalendarProviderManager.obtainCalendarAccountID(this);
                List<CalendarEvent> events2 = CalendarProviderManager.queryAccountEvent(this, calID2);
                if (null != events2) {
                    if (events2.size() == 0) {
                        Toast.makeText(this, "没有事件可以删除", Toast.LENGTH_SHORT).show();
                    } else {
                        long eventID = events2.get(0).getId();
                        int result2 = CalendarProviderManager.deleteCalendarEvent(this, eventID);
                        if (result2 == -2) {
                            Toast.makeText(this, "没有权限", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "删除成功", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(this, "查询失败", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_main_update:
                // 更新事件
                long calID = CalendarProviderManager.obtainCalendarAccountID(this);
                List<CalendarEvent> events = CalendarProviderManager.queryAccountEvent(this, calID);
                if (null != events) {
                    if (events.size() == 0) {
                        Toast.makeText(this, "没有事件可以更新", Toast.LENGTH_SHORT).show();
                    } else {
                        long eventID = events.get(0).getId();
                        int result3 = CalendarProviderManager.updateCalendarEventTitle(
                                this, eventID, "改吃晚饭的房间第三方监督司法");
                        if (result3 == 1) {
                            Toast.makeText(this, "更新成功", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "更新失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(this, "查询失败", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_main_query:
                // 查询事件
                long calID4 = CalendarProviderManager.obtainCalendarAccountID(this);
                List<CalendarEvent> events4 = CalendarProviderManager.queryAccountEvent(this, calID4);
                StringBuilder stringBuilder4 = new StringBuilder();
                if (null != events4) {
                    for (CalendarEvent event : events4) {
                        stringBuilder4.append(events4.toString()).append("\n");
                    }
                    tvEvent.setText(stringBuilder4.toString());
                    Toast.makeText(this, "查询成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "查询失败", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_edit:
                // 启动系统日历进行编辑事件
                CalendarProviderManager.startCalendarForIntentToInsert(this, System.currentTimeMillis(),
                        System.currentTimeMillis() + 60000, "哈", "哈哈哈哈", "蒂埃纳",
                        false);
                break;
            case R.id.btn_search:
                if (CalendarProviderManager.isEventAlreadyExist(this, 1552986006309L,
                        155298606609L, "马上吃饭")) {
                    Toast.makeText(this, "存在", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "不存在", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_alart:
              createAlarm("我们的天空", System.currentTimeMillis(),System.currentTimeMillis()+10,1);
              //  startTimer("紧急会议",50);
            //    addCalendarEvent(this,"重要信息",1,1000);
                break;
            default:
                break;
        }
    }
    private void createAlarm(String message, Long hour, long minutes, int resId) {
        ArrayList<Integer> testDays = new ArrayList<>();
        testDays.add(Calendar.MONDAY);//周一
        testDays.add(Calendar.TUESDAY);//周二
        testDays.add(Calendar.FRIDAY);//周五
        int min= (int) (minutes+2);

        String packageName = getApplication().getPackageName();
        Uri ringtoneUri = Uri.parse("android.resource://" + packageName + "/" + resId);
        //action为AlarmClock.ACTION_SET_ALARM
        Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM)
                //闹钟的小时
                .putExtra(AlarmClock.EXTRA_HOUR, hour)
                //闹钟的分钟
                .putExtra(AlarmClock.EXTRA_MINUTES, min)
                //响铃时提示的信息
                .putExtra(AlarmClock.EXTRA_MESSAGE, message)
                //用于指定该闹铃触发时是否振动
                .putExtra(AlarmClock.EXTRA_VIBRATE, true)
                //一个 content: URI，用于指定闹铃使用的铃声，也可指定 VALUE_RINGTONE_SILENT 以不使用铃声。
                //如需使用默认铃声，则无需指定此 extra。
                .putExtra(AlarmClock.EXTRA_RINGTONE, ringtoneUri)
                //一个 ArrayList，其中包括应重复触发该闹铃的每个周日。
                // 每一天都必须使用 Calendar 类中的某个整型值（如 MONDAY）进行声明。
                //对于一次性闹铃，无需指定此 extra
                .putExtra(AlarmClock.EXTRA_DAYS, testDays)
                //如果为true，则调用startActivity()不会进入手机的闹钟设置界面
                .putExtra(AlarmClock.EXTRA_SKIP_UI, true);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }
    public void startTimer(String message, int seconds) {
        //action为AlarmClock.ACTION_SET_TIMER
        Intent intent = new Intent(AlarmClock.ACTION_SET_TIMER)
                .putExtra(AlarmClock.EXTRA_MESSAGE, message)
                //倒计总时长，以秒为单位
                .putExtra(AlarmClock.EXTRA_LENGTH, seconds)
                //是否要进入系统的倒计时设置界面
                .putExtra(AlarmClock.EXTRA_SKIP_UI, false);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }
    private static void addCalendarEvent(Context context, String meetingName, long startTime, long endTime) {
        //该方式需跳到系统日历事件界面由用户手动保存
        Calendar beginC = Calendar.getInstance();
        //提前15分钟提醒
        beginC.setTimeInMillis(startTime - 15 * 60 * 1000);
        Calendar endC = Calendar.getInstance();
        endC.setTimeInMillis(endTime);
        String desc = "您的会议：" + meetingName + " 将于15分钟后开始，请及时入会。";
        //action为Intent.ACTION_INSERT
        Intent intent = new Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                //事件的开始时间（从新纪年开始计算的毫秒数）。
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginC.getTimeInMillis())
                //事件的结束时间（从新纪年开始计算的毫秒数）。
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endC.getTimeInMillis())
                //指定此事件是否为全天事件。
                .putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, false)
                //事件地点。
                .putExtra(CalendarContract.Events.EVENT_LOCATION, "")
                //事件标题。
                .putExtra(CalendarContract.Events.TITLE, "会议提醒")
                //事件说明
                .putExtra(CalendarContract.Events.DESCRIPTION, desc);
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        }
    }

}
