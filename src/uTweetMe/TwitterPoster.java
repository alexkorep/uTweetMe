/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uTweetMe;

import javax.microedition.io.HttpConnection;

/**
 * Callback interface, used by TwitterPoster for notifying users about 
 * update status.
 * @author Alexey
 */
interface TwitterPosterStatusCallback
{
    /**
     * Callback fired when update posting is started
     * @param i_update the update object that being posted
     */
    public void TWASC_OnUpdateStarted(TwitterUpdate i_update);
    
    /**
     * Fired when update has been posted
     * @param i_update the update object just posted
     */
    public void TWASC_OnUpdatePosted(TwitterUpdate i_update);
    
    /**
     * Fired when error occured during posting update
     * @param i_update update object being posted
     * @param i_error error text string
     */
    public void TWASC_OnUpdateError(TwitterUpdate i_update, String i_error);
}

/**
 * Class used for posting updates to Twitter
 */
public class TwitterPoster implements Runnable {
   // URL consts

   private static final String c_updateURL = "http://twitter.com/statuses/update.xml";
   private String m_username;
   private String m_password;
   private String m_updateText;
   private long m_replyToId;
   private TwitterPosterStatusCallback m_callback;

   TwitterPoster(TwitterPosterStatusCallback i_callback) {
      m_callback = i_callback;
   }

   public void SetUsernamePassword(String i_username, String i_password) {
      m_username = i_username;
      m_password = i_password;
   }

   public void PostUpdate(String i_text, long i_replyToId) {
      m_updateText = i_text;
      m_replyToId = i_replyToId;
      Thread t = new Thread(this);
      t.start();
   }

   public void RequestUserTimeline() {
      Thread t = new Thread(this);
      t.start();
   }

   public void run() {
      postUpdate();
   }

   private void postUpdate() {
      TwitterUpdate update = TwitterUpdate.CreateNew(m_updateText, m_replyToId);
      m_callback.TWASC_OnUpdateStarted(update);

		if (update.m_text.length() > TwitterUpdate.m_maxUpdateTextLen)
		{
			final String newText = ShortenPoster.Post(update);
			update.m_text = newText;
		}

      try {
         String query = "source=utweetme" +
            (0 != m_replyToId ? "&in_reply_to_status_id=" + String.valueOf(m_replyToId) : "") +
            "&status=" + HttpUtils.URLEncode(m_updateText);
         HttpUtils.Request(c_updateURL, query, HttpConnection.POST, m_username, m_password, null);

         // Reporting about success
         //
         m_callback.TWASC_OnUpdatePosted(update);
      } catch (Exception ex) {
         ex.printStackTrace();

         // Reporting about error
         //
         m_callback.TWASC_OnUpdateError(update, ex.getMessage());
      }
   }
}
