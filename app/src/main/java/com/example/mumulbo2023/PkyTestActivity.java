package com.example.mumulbo2023;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;


import android.content.Intent;
import android.provider.Settings;


public class PkyTestActivity extends Activity{
    private Button button;
    private ImageView imageView;

    private static final int REQUEST_ACCESSIBILITY = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_pky);
        button = findViewById(R.id.button);
        imageView = findViewById(R.id.imageView);

        // 버튼 클릭 시 설명 메시지 토스트로 출력
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 내가 구현한 (무물보)ui 요소의 설명하는 코드
                String buttonDescription = button.getContentDescription().toString(); // contetnDescription 읽기
                Toast.makeText(PkyTestActivity.this, buttonDescription, Toast.LENGTH_SHORT).show(); // 출력하기

                // 다른 앱에 접근하여 ui를 설명해주는 코드
                if (isAccessibilityServiceEnabled()) {
                    Toast.makeText(PkyTestActivity.this,"--------startCustomAccessibilityService", Toast.LENGTH_SHORT).show();

                    startCustomAccessibilityService();
                } else {
                    requestAccessibilityService();
                }
            }
        });



        // 이미지 클릭 시 설명 메시지 토스트로 출력
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 내가 구현한  (무물보)ui 요소의 설명하는 코드
                String imageDescription = imageView.getContentDescription().toString();
                Toast.makeText(PkyTestActivity.this, imageDescription, Toast.LENGTH_SHORT).show();

            }
        });
    }

    private boolean isAccessibilityServiceEnabled() {
        int accessibilityEnabled = Settings.Secure.getInt(
                getContentResolver(),
                Settings.Secure.ACCESSIBILITY_ENABLED,
                0
        );
        return accessibilityEnabled == 1;
    }

    private void requestAccessibilityService() {
        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        startActivityForResult(intent, REQUEST_ACCESSIBILITY);
    }

    private void startCustomAccessibilityService() {
        Intent intent = new Intent(this, CustomAccessibilityService.class);
        startService(intent);
        Toast.makeText(this, "Custom Accessibility Service started", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ACCESSIBILITY) {
            if (isAccessibilityServiceEnabled()) {
                startCustomAccessibilityService();
            } else {
                Toast.makeText(this, "Accessibility service not enabled", Toast.LENGTH_SHORT).show();
            }
        }
    }

}

