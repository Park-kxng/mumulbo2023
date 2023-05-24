package com.example.mumulbo2023;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.content.Intent;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    String phoneNo;
    String sms;

    // 연락처는 여기에서 관리됨. static으로 여러곳에서 접근.
    public static ArrayList<OnePerson> personArrayList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        personArrayList = new ArrayList<>();

        Button button_ask =  (Button) findViewById(R.id.button_ask);
        Button button_act =  (Button) findViewById(R.id.button_act);
        Button button_please =  (Button) findViewById(R.id.button_please);
        Button addPersonMenuButton = (Button) findViewById(R.id.addPersonMenuButton);

        Button testPky = (Button) findViewById(R.id.pky_test);
        Button testPsh = (Button) findViewById(R.id.psh_test);



        // 접근 권한 요청, 권한을 부여할 권한 지정하는 부분
        // Manifest에 퍼미션 추가하고 여기에다가 권한 필요한거 싹다 집어넣으면 된다
        String[] permissions = {
                //android.Manifest.permission.READ_EXTERNAL_STORAGE,
                //android.Manifest.permission.INTERNET,
                android.Manifest.permission.RECORD_AUDIO,
                android.Manifest.permission.SEND_SMS,
                android.Manifest.permission.SET_ALARM,
                android.Manifest.permission.SYSTEM_ALERT_WINDOW,
                android.Manifest.permission.FOREGROUND_SERVICE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE

        };
        checkPermissions(permissions); // 권한 허용할 것인지 물어보는 것 부분 함수


        button_ask.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(getApplicationContext(), AskMmbActivity.class);
                startActivity(intent);
            }
        });

        button_act.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(getApplicationContext(), ActMmbActivity.class);
                startActivity(intent);
            }
        });

        addPersonMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddPersonActivity.class);
                startActivity(intent);
            }
        });

        // 원격 접속을 위한 메시지 전송하기
        button_please.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(getApplicationContext(), PleaseActivity.class);
                startActivity(intent);
                // 실제로는 내장 DB에서 전송 메시지 내용 가져오기
                // 전송 메시지: [ + <사용자 이름> + 님의 원격접속 해결 요청] + 요구사항 + 팀뷰어 다운로드 링크 + 사용자 퀵서포트 ID

                // 우선 테스트하려고 하드코딩 해놓음
                //phoneNo = "01095033866"; // 소현이 번호, 이 형태로 해야 오는지 010-9503-3866 (아래 70번째 줄에 +82 삭제)로 해야 오는지 테스트 필요

                //sms = "[박소현님의 원격접속 해결 요청]\n 건강보험자격득실 확인서 발급 어떻게 받니 \n 파트너 ID: 1 794 040 464";
                //sms = "https://www.teamviewer.com/ko/download/";
                //try {
                    //전송
                //    SmsManager smsManager = SmsManager.getDefault();
               //     smsManager.sendTextMessage(phoneNo, null, sms, null, null);
                //    Toast.makeText(getApplicationContext(), "전송을 완료하였습니다.", Toast.LENGTH_LONG).show();
                //} catch (Exception e) {
               //     Toast.makeText(getApplicationContext(), "전송 과정에 오류가 발생하였습니다. 다시 시도해주세요.", Toast.LENGTH_LONG).show();
               //     e.printStackTrace();
               // }
            }
        });
        testPky.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(getApplicationContext(), PkyTestActivity.class);
                startActivity(intent);
            }
        });
        testPsh.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(getApplicationContext(), PshTestActivity.class);
                startActivity(intent);

                finish();
            }
        });
    }


    // 앱을 맨 처음 실행했을 때 권한 permission 허용을 요청하는 함수
    public void checkPermissions(String[] permissions) {
        // premission들을 string 배열로 가지고 있는 위험 권한 permissions를 받아옴. 외부 저장장치 읽기
        ArrayList<String> targetList = new ArrayList<String>();
        for (int i = 0; i < permissions.length; i++) {
            String curPermission = permissions[i]; // 현재 요청할 permission을 curPermission에 넣고
            int permissionCheck = ContextCompat.checkSelfPermission(this, curPermission); // 현재 앱에서 권한이 있는지를 permissionCheck에 넣음
            //ContextCompat.checkSelfPermission()를 사용하여 앱에 이미 권한을 부여 받았는지 확인
            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                // 호출 결과로 PERMISSION_GRANTED or PERMISSION_DENIED 반환 받은 것을 확인
                // 초기 실행에서 권한 허용을 한 후에 다시 앱을 실행했을 때는 이미 권한이 있어서 아래와 같은 토스트 메세지를 띄워줌
                Toast.makeText(this, curPermission + " 권한 있음", Toast.LENGTH_SHORT).show();
            } else {
                // 만약 권한 설정이 허용되어 있지 않은 경우 권한 없음이 토스트 메세지로 뜨고
                Toast.makeText(this, curPermission + " 권한 없음", Toast.LENGTH_SHORT).show();
                // shouldShowRequestPermissionRationale는 사용자가 이전에 권한 요청을 거절했었을 때 true를 리턴하고
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, curPermission)) {
                    //Toast.makeText(this, curPermission + " 권한 설명 필요함.", Toast.LENGTH_SHORT).show();
                    Toast.makeText(this, "이번 거절시 더이상 물어보지 않습니다 -> 권한 없어 기능을 사용할 수 없음", Toast.LENGTH_SHORT).show();
                    // 거절을 2번 하면 이후는 물어보지 않음으로 안내 문구를 보여줌
                    targetList.add(curPermission);
                    // 권한 부여할 용도인 targetList에 현재 물어본 curPermission 넣음
                } else {
                    targetList.add(curPermission);
                }
            }
        }

        String[] targets = new String[targetList.size()];
        targetList.toArray(targets);

        for (int i=0; i< targets.length; i++){
            int permissionCheck = ContextCompat.checkSelfPermission(this, targets[i]); // 현재 앱에서 권한이 있는지를 permissionCheck에 넣음
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                // 위험 권한이 아직 허용되지 않은 상태인데 위에서 허용해달라고 눌렀으면 이리로 오게 됨.
                // 위험 권한 허용을 요청해서 이제 기능을 쓸 수 있음
                ActivityCompat.requestPermissions(this, targets, 101);
            }
        }
    }




}