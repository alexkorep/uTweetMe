package uTweetMe.UI;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Gauge;
import javax.microedition.lcdui.ImageItem;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.StringItem;
import uTweetMe.Settings;
import uTweetMe.TweetActionPerformer;
import uTweetMe.TwitterUpdate;

/**
 * @respons Class is responsible for confirming different actions done to
 * tweet like deleting and starring.
 */
public class UITweetActionManager
   implements CommandListener, uTweetMe.TweetActionCallback {

   private Command m_cmdYes = null;
   private Command m_cmdNo = null;
   private Form m_confirmation = null;
   private Form m_deleteProgressForm = null;
   private Display m_display;
   private UITweetActionCallback m_callback;
   TwitterUpdate m_tweet = null;

   /// @brief Error displayed to user when he/she's trying to delete other people's tweet
   private final String c_wrongUserError = "You can't delete other people's tweets.";

   /**
    * @brief Ctor
    */
   public UITweetActionManager(Display i_display, UITweetActionCallback i_callback) {
      m_display = i_display;
      m_callback = i_callback;
   }

   /**
    * @brief Shows confirmation to delete the tweet. If user confirmed, then 
    *        tweet is deleted.
    * @param i_tweetId in, id of tweet to delete
    */
   public void DeleteTweet(TwitterUpdate i_tweet) {
      m_tweet = i_tweet;
      final String username = Settings.GetInstance().GetSettings().m_name;
      if (i_tweet.m_author.equals(username)) {
         showDeleteTweetConfirmation();
      } else {
         m_callback.OnActionCancelled(c_wrongUserError);
      }
   }

   public void commandAction(Command c, Displayable d) {
      if (d == m_confirmation) {
         if (c == m_cmdNo) {
            m_callback.OnActionCancelled("");
         } else if (c == m_cmdYes) {
            TweetActionPerformer performer = new TweetActionPerformer(this);
            final String username = Settings.GetInstance().GetSettings().m_name;
            final String password = Settings.GetInstance().GetSettings().m_password;
            performer.SetUsernamePassword(username, password);
            performer.DeleteTweet(m_tweet.m_id);
            showDeleteProgressAlert();
         }
      }
   }

   /****************************************************************************
    *
    * Private functions
    *
    ***************************************************************************/
  private void showDeleteTweetConfirmation() {
      m_cmdYes = new Command("Yes", Command.OK, 0);
      m_cmdNo = new Command("No", Command.CANCEL, 0);

      Item item = new StringItem("", "Do you want to delete this tweet?");
      m_confirmation = new Form("Confirmation", new Item[] { item });
      m_confirmation.addCommand(m_cmdYes);
      m_confirmation.addCommand(m_cmdNo);
      m_confirmation.setCommandListener(this);

      m_display.setCurrent(m_confirmation);
   }

   private void showDeleteProgressAlert() {
      Gauge gauge = new Gauge("Deleting tweet...", false, 100, 0);
      gauge.setLayout(ImageItem.LAYOUT_CENTER | Item.LAYOUT_TOP | Item.LAYOUT_BOTTOM | Item.LAYOUT_VCENTER);
      gauge.setMaxValue(Gauge.INDEFINITE);
      gauge.setValue(Gauge.CONTINUOUS_RUNNING);
      m_deleteProgressForm = new Form("Deleting", new Item[] { gauge });
      m_deleteProgressForm.setCommandListener(this);
      m_display.setCurrent(m_deleteProgressForm);
   }

   public void OnTweetActionDone() {
      m_callback.OnDeleteTweet(m_tweet.m_id);
   }

   public void OnTweetActionError(String i_error) {
      m_callback.OnActionCancelled(i_error);
   }
}