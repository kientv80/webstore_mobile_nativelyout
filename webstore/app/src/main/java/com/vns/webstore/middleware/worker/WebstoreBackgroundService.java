package com.vns.webstore.middleware.worker;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.vns.webstore.middleware.entity.NotifyInfo;
import com.vns.webstore.middleware.entity.NotifyListener;
import com.vns.webstore.middleware.network.ConnectionManager;
import com.vns.webstore.middleware.network.HttpClientHelper;
import com.vns.webstore.middleware.service.WebstoreService;
import com.vns.webstore.middleware.storage.LocalStorageHelper;
import com.vns.webstore.ui.notification.Notification;
import com.webstore.webstore.R;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by LAP10572-local on 11/6/2016.
 */
public class WebstoreBackgroundService extends Service{
    public static final String UPDATE = "update";
    public static Boolean isRunning = false;
    private Thread runningThread = null;
    Intent intent;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.intent = intent;
        return START_STICKY;
    }
    /*
    @Override
    public void onTaskRemoved(Intent rootIntent) {
        // TODO Auto-generated method stub
        Intent restartService = new Intent(getApplicationContext(),this.getClass());
        restartService.setPackage(getPackageName());
        PendingIntent restartServicePI = PendingIntent.getService(getApplicationContext(), 1, restartService,PendingIntent.FLAG_ONE_SHOT);
        //Restart the service once it has been killed android
        AlarmManager alarmService = (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmService.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() +100, restartServicePI);

    }
    */
    @Override
    public void onCreate() {
        super.onCreate();
        isRunning = true;
        runningThread =  new Thread(){
            @Override
            public void run() {
                while(isRunning){
                    System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>> WebstoreBackgroundService running");
                    if(getApplicationContext() != null && ConnectionManager.isNetworkAvailable()) {
                        checkNewNotification();
                        //for offline articles
                        getNewArticles();
                    }
                    try {
                        Thread.sleep(5*60*1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        runningThread.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isRunning = false;
    }
    private void getNewArticles() {
        if(ConnectionManager.getConnectionType(getApplicationContext()) == ConnectionManager.ConnectionType.WIFI){
            String articles = LocalStorageHelper.getFromFile(UPDATE);
            if(articles != null && !articles.isEmpty()){
                JSONObject json = null;
                String cachedTime = (System.currentTimeMillis())+"";
                try {
                    try {
                        json = new JSONObject(articles.toString());
                        if(json.has("cachedTime"))
                            cachedTime = json.getLong("cachedTime")+"";
                    }catch (Exception ex){
                        ex.printStackTrace();
                    }
                    HttpClientHelper.executeHttpGetRequest("http://360hay.com/mobile/article/update?time="+cachedTime, null, UPDATE);
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        }
    }

    private void checkNewNotification() {
        WebstoreService.checkNewNotification(new NotifyListener() {
            @Override
            public void haveNewUpdate(List<NotifyInfo> notifyInfoList, int newNotifyCount) {
                try {
                    if (notifyInfoList != null && !notifyInfoList.isEmpty()) {
                        if (notifyInfoList.size() > 1) {
                            Notification.notifyNewUpate(getApplicationContext(), "Tin Mới", notifyInfoList.get(0).getTitle() + ". Và " + (notifyInfoList.size() - 1) + " tin khác.", R.drawable.appicon, Notification.NOTIFY_ID_NEWS_UPDATE);
                        } else {
                            Notification.notifyNewUpate(getApplicationContext(), "Tin Mới", notifyInfoList.get(0).getTitle(), R.drawable.appicon, Notification.NOTIFY_ID_NEWS_UPDATE);
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
