package uTweetMe;

/**
 *
 * @class DownloadableSearchCollection
 * @respons Kepps search result tweets
 * @note Two differences from DownloadableCollection:
 *		1. The way how tweet id is parsed - for search results the ID
 *			contains string part which is
 *       removed, e.g.: 1268434804 is extracted from "tag:search.twitter.com,2005:1268434804"
 *		2. Text is URL decoded, for example &apos; is converted to ' etc.
 */
public class DownloadableSearchCollection extends DownloadableCollection {

   // @brief ctor
   DownloadableSearchCollection(String i_name,
         String i_url,
         DownloadableCollectionStatusCallback i_downloadCallback,
         TimelineParsingStrategy i_parsingStrategy) {
      super(i_name, i_url, i_downloadCallback, i_parsingStrategy, 
              true); // true because it's a public timeline
   }

   public void DSC_OnNewTimelineItem(String i_id, String i_text, String i_author,
      String i_date, long i_replyToId) {
      
      final int pos = i_id.lastIndexOf(':');
      final String idStr = i_id.substring(pos + 1);
      final long id = Long.parseLong(idStr);

      final long date = DateUtils.ParseSearchDate(i_date);
      final String author = HtmlUtils.GetInstance().Unescape(i_author);
      final String text = HtmlUtils.GetInstance().Unescape(
				  HtmlUtils.StripHtmlTags(i_text));
      
      onNewTimelineItem(id, text, author, date, i_replyToId);
   }
}
