/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uTweetMe;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Class incapsulating the update (posting) object.
 * @author Alexey
 */
public class TwitterUpdate {
	/// @brief max tweet length
	public static final int m_maxUpdateTextLen = 140;
	
	/// @brief max message posted to TwiGu.ru length
	public static final int m_maxLongTweetTextLen = 4000;
	public String m_text;      ///< update text
   public String m_author;    ///< update author
   public long m_id;          ///< unique ID of update
   public long m_date;        ///< date when tweet has been created
   boolean m_read;            ///< flag idicating if tweet has been read
   public long m_replyToId;   ///< ID of status being replied to

   /**
    * Update factory method. Creates new update with unique ID.
    * @param i_text update item text
    * @return new update object
    */
   public static TwitterUpdate CreateNew(String i_text, long i_replyToId) {
      long id = System.currentTimeMillis();
      return new TwitterUpdate(
         id,                     // use timestamp for unique id
         i_text,                 // status text
         Settings.GetInstance().GetSettings().m_name, // User id is current user
         id,                     // Current date/time
         i_replyToId);
   }

   /**
    * Ctor
    * @param i_text - update text
    */
   public TwitterUpdate(long i_id, String i_text, String i_author, long i_date,
      long i_replyToId) {
      m_id = i_id;
      m_text = i_text;
      m_author = i_author;
      m_date = i_date;
      m_replyToId = i_replyToId;
      m_read = false;
   }

   /**
    * Mark update as read
    */
   void MarkAsRead(boolean i_read) {
      m_read = i_read;
   }

   static TwitterUpdate Load(DataInputStream dis) {
      try {
         long updateId = dis.readLong();
         final String text = dis.readUTF();
         final String author = dis.readUTF();
         final long date = dis.readLong();
         final long replyToId = dis.readLong();
         TwitterUpdate item = new TwitterUpdate(updateId, text, author, date,
            replyToId);
         item.m_read = dis.readBoolean();
         return item;
      } catch (IOException ex) {
         ex.printStackTrace();
      }
      return null;
   }

   void Save(DataOutputStream i_dos) {
      try {
         i_dos.writeLong(m_id);
         i_dos.writeUTF(m_text);
         i_dos.writeUTF(m_author);
         i_dos.writeLong(m_date);
         i_dos.writeLong(m_replyToId);
         i_dos.writeBoolean(m_read);
      } catch (IOException ex) {
         ex.printStackTrace();
      }
   }
}
