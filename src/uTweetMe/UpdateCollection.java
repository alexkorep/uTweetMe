package uTweetMe;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Vector;
import javax.microedition.rms.InvalidRecordIDException;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreNotOpenException;

/**
 *
 * @author Alexey
 */
public class UpdateCollection {
   private Vector m_items;

   public UpdateCollection() {
      m_items = new Vector();
   }

   public TwitterUpdate ElementAt(int pos) {
      return (TwitterUpdate) (m_items.elementAt(pos));
   }

   public void AddItem(TwitterUpdate i_update) {
      if (find(i_update.m_id) != -1) {
         // We already have this item
         return;
      }

      m_items.addElement(i_update);
   }

   public void InsertItem(TwitterUpdate i_update, int i_pos) {
      if (find(i_update.m_id) != -1) {
         // We already have this item
         return;
      }

      m_items.insertElementAt(i_update, i_pos);
   }

   /**
    * Deletes the given update from container.
    * @param i_tweetId in, id of tweet to delete
    * @return true if element was actually found and deleted
    */
   public boolean DeleteItem(long i_tweetId) {
      final int n = find(i_tweetId);
      if (n == -1) {
         return false;
      }

      m_items.removeElementAt(n);
      return true;
   }

   /**
    * @brief Delete element at the given position
    * @param i_pos in, element position, 0-based index
    */
   public void RemoveElementAt(int i_pos) {
      m_items.removeElementAt(i_pos);
   }

   /**
    * Returns number of elements in the outbox
    * @return
    */
   public int GetItemCount() {
      return m_items.size();
   }

   /**
    * Deletes all updates from collection
    */
   public void Clear() {
      m_items.removeAllElements();
   }

   /**
    * @brief Finds the tweet with given id
    * @param i_tweetId - tweet id
    * @return found positon or -1 if not found
    */
   public int FindTweet(long i_tweetId) {
      for (int i = 0; i < GetItemCount(); i++) {
         final TwitterUpdate update = ElementAt(i);
         if (i_tweetId == update.m_id) {
            return i;
         }
      }
      return -1;
   }


   /**
    * Function finds is we already have the item in container with given id
    * @param i_id
    * @return element index if found, -1 otherwise
    */
   private int find(long i_id) {
      for (int i = 0; i < m_items.size(); ++i) {
         TwitterUpdate item = (TwitterUpdate) (m_items.elementAt(i));
         if (item.m_id == i_id) {
            return i;
         }
      }
      return -1;
   }

   public void Save(String i_recordStoreName) {
      try {
         RecordStore.deleteRecordStore(i_recordStoreName);
      } catch (RecordStoreException ex) {
         ex.printStackTrace();
      }

      // Saving items to RecordStore
      RecordStore recordStore;
      try {
         recordStore = RecordStore.openRecordStore(i_recordStoreName, true);
      } catch (RecordStoreException ex) {
         // Normal situation, record store does not exist
         return;
      }

      for (int i = 0; i < m_items.size(); ++i) {
         TwitterUpdate item = (TwitterUpdate) (m_items.elementAt(i));
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         DataOutputStream dos = new DataOutputStream(baos);
         item.Save(dos);
         try {
            recordStore.addRecord(baos.toByteArray(), 0, baos.toByteArray().length);
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

   public void Load(String i_recordStoreName) {
      // Loading items from RecordStore
      RecordStore recordStore;
      try {
         recordStore = RecordStore.openRecordStore(i_recordStoreName, true);
      } catch (RecordStoreException ex) {
         // Normal situation, record store does not exist
         //
         ex.printStackTrace();
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
         TwitterUpdate item = TwitterUpdate.Load(dis);
         if (null != item) {
            m_items.addElement(item);
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
}
