package project.prisma.starnotifier;

import android.content.SharedPreferences;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ReadDataTest {
    public static final String mypreference = "mypref";
    public static final String TimeStamp = "timStampKey";
    public static final String FirstRun = "firstRun";
    ReadData readData = new ReadData();
    SharedPreferences sharedpreferences;

    Long randomLong = 100L;

    @Test
    public void setTimeStampTest(){ }

    @Test
    public void returnLatestTsTest(){ }

    @Test
    public void downloadAndcheckLastTsTest(){
        assertFalse(readData.downloadAndCheckLastTs(0L));
    }


}