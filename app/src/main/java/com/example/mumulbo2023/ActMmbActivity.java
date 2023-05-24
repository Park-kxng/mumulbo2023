package com.example.mumulbo2023;
import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.content.Intent;
import android.provider.AlarmClock;
import android.view.View;
import android.widget.Button;

import java.util.Calendar;

public class ActMmbActivity extends Activity {
    Button kakaoButton, alarmButton, coupangButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act_mmb);

        kakaoButton = findViewById(R.id.kakaoButton);
        alarmButton = findViewById(R.id.alarmButton);
        coupangButton = findViewById(R.id.coupangButton);


        kakaoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 카카오톡 앱을 열기 위한 인텐트 생성
                Intent intent = getPackageManager().getLaunchIntentForPackage("com.kakao.talk");
                if (intent != null) {
                    // 카카오톡 앱이 설치되어 있는 경우 앱을 엽니다.
                    startActivity(intent);
                    sendMessage();
                } else {
                    // 카카오톡 앱이 설치되어 있지 않은 경우 마켓으로 이동합니다.
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.kakao.talk"));
                    startActivity(intent);
                }

            }
        });

        alarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_LAUNCHER);
                intent.setComponent(new ComponentName("com.sec.android.app.clockpackage", "com.sec.android.app.clockpackage.ClockPackage"));
                startActivity(intent);
            }
        });

        coupangButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 쿠팡 앱을 열기 위한 인텐트 생성
                Intent intent = getPackageManager().getLaunchIntentForPackage("com.coupang.mobile");
                if (intent != null) {
                    // 쿠팡 앱이 설치되어 있는 경우 앱을 엽니다.
                    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } else {
                    // 쿠팡 앱이 설치되어 있지 않은 경우 마켓으로 이동합니다.
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.coupang.mobile"));
                    startActivity(intent);
                }

                 /*
                String packageName = "com.coupang.mobile"; // 패키지 이름을 여기에 적용하세요

                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName));
                    intent.setPackage("com.android.vending"); // 구글 플레이 스토어 앱을 사용하여 열도록 지정
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + packageName));
                    startActivity(intent);
                }*/
            }
        });


    }

    private void sendMessage() {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("kakaotalk://kakaotalk.com?uri=qr"));


        startActivity(intent);
    }
}
