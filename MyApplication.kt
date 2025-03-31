package com.example.jsondb

import HistoryDatabaseHelper
import android.app.Application

class MyApplication : Application() {
    // Application 객체가 생성될 때 db 초기화
    // Application 객체는 앱의 전체 생애 주기를 관리
    // 앱 전체에서 공유하는 자원 관리 가능 (전역상태)
    // 시스템 앱이 실행될 때 한번만 DB를 생성
    // lateinit 키워드로 나중에 초기화한다는 것을 보장
    // private set은 dbHelper의 값을 외부에서 변경할 수 없도록 설정
    // Manifest에 android:name=".MyApplication" 필요
    lateinit var historyDbHelper: HistoryDatabaseHelper
        private set

    lateinit var changesDbHelper: ChangesDatabaseHelper
        private set

    // Application 클래스의 메서드
    override fun onCreate(){
        super.onCreate()
        historyDbHelper = HistoryDatabaseHelper.getInstance(this)
        changesDbHelper = ChangesDatabaseHelper.getInstance(this)
    }

}
