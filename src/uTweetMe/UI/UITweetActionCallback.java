package uTweetMe.UI;

/**
 * @interface UITweetActionCallback
 * @brief This interface methods will be called when particular actions 
 *       executed like tweet deleting or starring
 */
public interface UITweetActionCallback {
   /**
    * @brief Called when tweet is deleted via UI
    * @param i_tweetId in, tweet id being deleted
    */
   void OnDeleteTweet(long i_tweetId);

   /**
    * @brief Called when user didn't confirm actions on tweet
    * @param i_message in, message to display user about cancel reason
    */
   void OnActionCancelled(String i_message);
}
