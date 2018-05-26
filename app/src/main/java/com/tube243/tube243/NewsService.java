package com.tube243.tube243;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

import com.tube243.tube243.data.LocalData;
import com.tube243.tube243.data.Params;
import com.tube243.tube243.entities.News;
import com.tube243.tube243.entities.Tube;
import com.tube243.tube243.processes.LocalDBProcess;
import com.tube243.tube243.processes.LocalTextTask;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class NewsService extends Service {
    private LocalData localData;
    private Boolean isRunning;

    public NewsService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        isRunning = false;
        localData = new LocalData(getApplicationContext());
        started();
    }

    private void started() {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                if(localData.getLong("userId")!=null && localData.getString("localDB")!=null)
                {
                    if(isRunning)
                    {
                        started();
                        return;
                    }

                    isRunning = true;
                    LocalTextTask textTask = new LocalTextTask();
                    textTask.setUrlString(Params.SERVER_HOST+"?controller=utilities&method=news&user-id="+localData.getLong("userId"));
                    textTask.setListener(new LocalTextTask.ResultListener() {
                        @Override
                        public void onResult(Map<String, Object> result) {
                            isRunning = false;
                            try
                            {
                                if(result.containsKey("isDone") && (Boolean) result.get("isDone"))
                                {
                                    List<Map<String,Object>> maps = (List<Map<String,Object>>) result.get("data");
                                    Integer mapsSize = maps.size();
                                    if(mapsSize>0)
                                    {
                                        try
                                        {
                                            LocalDBProcess localDBProcess = new LocalDBProcess(localData.getString("localDB"));
                                            for(int i=0;i<mapsSize;i++)
                                            {
                                                Map<String,Object> map = maps.get(i);
                                                Tube tube = new Tube(Long.parseLong(map.get("tubeId").toString()),map.get("tubeName").toString(),map.get("tubeFolder").toString(),
                                                        map.get("tubeSize").toString(), Integer.parseInt(map.get("tubeQty").toString()));
                                                News news = new News(Long.parseLong(map.get("newsId").toString()),Long.parseLong(map.get("artistId").toString()), map.get("artistName").toString(), tube,0);
                                                localDBProcess.insertNews(news);
                                                localDBProcess.insertTube(tube);
                                                new Notifier(getApplicationContext(),tube);
                                            }
                                        }
                                        catch (ClassNotFoundException | SQLException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                            catch (Exception ignored){}
                        }
                    });
                }
                started();
            }
        }, 10000);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
