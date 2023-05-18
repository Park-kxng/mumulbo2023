package com.example.mumulbo2023;
import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Toast;

public class CustomAccessibilityService extends AccessibilityService {

    @Override
    protected void onServiceConnected() {
        // 접근성 서비스가 연결되었을 때 호출됩니다.
        Log.e("log1","서비스가 연결되었습니다.");
        Log.d("log1","서비스가 연결되었습니다.");

        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.eventTypes = AccessibilityEvent.TYPE_VIEW_CLICKED | AccessibilityEvent.TYPE_VIEW_LONG_CLICKED;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_SPOKEN;
        setServiceInfo(info);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        // 접근성 이벤트가 발생했을 때 호출됩니다.
        Log.d("log2","접근성 이벤트 발생");
        if (event.getEventType() == AccessibilityEvent.TYPE_VIEW_CLICKED) {
            CharSequence text = event.getContentDescription();
            if(text == null){
                Log.d("log2-1","내용이 없습니다.");
            }else{Log.d("log2-1",text.toString());}


        }
    }

    @Override
    public void onInterrupt() {
        Log.e("log3","접근성 이벤트 중단");

        // 접근성 서비스가 중단되었을 때 호출됩니다.
    }
}
