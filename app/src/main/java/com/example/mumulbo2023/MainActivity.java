package com.example.mumulbo2023;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button_ask =  (Button) findViewById(R.id.button_ask);
        Button button_act =  (Button) findViewById(R.id.button_act);
        Button button_please =  (Button) findViewById(R.id.button_please);

        // 접근 권한 요청, 권한을 부여할 권한 지정하는 부분
        // Manifest에 퍼미션 추가하고 여기에다가 권한 필요한거 싹다 집어넣으면 된다
        String[] permissions = {
                //android.Manifest.permission.READ_EXTERNAL_STORAGE,
                //android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                //android.Manifest.permission.INTERNET,
                android.Manifest.permission.RECORD_AUDIO
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

        button_please.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(getApplicationContext(), PleaseActivity.class);
                startActivity(intent);
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