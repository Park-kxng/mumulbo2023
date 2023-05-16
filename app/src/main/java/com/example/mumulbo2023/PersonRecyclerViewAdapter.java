package com.example.mumulbo2023;

import static com.example.mumulbo2023.MainActivity.personArrayList;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class PersonRecyclerViewAdapter extends RecyclerView.Adapter {
    /*
  어댑터의 동작원리 및 순서
  1.(getItemCount) 데이터 개수를 세어 어댑터가 만들어야 할 총 아이템 개수를 얻는다.
  2.(getItemViewType)[생략가능] 현재 itemview의 viewtype을 판단한다
  3.(onCreateViewHolder)viewtype에 맞는 뷰 홀더를 생성하여 onBindViewHolder에 전달한다.
  4.(onBindViewHolder)뷰홀더와 position을 받아 postion에 맞는 데이터를 뷰홀더의 뷰들에 바인딩한다.
  */
    String TAG = "RecyclerViewAdapter";
    //리사이클러뷰에 넣을 데이터 리스트
    ArrayList<OnePerson> dataModels;
    Context context;
    View edit_v_d;
    EditText editNameEditText;
    EditText editNumberEditText;

    //생성자를 통하여 데이터 리스트 context를 받음
    public PersonRecyclerViewAdapter(Context context, ArrayList<OnePerson> dataModels) {
        this.dataModels = dataModels;
        this.context = context;
    }

    public int getItemCount() {
        //데이터 리스트의 크기를 전달해주어야 함
        return dataModels.size();
    }

    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder");
        //자신이 만든 itemview를 inflate한 다음 뷰홀더 생성
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.one_person_info_view, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        //생선된 뷰홀더를 리턴하여 onBindViewHolder에 전달한다.
        return viewHolder;
    }

    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Log.d(TAG, "onBindViewHolder");
        MyViewHolder myViewHolder = (MyViewHolder) holder;
        myViewHolder.personNameTextView.setText(dataModels.get(position).getPerson_name());
        myViewHolder.personNumberTextView.setText(dataModels.get(position).getPerson_number());

        //▼ 리사이클러 내의 아이템 클릭시 동작하는 부분

        myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(context, position+"번째 아이템 클릭", Toast.LENGTH_SHORT).show();
                // 인텐트로 넘겨줘야 하는 부분
                //Intent intent = new Intent(myViewHolder.itemView.getContext(), DiarySelectedActivity.class);
                //intent.putExtra("clickPosition", position); // position으로 array접근해서 보여주기
                /*
                intent.putExtra("title", dataModels.get(position).getTitle());
                intent.putExtra("date", dataModels.get(position).getDate());
                intent.putExtra("feel", dataModels.get(position).getFeel());
                intent.putExtra("uri", dataModels.get(position).getPicture_uri());
                intent.putExtra("text", dataModels.get(position).getText());

                 */
                //ContextCompat.startActivity(myViewHolder.itemView.getContext(), intent, null);

                // 다이얼로그 만들기
                AlertDialog.Builder d = new AlertDialog.Builder(context);

                d.setTitle("아래에 원격접속 요청할 사람 정보 입력");
                //View.inflate 이용하여 그 뷰에 해당하는 것을 '구현/실행'해주고,
                edit_v_d = (View) View.inflate(context, R.layout.dialog, null);
                //실행한 것을 setView 함수로 전달.
                d.setView(edit_v_d);
                //dialog라는 레이아웃이 실제로 생성(inflate)되고 난 다음에, v_d라는 애를 통해서,
                //그 안에 속한 것 중에서 찾아오는 것이 가능하다.
                //setPositiveButton이 눌리게 되었을 때, dialog에 해당하는 우리가 만든 View (=v_d)로부터 et를 찾아옴
                editNameEditText = (EditText) edit_v_d.findViewById(R.id.nameEditText);
                editNumberEditText= (EditText) edit_v_d.findViewById(R.id.numberEditText);
                editNameEditText.setText(dataModels.get(position).getPerson_name());
                editNumberEditText.setText(dataModels.get(position).getPerson_number());
                d.setPositiveButton("수정",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {


                                //dataModels.get(position).setPerson_name(editNameEditText.getText().toString());
                                //dataModels.get(position).setPerson_number(editNumberEditText.getText().toString());
                                personArrayList.get(position).setPerson_name(editNameEditText.getText().toString());
                                personArrayList.get(position).setPerson_number(editNumberEditText.getText().toString());

                                //사용자에게 입력받은 et1, et2를 tv1, tv2에 표시
                                //tv1.setText(et1.getText());
                                //tv2.setText(et2.getText());
                                Intent intent = new Intent(myViewHolder.itemView.getContext(), AddPersonActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                ContextCompat.startActivity(myViewHolder.itemView.getContext(), intent, null);

                            }
                        });
                d.setNeutralButton("삭제", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        personArrayList.remove(position);
                        Intent intent = new Intent(myViewHolder.itemView.getContext(), AddPersonActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        ContextCompat.startActivity(myViewHolder.itemView.getContext(), intent, null);
                    }
                });

                d.show();
            }
        });


    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView personNameTextView, personNumberTextView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            personNameTextView = itemView.findViewById(R.id.personNameTextView);
            personNumberTextView = itemView.findViewById(R.id.personNumberTextView);
        }
    }

}
