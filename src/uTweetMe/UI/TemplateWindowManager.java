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
import javax.microedition.lcdui.TextBox;
import javax.microedition.lcdui.TextField;
import uTweetMe.TwitterUpdate;
import uTweetMe.UpdateCollection;

/**
 * @class TemplateWindow
 * @respons Class is responsible for displaying templates window and handling
 *          notificatons related to this window
 * @collab List - the List class which represends the tempaltes
 * @collab UpdateCollection - collection of templates
 */
public class TemplateWindowManager implements CommandListener {
   private static TemplateWindowManager m_instance = null;
   private List m_list;

   // Commands
   private Command m_cmdBack = new Command("Back", Command.BACK, 0);
   private Command m_cmdEdit = new Command("Edit Template", Command.ITEM, 0);
   private Command m_cmdDelete = new Command("Delete", Command.ITEM, 0);
   private Command m_cmdOk = new Command("Ok", Command.OK, 0);
   private Command m_cmdYes = new Command("Yes", Command.OK, 0);
   private Command m_cmdNo = new Command("No", Command.CANCEL, 0);
   
   private Display m_display = null;
   private TextBox m_txtBoxTemplate = null;
   private Form m_frmDeleteConfirmation = null;
   private UpdateCollection m_templateCollection = null;
   private boolean m_editingTemplate = false;
   private TemplatesCallback m_callback = null;
   private int m_mode = 0;

   // Static consts
   private static final String c_newTemplateItem = "New Template";
   private static final String c_newTemplateBoxTitle = "New Template";
   private static final String c_editTemplateBoxTitle = "Edit Template";

   // Sybsystem working mode
   private static int eEditMode = 0;   ///< Templates are being modified
   private static int eSelectMode = 1; ///< Template is being selected

   public static TemplateWindowManager GetInstance() {
      if (null == m_instance) {
         m_instance = new TemplateWindowManager();
      }
      return m_instance;
   }

   TemplateWindowManager() {
      // Templates List
      m_list = new List("Templates", Choice.IMPLICIT);
      m_list.addCommand(m_cmdBack);
      m_list.addCommand(m_cmdEdit);
      m_list.addCommand(m_cmdDelete);
      m_list.setCommandListener(this);
   }

   /**
    * @brief Initializes class with templates collection and parent display
    */
   public void Initialize(UpdateCollection i_templateCollection,
      Display i_parentDisplay, TemplatesCallback i_callback) {
      m_callback = i_callback;
      m_templateCollection = i_templateCollection;
      m_display = i_parentDisplay;
   }

   public void Show() {
      m_mode = eEditMode;
      loadTemplatesFromCollection();
      m_display.setCurrent(m_list);
   }

   public void SelectTemplate() {
      m_mode = eSelectMode;
      loadTemplatesFromCollection();
      m_display.setCurrent(m_list);
   }

   public void SaveAsTempate(String i_text) {
      m_mode = eEditMode;
      loadTemplatesFromCollection();
      newTemplate(i_text);
      m_display.setCurrent(m_list);
   }

   public void commandAction(Command c, Displayable d) {
      if (d == m_list) {
         // Main list commands
         //
         if (c == m_cmdBack) {
            if (eEditMode == m_mode) {
               m_callback.OnEditCancelled();
            } else {
               m_callback.OnSelectCancelled();
            }
         } else if (c == List.SELECT_COMMAND) {
            if (0 == m_list.getSelectedIndex()) {
               showNewTemplateTextBox();
            } else {
               if (eEditMode == m_mode) {
                  createNewTweetBasedOnTemplate();
               } else {
                  selectItem();
               }
            }
         } else if (c == m_cmdEdit) {
            startEditingItem();
         } else if (c == m_cmdDelete) {
            deleteTemplateConfirmation();
         }
      } else if (d == m_txtBoxTemplate) {
         // Template Text Box commands
         //
         if (c == m_cmdBack) {
            m_display.setCurrent(m_list);
         } else if (c == m_cmdOk) {
            if (m_editingTemplate) {
               changeTempate();
            } else {
               newTemplate(getTxtboxTemplate().getString());
            }
         }
      } else if (d == m_frmDeleteConfirmation) {
         if (c == m_cmdYes) {
            deleteTemplate();
         } else if (c == m_cmdNo) {
            m_display.setCurrent(m_list);
         }
      }

   }

   /****************************************************************************
    *
    * Private functions
    *
    ***************************************************************************/

