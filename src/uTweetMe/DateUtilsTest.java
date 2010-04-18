/*
 * DateUtilsTest.java
 * JMUnit based test
 *
 * Created on 08.03.2009, 10:27:38
 */

package uTweetMe;


import jmunit.framework.cldc10.*;

/**
 * @author Alexey
 */
public class DateUtilsTest extends TestCase {
    
    public DateUtilsTest() {
        //The first parameter of inherited constructor is the number of test cases
        super(1,"DateUtilsTest");
    }            

    public void test(int testNumber) throws Throwable {
      switch (testNumber) {
         case 0:
            testParseDate();
            break;
         default:
            break;
      }
    }

   /**
    * Test of testParseDate method, of class DateUtils.
    */
   public void testParseDate() throws AssertionFailedException {
      String dateString = "Sun Mar 08 01:20:09 +0000 2009";
      long expResult = 1236475209000L;
      long result_1 = DateUtils.ParseDate(dateString);
      assertEquals(expResult, result_1);
   }
}
