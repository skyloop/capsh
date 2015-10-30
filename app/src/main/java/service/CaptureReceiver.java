package service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import util.RunningApp;

/**
 * Created by 1000937 on 2015. 10. 18..
 */
public class CaptureReceiver extends BroadcastReceiver
{

    @Override
    public void onReceive(Context context, Intent intent)
    {
        if(RunningApp.isServiceRunningCheck(context, "min.com.capsh.service.CaptureSerivce") == false)
        {
            Intent serviceIntent = new Intent(context, CaptureService.class);
            context.startService(serviceIntent);
        }
    }


}
