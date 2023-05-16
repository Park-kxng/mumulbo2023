package com.example.mumulbo2023;

// 연락처 하나 구성하는 객체
public class OnePerson {
    //int person_id; // 구분용
    String person_name; // 사람 이름
    String person_number; // 전화번호

    public OnePerson(String person_name, String person_number){
        this.person_name = person_name;
        this.person_number = person_number;
    }



    public String getPerson_name() {
        return person_name;
    }

    public void setPerson_name(String person_name) {
        this.person_name = person_name;
    }

    public String getPerson_number() {
        return person_number;
    }

    public void setPerson_number(String person_number) {
        this.person_number = person_number;
    }
}
