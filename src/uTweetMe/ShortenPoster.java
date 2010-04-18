/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uTweetMe;

/**
 *
 * @author user
 */
public class ShortenPoster {

	static String Post(TwitterUpdate update) {
		final int len = TwitterUpdate.m_maxUpdateTextLen - 30;
		String str = update.m_text.substring(0, len);
		str = str + "http://shorten.com/122334";
		return str;
	}
}
