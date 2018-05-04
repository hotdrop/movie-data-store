import jp.hotdrop.mds.repository.ex.toDateStr
import jp.hotdrop.mds.repository.ex.toEpoch
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class DateTest {

    @Test
    fun stringToDateEpochTest() {
        val epoch = "2017/02/13".toEpoch()
        println(epoch)
        assert(epoch == "1486911600000")

        val nullEpoch = "".toEpoch() ?: "nullです"
        assert(nullEpoch == "nullです")

        val inCorrectEpoch = "aiueo".toEpoch() ?: "nullです"
        assert(inCorrectEpoch == "nullです")
    }

    @Test
    fun dateEpochToStringTest() {
        val dateStr = "1486911600000".toDateStr()
        assert(dateStr == "2017/02/13")
    }
}