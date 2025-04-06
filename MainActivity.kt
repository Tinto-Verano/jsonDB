package com.example.jsondb

import HistoryDatabaseHelper
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.*
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader

class MainActivity : AppCompatActivity() {
    private var FLAG: Boolean = false
    private lateinit var db: AppDatabase
    private lateinit var dao: HistoryDao

    private val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = AppDatabase.getInstance(this)
        dao = db.historyDao()

        val button: Button = findViewById(R.id.button_load)
        val buttonShow: Button = findViewById(R.id.button_show)
        val buttonReset: Button = findViewById(R.id.button_reset)
        val buttonToggleFlag: Button = findViewById(R.id.button_toggle_flag)

        button.setOnClickListener {
            lifecycleScope.launch {
                val jsonObject = loadJsonFromAssets("history.json")
                Log.d("MainActivity", "불러온 JSON: $jsonObject")

                if (dao.isEmpty()) {
                    jsonObject?.let {
                        val imei = it.optString("IMEI")
                        val info = it.optJSONObject("information")

                        val entity = HistoryEntity(
                            IMEI = imei,
                            AP = info?.optString("AP") ?: "",
                            CAMERA = info?.optString("CAMERA") ?: "",
                            OCTA = info?.optString("OCTA") ?: ""
                        )

                        dao.insert(entity)
                        Log.d("RoomDB", "DB 저장 완료")

                        val list = dao.getAll()
                        withContext(Dispatchers.Main) {
                            list.forEach {
                                Log.d("RoomDB", it.toString())
                            }
                        }
                    }
                } else {
                    val latest = dao.getLatest()
                    latest?.let {
                        val json = it.toJson()
                        Log.d("RoomDB", "최근 데이터 JSON 변환: $json")
                    }
                }

                if (FLAG && jsonObject != null) {
                    val info = jsonObject.getJSONObject("information")
                    info.put("CAMERA", "changed")
                    Log.d("MainActivity", "수정된 JSON: $jsonObject")
                }
            }
        }


        buttonShow.setOnClickListener {
            coroutineScope.launch {
                val list = dao.getAll()
                list.forEach {
                    Log.d("RoomDB", it.toString())
                }
            }
        }

        buttonReset.setOnClickListener {
            coroutineScope.launch {
                dao.clear()
                Log.d("RoomDB", "DB 초기화 완료")
            }
        }

        buttonToggleFlag.setOnClickListener {
            FLAG = !FLAG
            Log.d("MainActivity", "FLAG 값 변경: $FLAG")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel() // 코루틴 정리
    }

    private suspend fun loadJsonFromAssets(fileName: String): JSONObject? = withContext(Dispatchers.IO) {
        try {
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

    fun HistoryEntity.toJson(): JSONObject {
        val json = JSONObject()
        json.put("IMEI", IMEI)

        val info = JSONObject()
        info.put("AP", AP)
        info.put("CAMERA", CAMERA)
        info.put("OCTA", OCTA)

        json.put("information", info)

        return json
    }
}
