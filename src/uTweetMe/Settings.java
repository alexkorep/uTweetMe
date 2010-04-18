/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uTweetMe;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import javax.microedition.rms.InvalidRecordIDException;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreNotOpenException;

/**
 *
 * @author Alexey
 */
public class Settings {

   // number of tweets per page visible in timelines
   public static final int c_pageSize = 10;

   public class SettingsRec {
      public String m_name;
      public String m_password;
      public boolean m_onStartNewTweet;
      public boolean m_onStartDownloadUpdates;
   }

   public static Settings GetInstance() {
      if (null == m_settings) {
         m_settings = new Settings();
      }
      return m_settings;
   }

   public final SettingsRec GetSettings() {
      if (null == m_settingsRec) {
         m_settingsRec = new SettingsRec();
         m_settingsRec.m_name = getValue(IDName);
         m_settingsRec.m_password = getValue(IDPassword);

         m_settingsRec.m_onStartNewTweet =
            !getValue(IDOnStartNewTweet).equals("0"); // that's true by default

         m_settingsRec.m_onStartDownloadUpdates =
            getValue(IDOnStartDownloadUpdates).equals("1"); // that's false by default
      }
      return m_settingsRec;
   }

   public void ApplySettings(SettingsRec i_settings) {
      m_settingsRec = i_settings;
      setValue(IDName, m_settingsRec.m_name);
      setValue(IDPassword, m_settingsRec.m_password);
      setValue(IDOnStartNewTweet, m_settingsRec.m_onStartNewTweet ? "1" : "0");
      setValue(IDOnStartDownloadUpdates, m_settingsRec.m_onStartDownloadUpdates ? "1" : "0");
   }

   ///@breif the one and only instance
   private static Settings m_settings = null;
   private SettingsRec m_settingsRec = null;
   private static final String c_recordStoreName = "settings";
   private RecordStore m_recordStore;
   /// IDs of setting values
   private static final int IDName                    = 1;
   private static final int IDPassword                = 2;
   private static final int IDOnStartNewTweet         = 3;
   private static final int IDOnStartDownloadUpdates  = 4;
   private static final int IDPageNumbers             = 5;
   
   private static final int IDLast                    = 5;

   public Settings() {
      try {
         m_recordStore = RecordStore.openRecordStore(c_recordStoreName, true);

         // Adding records that don't exist
         byte[] nothing = {0};
         for (int i = m_recordStore.getNumRecords(); i < IDLast; ++i) {
            m_recordStore.addRecord(nothing, 0, 1);
         }
      } catch (RecordStoreException ex) {
         ex.printStackTrace();
      }
   }

   public void Close() {
      try {
         m_recordStore.closeRecordStore();
      } catch (RecordStoreNotOpenException ex) {
         ex.printStackTrace();
      } catch (RecordStoreException ex) {
         ex.printStackTrace();
      }
   }

   /**
    * Stores settings value to the resord store
    * @param i_id setting id
    * @param i_value setting value
    */
   private void setValue(int i_id, String i_value) {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      DataOutputStream dos = new DataOutputStream(baos);
      try {
         dos.writeUTF(i_value);
      } catch (IOException ioe) {
         ioe.printStackTrace();
      }

      try {
         m_recordStore.setRecord(i_id, baos.toByteArray(), 0, baos.toByteArray().length);
      } catch (RecordStoreNotOpenException ex) {
         ex.printStackTrace();
      } catch (InvalidRecordIDException ex) {
         ex.printStackTrace();
      } catch (RecordStoreException ex) {
         ex.printStackTrace();
      }
   }

   /**
    * Gets setting value
    * @param i_id setting id
    * @return setting value
    */
   private String getValue(int i_id) {
      byte[] rec;
      try {
         rec = m_recordStore.getRecord(i_id);
      } catch (RecordStoreNotOpenException ex) {
         ex.printStackTrace();
         return "";
      } catch (InvalidRecordIDException ex) {
         ex.printStackTrace();
         return "";
      } catch (RecordStoreException ex) {
         ex.printStackTrace();
         return "";
      }
      ByteArrayInputStream bais = new ByteArrayInputStream(rec);
      DataInputStream dis = new DataInputStream(bais);

      try {
         String str = dis.readUTF();
         return str;
      } catch (IOException ioe) {
         ioe.printStackTrace();
      }

      return "";
   }
}
