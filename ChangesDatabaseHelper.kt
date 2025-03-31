package com.example.jsondb

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class ChangesDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
    companion object {
        private const val DB_NAME = "changes.db"
        private const val DB_VERSION = 1
        private const val TABLE_NAME = "changes"
        private const val COLUMN_ID = "id"
        private const val COLUMN_PART = "part"
        private const val COLUMN_NEW_VALUE = "new_value"
        private const val COLUMN_DATE = "date"

        @Volatile
        private var instance: ChangesDatabaseHelper? = null

        // 싱글턴 인스턴스를 반환하는 메서드
        fun getInstance(context: Context): ChangesDatabaseHelper {
            return instance ?: synchronized(this) {
                instance ?: ChangesDatabaseHelper(context.applicationContext).also { instance = it }
            }
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = """
            CREATE TABLE $TABLE_NAME (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_PART TEXT NOT NULL,
                $COLUMN_NEW_VALUE TEXT NOT NULL,
                $COLUMN_DATE TEXT NOT NULL
            )
        """.trimIndent()
        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun insertChange(part: String, newValue: String, date: String) {
        val db = writableDatabase
        val values = ContentValues()

        values.put(COLUMN_PART, part)
        values.put(COLUMN_NEW_VALUE, newValue)
        values.put(COLUMN_DATE, date)

        db.insert(TABLE_NAME, null, values)
        db.close()
    }

    fun getAllChanges() {
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_NAME"
        val cursor = db.rawQuery(query, null)

        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
            val part = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PART))
            val newValue = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NEW_VALUE))
            val date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE))

            Log.d("ChangesDatabaseHelper", "ID: $id, PART: $part, NEW_VALUE: $newValue, DATE: $date")
        }
        cursor.close()
        db.close()
    }

    fun clearDatabase(){
        val db = writableDatabase
        db.execSQL("DELETE FROM $TABLE_NAME")
        db.close()
    }
}
