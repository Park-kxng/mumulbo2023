package com.example.mumulbo2023;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.provider.AlarmClock;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ActMmbActivity extends Activity {
    Button kakaoButton, alarmButton, coupangButton, recordButton;
    //////
    TextView recordingText; // TTS로 음성으로 나오기도 하지만 텍스트로도 표시하기 위함
    //TextView answerText;
    // STT 녹음 관련
    SpeechRecognizer speechRecognizer;
    Intent recordIntent;
    boolean recording = false;  //현재 녹음중인지 여부
    String userSTT = ""; // 음성 인식 결과
    EditText editText; // ▶ STT 잘 되고 있는지 확인 용도 최종 결과에선 삭제해도 됨
    // TTS 텍스트를 음성으로 관련
    TextToSpeech textToSpeech;


    // Chat GPT 관련
    Handler handler = new Handler(Looper.getMainLooper());
    String temp = "";
    String question = "";

    boolean answer = false;  //현재 녹음중인지 여부
    boolean mmb_status = false; // 음성봇이 이야기는지 여부
    String gptTTS = ""; // GPT 검색 결과
    String result_copy = "";
    private static final String TAG = "AppPackageNames";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act_mmb);
        // 내 핸드폰 내의 어떤 앱들 깔려 있나 패키지 이름 출력
        printAllAppPackageNames();
        // 카카오톡 유튜브 있나 출력
        printAppPackageNames("카카오톡", "유튜브", "쿠팡", "시계", "갤러리");
        // Package Name: com.samsung.android.accessibility.talkback
        // Package Name: com.samsung.android.app.settings.bixby
        // com.samsung.android.camerasdkservice
        // com.samsung.android.bixby.agent

        kakaoButton = findViewById(R.id.kakaoButton);
        alarmButton = findViewById(R.id.alarmButton);
        coupangButton = findViewById(R.id.coupangButton);

        // STT 관련
        recordButton = findViewById(R.id.sttButton);
        editText = findViewById(R.id.sttEditText);
        recordingText = findViewById(R.id.recordingNowTextView);

        /* STT  */
        recordIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recordIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());
        recordIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");   //한국어
        recordButton.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                System.out.println("버튼을 클릭하였습니다.");
                                                System.out.println(answer);

                                                // answer == true -> 음성봇이 말해야 하는 경우
                                                if (answer == true) { // 챗 지피티 답변 시작
                                                    /*
                                                    // 캐릭터의 얼굴을 웃는 얼굴로 변경합니다.
                                                    faceImage.setImageResource(R.drawable.character_mmb_smile);
                                                    question = editText.getText().toString();  // 물어본 답변은 저장합니다.
                                                    //챗 지피티에게 물어보고 답변을 gptTTS에 저장합니다.
                                                    System.out.println(question);
                                                    gptTTS = callAPI(question);
                                                    Toast.makeText(getApplicationContext(), temp, Toast.LENGTH_SHORT).show();
                                                    System.out.println("gptTTS : "+ gptTTS);
                                                    System.out.println("temp : "+temp);
                                                    System.out.println("answerText : "+answerText);
                                                    answerText.setText(gptTTS); // 답변 부분에 출력
                                                    textToSpeech.setPitch(1.0f); // 높낮이
                                                    textToSpeech.setSpeechRate(1.0f); // 바르기
                                                    textToSpeech.speak(gptTTS, TextToSpeech.QUEUE_FLUSH, null);
                                                    */
                                                    answer = false; // 챗 지피티가 답변을 완료하였습니다.

                                                    // 아이콘을 녹음 모양으로 변경합니다.
                                                    //recordButton.setImageResource(R.drawable.icon_speak);

                                                }
                                                // answer == false --> 사람이 말해야 하는 경우
                                                else if (answer == false) {
                                                    if (!recording) {   //녹음 시작
                                                        //textToSpeech.stop();
                                                        //textToSpeech.shutdown();

                                                        recording = true;
                                                        startRecord();
                                                        Toast.makeText(getApplicationContext(), "지금부터 무엇이든 물어보세요!", Toast.LENGTH_SHORT).show();
                                                        recordingText.setText("녹음중? YES");
                                                        // 캐릭터의 얼굴을 무표정 얼굴로 변경합니다.
                                                        //faceImage.setImageResource(R.drawable.character_mmb);

                                                        // 기존에 있던 애들 초기화
                                                        //answerText.setText("");
                                                        editText.setText("");
                                                        userSTT = "";
                                                        gptTTS = "";

                                                    }

                                                    /*
                                                    else {  //이미 녹음 중이면 녹음 중지
                                                        recording = false;
                                                        stopRecord();
                                                        recordingText.setText("녹음중? NO");
                                                        // TTS 테스트용으로 녹음 종료시 녹음된걸 말해주는거 넣어둠
                                                        question = editText.getText().toString();  // 물어본 답변은 저장합니다.
                                                        // 아이콘을 스피커 모양으로 변경합니다.
                                                        //recordButton.setImageResource(R.drawable.icon_speak_mmb);
                                                        answer = true;
                                                        // ★★★★★★★★★★★★★★★★★★★★★★
                                                        // 버튼 눌렀을 때 녹음 종료하고 앱 이동할거면 여기에 삽입
                                                    }

                                                     */
                                                }


                                            }
                                        }
        );
        /* TTS  */
        // TTS 객체 초기화
        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != android.speech.tts.TextToSpeech.ERROR) {

                    textToSpeech.setLanguage(Locale.KOREAN);
                }
            }
        });
        //////////////////////////////////////////////
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


    ////////////

    private void printAllAppPackageNames() {
        PackageManager packageManager = getPackageManager();
        List<ApplicationInfo> packages = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);

        for (ApplicationInfo packageInfo : packages) {
            String packageName = packageInfo.packageName;
            Log.d(TAG, "Package Name: " + packageName);
        }
    }


    private void printAppPackageNames(String... appNames) {
        PackageManager packageManager = getPackageManager();
        Log.d(TAG, "==========================================");
        for (String appName : appNames) {
            Intent intent = new Intent(Intent.ACTION_MAIN, null);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.setPackage(null);

            List<ResolveInfo> resolveInfoList = packageManager.queryIntentActivities(intent, 0);

            for (ResolveInfo resolveInfo : resolveInfoList) {
                String packageName = resolveInfo.activityInfo.packageName;
                String label = resolveInfo.loadLabel(packageManager).toString();
                if (label.equalsIgnoreCase(appName)) {
                    Log.d(TAG, "App Name: " + appName + ", Package Name: " + packageName);
                    break;
                }
            }
        }
    }

    private String findAppPackageNames(String... appNames) {
        PackageManager packageManager = getPackageManager();
        Log.d(TAG, "==========================================");
        for (String appName : appNames) {
            Intent intent = new Intent(Intent.ACTION_MAIN, null);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.setPackage(null);

            List<ResolveInfo> resolveInfoList = packageManager.queryIntentActivities(intent, 0);

            for (ResolveInfo resolveInfo : resolveInfoList) {
                String packageName = resolveInfo.activityInfo.packageName;
                String label = resolveInfo.loadLabel(packageManager).toString();
                if (label.equalsIgnoreCase(appName)) {
                    Log.d(TAG, "App Name: " + appName + ", Package Name: " + packageName);
                    return packageName;
                    //break;
                }
            }
        }
        return "";
    }
    ////////
    private void sendMessage() {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("kakaotalk://kakaotalk.com?uri=qr"));


        startActivity(intent);
    }


    // 녹음 관련
    // 녹음 시작
    void startRecord() {
        //recording = true;

        //recordButton.setText("음성 녹음 변환 중지");
        Toast.makeText(getApplicationContext(), "음성인식 시작", Toast.LENGTH_SHORT).show();
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(getApplicationContext());
        speechRecognizer.setRecognitionListener(listener);
        speechRecognizer.startListening(recordIntent);
    }

    //녹음 중지
    void stopRecord() {
        //recording = false;

        //recordButton.setText("음성 녹음 변환 시작");
        speechRecognizer.stopListening();   //녹음 중지
        speechRecognizer.destroy(); // 걍 없애버림 자꾸 소리 들으면 인식하려고 하길래
        Toast.makeText(getApplicationContext(), "음성 기록을 중지합니다.", Toast.LENGTH_SHORT).show();
    }

    RecognitionListener listener = new RecognitionListener() {
        @Override
        public void onReadyForSpeech(Bundle bundle) {
        }

        @Override
        public void onBeginningOfSpeech() {
            //사용자가 말하기 시작
        }

        @Override
        public void onRmsChanged(float v) {
        }

        @Override
        public void onBufferReceived(byte[] bytes) {
        }

        @Override
        public void onEndOfSpeech() {

        }

        @Override
        public void onError(int error) {    //토스트 메세지로 에러 출력
            String message;
            switch (error) {
                case SpeechRecognizer.ERROR_AUDIO:
                    message = "오디오 에러";
                    break;
                case SpeechRecognizer.ERROR_CLIENT:
                    //message = "클라이언트 에러";
                    //speechRecognizer.stopListening()을 호출하면 발생하는 에러
                    return; //토스트 메세지 출력 X
                case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                    message = "퍼미션 없음";
                    break;
                case SpeechRecognizer.ERROR_NETWORK:
                    message = "네트워크 에러";
                    break;
                case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                    message = "네트웍 타임아웃";
                    break;
                case SpeechRecognizer.ERROR_NO_MATCH:
                    //message = "찾을 수 없음";
                    //녹음을 오래하거나 speechRecognizer.stopListening()을 호출하면 발생하는 에러
                    //speechRecognizer를 다시 생성하여 녹음 재개
                    if (recording)
                        startRecord();
                    return; //토스트 메세지 출력 X
                case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                    message = "RECOGNIZER가 바쁨";
                    break;
                case SpeechRecognizer.ERROR_SERVER:
                    message = "서버가 이상함";
                    break;
                case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                    message = "말하는 시간초과";
                    break;
                default:
                    message = "알 수 없는 오류임";
                    break;
            }
            Toast.makeText(getApplicationContext(), "에러가 발생하였습니다. : " + message, Toast.LENGTH_SHORT).show();
        }

        //인식 결과가 준비되면 호출
        @Override
        public void onResults(Bundle bundle) {
            Log.d("녹음 상태 onResults", "onResults 호출됨 userSTT저장");
            ArrayList<String> matches = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);    //인식 결과를 담은 ArrayList


            for (int i = 0; i < matches.size(); i++) {
                userSTT += matches.get(i);
            }
            userSTT += " "; // 연속해서 말할 경우 띄어쓰기 포함
            editText.setText(userSTT);    //기존의 text에 인식 결과 보여
            // ★★★★★★★★★★★★★★★★★★★★★★
            if (userSTT.contains("카카오톡") || userSTT.contains("카톡")) {
                // 카카오톡 앱을 열기 위한 인텐트 생성
                Intent intent = getPackageManager().getLaunchIntentForPackage("com.kakao.talk");
                if (intent != null) {
                    // 카카오톡 앱이 설치되어 있는 경우 앱을 엽니다.
                    startActivity(intent);
                    sendMessage();
                } else {
                    // 카카오톡 앱이 설치되어 있지 않은 경우 마켓으로 이동합니다.
                    Log.d("카카오톡 설치x", "카카오톡 설치되어 있지 않음 마켓으로 이동");
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.kakao.talk"));
                    startActivity(intent);
                }
            } else if (userSTT.contains("쿠팡") || userSTT.contains("쇼핑")) {
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
            } else if (userSTT.contains("알람") || userSTT.contains("시계")) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_LAUNCHER);
                intent.setComponent(new ComponentName("com.sec.android.app.clockpackage", "com.sec.android.app.clockpackage.ClockPackage"));
                startActivity(intent);
            }else{
                String pakageName =  findAppPackageNames(userSTT.split(" "));
                Intent intent = getPackageManager().getLaunchIntentForPackage( pakageName);
                if (intent != null) {
                    // 쿠팡 앱이 설치되어 있는 경우 앱을 엽니다.
                    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } else {
                    // 쿠팡 앱이 설치되어 있지 않은 경우 마켓으로 이동합니다.
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" +  pakageName));
                    startActivity(intent);
                }
            }

            recording = false;

            recordingText.setText("녹음중? NO");
            // TTS 테스트용으로 녹음 종료시 녹음된걸 말해주는거 넣어둠
            question = editText.getText().toString();  // 물어본 답변은 저장합니다.
            // 아이콘을 스피커 모양으로 변경합니다.
            //recordButton.setImageResource(R.drawable.icon_speak_mmb);
            //answer = true;
            stopRecord();

            //speechRecognizer.startListening(recordIntent);    //녹음버튼을 누를 때까지 계속 녹음해야 하므로 녹음 재개
        }

        @Override
        public void onPartialResults(Bundle bundle) {
            Log.d("사용자 말 상태 : ", "onPartialResults : 중간중간");
        }

        @Override
        public void onEvent(int i, Bundle bundle) {

        }
    };

    // TTS 관련, Activity 종료시 사용한 TTS 객체 정리
    @Override
    public void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
            textToSpeech = null;
        }
        super.onDestroy();
    }
}
