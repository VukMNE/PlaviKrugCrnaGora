package me.plavikrug;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

import java.util.UUID;

/**
 * Created by Vuk on 6.11.2017..
 */

public class NotificationPrijemnik extends JobService {

    private AsyncTask mBackgroundTask;
    private Bundle getJobData;

    @Override
    public boolean onStartJob(final JobParameters job) {
        getJobData = job.getExtras();
        mBackgroundTask = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {
                Context context = NotificationPrijemnik.this;
                Intent notificationIntent = new Intent(context, PodsjetniciLista.class);
                notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                stackBuilder.addParentStack(PodsjetniciLista.class);
                stackBuilder.addNextIntent(notificationIntent);
                UUID id = UUID.randomUUID();
                PendingIntent pendingIntent = stackBuilder.getPendingIntent(100, PendingIntent.FLAG_UPDATE_CURRENT);
                Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

                NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
                Notification notification = builder.setAutoCancel(true)
                        .setSmallIcon(R.mipmap.plavikrug)
                        .setContentTitle("Podsjetnik:")
                        .setContentText(getJobData.getString("podnaslov","Naslov"))
                        .setTicker("Podsjetnik: " + getJobData.getString("podnaslov","Naslov"))
                        .setWhen(getJobData.getLong("kada?"))
                        .setSound(uri)
                        .setContentIntent(pendingIntent).build();


                //builder.setWhen(alarmVrijeme.getTimeInMillis());


                NotificationManager nm = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
                nm.notify(getJobData.getInt("notifikacija_id"), notification);
                SharedPreferences podaci = getSharedPreferences("PODACI",MODE_PRIVATE);
                SharedPreferences.Editor editor = podaci.edit();
                editor.remove("notifikacija_id");
                editor.commit();
                editor.putInt("notifikacija_id", getJobData.getInt("notifikacija_id")+1);
                editor.commit();
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                jobFinished(job, false);
            }
        };
        mBackgroundTask.execute();
        if(mBackgroundTask != null) mBackgroundTask.cancel(true);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        return false;
    }
}
