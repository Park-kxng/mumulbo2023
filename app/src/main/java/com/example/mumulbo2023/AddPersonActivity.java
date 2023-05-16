package com.example.mumulbo2023;

import static com.example.mumulbo2023.MainActivity.personArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import java.util.ArrayList;

public class AddPersonActivity extends Activity {
    // 사람 추가하는 액티비티
    Button addPersonButton;
    RecyclerView recyclerView;
    public static PersonRecyclerViewAdapter adapter;

    //dialog.xml 연결해주는 View 선언하고,
    View v_d;
    EditText nameEditText;
    EditText numberEditText;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_person);

        recyclerView = findViewById(R.id.recyclerView);
        addPersonButton = findViewById(R.id.addPersonButton);

        //personArrayList.add(new OnePerson("정유진", "010111122222"));

        // 추가하기 버튼 눌렀을 때 다이얼로그그
       addPersonButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 다이얼로그 만들기
                AlertDialog.Builder d = new AlertDialog.Builder(AddPersonActivity.this);
                d.setTitle("아래에 원격접속 요청할 사람 정보 입력");
                //View.inflate 이용하여 그 뷰에 해당하는 것을 '구현/실행'해주고,
                v_d = (View) View.inflate(AddPersonActivity.this, R.layout.dialog, null);
                //실행한 것을 setView 함수로 전달.
                d.setView(v_d);

                d.setPositiveButton("입력",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                //dialog라는 레이아웃이 실제로 생성(inflate)되고 난 다음에, v_d라는 애를 통해서,
                                //그 안에 속한 것 중에서 찾아오는 것이 가능하다.
                                //setPositiveButton이 눌리게 되었을 때, dialog에 해당하는 우리가 만든 View (=v_d)로부터 et를 찾아옴
                                nameEditText = (EditText) v_d.findViewById(R.id.nameEditText);
                                numberEditText = (EditText) v_d.findViewById(R.id.numberEditText);

                                personArrayList.add(new OnePerson(nameEditText.getText().toString(),
                                                                    numberEditText.getText().toString()));
                                //사용자에게 입력받은 et1, et2를 tv1, tv2에 표시
                                //tv1.setText(et1.getText());
                                //tv2.setText(et2.getText());

                            }
                        });

                d.show();
            }
       });


        if (personArrayList!=null){
            // 정보 있을 때
            adapter = new PersonRecyclerViewAdapter(this, personArrayList);
            recyclerView.setAdapter(adapter);

            //레이아웃 매니저 연결
            recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        }else if(personArrayList==null){
            // 아무 정보 없을 때는 아무것도 안 보이게 함
            Log.d("정보 있나> ", "없음--------------");
        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        if (personArrayList!=null){
            // 정보 있을 때
            adapter = new PersonRecyclerViewAdapter(this, personArrayList);
            recyclerView.setAdapter(adapter);

            //레이아웃 매니저 연결
            recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        }else if(personArrayList==null){
            // 아무 정보 없을 때는 아무것도 안 보이게 함
            Log.d("정보 있나> ", "없음--------------");
        }
    }

}
