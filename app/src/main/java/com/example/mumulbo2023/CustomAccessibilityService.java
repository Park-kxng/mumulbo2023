package com.example.mumulbo2023;
import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.accessibilityservice.GestureDescription;
import android.graphics.Path;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
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
        // ▲ 위는 클릭했을 때 어떤 것인지 log 찍는 것

        // ▼ 객체를 통해 현재 화면의 모든 UI에 대한 정보를 얻으려면 AccessibilityEvent.TYPE_WINDOWS_CHANGED 이벤트 수신
        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOWS_CHANGED) {

            AccessibilityNodeInfo rootNode = getRootInActiveWindow();
            if (rootNode != null) {
                traverseNode(rootNode);
                rootNode.recycle(); // 노드 반환
            }
        }

        if (event.getEventType() == AccessibilityEvent.TYPE_VIEW_SELECTED) {
            CharSequence text = event.getContentDescription();

            if (text == null) {
                Log.d("log", "ContentDescription is null");
            } else {
                Log.d("log3-1","============================");
                Log.d("log", text.toString());
            }
        }

        /*
        int eventType = event.getEventType();
        if (eventType == AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED) {
            AccessibilityNodeInfo source = event.getSource();
            if (source != null) {
                traverseNode(source);
                source.recycle();
            }
        }*/
    }

    @Override
    public void onInterrupt() {
        Log.e("log3","접근성 이벤트 중단");

        // 접근성 서비스가 중단되었을 때 호출됩니다.
    }

    private void traverseNode(AccessibilityNodeInfo node) {
        if (node == null) {
            return;
        }

        // 요소의 contentDescription이 있는 경우 Log로 출력
        CharSequence contentDescription = node.getContentDescription();
        if (contentDescription != null) {
            Log.d("Accessibility", "Content Description: " + contentDescription.toString());
        }

        // 하위 요소들에 대해서 재귀적으로 탐색
        int childCount = node.getChildCount();
        for (int i = 0; i < childCount; i++) {
            AccessibilityNodeInfo childNode = node.getChild(i);
            traverseNode(childNode);
            childNode.recycle();
        }
    }


    public void performClickAtCoordinates(int x, int y) {
        Path path = new Path();
        path.moveTo(x, y);

        GestureDescription.StrokeDescription stroke = new GestureDescription.StrokeDescription(path, 0, 100);
        GestureDescription gesture = new GestureDescription.Builder().addStroke(stroke).build();

        boolean result = dispatchGesture(gesture, new GestureResultCallback() {
            @Override
            public void onCompleted(GestureDescription gestureDescription) {
                super.onCompleted(gestureDescription);
                // 클릭 동작 완료 후 호출되는 콜백
            }
        }, null);

        if (!result) {
            // 클릭 동작이 실패한 경우의 처리
        }
    }

}
