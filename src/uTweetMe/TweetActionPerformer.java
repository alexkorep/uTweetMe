package uTweetMe;

import javax.microedition.io.HttpConnection;

/**
 *
 * @author user
 */
public class TweetActionPerformer implements Runnable {
   // URL consts
   private static final String c_deleteURL =
           "http://twitter.com/statuses/destroy/";
   private String m_username;
   private String m_password;
   private long m_tweetId;
   private TweetActionCallback m_callback;

   public TweetActionPerformer(TweetActionCallback i_callback) {
      m_callback = i_callback;
   }

   public void SetUsernamePassword(String i_username, String i_password) {
      m_username = i_username;
      m_password = i_password;
   }

   public void DeleteTweet(long i_tweetId) {
      m_tweetId = i_tweetId;
      Thread t = new Thread(this);
      t.start();
   }

   public void run() {
      deleteTweet();
   }

   private void deleteTweet() {
      try {
         String url = c_deleteURL + String.valueOf(m_tweetId) + ".xml";
         HttpUtils.Request(url, "", HttpConnection.POST,
                 m_username, m_password, null);

         // Reporting about success
         //
         m_callback.OnTweetActionDone();
      } catch (Exception ex) {
         ex.printStackTrace();

         // Reporting about error
         //
         m_callback.OnTweetActionError(ex.getMessage());
      }
   }
}
