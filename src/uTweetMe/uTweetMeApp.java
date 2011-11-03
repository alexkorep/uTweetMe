/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uTweetMe;

/**
 *
 * @class uTweetMeApp
 * @respons Incapsulates application functionality:
 *          - Sends update
 *          - Provides access to application settings
 *          - Gets timellines
 * @collab UpdateCollection for getting updates
 *         TwitterPoster for posting update
 *         Setting for getting and changing settings
 */
public class uTweetMeApp {
   
   private static final String c_draftsRecordStoreName = "drafts";
   private static final String c_templatesRecordStoreName = "templates";
   private static final String c_draftsHomeStoreName = "friends";
   private static final String c_repliesStoreName = "replies";
   private static final String c_directInboxStoreName = "directinbox";
   private static final String c_directSentStoreName = "directsent";

   private static final String c_friendsTimelineURL = "http://twitter.com/statuses/friends_timeline.xml";
   private static final String c_repliesTimelineURL = "http://twitter.com/statuses/replies.xml";
   private static final String c_directInboxTimelineURL = "http://twitter.com/direct_messages.xml";
   private static final String c_directSentTimelineURL = "http://twitter.com/direct_messages/sent.xml";

   private TwitterPoster m_twitterPoster = null;
   private UpdateCollection m_outbox = null;
   private UpdateCollection m_drafts = null;
   private UpdateCollection m_templates = null; ///< Template records collection

   /// Home timeline
   private DownloadableCollection m_home;
   private DownloadableCollection m_replies;
   private DownloadableCollection m_directInbox;
   private DownloadableCollection m_directSent;

   /// @brief Search manager
   private SearchManager m_searchManager;

   uTweetMeApp(TwitterPosterStatusCallback i_posterCallback, 
      DownloadableCollectionStatusCallback i_downloadCallback) {

      m_twitterPoster = new TwitterPoster(i_posterCallback);
      m_outbox = new UpdateCollection();
      
      // initializing drafts
      //
      m_drafts = new UpdateCollection();
      m_drafts.Load(c_draftsRecordStoreName);

      // Initializing templates
      //
      m_templates = new UpdateCollection();
      m_templates.Load(c_templatesRecordStoreName);

      m_home = new DownloadableCollection("Friends",
         c_friendsTimelineURL,
         i_downloadCallback,
         new TimelineParsingStrategy(
            "status",
            "text",
            "id",
            "created_at",
            "screen_name",
            "in_reply_to_status_id"),
         false);
      m_home.Load(c_draftsHomeStoreName);

      m_replies = new DownloadableCollection("Replies",
         c_repliesTimelineURL,
         i_downloadCallback,
         new TimelineParsingStrategy(
            "status",
            "text",
            "id",
            "created_at",
            "screen_name",
            "in_reply_to_status_id"),
         false);
      m_replies.Load(c_repliesStoreName);

      m_directInbox = new DownloadableCollection("Direct - Inbox",
         c_directInboxTimelineURL,
         i_downloadCallback,
         new TimelineParsingStrategy(
            "direct_message",
            "text",
            "id",
            "created_at",
            "screen_name",
            ""),
         false);
      m_directInbox.Load(c_directInboxStoreName);

      m_directSent = new DownloadableCollection("Direct - Sent",
         c_directSentTimelineURL,
         i_downloadCallback,
         new TimelineParsingStrategy(
            "direct_message",
            "text",
            "id",
            "created_at",
            "screen_name",
            ""),
         false);
      m_directSent.Load(c_directSentStoreName);

      m_searchManager = new SearchManager(i_downloadCallback);
      m_searchManager.Load();
   }

   void Close() {
      // Store timelines
      Settings.GetInstance().Close();
      // Saving drafts
      m_drafts.Save(c_draftsRecordStoreName);
      // Saving templates
      m_templates.Save(c_templatesRecordStoreName);
      // Saving timelines
      m_home.Save(c_draftsHomeStoreName);
      m_replies.Save(c_repliesStoreName);
      m_directInbox.Save(c_directInboxStoreName);
      m_directSent.Save(c_directSentStoreName);
      m_searchManager.Save();
   }


   class FoundTweet {
      DownloadableCollection m_collection;   ///< Collection where update was found
      int m_pos;                             ///< Found update position in collection
   }

   /**
    * @brief Finds tweet with given ID in all containers:
    *        Friends, replies, direct and direct sent
    * @return Tweet or null if not found
    */
   FoundTweet FindTweet(long i_tweetId) {
      FoundTweet foundTweet = new FoundTweet();

      int pos = m_home.FindTweet(i_tweetId);
      if (-1 != pos) {
         foundTweet.m_collection = m_home;
         foundTweet.m_pos = pos;
         return foundTweet;
      }

      pos = m_replies.FindTweet(i_tweetId);
      if (-1 != pos) {
         foundTweet.m_collection = m_replies;
         foundTweet.m_pos = pos;
         return foundTweet;
      }

      pos = m_directInbox.FindTweet(i_tweetId);
      if (-1 != pos) {
         foundTweet.m_collection = m_directInbox;
         foundTweet.m_pos = pos;
         return foundTweet;
      }

      pos = m_directSent.FindTweet(i_tweetId);
      if (-1 != pos) {
         foundTweet.m_collection = m_directSent;
         foundTweet.m_pos = pos;
         return foundTweet;
      }
      
      return null;
   }

   int GetUnreadItemCount() {
      return m_home.GetUnreadItemCount() + m_replies.GetUnreadItemCount() +
         m_directInbox.GetUnreadItemCount() + m_directSent.GetUnreadItemCount() +
         m_searchManager.GetUnreadCount();
   }

   void PostUpdate(String i_updateText, long i_replyToId) {
      final String username = Settings.GetInstance().GetSettings().m_name;
      final String password = Settings.GetInstance().GetSettings().m_password;
      m_twitterPoster.SetUsernamePassword(username, password);
      m_twitterPoster.PostUpdate(i_updateText, i_replyToId);
   }

   UpdateCollection GetOutbox() {
      return m_outbox;
   }

   UpdateCollection GetDrafts() {
      return m_drafts;
   }

   DownloadableCollection GetHomeTimeline() {
      return m_home;
   }

   DownloadableCollection GetRepliesTimeline() {
      return m_replies;
   }

   DownloadableCollection GetDirectInboxTimeline() {
      return m_directInbox;
   }

   DownloadableCollection GetDirectSentTimeline() {
      return m_directSent;
   }

   SearchManager GetSearchManager() {
      return m_searchManager;
   }

   UpdateCollection GetTemplates() {
      return m_templates;
   }

    boolean IsDownloading() {
      return m_home.Downloading() ||
         m_replies.Downloading() ||
         m_directInbox.Downloading() ||
         m_directSent.Downloading() ||
         m_searchManager.Downloading();
   }

   void DownloadAll() {
      GetHomeTimeline().Download();
      GetRepliesTimeline().Download();
      GetDirectInboxTimeline().Download();
      GetDirectSentTimeline().Download();
      m_searchManager.Download();
   }

   /**
    * @brief Deletes tweet with given ID from all timelines, except search.
    *        Items from search aren't deleted because Twitter seems doesn't
    *        delete them from search DB when user deletes the tweet.
    * @param i_tweetId in, id of tweet to delete.
    */
   void DeleteTweetFromContainers(long i_tweetId) {
      GetHomeTimeline().DeleteItem(i_tweetId);
      GetRepliesTimeline().DeleteItem(i_tweetId);
      GetDirectInboxTimeline().DeleteItem(i_tweetId);
      GetDirectSentTimeline().DeleteItem(i_tweetId);
   }
}