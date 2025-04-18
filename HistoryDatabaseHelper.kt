import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import org.json.JSONObject

// 싱글턴 패턴
// SQLiteOpenHelper 객체를 하나만 생성하여 데이터베이스에 접근
// private constructor -> 외부에서 인스턴스 생성을 막았다
class HistoryDatabaseHelper (context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
    // 동반 객체 -> 클래스의 인스턴스 없이 호출할 수 있는 기능 제공 (싱글턴의 핵심)
    companion object {
        const val DB_NAME = "history.db"
        private const val DB_VERSION = 1
        private const val TABLE_NAME = "history"
        private const val COLUMN_ID = "id"
        private const val COLUMN_IMEI = "IMEI"
        private const val COLUMN_AP = "AP"
        private const val COLUMN_CAMERA = "CAMERA"
        private const val COLUMN_OCTA = "OCTA"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = """
            CREATE TABLE $TABLE_NAME (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_IMEI TEXT NOT NULL,
                $COLUMN_AP TEXT NOT NULL,
                $COLUMN_CAMERA TEXT NOT NULL,
                $COLUMN_OCTA TEXT NOT NULL
            )
        """.trimIndent()
        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun insertHistory(jsonObject: JSONObject) {
        val db = writableDatabase
        val values = ContentValues()

        val imei = jsonObject.optString("IMEI", "")
        val information = jsonObject.optJSONObject("information")

        val ap = information?.optString("AP", "") ?: ""
        val camera = information?.optString("CAMERA", "") ?: ""
        val octa = information?.optString("OCTA", "") ?: ""

        values.put(COLUMN_IMEI, imei)
        values.put(COLUMN_AP, ap)
        values.put(COLUMN_CAMERA, camera)
        values.put(COLUMN_OCTA, octa)

        db.insert(TABLE_NAME, null, values)
        db.close()
    }

    fun getAllHistory(): String {
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_NAME"
        val cursor = db.rawQuery(query, null)

        val result = StringBuilder()

        if (cursor.moveToFirst()) { // 🔥 커서 첫 번째 위치로 이동
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
                val imei = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMEI))
                val ap = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_AP))
                val camera = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CAMERA))
                val octa = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_OCTA))

                result.append("ID: $id, IMEI: $imei, AP: $ap, CAMERA: $camera, OCTA: $octa\n")
                //Log.d("HistoryDatabaseHelper", "ID: $id, IMEI: $imei, AP: $ap, CAMERA: $camera, OCTA: $octa")
            } while (cursor.moveToNext()) // 🔥 다음 데이터가 있으면 이동
        }

        cursor.close()
        db.close()
        return result.toString()
    }

    fun clearDatabase(){
        val db = writableDatabase
        db.execSQL("DELETE FROM $TABLE_NAME")
        db.execSQL("DELETE FROM sqlite_sequence WHERE name='$TABLE_NAME'")
        db.close()
    }
}
