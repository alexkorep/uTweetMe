/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uTweetMe;

import com.twitterapime.rest.Credential;
import com.twitterapime.rest.UserAccountManager;
import com.twitterapime.search.LimitExceededException;
import java.io.IOException;

/**
 *
 * @author user
 */
public class LoginManager {

   // Consumer key
   static final String cs_consumerKey = "RB5wajTEOUIHucU7Eu774w";

   // Consumer secret
   static final String cs_consumerSecret = "bvfb0nMGljffrrZaTVle8Zdt6JtQEewL1YEJb4aeY";

   // Request token URL
   //static final String cs_requestTokenUrl = "https://api.twitter.com/oauth/request_token";
   //static final String cs_requestTokenUrl = "https://api.twitter.com/oauth/request_token";
   //static final String cs_requestTokenUrl = "https://www.verisign.com/";

   // Access token URL
   static final String cs_accessTokenUrl = "https://api.twitter.com/oauth/access_token";

   // Authorize URL
   static final String cs_authorizeUrl = "https://api.twitter.com/oauth/authorize?oauth_token=";

   private UserAccountManager m_userAccMgr;
   
   ///@breif the one and only instance
   private static LoginManager m_instance = null;

   public static LoginManager GetInstance() {
      if (null == m_instance) {
         m_instance = new LoginManager();
      }
      return m_instance;
   }

   private LoginManager() {
      m_userAccMgr = null;
   }

   public boolean IsLoggedIn() {
      return null != m_userAccMgr;
   }

   public boolean Login(String username, String password) 
           throws IOException, LimitExceededException {
      // todo replace username/password with passed ones
      Credential credential = new Credential(username,
         password, cs_consumerKey, cs_consumerSecret);
      m_userAccMgr = UserAccountManager.getInstance(credential);
      if (!m_userAccMgr.verifyCredential()) {
         m_userAccMgr = null; // not logged in, set to null in order to have
                              // IsLoggedIn() return false
         return false;
      }

      uTweetMe.Settings.SettingsRec settings =
         uTweetMe.Settings.GetInstance().GetSettings();
      settings.m_name = username;
      settings.m_password = password;

      return true;
    }

   UserAccountManager GetUserAccountManager() {
      return m_userAccMgr;
   }
}
