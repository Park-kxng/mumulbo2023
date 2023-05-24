package com.example.mumulbo2023;
import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.accessibilityservice.GestureDescription;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Path;
import android.os.Environment;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CustomAccessibilityService extends AccessibilityService {
    int Step = 0;
    // 단계별 동작을 저장하기 위한 배열을 선언합니다.
    Map<Integer, String> ActionsOfKakao= new HashMap<>(); // 단계 저장
    Map<Integer, String> CommentsOfKakao= new HashMap<>(); // 단계별 코멘트 저장 - 사용자가 잘못 누른 경우 제대로 누르도록 설명해주는 코드

    String folder = "Test_Directory"; // 캡쳐화면 저장 폴더 이름

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
        //textToSpeech.speak("말할 부분", TextToSpeech.QUEUE_FLUSH, null);

        // 3.내가 지금 실행하고 있는 패키지가 카카오톡인 경우
        if("com.kakao.talk".equals(packageName)){
            // 예시. 친구 추가하기 버튼을 찾는 로직을 구현할 예정
            Log.d("log3","카카오톡에서 실행 중!");
            AddDataToKaKao(); // 카카오톡 관련 데이터를 추가합니다.
            Step = 1;

            // 내가 원하는 버튼 찾기 (미완료)
            sourceNode = event.getSource();
            if (sourceNode != null) {
                Log.d("log3-2","친구추가 버튼 누르기");
                // 카카오톡이 클릭되었을 때의 동작을 여기에 구현
                performFriendAddAction(sourceNode);
            }

            // 클릭하면 실행되는 함수 (진행 중)
            if (event.getEventType() == AccessibilityEvent.TYPE_VIEW_CLICKED) {
                // 클릭한 내용의 contentDescription을 읽는다.
                CharSequence text = event.getContentDescription();
                if (text == null) {
                    Log.d("log2-1", "내용이 없습니다.");
                    textToSpeech.speak("인식할 수 있는 내용이 없습니다.", TextToSpeech.QUEUE_FLUSH, null);
                }
                else {
                    String textToString = text.toString();
                    Log.d("log2-1", text.toString());
                    // 단계에 따라 동작을 안내하는 알고리즘
                    switch (Step) {
                        case 1:
                            if(textToString.contains("친구 탭")){// 친구한테 카톡보내기 로직 실행
                                PrintLog(packageName,Step,1);
                                Step = 1;
                            }
                            else{PrintLog(packageName,Step,0);}
                            break;
                        case 2:
                            if(textToString.contains("검색")){// 친구한테 카톡보내기 로직 실행
                                PrintLog(packageName,Step,0);
                                Step = 2;
                            }
                            else{PrintLog(packageName,Step,1);}
                            break;
                        default:
                            // 모든 case에 해당하지 않을 때 실행되는 코드 블록
                    }
                }
            }
            // ▲ 위는 클릭했을 때 어떤 것인지 log 찍는 것


        }

        // ▼ 유진언니 코드

        // ▼ 객체를 통해 현재 화면의 모든 UI에 대한 정보를 얻으려면 AccessibilityEvent.TYPE_WINDOWS_CHANGED 이벤트 수신
        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOWS_CHANGED) {

            // 현재 화면 캡쳐 후 갤러리에 저장
            //전체화면
            //View rootView = getWindow().getDecorView();

            //File screenShot = ScreenShot(rootView);
            //if(screenShot!=null){
                //갤러리에 추가
            //    sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(screenShot)));
            //}

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

    private void PrintLog(String packageName,int Step, int Action){
        // 로그와 TTS를 실행하는 함수
        // input : 현재 패키지, 단계, 액션 올바르게 했는지 여부
        if(Action == 1){
            // 올바른 액션을 취한 경우
            Log.d(packageName, Step+"단계 :"+ActionsOfKakao.get(Step));
            textToSpeech.speak(Step+"단계 :" + ActionsOfKakao.get(Step+1), TextToSpeech.QUEUE_FLUSH, null);
        }
        else if(Action == 0){
            // 바르지 못한 액션을 취한 경우
            Log.d(packageName, Step+"단계 실행 실패 :"+ActionsOfKakao.get(Step));
            textToSpeech.speak(Step+"단계 :" + CommentsOfKakao.get(Step), TextToSpeech.QUEUE_FLUSH, null);
        }


    }

    private void AddDataToKaKao(){
        // 카카오 관련 데이터를 추가하는 함수
        ActionsOfKakao.put(1, "채팅 탭");
        CommentsOfKakao.put(1, "친구 목록을 확인하기 위해 가장 아래에 있는 사람 모양의 아이콘을 눌러주세요");

        ActionsOfKakao.put(2, "검색 버튼");
        CommentsOfKakao.put(2, "보낼 친구를 검색하기 위해 검색하기 위해 가장 윗부분에 돋보기 버튼을 눌려주세요");

        ActionsOfKakao.put(3, "친구 클릭");
        CommentsOfKakao.put(3, "");

        ActionsOfKakao.put(4, "플러스 버튼");
        CommentsOfKakao.put(4, "");

        ActionsOfKakao.put(5, "사진 클릭");
        CommentsOfKakao.put(5, "");

        ActionsOfKakao.put(6, "전송 버튼");
        CommentsOfKakao.put(6, "");
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

    //화면 캡쳐하기
    public File ScreenShot(View view){
        view.setDrawingCacheEnabled(true);  //화면에 뿌릴때 캐시를 사용하게 한다

        Bitmap screenBitmap = view.getDrawingCache();   //캐시를 비트맵으로 변환

        String filename = "screenshot.png";
        File file = new File(Environment.getExternalStorageDirectory()+"/Pictures", filename);  //Pictures폴더 screenshot.png 파일
        FileOutputStream os = null;
        try{
            os = new FileOutputStream(file);
            screenBitmap.compress(Bitmap.CompressFormat.PNG, 90, os);   //비트맵을 PNG파일로 변환
            os.close();
        }catch (IOException e){
            e.printStackTrace();
            return null;
        }

        view.setDrawingCacheEnabled(false);
        return file;
    }


}
