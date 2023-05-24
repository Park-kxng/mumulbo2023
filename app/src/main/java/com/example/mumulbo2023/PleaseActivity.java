package com.example.mumulbo2023;
import static com.example.mumulbo2023.MainActivity.personArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;

public class PleaseActivity extends Activity {
    ImageButton recordButton, sendButton;
    SpeechRecognizer speechRecognizer;
    Intent recordIntent;
    boolean recording = false;  //현재 녹음중인지 여부
    String userSTT = ""; // 음성 인식 결과
    EditText please_request;
    String request;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_please);
        recordButton = findViewById(R.id.recordButton);
        sendButton = findViewById(R.id.sendButton);
        please_request = findViewById(R.id.please_request);
        /* STT  */
        recordIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recordIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());
        recordIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");   //한국어

        recordButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!recording) {   //녹음 시작
                    //textToSpeech.stop();
                    //textToSpeech.shutdown();

                    recording = true;
                    startRecord();
                    please_request.setText("");
                    userSTT = "";
                }
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // 실제로는 내장 DB에서 전송 메시지 내용 가져오기
                // 전송 메시지: [ + <사용자 이름> + 님의 원격접속 해결 요청] + 요구사항 + 팀뷰어 다운로드 링크 + 사용자 퀵서포트 ID

                // 우선 테스트하려고 하드코딩 해놓음
                //String phoneNo = "01095033866"; // 소현이 번호

                //String sms1 = "[정유진님의 원격접속 해결 요청]\n" + request +  "\n 파트너 ID: 1 893 172 444";
                String sms1 = "[정유진님의 원격접속 해결 요청]\n 파트너 ID: 1 893 172 444";
                String sms2 = "https://www.teamviewer.com/ko/download/";
                
                // 연락처 저장해둔 곳에서 번호 가져와서 보냄
                for(int i=0; i < personArrayList.size(); i++){

                    sendMsg(personArrayList.get(i).getPerson_number(), sms1);
                    sendMsg(personArrayList.get(i).getPerson_number(), request);
                    sendMsg(personArrayList.get(i).getPerson_number(), sms2);
                }
                //sendMsg(phoneNo, sms1);
                //sendMsg(phoneNo, sms2);

                Intent intent = getPackageManager().getLaunchIntentForPackage("com.teamviewer.quicksupport.market");
                if (intent != null) {
                    startActivity(intent);
                } else {
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.teamviewer.quicksupport.market"));
                    startActivity(intent);
                }

            }
        });
    }

    // 녹음 관련
    // 녹음 시작
    void startRecord() {
        //recording = true;

        //recordButton.setText("음성 녹음 변환 중지");
        Toast.makeText(getApplicationContext(), "음성인식 시작", Toast.LENGTH_SHORT).show();
        Log.d("녹음 상태 startRecord()", "startRecord()--------------------");
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
        Log.d("녹음 상태 stopRecord()", "stopRecord()--------------------");
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
           // stopRecord();
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
                    Log.d("녹음 상태 ERROR_NO_MATCH", "ERROR_NO_MATCH에러다--------------------");
                    message = "찾을 수 없음";
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
            Log.d("에러가 발생하였습니다. : ", "에러다ㅏㅏㅏㅏㅏㅏㅏㅏㅏㅏㅏ"+message);
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
            please_request.setText(userSTT);    //기존의 text에 인식 결과 보여

            request = userSTT;


            recording = false;

            //recordingText.setText("녹음중? NO");
            // TTS 테스트용으로 녹음 종료시 녹음된걸 말해주는거 넣어둠
           // question = editText.getText().toString();  // 물어본 답변은 저장합니다.
            // 아이콘을 스피커 모양으로 변경합니다.
            //recordButton.setImageResource(R.drawable.icon_speak_mmb);
            //answer = true;
            Log.d("녹음 상태 onResults",  userSTT);
            //stopRecord();

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
    void sendMsg(String phoneNo, String sms){
        try {
            //전송
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, sms, null, null);
            Toast.makeText(getApplicationContext(), "전송을 완료하였습니다.", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "전송 과정에 오류가 발생하였습니다. 다시 시도해주세요.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

}
