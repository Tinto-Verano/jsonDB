package com.example.jsondb

import HistoryDatabaseHelper
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader

class MainActivity : AppCompatActivity() {
    private var FLAG: Boolean = false
    private lateinit var historyDatabaseHelper: HistoryDatabaseHelper
    private lateinit var changesDatabaseHelper: ChangesDatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Application 클래스에서 dbHelper 가져오기
        historyDatabaseHelper = (application as MyApplication).historyDbHelper
        changesDatabaseHelper = (application as MyApplication).changesDbHelper

        // DB 경로 확인 로그 추가
        val dbPath = getDatabasePath(HistoryDatabaseHelper.DB_NAME).absolutePath
        Log.d("MainActivity", "Database Path: $dbPath")

        val button: Button = findViewById(R.id.button_load)
        val buttonShow: Button = findViewById(R.id.button_show)
        val buttonReset: Button = findViewById(R.id.button_reset)
        val buttonToggleFlag: Button = findViewById(R.id.button_toggle_flag)

        button.setOnClickListener {
            val jsonObject = loadJsonFromAssets("history.json")

            if (FLAG && jsonObject != null) {
                val information = jsonObject.getJSONObject("information")
                information.put("CAMERA", "changed")

                // JSON 변경 확인
                Log.d("MainActivity", "수정된 JSON: ${jsonObject.toString()}")
            }

                // JSON 수정이 반영되었는지 확인
            Log.d("MainActivity", "수정된 JSON: ${jsonObject.toString()}")

            if (jsonObject != null) {
                historyDatabaseHelper.insertHistory(jsonObject)
                Log.d("MainActivity", "JSON 데이터가 DB에 저장되었습니다.")

                // 저장된 데이터 출력
                historyDatabaseHelper.getAllHistory()
            }
        }

        buttonShow.setOnClickListener {
            historyDatabaseHelper.getAllHistory()
        }


        buttonReset.setOnClickListener {
            historyDatabaseHelper.clearDatabase() // 🔥 테이블 초기화
            Log.d("MainActivity", "DB 초기화 완료!")
        }

        buttonToggleFlag.setOnClickListener {
            FLAG = !FLAG // 🔥 FLAG 상태 변경
            Log.d("MainActivity", "FLAG 값 변경: $FLAG")
        }
    }

    private fun loadJsonFromAssets(fileName: String): JSONObject? {
        return try {
            val inputStream = assets.open(fileName)
            val reader = BufferedReader(InputStreamReader(inputStream))
            val jsonString = reader.readText()
            reader.close()
            JSONObject(jsonString)
        } catch (e: Exception) {
            Log.e("MainActivity", "JSON 파일 읽기 오류: ${e.message}")
            null
        }
    }
}