   /**
    * Returns an initiliazed instance of txtboxUpdate component.
    * @return the initialized component instance
    */
   private TextBox getTxtboxTemplate() {
      if (m_txtBoxTemplate == null) {
         m_txtBoxTemplate = new TextBox("New Template", null, 4000,
            TextField.ANY | TextField.INITIAL_CAPS_SENTENCE);
         m_txtBoxTemplate.addCommand(m_cmdOk);
         m_txtBoxTemplate.addCommand(m_cmdBack);
         m_txtBoxTemplate.setCommandListener(this);
      }
      return m_txtBoxTemplate;
   }

   private void newTemplate(String i_text) {
      // Add item to collection
      TwitterUpdate template = TwitterUpdate.CreateNew(i_text, 0);
      m_templateCollection.AddItem(template);
      final int position = m_templateCollection.FindTweet(template.m_id);
      
      // Add item visually
      // +1 because the first item is "New Template"
      // TODO add icons to templates
      m_list.insert(position + 1, i_text, null);
      m_list.setSelectedIndex(position + 1, true);

      // Show templates list
      m_display.setCurrent(m_list);
   }

   private void changeTempate() {
      final String text = getTxtboxTemplate().getString();
      TwitterUpdate newItem = TwitterUpdate.CreateNew(text, 0);

      // Modify item in collection
      final int pos = m_list.getSelectedIndex() - 1;
      TwitterUpdate oldItem = m_templateCollection.ElementAt(pos);
      m_templateCollection.DeleteItem(oldItem.m_id);
      m_templateCollection.InsertItem(newItem, pos);

      // Update item visually
      // +1 because the first item is "New Template"
      m_list.set(pos + 1, text, null);

      // Show templates list
      m_display.setCurrent(m_list);
   }


   /**
    * @brief Shows text box where user can enter new template text
    */
   private void showNewTemplateTextBox() {
      getTxtboxTemplate().setTitle(c_newTemplateBoxTitle);
      getTxtboxTemplate().setString("");
      m_display.setCurrent(getTxtboxTemplate());
      m_editingTemplate = false;
   }

   /**
    * @brief Loads templates from collection and inserts them to the list view
    *
    */
   private void loadTemplatesFromCollection() {
      m_list.deleteAll();
      m_list.append(c_newTemplateItem, null);
      for (int i = 0; i < m_templateCollection.GetItemCount(); ++i) {
         final TwitterUpdate item = m_templateCollection.ElementAt(i);
         m_list.insert(i + 1, item.m_text, null);
      }
   }

   /**
    * Deletes template item selected in list
    */
   private void deleteTemplate() {
      final int pos = m_list.getSelectedIndex();
      if (pos > 0) {
         // -1 because first item is "New Template"
         m_templateCollection.RemoveElementAt(pos - 1);
         m_list.delete(pos);
      }
      m_display.setCurrent(m_list);
   }

   private void deleteTemplateConfirmation() {
      final int pos = m_list.getSelectedIndex();
      if (pos <= 0) {
         // New Template item selected or none
         return;
      }

      // Create form only once, if it's not created yet
      if (null == m_frmDeleteConfirmation)
      {
         Item item = new StringItem("", "Do you want to delete this template?");
         m_frmDeleteConfirmation = new Form("Confirmation",
            new Item[] { item });
         m_frmDeleteConfirmation.addCommand(m_cmdYes);
         m_frmDeleteConfirmation.addCommand(m_cmdNo);
         m_frmDeleteConfirmation.setCommandListener(this);
      }

      m_display.setCurrent(m_frmDeleteConfirmation);
   }


   private void startEditingItem() {
      final int pos = m_list.getSelectedIndex();
      if (pos <= 0) {
         // New Template item selected or none
         return;
      }

      getTxtboxTemplate().setTitle(c_editTemplateBoxTitle);
      final String text = m_list.getString(pos);
      getTxtboxTemplate().setString(text);
      m_display.setCurrent(getTxtboxTemplate());
      m_editingTemplate = true;
   }

   private void selectItem() {
      final int pos = m_list.getSelectedIndex();
      final String text = m_list.getString(pos);
      m_callback.OnSelectTemplate(text);
   }

   private void createNewTweetBasedOnTemplate() {
      final int pos = m_list.getSelectedIndex();
      final String text = m_list.getString(pos);
      m_callback.OnCreateNewTweet(text);
   }
}