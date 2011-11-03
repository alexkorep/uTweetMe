package uTweetMe;

/**
 * Callback interface, used by app for notifying users about
 * timeline downloading finishing.
 */
interface DownloadableCollectionStatusCallback {

   public void DCSC_OnDownloadStep(UpdateCollection i_timeline, int i_percent);

   /**
    * Called when downloading is finished
    */
   public void DCSC_OnDownloadFinished(UpdateCollection i_timeline);
}
/**
 *
 * @author Alexey
 */
public class DownloadableCollection extends UpdateCollection
   implements DownloadStatusCallback {
   private String m_name;
   private String m_url;
   private DownloadableCollectionStatusCallback m_downloadCallback;
   private TimelineParsingStrategy m_parsingStrategy;
   private int m_downloadProgress = 0;
   private TimelineDownloader m_downloader = null;
   private int m_maxTweetCountAfterDownload; ///< number of items timeline can have after download
   boolean m_interrupted = false;

   /**
    * When downloading updates, if we already have existing updates, new ones
    * should be inserted before existing ones. This variable is position where
    * the new coming update should be inserted.
    */
   private int m_insertAt = 0;

   boolean m_publicTimeline = false;

   /**
    * @brief Ctor
    * @param i_name in, collection name as it will be displayed to user
    */
   DownloadableCollection(String i_name,
         String i_url,
         DownloadableCollectionStatusCallback i_downloadCallback,
         TimelineParsingStrategy i_parsingStrategy,
         boolean i_publicTimeline) {
      super();
      m_name = i_name;
      m_url = i_url;
      m_downloadCallback = i_downloadCallback;
      m_parsingStrategy = i_parsingStrategy;
      m_downloader = null;
      m_maxTweetCountAfterDownload = 0;
      m_publicTimeline = i_publicTimeline;
   }

   boolean Downloading() {
      return m_downloader != null;
   }

   int GetDownloadProgress() {
      return m_downloadProgress;
   }

   String GetName() {
      return m_name;
   }

   /**
    * @brief Downloads timeline.
    *        If there are no local tweets, then download full first page
    *        If there are local tweets and i_prevPage is false, then
    *        download only newer tweets
    *        If i_prevPage is true, then calculate how many tweets do we locally
    *        have 
    */
   void Download() {
      m_downloadProgress = 0;
      m_insertAt = 0;
      m_interrupted = false;

      if (0 == GetItemCount()) {
         m_maxTweetCountAfterDownload = Settings.c_pageSize;
      } else {
         // Number of full pages
         final int fullPages = GetItemCount()/Settings.c_pageSize;
         m_maxTweetCountAfterDownload =
            (fullPages + (GetItemCount()%Settings.c_pageSize == 0 ? 0 : 1))*Settings.c_pageSize;
      }
      m_downloader = new TimelineDownloader(this, getDownloadUrlForNewUpdates(),
              m_parsingStrategy, m_publicTimeline);
   }

   void DownloadEarlier() {
      m_downloadProgress = 0;
      m_insertAt = GetItemCount();
      m_interrupted = false;

      // How many complete pages do we have
      final int pages = GetItemCount()/Settings.c_pageSize;
      m_maxTweetCountAfterDownload = 0;
      final String url = getDownloadUrlForPage(pages + 1);
      m_downloader = new TimelineDownloader(this, url, m_parsingStrategy,
              m_publicTimeline);
   }

   /**
    * @brief Returns number of unread items
    */
   int GetUnreadItemCount() {
      int unread = 0;
      for (int i = 0; i < GetItemCount(); ++i) {
         if (!ElementAt(i).m_read) {
            ++unread;
         }
      }
      return unread;
   }

   void Interrupt() {
      m_interrupted = true;
   }

   /***************************************************************************'
    *
    * Interfaces implementation
    *
    **************************************************************************'*/
   public void DSC_OnNewTimelineItem(String i_id, String i_text, String i_author,
      String i_date, long i_replyToId) {

      final long id = Long.parseLong(i_id);
      final long date = DateUtils.ParseDate(i_date);
      onNewTimelineItem(id, i_text, i_author, date, i_replyToId);
   }

   public void OnDownloadFinished() {
      // Do nothing, we also need to finish parsing
   }

   public void DSC_OnParsingFinished() {
      m_downloader = null;
      
      if (m_maxTweetCountAfterDownload > 0) {
         // If max number of allowed items is specified,
         // then trim number of items to this value.
         while (GetItemCount() > m_maxTweetCountAfterDownload) {
            RemoveElementAt(GetItemCount() - 1);
         }
      }

      m_downloadCallback.DCSC_OnDownloadFinished(this);
   }

   public boolean OnProgress(int i_percent) {
      m_downloadProgress = i_percent;
      m_downloadCallback.DCSC_OnDownloadStep(this, i_percent);
      return m_interrupted;
   }

   /***************************************************************************'
    *
    * Protected functions
    *
    **************************************************************************'*/
   public void onNewTimelineItem(long i_id, String i_text, String i_author,
      long i_date, long i_replyToId) {

      TwitterUpdate update = new TwitterUpdate(i_id, i_text, i_author, i_date,
         i_replyToId);
      InsertItem(update, m_insertAt);
      m_insertAt++;
   }

   /***************************************************************************'
    *
    * Private functions
    *
    **************************************************************************'*/

   /**
    * @brief Generates URL where timeline should be downloaded from, using the
    *        last downloaded tweet ID
    */
   private String getDownloadUrlForNewUpdates() {
      // This symbol is used as separator for the parameter added to the URL
      final String separator = (m_url.indexOf("?") == -1 ? "?" : "&");

      if (0 == GetItemCount())
      {
         // If we have no items, then download complete timeline 
         return m_url;
      }

      // Download only newer items
      String lastId = String.valueOf(ElementAt(0).m_id);
      return m_url + separator + "since_id=" + lastId;
   }

   /**
    * @brief Generates URL where timeline should be downloaded from, using the
    *        provided page number
    */
   private String getDownloadUrlForPage(int i_page) {
      // This symbol is used as separator for the parameter added to the URL
      final String separator = (m_url.indexOf("?") == -1 ? "?" : "&");
      return m_url + separator + "count=" + Integer.toString(Settings.c_pageSize) +
         "&page=" + String.valueOf(i_page);
   }
}
