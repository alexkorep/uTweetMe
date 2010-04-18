package uTweetMe;

/**
 * Callback interface, used by TwitterTimeline for notifying users about
 * update status.
 * @author Alexey
 */
interface DownloadStatusCallback extends IDownloadProgress {
   /**
    * Called when a new item is downloaded
    * @param i_id update id
    * @param i_text item text
    * @param i_author update author
    */
   public void DSC_OnNewTimelineItem(String i_id, String i_text, String i_author, 
      String i_date, long i_replyToId);

   /**
    * Called when timeline parsing is finished
    */
   public void DSC_OnParsingFinished();

}
