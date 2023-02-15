package com.example.notificationapp;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel ;
import android.app.NotificationManager ;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Intent ;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle ;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import android.view.Menu ;
import android.view.MenuItem ;
import android.view.View ;
import android.widget.Button ;
import android.widget.TextView ;

public class MainActivity extends AppCompatActivity implements MyListener {
    private TextView txtView ;
    public static final String NOTIFICATION_CHANNEL_ID = "10001" ;
    private final static String default_notification_channel_id = "default" ;
    final int PERMISSION_REQUEST_CODE = 112;
    private NotificationManager mNotificationManager;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super .onCreate(savedInstanceState) ;
        setContentView(R.layout. activity_main ) ;
        new NotificationService().setListener( this ) ;
        mNotificationManager = (NotificationManager) getSystemService( NOTIFICATION_SERVICE );
        txtView = findViewById(R.id. textView ) ;

        Button btnCreateNotification = findViewById(R.id. btnCreateNotification ) ;
        btnCreateNotification.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                mNotificationManager.notify(( int ) System. currentTimeMillis () , normalNotification()) ;
            }
        }) ;

        Button btnAlterNotification = findViewById(R.id.btnAlterNotification);
        btnAlterNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mNotificationManager.notify(
                        ( int ) System. currentTimeMillis () ,
                        alterNotification(normalNotification())) ;
            }
        });

        Button btnRedactNotification = findViewById(R.id.btnRedactNotification);
        btnRedactNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mNotificationManager.notify(
                        ( int ) System. currentTimeMillis () ,
                        redactNotification(normalNotification())) ;
            }
        });

        if (!shouldShowRequestPermissionRationale("112")){
            getNotificationPermission();
        }

    }

    public void getNotificationPermission(){
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.POST_NOTIFICATIONS},
                PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {

                }
        }
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        getMenuInflater().inflate(R.menu. menu_main , menu) ; //Menu Resource, Menu
        return true;
    }
    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        switch (item.getItemId()) {
            case R.id. action_settings :
                Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS" ) ;
                startActivity(intent) ;
                return true;
            default :
                return super .onOptionsItemSelected(item) ;
        }
    }
    @Override
    public void setValue (String packageName) {
        txtView .append( " \n " + packageName) ;
    }

    public Notification normalNotification() {
        Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS" );
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(MainActivity.this);
        stackBuilder.addNextIntentWithParentStack(intent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0,
                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(MainActivity. this, default_notification_channel_id ) ;

        mBuilder.setContentTitle( "My Notification" ) ;
        mBuilder.setContentText( "Notification Listener Service Example" ) ;
        mBuilder.setTicker( "Notification Listener Service Example" ) ;
        mBuilder.setSmallIcon(R.drawable. ic_launcher_foreground ) ;
        mBuilder.setContentIntent(resultPendingIntent);
        mBuilder.setAutoCancel( true ) ;
        int importance = NotificationManager. IMPORTANCE_HIGH ;
        NotificationChannel notificationChannel = new NotificationChannel( NOTIFICATION_CHANNEL_ID , "NOTIFICATION_CHANNEL_NAME" , importance) ;
        mBuilder.setChannelId( NOTIFICATION_CHANNEL_ID ) ;
        assert mNotificationManager != null;
        mNotificationManager.createNotificationChannel(notificationChannel) ;
        assert mNotificationManager != null;
        return mBuilder.build();
    }

    public Notification alterNotification(Notification notification) {
        return Notification.Builder.recoverBuilder(MainActivity.this, notification)
                    .setContentTitle("New altered Notification")
                    .setContentText("Content hidden by altering notification content")
                    .build();
    }

    public Notification redactNotification(Notification notification) {
        return new Notification.Builder(MainActivity. this, notification.getChannelId())
                        .setContentTitle("New redacted Notification")
                        .setContentText("Content hidden by redacting notification")
                        .setContentIntent(notification.contentIntent)
                        .setSmallIcon(notification.getSmallIcon())
                        .setGroup(notification.getGroup())
                        .setGroupAlertBehavior(notification.getGroupAlertBehavior())
                        .setSettingsText(notification.getSettingsText())
                        .setShortcutId(notification.getShortcutId())
                        .setSortKey(notification.getSortKey())
                        .build();
    }
}