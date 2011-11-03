/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uTweetMe;

import javax.microedition.io.HttpConnection;


/**
 * @class TimelineDownloader
 * @brief Downloads timeline and notifies caller about tweets.
 * 
 */
public class TimelineDownloader implements Runnable {
   DownloadStatusCallback m_callback;
   Thread m_thread = null;
   String m_timelineUrl;
   TimelineParsingStrategy m_parsingStrategy;
   
   ///@brief if true, then no xAuth authorization is used to download this timeline.
   ///       E.g. search timeline, which is available without authorization.
   boolean m_publicTimeline = false;
   
   TimelineDownloader(DownloadStatusCallback i_callback, String i_timelineUrl,
           TimelineParsingStrategy i_parsingStrategy, boolean i_publicTimeline) {
      m_callback = i_callback;
      m_timelineUrl = i_timelineUrl;
      m_parsingStrategy = i_parsingStrategy;
      m_publicTimeline = i_publicTimeline;
      m_thread = new Thread(this);
      m_thread.start();
   }

   public void run() {
      String query = "";
      String response = "";
      try {
         final String username = Settings.GetInstance().GetSettings().m_name;
         final String password = Settings.GetInstance().GetSettings().m_password;
          if (m_publicTimeline) {
              response = HttpUtils.RequestPublicTimeline(m_timelineUrl, query,
                      HttpConnection.GET, m_callback);

          } else {
              response = HttpUtils.Request(m_timelineUrl, query, null,
                      HttpConnection.GET, username, password, m_callback);
          }

         if (0 == response.length()) {
            // We got empty timeline
            m_callback.DSC_OnParsingFinished();
            return;
         }

         parse(response);

      } catch (Exception ex) {
         ex.printStackTrace();
      }

      m_callback.DSC_OnParsingFinished();
   }

   private void parse(String i_text) {
      String text = i_text;
      String node = "";
      do {
         node = HtmlUtils.ExtractTextInTag(text,
            m_parsingStrategy.m_statusNodeName);
         if (0 == node.length()) {
            continue;
         }
         String msg = HtmlUtils.ExtractTextInTag(node,
            m_parsingStrategy.m_textNodeName);
         msg = HtmlUtils.GetInstance().Unescape(msg);

         final String id = HtmlUtils.ExtractTextInTag(node,
            m_parsingStrategy.m_idNodeName);

         String date = HtmlUtils.ExtractTextInTag(node,
            m_parsingStrategy.m_dateNodeName);

         String author = HtmlUtils.ExtractTextInTag(node,
            m_parsingStrategy.m_usernameNodeName);

         long replyToId = 0;
         if (0 != m_parsingStrategy.m_replyIdNodeName.length()) {
            final String replyToIdStr = HtmlUtils.ExtractTextInTag(node,
               m_parsingStrategy.m_replyIdNodeName);
            if (0 != replyToIdStr.length()) {
               replyToId = Long.parseLong(replyToIdStr);
            }
         }

         m_callback.DSC_OnNewTimelineItem(id, msg, author, date, replyToId);

         // Remove already parsed block
         text = HtmlUtils.TailAfterTag(text, m_parsingStrategy.m_statusNodeName);
      } while (node.length() != 0);
   }
}