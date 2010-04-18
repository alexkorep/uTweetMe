package uTweetMe.UI;

import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.List;
import javax.microedition.lcdui.StringItem;
import uTweetMe.HtmlUtils;

/**
 * @class UrlLauncher
 * @respond Class is responsible for extracting URLs from
 *          text and showing UI elements for selecting one of these URLs
 * @collab UrlLauncherCallback callback to handle user actions
 */
public class UrlLauncher implements CommandListener {
   private Command m_cmdBack = new Command("Back", Command.BACK, 0);
   private Command m_cmdYes = new Command("Yes", Command.OK, 0);
   private Command m_cmdNo = new Command("No", Command.CANCEL, 0);

   private List m_list = null; ///< main list window
   private UrlLauncherCallback m_callback = null;
   private Display m_display = null; ///< main display

   /**
    * @brief Ctor
    */
   public UrlLauncher(Display i_display, UrlLauncherCallback i_callback) {
      m_display = i_display;
      m_callback = i_callback;
   }

   public void ShowExtractUrlsList(String i_text) {
      if (null == m_list) {
         m_list = new List("URLs", Choice.IMPLICIT);
         m_list.addCommand(m_cmdBack);
         m_list.setCommandListener(this);
      }

      m_list.deleteAll();
      String[] urls = HtmlUtils.ParseUrls(i_text);
      for (int i = 0; i < urls.length; ++i) {
         m_list.append(urls[i], null);
      }
      setDisplayable(m_list);
   }

   public void commandAction(Command c, Displayable d) {
      if (d == m_list) {
         // Main list commands
         //
         if (c == m_cmdBack) {
            m_callback.OnLaunchCancelled();
         } else if (c == List.SELECT_COMMAND) {
            selectItemConfirmation();
         }
      } else {
         if (c == m_cmdYes) {
            selectItem();
         } else if (c == m_cmdNo) {
            setDisplayable(m_list);
         }
      }
   }

   private void selectItem() {
      final int sel = m_list.getSelectedIndex();
      final String url = m_list.getString(sel);
      m_callback.OnUrlSelected(url);
   }

   private void selectItemConfirmation() {
      final int sel = m_list.getSelectedIndex();
      final String url = m_list.getString(sel);

      Item item = new StringItem("", "Do you want to open " + url + 
         "? If you select Yes, uTweetMe will be closed and the phone browser will be launched.");
      Form form = new Form("Confirmation", new Item[] { item });
      form.addCommand(m_cmdYes);
      form.addCommand(m_cmdNo);
      form.setCommandListener(this);

      setDisplayable(form);
   }

   private void setDisplayable(Displayable i_disp) {
      m_display.setCurrent(i_disp);
   }
}
