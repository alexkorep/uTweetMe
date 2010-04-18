package uTweetMe;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Vector;
import javax.microedition.rms.InvalidRecordIDException;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreNotOpenException;

/**
 * @class SearchTimelineManager
 * @respons Keeps information about search timelines, creates them,
 *          serializes itself into/from recordstore
 * @collab  DownloadableCollection - keeps list of it as the search timelines
 *          Used by UTweetMIDlet and uTweetMeApp
 */
public class SearchManager {
   private final static String c_searchUrl = "http://search.twitter.com/search.atom?rpp=" +
      Integer.toString(Settings.c_pageSize) + "&q=";

   /// @brief Recordstore where the information about searches is kept
   private final static String c_recordstoreName = "searchlist";

   /// @if user starts the app first time, then this default search will appear in list
   private final static String c_defaultSearch = "from:utweetme";

   private Vector m_timelines = null;
   DownloadableCollectionStatusCallback m_downloadCallback = null;

   public class SearchItem {
      public DownloadableCollection m_timeline;
      public String m_search;
   }

   SearchManager(DownloadableCollectionStatusCallback i_downloadCallback){
      m_downloadCallback = i_downloadCallback;
      m_timelines = new Vector();
   }

   /**
    * @brief Adds a new search timeline
    * @param i_search in, search text
    * @return Added item position
    */
   int AddSearchTimeline(String i_search) {
      SearchItem item = new SearchItem();
      item.m_search = i_search;
      final String url = c_searchUrl + HttpUtils.URLEncode(i_search);
      item.m_timeline = new DownloadableSearchCollection(
         i_search,
         url,
         m_downloadCallback, 
         new TimelineParsingStrategy(
            "entry",
            "content",
            "id",
            "published",
            "name",
            ""));

      m_timelines.addElement(item);
      return m_timelines.size() - 1;
   }

   void Delete(int i_pos) {
      m_timelines.removeElementAt(i_pos);
   }

   SearchItem GetTimeline(int i_num) {
      return (SearchItem)m_timelines.elementAt(i_num);
   }
   
   int GetTimelineCount() {
      return m_timelines.size();
   }

   /**
    * Check if any of timelines is being downloaded now
    */
   boolean Downloading() {
      for (int i = 0; i < GetTimelineCount(); ++i) {
         if (GetTimeline(i).m_timeline.Downloading()) {
            return true;
         }
      }
      return false;
   }

   void Download() {
      for (int i = 0; i < GetTimelineCount(); ++i) {
         GetTimeline(i).m_timeline.Download();
      }
   }

   /**
    * Get unread item count for all search timelines
    */
   int GetUnreadCount() {
      int ureadItems = 0;
      for (int i = 0; i < GetTimelineCount(); ++i) {
         ureadItems += GetTimeline(i).m_timeline.GetUnreadItemCount();
      }
      return ureadItems;
   }

   /***************************************************************************
    *
    * Serealization
    *
    **************************************************************************/

   void Save() {
      try {
         RecordStore.deleteRecordStore(c_recordstoreName);
      } catch (RecordStoreException ex) {
         ex.printStackTrace();
      }

      // Saving items to RecordStore
      RecordStore recordStore;
      try {
         recordStore = RecordStore.openRecordStore(c_recordstoreName, true);
      } catch (RecordStoreException ex) {
         // Normal situation, record store does not exist
         return;
      }

      for (int i = 0; i < GetTimelineCount(); ++i) {
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         DataOutputStream dos = new DataOutputStream(baos);
         try {
            dos.writeUTF(GetTimeline(i).m_search);
            recordStore.addRecord(baos.toByteArray(), 0, baos.toByteArray().length);

            // Save timeline content
            GetTimeline(i).m_timeline.Save(getTimelineRecordstoreName(i));
         } catch (IOException ex) {
            ex.printStackTrace();
         } catch (RecordStoreNotOpenException ex) {
            ex.printStackTrace();
         } catch (RecordStoreException ex) {
            ex.printStackTrace();
         }
      }

      // Close recordstore on exit
      try {
         recordStore.closeRecordStore();
      } catch (RecordStoreNotOpenException ex) {
         ex.printStackTrace();
      } catch (RecordStoreException ex) {
         ex.printStackTrace();
      }
   }

   void Load() {
      RecordStore recordStore;
      try {
         recordStore = RecordStore.openRecordStore(c_recordstoreName, false);
      } catch (RecordStoreException ex) {
         // Normal situation, record store does not exist. Which means
         // that we started the app first time. So we are adding the
         // default search timeline with uTweetMe news and exit
         //
         AddSearchTimeline(c_defaultSearch);
         return;
      }

      int numRecs = 0;
      try {
         numRecs = recordStore.getNumRecords();
      } catch (RecordStoreNotOpenException ex) {
         ex.printStackTrace();
      }

      for (int i = 1; i <= numRecs; ++i) {
         byte[] rec;
         try {
            rec = recordStore.getRecord(i);
         } catch (RecordStoreNotOpenException ex) {
            ex.printStackTrace();
            return;
         } catch (InvalidRecordIDException ex) {
            ex.printStackTrace();
            return;
         } catch (RecordStoreException ex) {
            ex.printStackTrace();
            return;
         }
         ByteArrayInputStream bais = new ByteArrayInputStream(rec);
         DataInputStream dis = new DataInputStream(bais);
         try {
            final String search = dis.readUTF();
            if (null != search) {
               final int pos = AddSearchTimeline(search);
               // load timieline content
               GetTimeline(pos).m_timeline.Load(getTimelineRecordstoreName(pos));
            }
         } catch (IOException ex) {
            ex.printStackTrace();
         }
      }

      // Close recordstore
      try {
         recordStore.closeRecordStore();
      } catch (RecordStoreNotOpenException ex) {
         ex.printStackTrace();
      } catch (RecordStoreException ex) {
         ex.printStackTrace();
      }
   }

   /**
    * @brief Get recordstore name for given search timeline
    * @param i_num in, timeline number
    */
   private String getTimelineRecordstoreName(int i_num) {
      return "search" + String.valueOf(i_num);
   }
}
