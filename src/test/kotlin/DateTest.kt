import jp.hotdrop.mds.repository.ex.toDateEpoch
import jp.hotdrop.mds.repository.ex.toDateStr
import jp.hotdrop.mds.repository.ex.toDateTimeEpoch
import jp.hotdrop.mds.repository.ex.toDateTimeStr
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@RunWith(JUnit4::class)
class DateTest {

    @Test
    fun stringToDateEpochTest() {
        val dateEpoch = "2017/02/13".toDateEpoch()
        val dateTimeEpoch = "2017/02/13 11:43:34".toDateTimeEpoch()

        println("dateEpoch=$dateEpoch")
        println("dateTimeEpoch=$dateTimeEpoch")

        assert(dateEpoch == 17210L)
        assert(dateTimeEpoch == 1486953814L)

        val nowTimeEpoch = LocalDateTime.now().toEpochSecond(ZoneOffset.ofHours(9))
        println("おまけ　nowTimeEpoch=$nowTimeEpoch")
    }

    @Test
    fun dateEpochToStringTest() {
        val dateStr = 17210L.toDateStr()
        val dateTimeStr = 1486953814L.toDateTimeStr()

        println("dateStr=$dateStr")
        println("dateTimeStr=$dateTimeStr")

        assert(dateStr == "2017/02/13")
        assert(dateTimeStr == "2017/02/13 11:43:34")
    }

    @Test
    fun rangeTest() {
        val dtf = DateTimeFormatter.ofPattern("uuuu/MM/dd")
        val currentLocalDate = LocalDate.parse("2017/05/05", dtf)
        val currentEpoch =  currentLocalDate.toEpochDay()
        val twoMonthAgoEpoch = currentLocalDate.minusMonths(2L).toEpochDay()

        val rangeInEpoch = "2017/04/12".toDateEpoch()
        val outOfRangeEpoch = "2017/03/04".toDateEpoch()
        val overRangeEpoch = "2017/05/06".toDateEpoch()

        assert((rangeInEpoch in twoMonthAgoEpoch..currentEpoch))
        assert((outOfRangeEpoch !in twoMonthAgoEpoch..currentEpoch))
        assert((overRangeEpoch !in twoMonthAgoEpoch..currentEpoch))
    }
}