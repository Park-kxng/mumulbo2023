package com.example.mumulbo2023;
import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.accessibilityservice.GestureDescription;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Path;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import java.util.List;
import java.util.Locale;

public class CustomAccessibilityService extends AccessibilityService {
    int Step = 0;
    // TTS 텍스트를 음성으로 관련
    TextToSpeech textToSpeech;
    @Override
    protected void onServiceConnected() {
        // 접근성 서비스가 연결되었을 때 호출됩니다.
        Log.e("log1","서비스가 연결되었습니다.");
        Log.d("log1","서비스가 연결되었습니다.");

        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.eventTypes = AccessibilityEvent.TYPE_VIEW_CLICKED | AccessibilityEvent.TYPE_VIEW_LONG_CLICKED;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_SPOKEN;
        setServiceInfo(info);

        // TTS 객체 초기화
        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status!=android.speech.tts.TextToSpeech.ERROR) {

                    textToSpeech.setLanguage(Locale.KOREAN);
                    textToSpeech.setPitch(1.0f); // 높낮이
                    textToSpeech.setSpeechRate(1.0f); // 바르기
                }
            }
        });

        // 런처 말고 내부 앱 패키지 이름 가져오는 것 테스트
        /*
        String packageName = "com.kakao.talk";
        try {
            PackageManager packageManager = getPackageManager();
            Intent launchIntent = packageManager.getLaunchIntentForPackage(packageName);
            Log.d("Package Name", "=========packageName1=============");
            if (launchIntent != null) {
                ComponentName componentName = launchIntent.getComponent();
                Log.d("Package Name", "=========packageName2=============");
                if (componentName != null) {
                    packageName = componentName.getPackageName();
                    Log.d("Package Name", "=========packageName3=============");
                    Log.d("Package Name", packageName);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("Package Name", e.toString());
        }
        */
        ////////

    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        // 접근성 이벤트가 발생했을 때
        Log.d("log2","접근성 이벤트 발생"); // 발생했다고 알려줌

        //1. 초기화
        String packageName = ""; // 패키지 이름 담는 변수 초기화

        //2. 패키지 이름 제대로 받아오기
        AccessibilityNodeInfo sourceNode = event.getSource();
        if(sourceNode != null){
            packageName = String.valueOf(sourceNode.getPackageName());
            //String packageName = String.valueOf(event.getPackageName()); // 지금 들어와있는 패키지 이름 저장
            Log.d("log3-1",packageName); // 현재 어떤 앱에 위치해 있는지 출력

        }
        // TTS를 사용하고 싶다면 아래 한줄만 사용하면 됨. 매개변수 제일 첫번째에 String 말할거 넣으면 됨.
        //textToSpeech.speak(packageName, TextToSpeech.QUEUE_FLUSH, null);

        //3.내가 지금 실행하고 있는 패키지가 카카오톡인 경우
        if("com.kakao.talk".equals(packageName)){
            // 예시. 친구 추가하기 버튼을 찾는 로직을 구현할 예정
            Log.d("log3","룰루랄라");
            sourceNode = event.getSource();
            if (sourceNode != null) {
                Log.d("log3-2","친구추가 버튼 누르기");

                // 카카오톡이 클릭되었을 때의 동작을 여기에 구현
                performFriendAddAction(sourceNode);
            }

            // 클릭하면 실행되는 함수
            if (event.getEventType() == AccessibilityEvent.TYPE_VIEW_CLICKED) {
                // 클릭한 내용의 contentDescription을 읽는다.
                CharSequence text = event.getContentDescription();
                if (text == null) {
                    Log.d("log2-1", "내용이 없습니다.");
                }
                else {
                    String textToString = text.toString();
                    Log.d("log2-1", text.toString());
                    textToSpeech.speak(textToString, TextToSpeech.QUEUE_FLUSH, null);
                    // 친구한테 카톡 보내기 1단계
                    if(Step==0){
                        if(textToString.contains("친구 탭")){
                            // 친구한테 카톡보내기 로직 실행
                            Log.d("log2-2", "친구한테 카톡 보내기 : 1단계");
                            textToSpeech.speak("친구한테 카톡 보내기 : 1단계", TextToSpeech.QUEUE_FLUSH, null);
                            Step = 1;
                        }
                        else{
                            Log.d("log2-2", "친구 목록을 확인하기 위해 가장 아래에 있는 사람 모양의 아이콘을 눌러주세요.");
                        }
                    }
                    // 친구한테 카톡 보내기 2단계
                    else if(Step == 1){
                        if(textToString.contains("검색")){
                            // 친구한테 카톡보내기 로직 실행
                            Log.d("log2-2", "친구한테 카톡 보내기 : 2단계");
                            Step = 2;
                        }
                        else{
                            Log.d("log2-2", "보낼 친구를 검색하기 위해 검색하기 위해 가장 윗부분에 돋보기 버튼을 눌려주세요");
                        }
                    }
                }





            }
            // ▲ 위는 클릭했을 때 어떤 것인지 log 찍는 것


        }

        // ▼ 유진언니 코드

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
    private void performFriendAddAction(AccessibilityNodeInfo rootNode) {
        // 친구 추가하기 버튼을 찾는 로직
        AccessibilityNodeInfo addButton = findFriendAddButton(rootNode);

        if (addButton != null) {
            // 친구 추가하기 버튼을 클릭하는 동작
            Log.d("log3-4","친구 추가 버튼");

            addButton.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        }
    }

    private AccessibilityNodeInfo findFriendAddButton(AccessibilityNodeInfo node) {
        //특정 버튼을 찾는 알고리즘 (구현 미완료)
        if (node.getClassName().equals("android.widget.Button")) {
            CharSequence buttonText = node.getText();
            if (buttonText != null && buttonText.toString().equals("친구 추가하기")) {
                Log.d("log3-3", "찾았다! 친구 추가하기");
                return node;
            }

        }

            for (int i = 0; i < node.getChildCount(); i++) {
                AccessibilityNodeInfo childNode = node.getChild(i);
                if (childNode != null) {
                    AccessibilityNodeInfo targetNode = findFriendAddButton(childNode);
                    if (targetNode != null) {
                        return targetNode;
                    }
                }
            }
        return null;
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
