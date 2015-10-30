package service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.FileObserver;
import android.os.IBinder;
import android.util.Log;

import java.util.Calendar;

import min.com.capsh.ShareActivity;
import util.PreferencesStorage;
import util.SettingInfo;

/**
 * Created by 1000937 on 2015. 10. 18..
 */
public class CaptureService extends Service
{
    public static final String SCREEN_CATURE_LOCATION = Environment.getExternalStorageDirectory() + "/" + Environment.DIRECTORY_PICTURES + "/" + "Screenshots";
    private FileObserver mScreenShotObserver = null;
    public static String msShareFilePath = null;

    @Override
    public boolean onUnbind(Intent intent)
    {
        return super.onUnbind(intent);
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    @Override
    public void onDestroy()
    {
        if (mScreenShotObserver != null)
        {
            mScreenShotObserver.stopWatching();
            mScreenShotObserver = null;
        }
        PreferencesStorage prefSettingStorage = new PreferencesStorage(getApplicationContext(), SettingInfo.SETTING_STORAGENAME);

        if (prefSettingStorage.read(SettingInfo.ONOFF_KEYNAME, false) == true)
        {
            AlarmManager alarm = (AlarmManager) getSystemService(ALARM_SERVICE);
            Intent Intent = new Intent(getBaseContext(), CaptureReceiver.class);

            PendingIntent pintent = PendingIntent.getBroadcast(this, 0, Intent, 0);
            // 알람이 실행될 시간을 셋팅
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(System.currentTimeMillis()); // 현재 시간 셋팅
            cal.add(Calendar.SECOND, 1);

            // 알람매니저가 실행될 시간과 알람매니저에 의해서 실행될 인텐트를 셋팅
            alarm.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pintent);
        }

        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        screenShotObserver();
        return super.onStartCommand(intent, flags, startId);
    }

    private void screenShotObserver()
    {
        mScreenShotObserver = new FileObserver(SCREEN_CATURE_LOCATION)
        {
            @Override
            protected void finalize()
            {
                // TODO Auto-generated method stub
                if( mScreenShotObserver != null)
                {
                    mScreenShotObserver.stopWatching();
                    mScreenShotObserver = null;
                }

                CaptureService.this.stopSelf();
                Log.e("onStartCommand", "finalize");
                super.finalize();
            }

            @Override
            public void onEvent(int event, String path)
            {
                if (event == FileObserver.CLOSE_WRITE)
                {
                    final String capturePath = SCREEN_CATURE_LOCATION + "/" + path;
                    msShareFilePath = capturePath;
                    Intent intent = new Intent(getBaseContext(), ShareActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getBaseContext().startActivity(intent);
                }
            }
        };
        mScreenShotObserver.startWatching();
    }
}
