package com.example.hstalk;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class MyFirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();

    // 메시지 수신
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.i(TAG, "onMessageReceived");

        Map<String, String> data = remoteMessage.getData();
        String title = data.get("title");
        String message = data.get("body");
        String uid = data.get("uid");
        String postId = data.get("postId");
        String pushId = data.get("pushId");
        String name = data.get("name");
        sendNotification(title, message, uid, postId, pushId, name);

    }

    private void sendNotification(String title, String message, String ruid, String postId, String pushId, String name) {

        String channelId = "channel";
        String channelName = "suhwa";
        String puid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel mChannel = new NotificationChannel(
                    channelId, channelName, importance);

            notificationManager.createNotificationChannel(mChannel);
        }

        String liveTitle = "실시간 매칭이 성사되었습니다!";
        String boardTitle = "예약매칭 신청 알림";
        String boardCompleteTitle = "예약 매칭이 성사되었습니다!";

        if(title.equals(liveTitle)){
            Intent intent1 = new Intent(this,VideoChatActivity.class);

            Bundle bundle = new Bundle();
            bundle.putString("pushId",pushId);
            intent1.putExtras(bundle);
            intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            PendingIntent pendingIntent1 = PendingIntent.getActivity(this, 0 /* Request code */, intent1,
                    PendingIntent.FLAG_CANCEL_CURRENT);

            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this,channelId)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.suhwa_logo))
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent1);


            notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
        }else if(title.equals(boardTitle)){
            Intent intent2 = new Intent(this, ReserveMatchingActivity.class);

            Bundle bundle = new Bundle();
            bundle.putString("sender", ruid);
            bundle.putString("postId", postId);
            intent2.putExtras(bundle);
            intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            PendingIntent pendingIntent2 =  PendingIntent.getActivity(this, 0 /* Request code */, intent2,
                    PendingIntent.FLAG_CANCEL_CURRENT);
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this,channelId)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.suhwa_logo))
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent2);

            notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());

        }
        else if(title.equals(boardCompleteTitle)){
            Intent intent3 = new Intent(this,MatchingInfoActivity.class);

            PendingIntent pendingIntent3 = PendingIntent.getActivity(this, 0 /* Request code */, intent3,
                    PendingIntent.FLAG_CANCEL_CURRENT);

            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this,channelId)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.suhwa_logo))
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent3);


            notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());

        }
        else{
            Intent intent4 = new Intent(this, MatchingActivity.class);
            //번들에 수신한 메세지를 담아서 액티비티로 넘겨 보자.
            Bundle bundle = new Bundle();
            bundle.putString("title", title);
            bundle.putString("body", message);
            bundle.putString("user", puid);
            bundle.putString("receiver", ruid);
            bundle.putString("pushId", pushId);
            bundle.putString("name", name);
            intent4.putExtras(bundle);
            intent4.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            PendingIntent pendingIntent4 = PendingIntent.getActivity(this, 0 /* Request code */, intent4,
                    PendingIntent.FLAG_CANCEL_CURRENT);

            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this,channelId)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.suhwa_logo))
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent4);


            notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
        }
    }
}
