package com.example.mumulbo2023;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.renderscript.Sampler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

// 챗 지피티
import okhttp3.*;

public class AskMmbActivity extends Activity {
    ImageButton recordButton; // STT 시작 및 종료하는 버튼
    ImageView faceImage; // TTS 시작할 때 웃는 얼굴로 바뀜
    TextView answerText, recordingText; // TTS로 음성으로 나오기도 하지만 텍스트로도 표시하기 위함
    // STT 녹음 관련
    SpeechRecognizer speechRecognizer;
    Intent recordIntent;
    boolean recording = false;  //현재 녹음중인지 여부
    String userSTT=""; // 음성 인식 결과
    EditText editText; // ▶ STT 잘 되고 있는지 확인 용도 최종 결과에선 삭제해도 됨
    // TTS 텍스트를 음성으로 관련
    TextToSpeech textToSpeech;


    // Chat GPT 관련
    Handler handler = new Handler(Looper.getMainLooper());
    String temp ="";
    String question = "";

    boolean answer = false;  //현재 녹음중인지 여부

    String gptTTS=""; // GPT 검색 결과
    String result_copy ="";

    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    OkHttpClient client = new OkHttpClient().newBuilder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build();

    private static final String MY_SECRET_KEY = "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";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask_mmb);

        // 객체 생성
        recordButton = findViewById(R.id.imageButton);
        faceImage = findViewById(R.id.imageView4);
        answerText = findViewById(R.id.speakText); // ▶ 챗 지피티 답변을 받는 변수
        recordingText = findViewById(R.id.textView6); // ▶ 녹음 중인지 확인 용도 나중에 빼도 됨
        editText = findViewById(R.id.editText); // ▶ STT 잘 되고 있는지 확인 용도 최종 결과에선 삭제해도 됨
        // 챗 지피티 관련 객체 생성


        /* STT  */
        recordIntent=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recordIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,getPackageName());
        recordIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"ko-KR");   //한국어
        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!answer){ // 챗 지피티 답변 시작
                    question = editText.getText().toString();  // 물어본 답변은 저장합니다.
                    //챗 지피티에게 물어보고 답변을 gptTTS에 저장합니다.
                    System.out.println(question);
                    gptTTS = callAPI("이 핸드폰의 기종은 안드로이드입니다"+question);
                    Toast.makeText(getApplicationContext(), temp, Toast.LENGTH_SHORT).show();
                    System.out.println("gptTTS : "+ gptTTS);

                    System.out.println("temp : "+temp);
                    textToSpeech.setPitch(1.0f); // 높낮이
                    textToSpeech.setSpeechRate(1.0f); // 바르기
                    textToSpeech.speak(gptTTS, TextToSpeech.QUEUE_FLUSH, null);
                    answerText.setText(gptTTS); // 답변 부분에 출력
                    answer = true; // 챗 지피티가 답변을 완료하였습니다.
                }
                else{ // 녹음 시작
                    if (!recording) {   //녹음 시작
                        recording = true;
                        startRecord();
                        Toast.makeText(getApplicationContext(), "지금부터 무엇이든 물어보세요!", Toast.LENGTH_SHORT).show();
                        recordingText.setText("녹음중? YES");
                        // 기존에 있던 애들 초기화
                        answerText.setText("");
                        editText.setText("");
                        userSTT ="";
                        gptTTS = "";
                    }
                    else {  //이미 녹음 중이면 녹음 중지
                        recording = false;
                        stopRecord();
                        recordingText.setText("녹음중? NO");
                        // TTS 테스트용으로 녹음 종료시 녹음된걸 말해주는거 넣어둠
                        question = editText.getText().toString();  // 물어본 답변은 저장합니다.
                        answer = false; // 챗 지피티가 답변을 해야 한다는 표시

                    }
                }

            }
        }
        );

        /* TTS  */
        // TTS 객체 초기화
        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status!=android.speech.tts.TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.KOREAN);
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
        speechRecognizer=SpeechRecognizer.createSpeechRecognizer(getApplicationContext());
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
            //사용자가 말을 멈추면 호출
            //인식 결과에 따라 onError나 onResults가 호출됨
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
            ArrayList<String> matches = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);	//인식 결과를 담은 ArrayList


           for (int i = 0; i < matches.size() ; i++) {
                userSTT += matches.get(i);
            }

            editText.setText(userSTT);	//기존의 text에 인식 결과 보여
            speechRecognizer.startListening(recordIntent);    //녹음버튼을 누를 때까지 계속 녹음해야 하므로 녹음 재개
        }

        @Override
        public void onPartialResults(Bundle bundle) {

        }

        @Override
        public void onEvent(int i, Bundle bundle) {

        }
    };

    // TTS 관련, Activity 종료시 사용한 TTS 객체 정리
    @Override public void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
            textToSpeech = null;
        }
        super.onDestroy();
    }

    String callAPI(String question){
        //okhttp
        // 챗 지피티가 답변하기 전, 말할 부분을 표시하기 위해 원래 있던 부분을 초기화합니다.
        editText.setText("");
        //추가된 내용
        JSONArray arr = new JSONArray();
        JSONObject baseAi = new JSONObject();
        JSONObject userMsg = new JSONObject();
        try {
            //AI 속성설정
            baseAi.put("role", "user");
            baseAi.put("content", "You are a helpful and kind AI Assistant.");
            //유저 메세지
            userMsg.put("role", "user");
            userMsg.put("content", question);
            //array로 담아서 한번에 보낸다
            arr.put(baseAi);
            arr.put(userMsg);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        //
        JSONObject object = new JSONObject();
        try {
            object.put("model", "gpt-3.5-turbo");
            object.put("messages", arr);
        } catch (JSONException e){
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(object.toString(), JSON);
        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")
                .header("Authorization", "Bearer "+MY_SECRET_KEY)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Toast.makeText(getApplicationContext(), "에러가 발생하였습니다. : " , Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(response.isSuccessful()){
                    JSONObject jsonObject = null;
                    try {
                        //아래 body().toString()이 아니라 .string() 주의
                        jsonObject = new JSONObject(response.body().string());
                        JSONArray jsonArray = jsonObject.getJSONArray("choices");
                        JSONObject value = jsonArray.getJSONObject(0).getJSONObject("message");
                        result_copy = value.getString("content");
                        //Toast.makeText(getApplicationContext(), result.trim(), Toast.LENGTH_SHORT).show();
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                } else {
                    addResponse("Failed to load response due to "+response.body().string());
                    result_copy = response.body().string();


                }
            }
        });
        return result_copy;
    }
    void addResponse(String response){
        temp = response;
    }



}
