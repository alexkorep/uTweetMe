/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uTweetMe;

import java.util.Hashtable;
import javax.microedition.io.HttpConnection;

/**
 *
 * @author user
 */
public class ShortenPoster {
	public static String c_shortenPath = "http://twigu.ru/";
	private static String c_addURL = "add.php";
	private static String c_ellipsis = "… ";

	static String Post(TwitterUpdate update) throws Exception {

		String id = "";
      Hashtable bodyParams = new Hashtable();
      bodyParams.put("user_id", String.valueOf(update.m_author));
      bodyParams.put("text", update.m_text);
      bodyParams.put("version", "1");
		//String query = "user_id=" + String.valueOf(update.m_author) +
		//	"&text=" + HttpUtils.URLEncode(update.m_text) + "&version=1";
		id = HttpUtils.Request(c_shortenPath + c_addURL, "", bodyParams,
			HttpConnection.POST, "", "", null);

		if (id.length() == 0)
		{
			throw new Exception("TwiGu.ru posting error");
		}

		final int len = TwitterUpdate.m_maxUpdateTextLen - 
			 c_ellipsis.length() - c_shortenPath.length() - id.length();
		String str = update.m_text.substring(0, len);
		str = str + c_ellipsis + c_shortenPath + id;
		return str;
	}
}
