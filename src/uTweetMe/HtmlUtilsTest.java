/*
 * HtmlUtilsTest.java
 * JMUnit based test
 *
 * Created on 18.02.2009, 0:22:27
 */

package uTweetMe;


import jmunit.framework.cldc10.*;

/**
 * @author Alexey
 */
public class HtmlUtilsTest extends TestCase {
    
    public HtmlUtilsTest() {
        //The first parameter of inherited constructor is the number of test cases
        super(4,"HtmlUtilsTest");
    }            

    public void test(int testNumber) throws Throwable {
      switch (testNumber) {
         case 0:
            testUnescape();
            break;
         case 1:
            testStripHtmlTags();
            break;
         case 2:
            testUrlParse();
            break;
         case 3:
            testExtractTextInTag();
            break;
         default:
            break;
      }
    }

   /**
    * Test of testUnescape method, of class HtmlUtils.
    */
   public void testUnescape() throws AssertionFailedException {
      System.out.println("Unescape");
      //char c = 1234;
      //c = (char) (c - 1);
      HtmlUtils instance = HtmlUtils.GetInstance();
      String str_1 = "&#1089;&#1098;&#1077;&#1096;&#1100; &#1077;&#1097;&#1105; &#1101;&#1090;&#1080;&#1093; &#1084;&#1103;&#1075;&#1082;&#1080;&#1093; &#1092;&#1088;&#1072;&#1085;&#1094;&#1091;&#1079;&#1089;&#1082;&#1080;&#1093; &#1073;&#1091;&#1083;&#1086;&#1082; &#1076;&#1072; &#1074;&#1099;&#1087;&#1077;&#1081; &#1095;&#1072;&#1102;";
      String expResult_1 = "съешь ещё этих мягких французских булок да выпей чаю";
      String result_1 = instance.Unescape(str_1);
      assertEquals(expResult_1, result_1);
   }

   private void testStripHtmlTags() {
      String src = "this <is a='2'>xxx<test the tag> stripping";
      String expected = "this xxx stripping";
      String result = HtmlUtils.StripHtmlTags(src);
      assertEquals(expected, result);
   }

   private void testUrlParse() {
      String src = "this http://google.com(http://a.hlp, http://xxx.com)in the http://ti.in/12idy";
      String[] result = HtmlUtils.ParseUrls(src);
      assertEquals(4, result.length);
   }

   private void testExtractTextInTag() {
      String src = "this <is a='2'>xxx</is> stripping";
      String expected = "xxx";
      String result = HtmlUtils.ExtractTextInTag(src, "is");
      assertEquals(expected, result);

      src = "this <is a='2'>xxx</sss> stripping";
      expected = "";
      result = HtmlUtils.ExtractTextInTag(src, "is");
      assertEquals(expected, result);

      src = "this <in a='2'>  <node>po</node> </in> stripping";
      expected = "po";
      result = HtmlUtils.ExtractTextInTag(src, "node");
      assertEquals(expected, result);

      String tail = HtmlUtils.TailAfterTag(src, "node");
      assertEquals(" </in> stripping", tail);
   }
}