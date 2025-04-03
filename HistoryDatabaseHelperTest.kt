import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import io.mockk.*
import junit.framework.TestCase.assertEquals
import org.junit.After
import org.junit.Before
import org.junit.Test

class HistoryDatabaseHelperTest {
    private lateinit var dbHelper: HistoryDatabaseHelper
    private val db = mockk<SQLiteDatabase>(relaxed = true)
    private val cursor = mockk<Cursor>(relaxed = true)

    @Before
    fun setup() {
        dbHelper = spyk(HistoryDatabaseHelper(mockk()))
        every { dbHelper.readableDatabase } returns db
        every { db.rawQuery(any(), any()) } returns cursor
    }

    @Test
    fun testGetAllHistory_whenCursorIsEmpty() {
        every { cursor.moveToFirst() } returns false

        dbHelper.getAllHistory()
        println("Stop") // moveToFirst()가 false이면 "done!" 출력
    }

    @Test
    fun testGetAllHistory_whenCursorHasData() {
        every { cursor.moveToFirst() } returns true
        every { cursor.moveToNext() } returns false // 한 번만 실행 후 종료
        every { cursor.getInt(any()) } returns 1
        every { cursor.getString(any()) } returns "test"

        val result = dbHelper.getAllHistory()
        assertEquals("ID: 1, IMEI: test, AP: test, CAMERA: test, OCTA: test\n", result)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }
}