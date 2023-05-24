package com.example.mumulbo2023;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.util.Calendar;

public class WidgetService extends Service {

    int LAYOUT_FLAG;
    View mFloatingView;
    WindowManager windowManager;
    ImageView imageClose;
    TextView tvWidth;
    float height, width;



    @Nullable
    @Override
    public IBinder onBind(Intent intent){
        return null;
    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;


        }else{

            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;

        }

        // 우리가 만든 플로팅 뷰 레이아웃 확장
        mFloatingView = LayoutInflater.from(this).inflate(R.layout.layout_widget, null);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                LAYOUT_FLAG,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);


        // 보기 위치 지정
        // 처음에는보기가 오른쪽 상단 모서리에 추가되며 필요에 따라 x-y 좌표를 변경
        //layoutParams.gravity = Gravity.TOP|Gravity.CENTER;
        layoutParams.x = 50;
        layoutParams.y = 50;

        WindowManager.LayoutParams imageParams = new WindowManager.LayoutParams( 140,
                140,
                LAYOUT_FLAG,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        imageParams.gravity = Gravity.BOTTOM|Gravity.CENTER;
        imageParams.y = 100;


        windowManager = (WindowManager)getSystemService(WINDOW_SERVICE);
        imageClose = new ImageView(this);
        imageClose.setImageResource(R.drawable.close);
        imageClose.setVisibility(View.INVISIBLE);
        windowManager.addView(imageClose, imageParams);
        windowManager.addView(mFloatingView, layoutParams);
        mFloatingView.setVisibility(View.VISIBLE);

        height = windowManager.getDefaultDisplay().getHeight();
        width  = windowManager.getDefaultDisplay().getWidth();

        tvWidth = (TextView) mFloatingView.findViewById(R.id.imageView);



        // 사용자의 터치 동작을 사용하여 플로팅 뷰를 드래그하여 이동
        tvWidth.setOnTouchListener(new View.OnTouchListener() {

            int initialx, initialy;
            float initialTouchX, initialTouchY;
            long startCkickTime;

            // 클릭으로 볼 최대시간
            int MAX_CLICK_DURATION = 200;

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:

                        startCkickTime = Calendar.getInstance().getTimeInMillis();
                        imageClose.setVisibility(View.VISIBLE);

                        // 초기 위치 기억
                        initialx = layoutParams.x;
                        initialy = layoutParams.y;

                        //터치 위치 좌표 얻기
                        initialTouchX = motionEvent.getRawX();
                        initialTouchY = motionEvent.getRawY();

                        return true;

                    case MotionEvent.ACTION_UP:

                        long clickDuration = Calendar.getInstance().getTimeInMillis()-startCkickTime;
                        imageClose.setVisibility(view.GONE);


                        // 초기 좌표와 현재 좌표의 차이 가져 오기
                        layoutParams.x = initialx+(int) (initialTouchX-motionEvent.getRawX());
                        layoutParams.y = initialy+(int)(motionEvent.getRawY()-initialTouchY);

                        // 사용자가 플로팅 위젯을 제거 이미지로 끌어다 놓으면 서비스를 중지합니다.
                        if(clickDuration>=MAX_CLICK_DURATION)
                        {
                            // 제거 이미지 주변 거리
                            if (layoutParams.y>(height * 0.6 )){
                                stopSelf();
                            }

                        }


                        return true;

                    case MotionEvent.ACTION_MOVE:

                        // 초기 좌표와 현재 좌표의 차이 가져 오기
                        layoutParams.x = initialx+(int)(initialTouchX-motionEvent.getRawX());
                        layoutParams.y = initialy +(int) (motionEvent.getRawY()- initialTouchY);

                        // 새로운 X 및 Y 좌표로 레이아웃 업데이트
                        windowManager.updateViewLayout(mFloatingView, layoutParams);

                        if (layoutParams.y> (height * 0.6)){

                            imageClose.setImageResource(R.drawable.close);
                        }
                        else {
                            imageClose.setImageResource(R.drawable.close);
                        }
                        return true;

                }
                return false;
            }
        });



        return START_STICKY;
    }

    // 앱이 종료될때 실행
    @Override
    public void onDestroy(){
        super.onDestroy();

        if(mFloatingView != null){
            windowManager.removeView(mFloatingView);

        }
        if (imageClose != null){

            windowManager.removeView(imageClose);
        }

    }
}