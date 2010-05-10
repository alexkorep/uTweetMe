/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uTweetMe;

import java.io.IOException;
import java.io.InputStream;
import javax.microedition.io.ConnectionNotFoundException;
import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
import uTweetMe.UI.UITweetActionManager;
import uTweetMe.UI.UrlLauncher;

/**
 * @author Alexey
 */
public class UTweetMIDlet extends MIDlet
        implements CommandListener, TwitterPosterStatusCallback, 
        DownloadableCollectionStatusCallback, ItemCommandListener,
        uTweetMe.UI.TemplatesCallback,  uTweetMe.UI.UrlLauncherCallback,
        uTweetMe.UI.UITweetActionCallback {
   
    private boolean midletPaused = false;

    private uTweetMeApp m_app = null;

    private UrlLauncher m_urlLauncher = null;

    private UITweetActionManager m_tweetActionManager = null;

    /**
     *  Flag indicating that J2ME application is running in background
     */
    private boolean m_runningInBackground; 
    
    /**
     * Form to show when Back is pressed in the Edit Update form
     */
    private Displayable m_displayableToReturnFromEdit;
    
    /**
     * UpdateCollection item position in the main form list 
     */
    private static final int c_timelinesItemPos = 1;
    private static final String c_timelinesTitle = "Inbox";

    private static final int c_OutboxItemPos = 2;
    private static final String c_outboxListDisplayableTitle = "Outbox";
    
    private static final int c_DraftsItemPos = 3;
    private static final String c_draftsListDisplayableTitle = "Drafts";
    
    private static final String c_readmeFileName = "/readme.txt";

    private DownloadableCollection m_currentTimeline = null;

    ///@brief position of tweet being viewed
    private int m_currentTweetNo = 0;

    ///@brief ID of tweet which was last in list before downloading previous
    ///       page
    private long m_lastTweetBeforeDownloadOlder = 0;

    ///@breif Tweet ID being replied to
    private long m_replyToId = 0;

    ///@brief In reply to label displayed in tweet details form
    private String c_inReplyToTitle = "in reply to ";

    private static final String c_nextPage = "[ more ]";

    ///@brief Position of the first search timeline in the timieline list
    private static final int c_firstSearchTimelinePos = 4;

	 private static final String c_longTweetWarning =
		"\n----\n" +
		"Warning: your tweet length is #chars# characters which exceeds 140 character " +
		"limit. First 140 characters will be posted to Twitter with the link to the " +
		"full text posted to TwiGu.ru service. Please note that full text at TwiGu.ru " +
		"might be visible to everyone even if you posted a direct message or " +
		"protected your updates on Twitter. Continue?";

	 //<editor-fold defaultstate="collapsed" desc=" Generated Fields ">//GEN-BEGIN:|fields|0|
	 private Command backCommand;
	 private Command cmdPost;
	 private Command cmdReply;
	 private Command cmdDirectMessage;
	 private Command cmdBackExit;
	 private Command cmdOK;
	 private Command cmdReceive;
	 private Command cmdMarkTweetUnread;
	 private Command itemCommand;
	 private Command cmdMarkAllUnread;
	 private Command cmdReceiveAll;
	 private Command cmdMarkAllRead;
	 private Command cmdDeleteSearch;
	 private Command okCommand;
	 private Command cmdClearCache;
	 private Command stopCommand;
	 private Command cmdShowReply;
	 private Command cmdAddSearch;
	 private Command cmdCancelDownloading;
	 private Command cmdBack;
	 private Command cmdRetweet;
	 private Command cmdSaveAsTemplate;
	 private Command cmdDeleteItem;
	 private Command cmdDeleteTweet;
	 private Command cmdOpenUrls;
	 private Command cmdTemplates;
	 private Command cmdEditItem;
	 private Command cmdNo;
	 private Command cmdYes;
	 private Command cmdBackClose;
	 private List listTimeline;
	 private Form frmSaveToDraftsConfirm;
	 private StringItem stringItem;
	 private TextBox txtboxUpdate;
	 private List listTimelines;
	 private Form frmConfig;
	 private TextField frmConfigTxtPassword;
	 private TextField frmConfigTxtUserName;
	 private ChoiceGroup frmConfigOnStartGroup;
	 private Form frmTweetDetails;
	 private StringItem txtInReplyTo;
	 private StringItem txtTweetDetailsText;
	 private List listOutbox;
	 private Alert alertSaveSearchConfirm;
	 private Form frmDownloadProgress;
	 private Gauge frmDownloadProgressBar;
	 private TextBox txtboxSearch;
	 private List listMainForm;
	 private Alert alertError;
	 private List listDrafts;
	 private Form frmPreview;
	 private StringItem frmPreviewText;
	 private Form frmReadme;
	 private StringItem frmReadmeText;
	 private Image imgDirect;
	 private Image imgFriends;
	 private Image imgReplies;
	 private Image imgDirectOutbox;
	 private Ticker ticker;
	 private Image imgDownload;
	 private Image imgTweetRead;
	 private Image imgTweet;
	 private Image imgSearch;
	 private Image imgSettings;
	 private Image imgDrafts;
	 private Image imgOutbox;
	 private Image imgNewUpdate;
	 private Image imgReadme;
	 //</editor-fold>//GEN-END:|fields|0|
   // Timelines view items
   // Friends timeline item
   private static final String[] c_timelinesItemTitles = {
      "Friends + me",
      "Replies",
      "Direct - Inbox",
      "Direct - Sent"
   };

   private final Image[] c_timelinesItemImages = {
      getImgFriends(),
      getImgReplies(),
      getImgDirect(),
      getImgDirectOutbox()
   };

   /**
    * The UTweeMIDlet constructor.
    */
   public UTweetMIDlet() {
      m_app = new uTweetMeApp(this, this);
      uTweetMe.UI.TemplateWindowManager.GetInstance().Initialize(m_app.GetTemplates(),
         getDisplay(), this);
      m_urlLauncher = new uTweetMe.UI.UrlLauncher(getDisplay(), this);
      m_tweetActionManager = new UITweetActionManager(getDisplay(), this);
      m_runningInBackground = false;

   }

	//<editor-fold defaultstate="collapsed" desc=" Generated Methods ">//GEN-BEGIN:|methods|0|
	//</editor-fold>//GEN-END:|methods|0|

	//<editor-fold defaultstate="collapsed" desc=" Generated Method: initialize ">//GEN-BEGIN:|0-initialize|0|0-preInitialize
	/**
	 * Initilizes the application.
	 * It is called only once when the MIDlet is started. The method is called before the <code>startMIDlet</code> method.
	 */
	private void initialize() {//GEN-END:|0-initialize|0|0-preInitialize
        // write pre-initialize user code here
		try {//GEN-BEGIN:|0-initialize|1|232-@java.io.IOException
			imgSettings = Image.createImage("/settings.PNG");
		} catch (java.io.IOException e) {//GEN-END:|0-initialize|1|232-@java.io.IOException
            e.printStackTrace();
		}//GEN-BEGIN:|0-initialize|2|231-@java.io.IOException
		try {
			imgDrafts = Image.createImage("/drafts.PNG");
		} catch (java.io.IOException e) {//GEN-END:|0-initialize|2|231-@java.io.IOException
            e.printStackTrace();
		}//GEN-BEGIN:|0-initialize|3|230-@java.io.IOException
		try {
			imgOutbox = Image.createImage("/outbox.PNG");
		} catch (java.io.IOException e) {//GEN-END:|0-initialize|3|230-@java.io.IOException
            e.printStackTrace();
		}//GEN-BEGIN:|0-initialize|4|229-@java.io.IOException
		try {
			imgNewUpdate = Image.createImage("/newupdate.PNG");
		} catch (java.io.IOException e) {//GEN-END:|0-initialize|4|229-@java.io.IOException
            e.printStackTrace();
		}//GEN-BEGIN:|0-initialize|5|244-@java.io.IOException
		try {
			imgReadme = Image.createImage("/readme.png");
		} catch (java.io.IOException e) {//GEN-END:|0-initialize|5|244-@java.io.IOException
            e.printStackTrace();
		}//GEN-LINE:|0-initialize|6|0-postInitialize
        // write post-initialize user code here
	}//GEN-BEGIN:|0-initialize|7|
	//</editor-fold>//GEN-END:|0-initialize|7|

	//<editor-fold defaultstate="collapsed" desc=" Generated Method: startMIDlet ">//GEN-BEGIN:|3-startMIDlet|0|3-preAction
	/**
	 * Performs an action assigned to the Mobile Device - MIDlet Started point.
	 */
	public void startMIDlet() {//GEN-END:|3-startMIDlet|0|3-preAction
        // write pre-action user code here
        m_runningInBackground = false;
		  IsConfigured();//GEN-LINE:|3-startMIDlet|1|3-postAction
        // write post-action user code here
        m_displayableToReturnFromEdit = getListMainForm();
        updateDraftsMainWindowItem();
        updateDraftsDisplayable(true);
	}//GEN-BEGIN:|3-startMIDlet|2|
	//</editor-fold>//GEN-END:|3-startMIDlet|2|

	//<editor-fold defaultstate="collapsed" desc=" Generated Method: resumeMIDlet ">//GEN-BEGIN:|4-resumeMIDlet|0|4-preAction
	/**
	 * Performs an action assigned to the Mobile Device - MIDlet Resumed point.
	 */
	public void resumeMIDlet() {//GEN-END:|4-resumeMIDlet|0|4-preAction
        // write pre-action user code here
        m_runningInBackground = false;
		  switchDisplayable(null, getListMainForm());//GEN-LINE:|4-resumeMIDlet|1|4-postAction
        // write post-action user code here
	}//GEN-BEGIN:|4-resumeMIDlet|2|
	//</editor-fold>//GEN-END:|4-resumeMIDlet|2|

	//<editor-fold defaultstate="collapsed" desc=" Generated Method: switchDisplayable ">//GEN-BEGIN:|5-switchDisplayable|0|5-preSwitch
	/**
	 * Switches a current displayable in a display. The <code>display</code> instance is taken from <code>getDisplay</code> method. This method is used by all actions in the design for switching displayable.
	 * @param alert the Alert which is temporarily set to the display; if <code>null</code>, then <code>nextDisplayable</code> is set immediately
	 * @param nextDisplayable the Displayable to be set
	 */
	public void switchDisplayable(Alert alert, Displayable nextDisplayable) {//GEN-END:|5-switchDisplayable|0|5-preSwitch
        // write pre-switch user code here
		Display display = getDisplay();//GEN-BEGIN:|5-switchDisplayable|1|5-postSwitch
		if (alert == null) {
			display.setCurrent(nextDisplayable);
		} else {
			display.setCurrent(alert, nextDisplayable);
		}//GEN-END:|5-switchDisplayable|1|5-postSwitch
        // write post-switch user code here
	}//GEN-BEGIN:|5-switchDisplayable|2|
	//</editor-fold>//GEN-END:|5-switchDisplayable|2|

	//<editor-fold defaultstate="collapsed" desc=" Generated Method: commandAction for Displayables ">//GEN-BEGIN:|7-commandAction|0|7-preCommandAction
	/**
	 * Called by a system to indicated that a command has been invoked on a particular displayable.
	 * @param command the Command that was invoked
	 * @param displayable the Displayable where the command was invoked
	 */
	public void commandAction(Command command, Displayable displayable) {//GEN-END:|7-commandAction|0|7-preCommandAction
        // write pre-action user code here
		if (displayable == alertSaveSearchConfirm) {//GEN-BEGIN:|7-commandAction|1|384-preAction
			if (command == cmdNo) {//GEN-END:|7-commandAction|1|384-preAction
            // write pre-action user code here
//GEN-LINE:|7-commandAction|2|384-postAction
            // write post-action user code here
			} else if (command == cmdYes) {//GEN-LINE:|7-commandAction|3|383-preAction
            // write pre-action user code here
//GEN-LINE:|7-commandAction|4|383-postAction
            // write post-action user code here
			}//GEN-BEGIN:|7-commandAction|5|227-preAction
		} else if (displayable == frmConfig) {
			if (command == cmdBackClose) {//GEN-END:|7-commandAction|5|227-preAction
                // write pre-action user code here
				switchDisplayable(null, getListMainForm());//GEN-LINE:|7-commandAction|6|227-postAction
                // write post-action user code here
                UpdateConfig();
			}//GEN-BEGIN:|7-commandAction|7|358-preAction
		} else if (displayable == frmDownloadProgress) {
			if (command == cmdBack) {//GEN-END:|7-commandAction|7|358-preAction
            // write pre-action user code here
            switchDisplayable(null, getListTimelines());
//GEN-LINE:|7-commandAction|8|358-postAction
            // write post-action user code here
			} else if (command == cmdCancelDownloading) {//GEN-LINE:|7-commandAction|9|362-preAction
            // write pre-action user code here
            m_currentTimeline.Interrupt();
            switchDisplayable(null, getListTimeline());
				//GEN-LINE:|7-commandAction|10|362-postAction
            // write post-action user code here
			}//GEN-BEGIN:|7-commandAction|11|261-preAction
		} else if (displayable == frmPreview) {
			if (command == cmdBack) {//GEN-END:|7-commandAction|11|261-preAction
                // write pre-action user code here
				switchDisplayable(null, getTxtboxUpdate());//GEN-LINE:|7-commandAction|12|261-postAction
                // write post-action user code here
			} else if (command == cmdPost) {//GEN-LINE:|7-commandAction|13|237-preAction
                // write pre-action user code here
                PostUpdate();
					 switchDisplayable(null, getListMainForm());//GEN-LINE:|7-commandAction|14|237-postAction
                // write post-action user code here
			}//GEN-BEGIN:|7-commandAction|15|248-preAction
		} else if (displayable == frmReadme) {
			if (command == cmdBack) {//GEN-END:|7-commandAction|15|248-preAction
                // write pre-action user code here
				switchDisplayable(null, getListMainForm());//GEN-LINE:|7-commandAction|16|248-postAction
                // write post-action user code here
			}//GEN-BEGIN:|7-commandAction|17|266-preAction
		} else if (displayable == frmSaveToDraftsConfirm) {
			if (command == cmdNo) {//GEN-END:|7-commandAction|17|266-preAction
                // write pre-action user code here
                getTxtboxUpdate().setString("");
					 switchDisplayable(null, getListMainForm());//GEN-LINE:|7-commandAction|18|266-postAction
                // write post-action user code here
			} else if (command == cmdYes) {//GEN-LINE:|7-commandAction|19|265-preAction
                // write pre-action user code here
                saveUpdateToDrafts(getTxtboxUpdate().getString(), m_replyToId);
                getTxtboxUpdate().setString("");
					 switchDisplayable(null, getListMainForm());//GEN-LINE:|7-commandAction|20|265-postAction
                // write post-action user code here
			}//GEN-BEGIN:|7-commandAction|21|302-preAction
		} else if (displayable == frmTweetDetails) {
			if (command == cmdBack) {//GEN-END:|7-commandAction|21|302-preAction
            // write pre-action user code here
            updateListTimelineTitle();
            m_replyToId = 0;
            goToListTimeline();
//GEN-LINE:|7-commandAction|22|302-postAction
            // write post-action user code here
			} else if (command == cmdDeleteTweet) {//GEN-LINE:|7-commandAction|23|398-preAction
            // write pre-action user code here
            TwitterUpdate update = m_currentTimeline.ElementAt(m_currentTweetNo);
            m_tweetActionManager.DeleteTweet(update);
//GEN-LINE:|7-commandAction|24|398-postAction
            // write post-action user code here
			} else if (command == cmdDirectMessage) {//GEN-LINE:|7-commandAction|25|314-preAction
            // write pre-action user code here
            m_displayableToReturnFromEdit = getFrmTweetDetails();
            replyDirectToTweet();
				//GEN-LINE:|7-commandAction|26|314-postAction
            // write post-action user code here
			} else if (command == cmdMarkTweetUnread) {//GEN-LINE:|7-commandAction|27|343-preAction
            // write pre-action user code here
            markViewedTweetUnread();
//GEN-LINE:|7-commandAction|28|343-postAction
            // write post-action user code here
			} else if (command == cmdOpenUrls) {//GEN-LINE:|7-commandAction|29|391-preAction
            // write pre-action user code here
            m_urlLauncher.ShowExtractUrlsList(getTxtTweetDetailsText().getText());
//GEN-LINE:|7-commandAction|30|391-postAction
            // write post-action user code here
			} else if (command == cmdReply) {//GEN-LINE:|7-commandAction|31|313-preAction
            // write pre-action user code here
            m_displayableToReturnFromEdit = getFrmTweetDetails();
            replyToTweet();
				//GEN-LINE:|7-commandAction|32|313-postAction
            // write post-action user code here
			} else if (command == cmdRetweet) {//GEN-LINE:|7-commandAction|33|396-preAction
            // write pre-action user code here
            m_displayableToReturnFromEdit = getFrmTweetDetails();
            retweet();
//GEN-LINE:|7-commandAction|34|396-postAction
            // write post-action user code here
			} else if (command == cmdSaveAsTemplate) {//GEN-LINE:|7-commandAction|35|394-preAction
            // write pre-action user code here
            saveAsTemplate(getTxtTweetDetailsText().getText());
//GEN-LINE:|7-commandAction|36|394-postAction
            // write post-action user code here
			}//GEN-BEGIN:|7-commandAction|37|154-preAction
		} else if (displayable == listDrafts) {
			if (command == List.SELECT_COMMAND) {//GEN-END:|7-commandAction|37|154-preAction
                // write pre-action user code here
				listDraftsAction();//GEN-LINE:|7-commandAction|38|154-postAction
                // write post-action user code here
			} else if (command == cmdBack) {//GEN-LINE:|7-commandAction|39|156-preAction
                // write pre-action user code here
				switchDisplayable(null, getListMainForm());//GEN-LINE:|7-commandAction|40|156-postAction
                // write post-action user code here
			} else if (command == cmdDeleteItem) {//GEN-LINE:|7-commandAction|41|159-preAction
                // write pre-action user code here
				deleteDraftItem();//GEN-LINE:|7-commandAction|42|159-postAction
                // write post-action user code here
			} else if (command == cmdEditItem) {//GEN-LINE:|7-commandAction|43|167-preAction
                // write pre-action user code here

                // edit the text currently selected
                //
                editDraftUpdate();

                // We should return to dafts if we cancel posting from update form
                //
                m_displayableToReturnFromEdit = getListDrafts();

					 switchDisplayable(null, getTxtboxUpdate());//GEN-LINE:|7-commandAction|44|167-postAction
                // write post-action user code here
			}//GEN-BEGIN:|7-commandAction|45|121-preAction
		} else if (displayable == listMainForm) {
			if (command == List.SELECT_COMMAND) {//GEN-END:|7-commandAction|45|121-preAction
                // write pre-action user code here
				listMainFormAction();//GEN-LINE:|7-commandAction|46|121-postAction
                // write post-action user code here
			} else if (command == cmdBackExit) {//GEN-LINE:|7-commandAction|47|128-preAction
                // write pre-action user code here
				exitMIDlet();//GEN-LINE:|7-commandAction|48|128-postAction
                // write post-action user code here
			}//GEN-BEGIN:|7-commandAction|49|84-preAction
		} else if (displayable == listOutbox) {
			if (command == List.SELECT_COMMAND) {//GEN-END:|7-commandAction|49|84-preAction
                // write pre-action user code here
				listOutboxAction();//GEN-LINE:|7-commandAction|50|84-postAction
                // write post-action user code here
			} else if (command == cmdBack) {//GEN-LINE:|7-commandAction|51|135-preAction
                // write pre-action user code here
				switchDisplayable(null, getListMainForm());//GEN-LINE:|7-commandAction|52|135-postAction
                // write post-action user code here
			}//GEN-BEGIN:|7-commandAction|53|276-preAction
		} else if (displayable == listTimeline) {
			if (command == List.SELECT_COMMAND) {//GEN-END:|7-commandAction|53|276-preAction
            // write pre-action user code here
				listTimelineAction();//GEN-LINE:|7-commandAction|54|276-postAction
             // write post-action user code here
			} else if (command == backCommand) {//GEN-LINE:|7-commandAction|55|288-preAction
            // write pre-action user code here
            updateTimelinesItemTitles();
            updateTimelinesWindowTitle();
				switchDisplayable(null, getListTimelines());//GEN-LINE:|7-commandAction|56|288-postAction
               // write post-action user code here
			} else if (command == cmdClearCache) {//GEN-LINE:|7-commandAction|57|378-preAction
            // write pre-action user code here
            clearTimeline();
//GEN-LINE:|7-commandAction|58|378-postAction
            // write post-action user code here
			} else if (command == cmdMarkAllRead) {//GEN-LINE:|7-commandAction|59|336-preAction
            // write pre-action user code here
            markAllTweetsRead(true);
//GEN-LINE:|7-commandAction|60|336-postAction
            // write post-action user code here
			} else if (command == cmdMarkAllUnread) {//GEN-LINE:|7-commandAction|61|338-preAction
            // write pre-action user code here
            markAllTweetsRead(false);
//GEN-LINE:|7-commandAction|62|338-postAction
            // write post-action user code here
			} else if (command == cmdReceive) {//GEN-LINE:|7-commandAction|63|295-preAction
            // write pre-action user code here
            receive();
//GEN-LINE:|7-commandAction|64|295-postAction
            // write post-action user code here
			}//GEN-BEGIN:|7-commandAction|65|318-preAction
		} else if (displayable == listTimelines) {
			if (command == List.SELECT_COMMAND) {//GEN-END:|7-commandAction|65|318-preAction
            // write pre-action user code here
				listTimelinesAction();//GEN-LINE:|7-commandAction|66|318-postAction
            // write post-action user code here
			} else if (command == backCommand) {//GEN-LINE:|7-commandAction|67|325-preAction
            // write pre-action user code here
            updateMainWindowTimelinesItem();
            leaveListTimeline();
//GEN-LINE:|7-commandAction|68|325-postAction
            // write post-action user code here
			} else if (command == cmdAddSearch) {//GEN-LINE:|7-commandAction|69|367-preAction
            // write pre-action user code here
            getTxtboxSearch().setString("");
				switchDisplayable(null, getTxtboxSearch());//GEN-LINE:|7-commandAction|70|367-postAction
            // write post-action user code here
			} else if (command == cmdDeleteSearch) {//GEN-LINE:|7-commandAction|71|376-preAction
            // write pre-action user code here
            deleteSearchTimeline();
//GEN-LINE:|7-commandAction|72|376-postAction
            // write post-action user code here
			} else if (command == cmdReceiveAll) {//GEN-LINE:|7-commandAction|73|345-preAction
            // write pre-action user code here
            receiveAllTimelines();
//GEN-LINE:|7-commandAction|74|345-postAction
            // write post-action user code here
			}//GEN-BEGIN:|7-commandAction|75|370-preAction
		} else if (displayable == txtboxSearch) {
			if (command == cmdBack) {//GEN-END:|7-commandAction|75|370-preAction
            // write pre-action user code here
				switchDisplayable(null, getListTimelines());//GEN-LINE:|7-commandAction|76|370-postAction
            // write post-action user code here
			} else if (command == okCommand) {//GEN-LINE:|7-commandAction|77|372-preAction
            // write pre-action user code here
            addSearchTimeline();
//GEN-LINE:|7-commandAction|78|372-postAction
            // write post-action user code here
			}//GEN-BEGIN:|7-commandAction|79|137-preAction
		} else if (displayable == txtboxUpdate) {
			if (command == cmdBack) {//GEN-END:|7-commandAction|79|137-preAction
                // write pre-action user code here
                //switchDisplayable(null, m_displayableToReturnFromEdit);
				isUpdateEmpty();//GEN-LINE:|7-commandAction|80|137-postAction
                // write post-action user code here
			} else if (command == cmdOK) {//GEN-LINE:|7-commandAction|81|223-preAction
                // write pre-action user code here
                if (getTxtboxUpdate().getString().length() == 0)
                {
                    // Empty update string, do nothing here.
                    return;
                }
					 final String previewText = getPreviewText(getTxtboxUpdate().getString());
                getFrmPreviewText().setText(previewText);
					 switchDisplayable(null, getFrmPreview());//GEN-LINE:|7-commandAction|82|223-postAction
                // write post-action user code here
			} else if (command == cmdSaveAsTemplate) {//GEN-LINE:|7-commandAction|83|393-preAction
            // write pre-action user code here
            saveAsTemplate(getTxtboxUpdate().getString());
//GEN-LINE:|7-commandAction|84|393-postAction
            // write post-action user code here
			} else if (command == cmdTemplates) {//GEN-LINE:|7-commandAction|85|389-preAction
            // write pre-action user code here
            uTweetMe.UI.TemplateWindowManager.GetInstance().SelectTemplate();
//GEN-LINE:|7-commandAction|86|389-postAction
            // write post-action user code here
			}//GEN-BEGIN:|7-commandAction|87|7-postCommandAction
		}//GEN-END:|7-commandAction|87|7-postCommandAction
        // write post-action user code here
	}//GEN-BEGIN:|7-commandAction|88|
	//</editor-fold>//GEN-END:|7-commandAction|88|

	//<editor-fold defaultstate="collapsed" desc=" Generated Getter: cmdPost ">//GEN-BEGIN:|28-getter|0|28-preInit
	/**
	 * Returns an initiliazed instance of cmdPost component.
	 * @return the initialized component instance
	 */
	public Command getCmdPost() {
		if (cmdPost == null) {//GEN-END:|28-getter|0|28-preInit
            // write pre-init user code here
			cmdPost = new Command("Post", Command.OK, 0);//GEN-LINE:|28-getter|1|28-postInit
            // write post-init user code here
		}//GEN-BEGIN:|28-getter|2|
		return cmdPost;
	}
	//</editor-fold>//GEN-END:|28-getter|2|

	//<editor-fold defaultstate="collapsed" desc=" Generated Getter: txtboxUpdate ">//GEN-BEGIN:|27-getter|0|27-preInit
	/**
	 * Returns an initiliazed instance of txtboxUpdate component.
	 * @return the initialized component instance
	 */
	public TextBox getTxtboxUpdate() {
		if (txtboxUpdate == null) {//GEN-END:|27-getter|0|27-preInit
            // write pre-init user code here
			txtboxUpdate = new TextBox("What are you doing?", null, 4000, TextField.ANY | TextField.INITIAL_CAPS_SENTENCE);//GEN-BEGIN:|27-getter|1|27-postInit
			txtboxUpdate.addCommand(getCmdOK());
			txtboxUpdate.addCommand(getCmdBack());
			txtboxUpdate.addCommand(getCmdTemplates());
			txtboxUpdate.addCommand(getCmdSaveAsTemplate());
			txtboxUpdate.setCommandListener(this);//GEN-END:|27-getter|1|27-postInit
            // write post-init user code here
		}//GEN-BEGIN:|27-getter|2|
		return txtboxUpdate;
	}
	//</editor-fold>//GEN-END:|27-getter|2|

	//<editor-fold defaultstate="collapsed" desc=" Generated Getter: cmdBackExit ">//GEN-BEGIN:|43-getter|0|43-preInit
	/**
	 * Returns an initiliazed instance of cmdBackExit component.
	 * @return the initialized component instance
	 */
	public Command getCmdBackExit() {
		if (cmdBackExit == null) {//GEN-END:|43-getter|0|43-preInit
            // write pre-init user code here
			cmdBackExit = new Command("Exit", Command.BACK, 0);//GEN-LINE:|43-getter|1|43-postInit
            // write post-init user code here
		}//GEN-BEGIN:|43-getter|2|
		return cmdBackExit;
	}
	//</editor-fold>//GEN-END:|43-getter|2|

	//<editor-fold defaultstate="collapsed" desc=" Generated Getter: cmdOK ">//GEN-BEGIN:|55-getter|0|55-preInit
	/**
	 * Returns an initiliazed instance of cmdOK component.
	 * @return the initialized component instance
	 */
	public Command getCmdOK() {
		if (cmdOK == null) {//GEN-END:|55-getter|0|55-preInit
            // write pre-init user code here
			cmdOK = new Command("OK", Command.OK, 0);//GEN-LINE:|55-getter|1|55-postInit
            // write post-init user code here
		}//GEN-BEGIN:|55-getter|2|
		return cmdOK;
	}
	//</editor-fold>//GEN-END:|55-getter|2|

	//<editor-fold defaultstate="collapsed" desc=" Generated Getter: frmConfig ">//GEN-BEGIN:|51-getter|0|51-preInit
	/**
	 * Returns an initiliazed instance of frmConfig component.
	 * @return the initialized component instance
	 */
	public Form getFrmConfig() {
		if (frmConfig == null) {//GEN-END:|51-getter|0|51-preInit
            // write pre-init user code here
			frmConfig = new Form("Config", new Item[] { getFrmConfigTxtUserName(), getFrmConfigTxtPassword(), getFrmConfigOnStartGroup() });//GEN-BEGIN:|51-getter|1|51-postInit
			frmConfig.addCommand(getCmdBackClose());
			frmConfig.setCommandListener(this);//GEN-END:|51-getter|1|51-postInit
            // write post-init user code here
		}//GEN-BEGIN:|51-getter|2|
		return frmConfig;
	}
	//</editor-fold>//GEN-END:|51-getter|2|

	//<editor-fold defaultstate="collapsed" desc=" Generated Getter: frmConfigTxtUserName ">//GEN-BEGIN:|53-getter|0|53-preInit
	/**
	 * Returns an initiliazed instance of frmConfigTxtUserName component.
	 * @return the initialized component instance
	 */
	public TextField getFrmConfigTxtUserName() {
		if (frmConfigTxtUserName == null) {//GEN-END:|53-getter|0|53-preInit
            // write pre-init user code here
			frmConfigTxtUserName = new TextField("User name", null, 32, TextField.ANY | TextField.NON_PREDICTIVE);//GEN-BEGIN:|53-getter|1|53-postInit
			frmConfigTxtUserName.setInitialInputMode("UCB_BASIC_LATIN");//GEN-END:|53-getter|1|53-postInit
            // write post-init user code here
            frmConfigTxtUserName.setString(Settings.GetInstance().GetSettings().m_name);
		}//GEN-BEGIN:|53-getter|2|
		return frmConfigTxtUserName;
	}
	//</editor-fold>//GEN-END:|53-getter|2|

	//<editor-fold defaultstate="collapsed" desc=" Generated Method: IsConfigured ">//GEN-BEGIN:|63-if|0|63-preIf
	/**
	 * Performs an action assigned to the IsConfigured if-point.
	 */
	public void IsConfigured() {//GEN-END:|63-if|0|63-preIf
        // enter pre-if user code here
		if ((Settings.GetInstance().GetSettings().m_name.length() != 0) &&//GEN-BEGIN:|63-if|1|64-preAction
				  (Settings.GetInstance().GetSettings().m_password.length() != 0)) {//GEN-END:|63-if|1|64-preAction
            // write pre-action user code here
            m_displayableToReturnFromEdit = getListMainForm();
            if (Settings.GetInstance().GetSettings().m_onStartNewTweet)
            {
					switchDisplayable(null, getTxtboxUpdate());//GEN-LINE:|63-if|2|64-postAction
            }
            else
            {
               switchDisplayable(null, getListMainForm());
            }
            // write post-action user code here
		} else {//GEN-LINE:|63-if|3|65-preAction
            // write pre-action user code here
			switchDisplayable(null, getFrmConfig());//GEN-LINE:|63-if|4|65-postAction
            // write post-action user code here
		}//GEN-LINE:|63-if|5|63-postIf
        // enter post-if user code here
	}//GEN-BEGIN:|63-if|6|
	//</editor-fold>//GEN-END:|63-if|6|

	//<editor-fold defaultstate="collapsed" desc=" Generated Getter: listOutbox ">//GEN-BEGIN:|82-getter|0|82-preInit
	/**
	 * Returns an initiliazed instance of listOutbox component.
	 * @return the initialized component instance
	 */
	public List getListOutbox() {
		if (listOutbox == null) {//GEN-END:|82-getter|0|82-preInit
            // write pre-init user code here
			listOutbox = new List("Update List", Choice.IMPLICIT);//GEN-BEGIN:|82-getter|1|82-postInit
			listOutbox.addCommand(getCmdBack());
			listOutbox.setCommandListener(this);//GEN-END:|82-getter|1|82-postInit
            // write post-init user code here
		}//GEN-BEGIN:|82-getter|2|
		return listOutbox;
	}
	//</editor-fold>//GEN-END:|82-getter|2|

	//<editor-fold defaultstate="collapsed" desc=" Generated Method: listOutboxAction ">//GEN-BEGIN:|82-action|0|82-preAction
	/**
	 * Performs an action assigned to the selected list element in the listOutbox component.
	 */
	public void listOutboxAction() {//GEN-END:|82-action|0|82-preAction
        // enter pre-action user code here
		String __selectedString = getListOutbox().getString(getListOutbox().getSelectedIndex());//GEN-LINE:|82-action|1|82-postAction
        // enter post-action user code here
	}//GEN-BEGIN:|82-action|2|
	//</editor-fold>//GEN-END:|82-action|2|

	//<editor-fold defaultstate="collapsed" desc=" Generated Getter: frmConfigTxtPassword ">//GEN-BEGIN:|54-getter|0|54-preInit
	/**
	 * Returns an initiliazed instance of frmConfigTxtPassword component.
	 * @return the initialized component instance
	 */
	public TextField getFrmConfigTxtPassword() {
		if (frmConfigTxtPassword == null) {//GEN-END:|54-getter|0|54-preInit
            // write pre-init user code here
			frmConfigTxtPassword = new TextField("Password", null, 32, TextField.ANY | TextField.PASSWORD | TextField.NON_PREDICTIVE);//GEN-LINE:|54-getter|1|54-postInit
            // write post-init user code here
            frmConfigTxtPassword.setString(Settings.GetInstance().GetSettings().m_password);
		}//GEN-BEGIN:|54-getter|2|
		return frmConfigTxtPassword;
	}
	//</editor-fold>//GEN-END:|54-getter|2|

	//<editor-fold defaultstate="collapsed" desc=" Generated Getter: listMainForm ">//GEN-BEGIN:|120-getter|0|120-preInit
	/**
	 * Returns an initiliazed instance of listMainForm component.
	 * @return the initialized component instance
	 */
	public List getListMainForm() {
		if (listMainForm == null) {//GEN-END:|120-getter|0|120-preInit
            // write pre-init user code here
			listMainForm = new List("uTweetMe", Choice.IMPLICIT);//GEN-BEGIN:|120-getter|1|120-postInit
			listMainForm.append("New Update", imgNewUpdate);
			listMainForm.append("Inbox", null);
			listMainForm.append("Outbox", null);
			listMainForm.append("Drafts", null);
			listMainForm.append("Templates", getImgFriends());
			listMainForm.append("Settings", imgSettings);
			listMainForm.append("Readme", imgReadme);
			listMainForm.addCommand(getCmdBackExit());
			listMainForm.setCommandListener(this);
			listMainForm.setFitPolicy(Choice.TEXT_WRAP_DEFAULT);
			listMainForm.setSelectedFlags(new boolean[] { false, false, false, false, false, false, false });//GEN-END:|120-getter|1|120-postInit
            // write post-init user code here
		}//GEN-BEGIN:|120-getter|2|
		return listMainForm;
	}
	//</editor-fold>//GEN-END:|120-getter|2|

	//<editor-fold defaultstate="collapsed" desc=" Generated Method: listMainFormAction ">//GEN-BEGIN:|120-action|0|120-preAction
	/**
	 * Performs an action assigned to the selected list element in the listMainForm component.
	 */
	public void listMainFormAction() {//GEN-END:|120-action|0|120-preAction
        // enter pre-action user code here
		switch (getListMainForm().getSelectedIndex()) {//GEN-BEGIN:|120-action|1|123-preAction
			case 0://GEN-END:|120-action|1|123-preAction
                // write pre-action user code here
                //
                // New Post item
                //
                m_displayableToReturnFromEdit = getListMainForm();
					 switchDisplayable(null, getTxtboxUpdate());//GEN-LINE:|120-action|2|123-postAction
                // write post-action user code here
					 break;//GEN-BEGIN:|120-action|3|290-preAction
			case 1://GEN-END:|120-action|3|290-preAction
            // write pre-action user code here
            updateTimelinesItemTitles();
            updateTimelinesWindowTitle();
				switchDisplayable(null, getListTimelines());//GEN-LINE:|120-action|4|290-postAction
            // write post-action user code here
				break;//GEN-BEGIN:|120-action|5|141-preAction
			case 2://GEN-END:|120-action|5|141-preAction
                // write pre-action user code here
                //
                // UpdateCollection item
                //
				switchDisplayable(null, getListOutbox());//GEN-LINE:|120-action|6|141-postAction
                // write post-action user code here
                updateOutboxListDisplayable();
					 break;//GEN-BEGIN:|120-action|7|150-preAction
			case 3://GEN-END:|120-action|7|150-preAction
                // write pre-action user code here
                //
                // Drafts item
                //
				switchDisplayable(null, getListDrafts());//GEN-LINE:|120-action|8|150-postAction
                // write post-action user code here
				break;//GEN-BEGIN:|120-action|9|387-preAction
			case 4://GEN-END:|120-action|9|387-preAction
            // write pre-action user code here
            uTweetMe.UI.TemplateWindowManager.GetInstance().Show();
//GEN-LINE:|120-action|10|387-postAction
            // write post-action user code here
				break;//GEN-BEGIN:|120-action|11|132-preAction
			case 5://GEN-END:|120-action|11|132-preAction
                // write pre-action user code here
                //
                // Settings item
                //
				switchDisplayable(null, getFrmConfig());//GEN-LINE:|120-action|12|132-postAction
                // write post-action user code here
				break;//GEN-BEGIN:|120-action|13|243-preAction
			case 6://GEN-END:|120-action|13|243-preAction
                // write pre-action user code here
                loadReadmeText();
					 switchDisplayable(null, getFrmReadme());//GEN-LINE:|120-action|14|243-postAction
                // write post-action user code here
					 break;//GEN-BEGIN:|120-action|15|120-postAction
		}//GEN-END:|120-action|15|120-postAction
        // enter post-action user code here
	}//GEN-BEGIN:|120-action|16|
	//</editor-fold>//GEN-END:|120-action|16|

	//<editor-fold defaultstate="collapsed" desc=" Generated Getter: cmdBack ">//GEN-BEGIN:|134-getter|0|134-preInit
	/**
	 * Returns an initiliazed instance of cmdBack component.
	 * @return the initialized component instance
	 */
	public Command getCmdBack() {
		if (cmdBack == null) {//GEN-END:|134-getter|0|134-preInit
            // write pre-init user code here
			cmdBack = new Command("Back", Command.BACK, 0);//GEN-LINE:|134-getter|1|134-postInit
            // write post-init user code here
		}//GEN-BEGIN:|134-getter|2|
		return cmdBack;
	}
	//</editor-fold>//GEN-END:|134-getter|2|

	//<editor-fold defaultstate="collapsed" desc=" Generated Getter: alertError ">//GEN-BEGIN:|142-getter|0|142-preInit
	/**
	 * Returns an initiliazed instance of alertError component.
	 * @return the initialized component instance
	 */
	public Alert getAlertError() {
		if (alertError == null) {//GEN-END:|142-getter|0|142-preInit
            // write pre-init user code here
			alertError = new Alert("Error", null, null, AlertType.ERROR);//GEN-BEGIN:|142-getter|1|142-postInit
			alertError.setTimeout(Alert.FOREVER);//GEN-END:|142-getter|1|142-postInit
            // write post-init user code here
		}//GEN-BEGIN:|142-getter|2|
		return alertError;
	}
	//</editor-fold>//GEN-END:|142-getter|2|

	//<editor-fold defaultstate="collapsed" desc=" Generated Getter: listDrafts ">//GEN-BEGIN:|153-getter|0|153-preInit
	/**
	 * Returns an initiliazed instance of listDrafts component.
	 * @return the initialized component instance
	 */
	public List getListDrafts() {
		if (listDrafts == null) {//GEN-END:|153-getter|0|153-preInit
            // write pre-init user code here
			listDrafts = new List("Drafts", Choice.IMPLICIT);//GEN-BEGIN:|153-getter|1|153-postInit
			listDrafts.addCommand(getCmdEditItem());
			listDrafts.addCommand(getCmdBack());
			listDrafts.addCommand(getCmdDeleteItem());
			listDrafts.setCommandListener(this);
			listDrafts.setSelectCommand(getCmdEditItem());
			listDrafts.setSelectedFlags(new boolean[] {  });//GEN-END:|153-getter|1|153-postInit
            // write post-init user code here
		}//GEN-BEGIN:|153-getter|2|
		return listDrafts;
	}
	//</editor-fold>//GEN-END:|153-getter|2|

	//<editor-fold defaultstate="collapsed" desc=" Generated Method: listDraftsAction ">//GEN-BEGIN:|153-action|0|153-preAction
	/**
	 * Performs an action assigned to the selected list element in the listDrafts component.
	 */
	public void listDraftsAction() {//GEN-END:|153-action|0|153-preAction
        // enter pre-action user code here
		String __selectedString = getListDrafts().getString(getListDrafts().getSelectedIndex());//GEN-LINE:|153-action|1|153-postAction
        // enter post-action user code here
	}//GEN-BEGIN:|153-action|2|
	//</editor-fold>//GEN-END:|153-action|2|

	//<editor-fold defaultstate="collapsed" desc=" Generated Getter: cmdDeleteItem ">//GEN-BEGIN:|158-getter|0|158-preInit
	/**
	 * Returns an initiliazed instance of cmdDeleteItem component.
	 * @return the initialized component instance
	 */
	public Command getCmdDeleteItem() {
		if (cmdDeleteItem == null) {//GEN-END:|158-getter|0|158-preInit
            // write pre-init user code here
			cmdDeleteItem = new Command("Delete", Command.CANCEL, 1);//GEN-LINE:|158-getter|1|158-postInit
            // write post-init user code here
		}//GEN-BEGIN:|158-getter|2|
		return cmdDeleteItem;
	}
	//</editor-fold>//GEN-END:|158-getter|2|

	//<editor-fold defaultstate="collapsed" desc=" Generated Getter: cmdEditItem ">//GEN-BEGIN:|162-getter|0|162-preInit
	/**
	 * Returns an initiliazed instance of cmdEditItem component.
	 * @return the initialized component instance
	 */
	public Command getCmdEditItem() {
		if (cmdEditItem == null) {//GEN-END:|162-getter|0|162-preInit
            // write pre-init user code here
			cmdEditItem = new Command("Edit", Command.ITEM, 0);//GEN-LINE:|162-getter|1|162-postInit
            // write post-init user code here
		}//GEN-BEGIN:|162-getter|2|
		return cmdEditItem;
	}
	//</editor-fold>//GEN-END:|162-getter|2|

	//<editor-fold defaultstate="collapsed" desc=" Generated Getter: cmdYes ">//GEN-BEGIN:|203-getter|0|203-preInit
	/**
	 * Returns an initiliazed instance of cmdYes component.
	 * @return the initialized component instance
	 */
	public Command getCmdYes() {
		if (cmdYes == null) {//GEN-END:|203-getter|0|203-preInit
            // write pre-init user code here
			cmdYes = new Command("Yes", Command.OK, 0);//GEN-LINE:|203-getter|1|203-postInit
            // write post-init user code here
		}//GEN-BEGIN:|203-getter|2|
		return cmdYes;
	}
	//</editor-fold>//GEN-END:|203-getter|2|

	//<editor-fold defaultstate="collapsed" desc=" Generated Getter: cmdNo ">//GEN-BEGIN:|205-getter|0|205-preInit
	/**
	 * Returns an initiliazed instance of cmdNo component.
	 * @return the initialized component instance
	 */
	public Command getCmdNo() {
		if (cmdNo == null) {//GEN-END:|205-getter|0|205-preInit
            // write pre-init user code here
			cmdNo = new Command("No", Command.CANCEL, 0);//GEN-LINE:|205-getter|1|205-postInit
            // write post-init user code here
		}//GEN-BEGIN:|205-getter|2|
		return cmdNo;
	}
	//</editor-fold>//GEN-END:|205-getter|2|

	//<editor-fold defaultstate="collapsed" desc=" Generated Getter: cmdBackClose ">//GEN-BEGIN:|226-getter|0|226-preInit
	/**
	 * Returns an initiliazed instance of cmdBackClose component.
	 * @return the initialized component instance
	 */
	public Command getCmdBackClose() {
		if (cmdBackClose == null) {//GEN-END:|226-getter|0|226-preInit
            // write pre-init user code here
			cmdBackClose = new Command("Close", Command.BACK, 0);//GEN-LINE:|226-getter|1|226-postInit
            // write post-init user code here
		}//GEN-BEGIN:|226-getter|2|
		return cmdBackClose;
	}
	//</editor-fold>//GEN-END:|226-getter|2|

	//<editor-fold defaultstate="collapsed" desc=" Generated Getter: frmPreview ">//GEN-BEGIN:|233-getter|0|233-preInit
	/**
	 * Returns an initiliazed instance of frmPreview component.
	 * @return the initialized component instance
	 */
	public Form getFrmPreview() {
		if (frmPreview == null) {//GEN-END:|233-getter|0|233-preInit
            // write pre-init user code here
			frmPreview = new Form("Preview", new Item[] { getFrmPreviewText() });//GEN-BEGIN:|233-getter|1|233-postInit
			frmPreview.addCommand(getCmdPost());
			frmPreview.addCommand(getCmdBack());
			frmPreview.setCommandListener(this);//GEN-END:|233-getter|1|233-postInit
            // write post-init user code here
		}//GEN-BEGIN:|233-getter|2|
		return frmPreview;
	}
	//</editor-fold>//GEN-END:|233-getter|2|

	//<editor-fold defaultstate="collapsed" desc=" Generated Getter: frmPreviewText ">//GEN-BEGIN:|234-getter|0|234-preInit
	/**
	 * Returns an initiliazed instance of frmPreviewText component.
	 * @return the initialized component instance
	 */
	public StringItem getFrmPreviewText() {
		if (frmPreviewText == null) {//GEN-END:|234-getter|0|234-preInit
            // write pre-init user code here
			frmPreviewText = new StringItem("", "", Item.PLAIN);//GEN-LINE:|234-getter|1|234-postInit
            // write post-init user code here
		}//GEN-BEGIN:|234-getter|2|
		return frmPreviewText;
	}
	//</editor-fold>//GEN-END:|234-getter|2|

	//<editor-fold defaultstate="collapsed" desc=" Generated Getter: frmReadme ">//GEN-BEGIN:|245-getter|0|245-preInit
	/**
	 * Returns an initiliazed instance of frmReadme component.
	 * @return the initialized component instance
	 */
	public Form getFrmReadme() {
		if (frmReadme == null) {//GEN-END:|245-getter|0|245-preInit
            // write pre-init user code here
			frmReadme = new Form("Readme", new Item[] { getFrmReadmeText() });//GEN-BEGIN:|245-getter|1|245-postInit
			frmReadme.addCommand(getCmdBack());
			frmReadme.setCommandListener(this);//GEN-END:|245-getter|1|245-postInit
            // write post-init user code here
		}//GEN-BEGIN:|245-getter|2|
		return frmReadme;
	}
	//</editor-fold>//GEN-END:|245-getter|2|

	//<editor-fold defaultstate="collapsed" desc=" Generated Getter: frmReadmeText ">//GEN-BEGIN:|250-getter|0|250-preInit
	/**
	 * Returns an initiliazed instance of frmReadmeText component.
	 * @return the initialized component instance
	 */
	public StringItem getFrmReadmeText() {
		if (frmReadmeText == null) {//GEN-END:|250-getter|0|250-preInit
            // write pre-init user code here
			frmReadmeText = new StringItem("", null, Item.PLAIN);//GEN-BEGIN:|250-getter|1|250-postInit
			frmReadmeText.setLayout(ImageItem.LAYOUT_DEFAULT | Item.LAYOUT_EXPAND | Item.LAYOUT_VEXPAND);//GEN-END:|250-getter|1|250-postInit
            // write post-init user code here
		}//GEN-BEGIN:|250-getter|2|
		return frmReadmeText;
	}
	//</editor-fold>//GEN-END:|250-getter|2|

	//<editor-fold defaultstate="collapsed" desc=" Generated Getter: frmSaveToDraftsConfirm ">//GEN-BEGIN:|263-getter|0|263-preInit
	/**
	 * Returns an initiliazed instance of frmSaveToDraftsConfirm component.
	 * @return the initialized component instance
	 */
	public Form getFrmSaveToDraftsConfirm() {
		if (frmSaveToDraftsConfirm == null) {//GEN-END:|263-getter|0|263-preInit
            // write pre-init user code here
			frmSaveToDraftsConfirm = new Form("Confirm", new Item[] { getStringItem() });//GEN-BEGIN:|263-getter|1|263-postInit
			frmSaveToDraftsConfirm.addCommand(getCmdYes());
			frmSaveToDraftsConfirm.addCommand(getCmdNo());
			frmSaveToDraftsConfirm.setCommandListener(this);//GEN-END:|263-getter|1|263-postInit
            // write post-init user code here
		}//GEN-BEGIN:|263-getter|2|
		return frmSaveToDraftsConfirm;
	}
	//</editor-fold>//GEN-END:|263-getter|2|

	//<editor-fold defaultstate="collapsed" desc=" Generated Getter: stringItem ">//GEN-BEGIN:|264-getter|0|264-preInit
	/**
	 * Returns an initiliazed instance of stringItem component.
	 * @return the initialized component instance
	 */
	public StringItem getStringItem() {
		if (stringItem == null) {//GEN-END:|264-getter|0|264-preInit
            // write pre-init user code here
			stringItem = new StringItem("", "Save update in Drafts?");//GEN-LINE:|264-getter|1|264-postInit
            // write post-init user code here
		}//GEN-BEGIN:|264-getter|2|
		return stringItem;
	}
	//</editor-fold>//GEN-END:|264-getter|2|

	//<editor-fold defaultstate="collapsed" desc=" Generated Method: isUpdateEmpty ">//GEN-BEGIN:|269-if|0|269-preIf
	/**
	 * Performs an action assigned to the isUpdateEmpty if-point.
	 */
	public void isUpdateEmpty() {//GEN-END:|269-if|0|269-preIf
        // enter pre-if user code here
		if (getTxtboxUpdate().getString().length() == 0) {//GEN-LINE:|269-if|1|270-preAction
            // write pre-action user code here
			switchDisplayable(null, getListMainForm());//GEN-LINE:|269-if|2|270-postAction
            // write post-action user code here
		} else {//GEN-LINE:|269-if|3|271-preAction
            // write pre-action user code here
			switchDisplayable(null, getFrmSaveToDraftsConfirm());//GEN-LINE:|269-if|4|271-postAction
            // write post-action user code here
		}//GEN-LINE:|269-if|5|269-postIf
        // enter post-if user code here
	}//GEN-BEGIN:|269-if|6|
	//</editor-fold>//GEN-END:|269-if|6|

	//<editor-fold defaultstate="collapsed" desc=" Generated Getter: listTimeline ">//GEN-BEGIN:|275-getter|0|275-preInit
	/**
	 * Returns an initiliazed instance of listTimeline component.
	 * @return the initialized component instance
	 */
	public List getListTimeline() {
		if (listTimeline == null) {//GEN-END:|275-getter|0|275-preInit
          // write pre-init user code here
			listTimeline = new List("Home", Choice.IMPLICIT);//GEN-BEGIN:|275-getter|1|275-postInit
			listTimeline.addCommand(getCmdReceive());
			listTimeline.addCommand(getBackCommand());
			listTimeline.addCommand(getCmdMarkAllRead());
			listTimeline.addCommand(getCmdMarkAllUnread());
			listTimeline.addCommand(getCmdClearCache());
			listTimeline.setCommandListener(this);
			listTimeline.setFitPolicy(Choice.TEXT_WRAP_ON);
			listTimeline.setSelectedFlags(new boolean[] {  });//GEN-END:|275-getter|1|275-postInit
          // write post-init user code here
		}//GEN-BEGIN:|275-getter|2|
		return listTimeline;
	}
	//</editor-fold>//GEN-END:|275-getter|2|

	//<editor-fold defaultstate="collapsed" desc=" Generated Method: listTimelineAction ">//GEN-BEGIN:|275-action|0|275-preAction
	/**
	 * Performs an action assigned to the selected list element in the listTimeline component.
	 */
	public void listTimelineAction() {//GEN-END:|275-action|0|275-preAction
       // enter pre-action user code here
		String __selectedString = getListTimeline().getString(getListTimeline().getSelectedIndex());//GEN-LINE:|275-action|1|275-postAction
       // enter post-action user code here
      if (!m_currentTimeline.Downloading())
      {
         final int index = getListTimeline().getSelectedIndex();
         if (__selectedString.equals(c_nextPage)) {
            // Next Page item
            viewTimelineNextPage();
         } else {
            viewTweet(m_currentTimeline, index);
         }
      }
	}//GEN-BEGIN:|275-action|2|
	//</editor-fold>//GEN-END:|275-action|2|


	//<editor-fold defaultstate="collapsed" desc=" Generated Getter: imgFriends ">//GEN-BEGIN:|280-getter|0|280-preInit
	/**
	 * Returns an initiliazed instance of imgFriends component.
	 * @return the initialized component instance
	 */
	public Image getImgFriends() {
		if (imgFriends == null) {//GEN-END:|280-getter|0|280-preInit
          // write pre-init user code here
			try {//GEN-BEGIN:|280-getter|1|280-@java.io.IOException
				imgFriends = Image.createImage("/friends.png");
			} catch (java.io.IOException e) {//GEN-END:|280-getter|1|280-@java.io.IOException
             e.printStackTrace();
			}//GEN-LINE:|280-getter|2|280-postInit
          // write post-init user code here
		}//GEN-BEGIN:|280-getter|3|
		return imgFriends;
	}
	//</editor-fold>//GEN-END:|280-getter|3|

	//<editor-fold defaultstate="collapsed" desc=" Generated Getter: imgDirect ">//GEN-BEGIN:|283-getter|0|283-preInit
	/**
	 * Returns an initiliazed instance of imgDirect component.
	 * @return the initialized component instance
	 */
	public Image getImgDirect() {
		if (imgDirect == null) {//GEN-END:|283-getter|0|283-preInit
          // write pre-init user code here
			try {//GEN-BEGIN:|283-getter|1|283-@java.io.IOException
				imgDirect = Image.createImage("/direct.png");
			} catch (java.io.IOException e) {//GEN-END:|283-getter|1|283-@java.io.IOException
             e.printStackTrace();
			}//GEN-LINE:|283-getter|2|283-postInit
          // write post-init user code here
		}//GEN-BEGIN:|283-getter|3|
		return imgDirect;
	}
	//</editor-fold>//GEN-END:|283-getter|3|

	//<editor-fold defaultstate="collapsed" desc=" Generated Getter: imgReplies ">//GEN-BEGIN:|284-getter|0|284-preInit
	/**
	 * Returns an initiliazed instance of imgReplies component.
	 * @return the initialized component instance
	 */
	public Image getImgReplies() {
		if (imgReplies == null) {//GEN-END:|284-getter|0|284-preInit
          // write pre-init user code here
			try {//GEN-BEGIN:|284-getter|1|284-@java.io.IOException
				imgReplies = Image.createImage("/replies.png");
			} catch (java.io.IOException e) {//GEN-END:|284-getter|1|284-@java.io.IOException
             e.printStackTrace();
			}//GEN-LINE:|284-getter|2|284-postInit
          // write post-init user code here
		}//GEN-BEGIN:|284-getter|3|
		return imgReplies;
	}
	//</editor-fold>//GEN-END:|284-getter|3|

	//<editor-fold defaultstate="collapsed" desc=" Generated Getter: backCommand ">//GEN-BEGIN:|287-getter|0|287-preInit
	/**
	 * Returns an initiliazed instance of backCommand component.
	 * @return the initialized component instance
	 */
	public Command getBackCommand() {
		if (backCommand == null) {//GEN-END:|287-getter|0|287-preInit
            // write pre-init user code here
			backCommand = new Command("Back", Command.BACK, 1);//GEN-LINE:|287-getter|1|287-postInit
            // write post-init user code here
		}//GEN-BEGIN:|287-getter|2|
		return backCommand;
	}
	//</editor-fold>//GEN-END:|287-getter|2|

	//<editor-fold defaultstate="collapsed" desc=" Generated Getter: cmdReceive ">//GEN-BEGIN:|294-getter|0|294-preInit
	/**
	 * Returns an initiliazed instance of cmdReceive component.
	 * @return the initialized component instance
	 */
	public Command getCmdReceive() {
		if (cmdReceive == null) {//GEN-END:|294-getter|0|294-preInit
          // write pre-init user code here
			cmdReceive = new Command("Receive", Command.ITEM, 0);//GEN-LINE:|294-getter|1|294-postInit
          // write post-init user code here
		}//GEN-BEGIN:|294-getter|2|
		return cmdReceive;
	}
	//</editor-fold>//GEN-END:|294-getter|2|

	//<editor-fold defaultstate="collapsed" desc=" Generated Getter: ticker ">//GEN-BEGIN:|296-getter|0|296-preInit
	/**
	 * Returns an initiliazed instance of ticker component.
	 * @return the initialized component instance
	 */
	public Ticker getTicker() {
		if (ticker == null) {//GEN-END:|296-getter|0|296-preInit
         // write pre-init user code here
			ticker = new Ticker("");//GEN-LINE:|296-getter|1|296-postInit
         // write post-init user code here
		}//GEN-BEGIN:|296-getter|2|
		return ticker;
	}
	//</editor-fold>//GEN-END:|296-getter|2|

	//<editor-fold defaultstate="collapsed" desc=" Generated Getter: frmTweetDetails ">//GEN-BEGIN:|297-getter|0|297-preInit
	/**
	 * Returns an initiliazed instance of frmTweetDetails component.
	 * @return the initialized component instance
	 */
	public Form getFrmTweetDetails() {
		if (frmTweetDetails == null) {//GEN-END:|297-getter|0|297-preInit
         // write pre-init user code here
			frmTweetDetails = new Form("Tweet Details", new Item[] { getTxtTweetDetailsText(), getTxtInReplyTo() });//GEN-BEGIN:|297-getter|1|297-postInit
			frmTweetDetails.addCommand(getCmdBack());
			frmTweetDetails.addCommand(getCmdReply());
			frmTweetDetails.addCommand(getCmdDirectMessage());
			frmTweetDetails.addCommand(getCmdRetweet());
			frmTweetDetails.addCommand(getCmdMarkTweetUnread());
			frmTweetDetails.addCommand(getCmdOpenUrls());
			frmTweetDetails.addCommand(getCmdSaveAsTemplate());
			frmTweetDetails.addCommand(getCmdDeleteTweet());
			frmTweetDetails.setCommandListener(this);//GEN-END:|297-getter|1|297-postInit
         // write post-init user code here
		}//GEN-BEGIN:|297-getter|2|
		return frmTweetDetails;
	}
	//</editor-fold>//GEN-END:|297-getter|2|

	//<editor-fold defaultstate="collapsed" desc=" Generated Getter: imgDirectOutbox ">//GEN-BEGIN:|304-getter|0|304-preInit
	/**
	 * Returns an initiliazed instance of imgDirectOutbox component.
	 * @return the initialized component instance
	 */
	public Image getImgDirectOutbox() {
		if (imgDirectOutbox == null) {//GEN-END:|304-getter|0|304-preInit
         // write pre-init user code here
			try {//GEN-BEGIN:|304-getter|1|304-@java.io.IOException
				imgDirectOutbox = Image.createImage("/direct_outbox.png");
			} catch (java.io.IOException e) {//GEN-END:|304-getter|1|304-@java.io.IOException
            e.printStackTrace();
			}//GEN-LINE:|304-getter|2|304-postInit
         // write post-init user code here
		}//GEN-BEGIN:|304-getter|3|
		return imgDirectOutbox;
	}
	//</editor-fold>//GEN-END:|304-getter|3|

	//<editor-fold defaultstate="collapsed" desc=" Generated Getter: cmdReply ">//GEN-BEGIN:|305-getter|0|305-preInit
	/**
	 * Returns an initiliazed instance of cmdReply component.
	 * @return the initialized component instance
	 */
	public Command getCmdReply() {
		if (cmdReply == null) {//GEN-END:|305-getter|0|305-preInit
         // write pre-init user code here
			cmdReply = new Command("Reply", Command.SCREEN, 0);//GEN-LINE:|305-getter|1|305-postInit
         // write post-init user code here
		}//GEN-BEGIN:|305-getter|2|
		return cmdReply;
	}
	//</editor-fold>//GEN-END:|305-getter|2|

	//<editor-fold defaultstate="collapsed" desc=" Generated Getter: cmdDirectMessage ">//GEN-BEGIN:|307-getter|0|307-preInit
	/**
	 * Returns an initiliazed instance of cmdDirectMessage component.
	 * @return the initialized component instance
	 */
	public Command getCmdDirectMessage() {
		if (cmdDirectMessage == null) {//GEN-END:|307-getter|0|307-preInit
         // write pre-init user code here
			cmdDirectMessage = new Command("Direct Message", Command.SCREEN, 0);//GEN-LINE:|307-getter|1|307-postInit
         // write post-init user code here
		}//GEN-BEGIN:|307-getter|2|
		return cmdDirectMessage;
	}
	//</editor-fold>//GEN-END:|307-getter|2|

	//<editor-fold defaultstate="collapsed" desc=" Generated Getter: listTimelines ">//GEN-BEGIN:|317-getter|0|317-preInit
	/**
	 * Returns an initiliazed instance of listTimelines component.
	 * @return the initialized component instance
	 */
	public List getListTimelines() {
		if (listTimelines == null) {//GEN-END:|317-getter|0|317-preInit
         // write pre-init user code here
			listTimelines = new List("Timelines", Choice.IMPLICIT);//GEN-BEGIN:|317-getter|1|317-postInit
			listTimelines.append("Friends", null);
			listTimelines.append("Replies", null);
			listTimelines.append("Direct - Inbox", null);
			listTimelines.append("Direct - Sent", null);
			listTimelines.addCommand(getBackCommand());
			listTimelines.addCommand(getCmdReceiveAll());
			listTimelines.addCommand(getCmdAddSearch());
			listTimelines.addCommand(getCmdDeleteSearch());
			listTimelines.setCommandListener(this);
			listTimelines.setSelectedFlags(new boolean[] { false, false, false, false });//GEN-END:|317-getter|1|317-postInit
         // write post-init user code here
		}//GEN-BEGIN:|317-getter|2|
		return listTimelines;
	}
	//</editor-fold>//GEN-END:|317-getter|2|

	//<editor-fold defaultstate="collapsed" desc=" Generated Method: listTimelinesAction ">//GEN-BEGIN:|317-action|0|317-preAction
	/**
	 * Performs an action assigned to the selected list element in the listTimelines component.
	 */
	public void listTimelinesAction() {//GEN-END:|317-action|0|317-preAction
      // enter pre-action user code here
		switch (getListTimelines().getSelectedIndex()) {//GEN-BEGIN:|317-action|1|321-preAction
			case 0://GEN-END:|317-action|1|321-preAction
            // write pre-action user code here
            showHomeTimeline();
//GEN-LINE:|317-action|2|321-postAction
            // write post-action user code here
				break;//GEN-BEGIN:|317-action|3|322-preAction
			case 1://GEN-END:|317-action|3|322-preAction
            // write pre-action user code here
            showRepliesTimeline();
//GEN-LINE:|317-action|4|322-postAction
            // write post-action user code here
				break;//GEN-BEGIN:|317-action|5|323-preAction
			case 2://GEN-END:|317-action|5|323-preAction
            // write pre-action user code here
            showDirectInboxTimeline();
//GEN-LINE:|317-action|6|323-postAction
            // write post-action user code here
				break;//GEN-BEGIN:|317-action|7|324-preAction
			case 3://GEN-END:|317-action|7|324-preAction
            // write pre-action user code here
            showDirectOutboxTimeline();
//GEN-LINE:|317-action|8|324-postAction
            // write post-action user code here
				break;//GEN-BEGIN:|317-action|9|317-postAction
		}//GEN-END:|317-action|9|317-postAction
      // enter post-action user code here
      if (getListTimelines().getSelectedIndex() >= c_firstSearchTimelinePos) {
         showSearchTimeline(getListTimelines().getSelectedIndex() - c_firstSearchTimelinePos);
      }
	}//GEN-BEGIN:|317-action|10|
	//</editor-fold>//GEN-END:|317-action|10|



	//<editor-fold defaultstate="collapsed" desc=" Generated Getter: txtTweetDetailsText ">//GEN-BEGIN:|328-getter|0|328-preInit
	/**
	 * Returns an initiliazed instance of txtTweetDetailsText component.
	 * @return the initialized component instance
	 */
	public StringItem getTxtTweetDetailsText() {
		if (txtTweetDetailsText == null) {//GEN-END:|328-getter|0|328-preInit
         // write pre-init user code here
			txtTweetDetailsText = new StringItem("author ", "tweet text");//GEN-BEGIN:|328-getter|1|328-postInit
			txtTweetDetailsText.setItemCommandListener(this);//GEN-END:|328-getter|1|328-postInit
         // write post-init user code here
		}//GEN-BEGIN:|328-getter|2|
		return txtTweetDetailsText;
	}
	//</editor-fold>//GEN-END:|328-getter|2|

	//<editor-fold defaultstate="collapsed" desc=" Generated Getter: imgTweet ">//GEN-BEGIN:|333-getter|0|333-preInit
	/**
	 * Returns an initiliazed instance of imgTweet component.
	 * @return the initialized component instance
	 */
	public Image getImgTweet() {
		if (imgTweet == null) {//GEN-END:|333-getter|0|333-preInit
         // write pre-init user code here
			try {//GEN-BEGIN:|333-getter|1|333-@java.io.IOException
				imgTweet = Image.createImage("/tweet.png");
			} catch (java.io.IOException e) {//GEN-END:|333-getter|1|333-@java.io.IOException
            e.printStackTrace();
			}//GEN-LINE:|333-getter|2|333-postInit
         // write post-init user code here
		}//GEN-BEGIN:|333-getter|3|
		return imgTweet;
	}
	//</editor-fold>//GEN-END:|333-getter|3|

	//<editor-fold defaultstate="collapsed" desc=" Generated Getter: imgTweetRead ">//GEN-BEGIN:|334-getter|0|334-preInit
	/**
	 * Returns an initiliazed instance of imgTweetRead component.
	 * @return the initialized component instance
	 */
	public Image getImgTweetRead() {
		if (imgTweetRead == null) {//GEN-END:|334-getter|0|334-preInit
         // write pre-init user code here
			try {//GEN-BEGIN:|334-getter|1|334-@java.io.IOException
				imgTweetRead = Image.createImage("/tweet_read.png");
			} catch (java.io.IOException e) {//GEN-END:|334-getter|1|334-@java.io.IOException
            e.printStackTrace();
			}//GEN-LINE:|334-getter|2|334-postInit
         // write post-init user code here
		}//GEN-BEGIN:|334-getter|3|
		return imgTweetRead;
	}
	//</editor-fold>//GEN-END:|334-getter|3|

	//<editor-fold defaultstate="collapsed" desc=" Generated Getter: cmdMarkAllRead ">//GEN-BEGIN:|335-getter|0|335-preInit
	/**
	 * Returns an initiliazed instance of cmdMarkAllRead component.
	 * @return the initialized component instance
	 */
	public Command getCmdMarkAllRead() {
		if (cmdMarkAllRead == null) {//GEN-END:|335-getter|0|335-preInit
         // write pre-init user code here
			cmdMarkAllRead = new Command("Mark All Read", Command.ITEM, 2);//GEN-LINE:|335-getter|1|335-postInit
         // write post-init user code here
		}//GEN-BEGIN:|335-getter|2|
		return cmdMarkAllRead;
	}
	//</editor-fold>//GEN-END:|335-getter|2|

	//<editor-fold defaultstate="collapsed" desc=" Generated Getter: cmdMarkAllUnread ">//GEN-BEGIN:|337-getter|0|337-preInit
	/**
	 * Returns an initiliazed instance of cmdMarkAllUnread component.
	 * @return the initialized component instance
	 */
	public Command getCmdMarkAllUnread() {
		if (cmdMarkAllUnread == null) {//GEN-END:|337-getter|0|337-preInit
         // write pre-init user code here
			cmdMarkAllUnread = new Command("Mark All Unread", Command.ITEM, 3);//GEN-LINE:|337-getter|1|337-postInit
         // write post-init user code here
		}//GEN-BEGIN:|337-getter|2|
		return cmdMarkAllUnread;
	}
	//</editor-fold>//GEN-END:|337-getter|2|

	//<editor-fold defaultstate="collapsed" desc=" Generated Getter: itemCommand ">//GEN-BEGIN:|339-getter|0|339-preInit
	/**
	 * Returns an initiliazed instance of itemCommand component.
	 * @return the initialized component instance
	 */
	public Command getItemCommand() {
		if (itemCommand == null) {//GEN-END:|339-getter|0|339-preInit
         // write pre-init user code here
			itemCommand = new Command("Item", Command.ITEM, 0);//GEN-LINE:|339-getter|1|339-postInit
         // write post-init user code here
		}//GEN-BEGIN:|339-getter|2|
		return itemCommand;
	}
	//</editor-fold>//GEN-END:|339-getter|2|

	//<editor-fold defaultstate="collapsed" desc=" Generated Getter: cmdMarkTweetUnread ">//GEN-BEGIN:|342-getter|0|342-preInit
	/**
	 * Returns an initiliazed instance of cmdMarkTweetUnread component.
	 * @return the initialized component instance
	 */
	public Command getCmdMarkTweetUnread() {
		if (cmdMarkTweetUnread == null) {//GEN-END:|342-getter|0|342-preInit
         // write pre-init user code here
			cmdMarkTweetUnread = new Command("Mark Unread", Command.ITEM, 0);//GEN-LINE:|342-getter|1|342-postInit
         // write post-init user code here
		}//GEN-BEGIN:|342-getter|2|
		return cmdMarkTweetUnread;
	}
	//</editor-fold>//GEN-END:|342-getter|2|

	//<editor-fold defaultstate="collapsed" desc=" Generated Getter: cmdReceiveAll ">//GEN-BEGIN:|344-getter|0|344-preInit
	/**
	 * Returns an initiliazed instance of cmdReceiveAll component.
	 * @return the initialized component instance
	 */
	public Command getCmdReceiveAll() {
		if (cmdReceiveAll == null) {//GEN-END:|344-getter|0|344-preInit
         // write pre-init user code here
			cmdReceiveAll = new Command("Receive All", Command.ITEM, 0);//GEN-LINE:|344-getter|1|344-postInit
         // write post-init user code here
		}//GEN-BEGIN:|344-getter|2|
		return cmdReceiveAll;
	}
	//</editor-fold>//GEN-END:|344-getter|2|

	//<editor-fold defaultstate="collapsed" desc=" Generated Getter: frmConfigOnStartGroup ">//GEN-BEGIN:|346-getter|0|346-preInit
	/**
	 * Returns an initiliazed instance of frmConfigOnStartGroup component.
	 * @return the initialized component instance
	 */
	public ChoiceGroup getFrmConfigOnStartGroup() {
		if (frmConfigOnStartGroup == null) {//GEN-END:|346-getter|0|346-preInit
         // write pre-init user code here
			frmConfigOnStartGroup = new ChoiceGroup("On Start", Choice.MULTIPLE);//GEN-BEGIN:|346-getter|1|346-postInit
			frmConfigOnStartGroup.append("New Tweet prompt", null);
			frmConfigOnStartGroup.append("Download Updates", null);
			frmConfigOnStartGroup.setSelectedFlags(new boolean[] { false, false });//GEN-END:|346-getter|1|346-postInit
         // write post-init user code here
         frmConfigOnStartGroup.setSelectedFlags(new boolean[] {
            Settings.GetInstance().GetSettings().m_onStartNewTweet,
            Settings.GetInstance().GetSettings().m_onStartDownloadUpdates });

		}//GEN-BEGIN:|346-getter|2|
		return frmConfigOnStartGroup;
	}
	//</editor-fold>//GEN-END:|346-getter|2|

	//<editor-fold defaultstate="collapsed" desc=" Generated Getter: imgDownload ">//GEN-BEGIN:|350-getter|0|350-preInit
	/**
	 * Returns an initiliazed instance of imgDownload component.
	 * @return the initialized component instance
	 */
	public Image getImgDownload() {
		if (imgDownload == null) {//GEN-END:|350-getter|0|350-preInit
         // write pre-init user code here
			try {//GEN-BEGIN:|350-getter|1|350-@java.io.IOException
				imgDownload = Image.createImage("/download.png");
			} catch (java.io.IOException e) {//GEN-END:|350-getter|1|350-@java.io.IOException
            e.printStackTrace();
			}//GEN-LINE:|350-getter|2|350-postInit
         // write post-init user code here
		}//GEN-BEGIN:|350-getter|3|
		return imgDownload;
	}
	//</editor-fold>//GEN-END:|350-getter|3|

	//<editor-fold defaultstate="collapsed" desc=" Generated Getter: txtInReplyTo ">//GEN-BEGIN:|351-getter|0|351-preInit
	/**
	 * Returns an initiliazed instance of txtInReplyTo component.
	 * @return the initialized component instance
	 */
	public StringItem getTxtInReplyTo() {
		if (txtInReplyTo == null) {//GEN-END:|351-getter|0|351-preInit
         // write pre-init user code here
			txtInReplyTo = new StringItem("In Reply To", "user");//GEN-BEGIN:|351-getter|1|351-postInit
			txtInReplyTo.addCommand(getCmdShowReply());
			txtInReplyTo.setItemCommandListener(this);
			txtInReplyTo.setDefaultCommand(getCmdShowReply());//GEN-END:|351-getter|1|351-postInit
         // write post-init user code here
		}//GEN-BEGIN:|351-getter|2|
		return txtInReplyTo;
	}
	//</editor-fold>//GEN-END:|351-getter|2|

	//<editor-fold defaultstate="collapsed" desc=" Generated Method: commandAction for Items ">//GEN-BEGIN:|17-itemCommandAction|0|17-preItemCommandAction
	/**
	 * Called by a system to indicated that a command has been invoked on a particular item.
	 * @param command the Command that was invoked
	 * @param displayable the Item where the command was invoked
	 */
	public void commandAction(Command command, Item item) {//GEN-END:|17-itemCommandAction|0|17-preItemCommandAction
      // write pre-action user code here
		if (item == txtInReplyTo) {//GEN-BEGIN:|17-itemCommandAction|1|354-preAction
			if (command == cmdShowReply) {//GEN-END:|17-itemCommandAction|1|354-preAction
            // write pre-action user code here
            showReply();
				//GEN-LINE:|17-itemCommandAction|2|354-postAction
            // write post-action user code here
			}//GEN-BEGIN:|17-itemCommandAction|3|17-postItemCommandAction
		}//GEN-END:|17-itemCommandAction|3|17-postItemCommandAction
      // write post-action user code here
	}//GEN-BEGIN:|17-itemCommandAction|4|
	//</editor-fold>//GEN-END:|17-itemCommandAction|4|

	//<editor-fold defaultstate="collapsed" desc=" Generated Getter: cmdShowReply ">//GEN-BEGIN:|352-getter|0|352-preInit
	/**
	 * Returns an initiliazed instance of cmdShowReply component.
	 * @return the initialized component instance
	 */
	public Command getCmdShowReply() {
		if (cmdShowReply == null) {//GEN-END:|352-getter|0|352-preInit
         // write pre-init user code here
			cmdShowReply = new Command("Item", Command.ITEM, 0);//GEN-LINE:|352-getter|1|352-postInit
         // write post-init user code here
		}//GEN-BEGIN:|352-getter|2|
		return cmdShowReply;
	}
	//</editor-fold>//GEN-END:|352-getter|2|

	//<editor-fold defaultstate="collapsed" desc=" Generated Getter: stopCommand ">//GEN-BEGIN:|359-getter|0|359-preInit
	/**
	 * Returns an initiliazed instance of stopCommand component.
	 * @return the initialized component instance
	 */
	public Command getStopCommand() {
		if (stopCommand == null) {//GEN-END:|359-getter|0|359-preInit
         // write pre-init user code here
			stopCommand = new Command("Stop", Command.STOP, 0);//GEN-LINE:|359-getter|1|359-postInit
         // write post-init user code here
		}//GEN-BEGIN:|359-getter|2|
		return stopCommand;
	}
	//</editor-fold>//GEN-END:|359-getter|2|

	//<editor-fold defaultstate="collapsed" desc=" Generated Getter: cmdCancelDownloading ">//GEN-BEGIN:|361-getter|0|361-preInit
	/**
	 * Returns an initiliazed instance of cmdCancelDownloading component.
	 * @return the initialized component instance
	 */
	public Command getCmdCancelDownloading() {
		if (cmdCancelDownloading == null) {//GEN-END:|361-getter|0|361-preInit
         // write pre-init user code here
			cmdCancelDownloading = new Command("Cancel", Command.CANCEL, 0);//GEN-LINE:|361-getter|1|361-postInit
         // write post-init user code here
		}//GEN-BEGIN:|361-getter|2|
		return cmdCancelDownloading;
	}
	//</editor-fold>//GEN-END:|361-getter|2|

	//<editor-fold defaultstate="collapsed" desc=" Generated Getter: frmDownloadProgress ">//GEN-BEGIN:|357-getter|0|357-preInit
	/**
	 * Returns an initiliazed instance of frmDownloadProgress component.
	 * @return the initialized component instance
	 */
	public Form getFrmDownloadProgress() {
		if (frmDownloadProgress == null) {//GEN-END:|357-getter|0|357-preInit
         // write pre-init user code here
			frmDownloadProgress = new Form("Downloading", new Item[] { getFrmDownloadProgressBar() });//GEN-BEGIN:|357-getter|1|357-postInit
			frmDownloadProgress.addCommand(getCmdBack());
			frmDownloadProgress.addCommand(getCmdCancelDownloading());
			frmDownloadProgress.setCommandListener(this);//GEN-END:|357-getter|1|357-postInit
         // write post-init user code here
		}//GEN-BEGIN:|357-getter|2|
		return frmDownloadProgress;
	}
	//</editor-fold>//GEN-END:|357-getter|2|

	//<editor-fold defaultstate="collapsed" desc=" Generated Getter: frmDownloadProgressBar ">//GEN-BEGIN:|364-getter|0|364-preInit
	/**
	 * Returns an initiliazed instance of frmDownloadProgressBar component.
	 * @return the initialized component instance
	 */
	public Gauge getFrmDownloadProgressBar() {
		if (frmDownloadProgressBar == null) {//GEN-END:|364-getter|0|364-preInit
         // write pre-init user code here
			frmDownloadProgressBar = new Gauge("Connecting to server", false, 100, 0);//GEN-BEGIN:|364-getter|1|364-postInit
			frmDownloadProgressBar.setLayout(ImageItem.LAYOUT_CENTER | Item.LAYOUT_TOP | Item.LAYOUT_BOTTOM | Item.LAYOUT_VCENTER);//GEN-END:|364-getter|1|364-postInit
         // write post-init user code here
		}//GEN-BEGIN:|364-getter|2|
		return frmDownloadProgressBar;
	}
	//</editor-fold>//GEN-END:|364-getter|2|

	//<editor-fold defaultstate="collapsed" desc=" Generated Getter: txtboxSearch ">//GEN-BEGIN:|365-getter|0|365-preInit
	/**
	 * Returns an initiliazed instance of txtboxSearch component.
	 * @return the initialized component instance
	 */
	public TextBox getTxtboxSearch() {
		if (txtboxSearch == null) {//GEN-END:|365-getter|0|365-preInit
         // write pre-init user code here
			txtboxSearch = new TextBox("Search", null, 140, TextField.ANY);//GEN-BEGIN:|365-getter|1|365-postInit
			txtboxSearch.addCommand(getCmdBack());
			txtboxSearch.addCommand(getOkCommand());
			txtboxSearch.setCommandListener(this);//GEN-END:|365-getter|1|365-postInit
         // write post-init user code here
		}//GEN-BEGIN:|365-getter|2|
		return txtboxSearch;
	}
	//</editor-fold>//GEN-END:|365-getter|2|

	//<editor-fold defaultstate="collapsed" desc=" Generated Getter: cmdAddSearch ">//GEN-BEGIN:|366-getter|0|366-preInit
	/**
	 * Returns an initiliazed instance of cmdAddSearch component.
	 * @return the initialized component instance
	 */
	public Command getCmdAddSearch() {
		if (cmdAddSearch == null) {//GEN-END:|366-getter|0|366-preInit
         // write pre-init user code here
			cmdAddSearch = new Command("New Search", Command.SCREEN, 0);//GEN-LINE:|366-getter|1|366-postInit
         // write post-init user code here
		}//GEN-BEGIN:|366-getter|2|
		return cmdAddSearch;
	}
	//</editor-fold>//GEN-END:|366-getter|2|

	//<editor-fold defaultstate="collapsed" desc=" Generated Getter: imgSearch ">//GEN-BEGIN:|368-getter|0|368-preInit
	/**
	 * Returns an initiliazed instance of imgSearch component.
	 * @return the initialized component instance
	 */
	public Image getImgSearch() {
		if (imgSearch == null) {//GEN-END:|368-getter|0|368-preInit
         // write pre-init user code here
			try {//GEN-BEGIN:|368-getter|1|368-@java.io.IOException
				imgSearch = Image.createImage("/search.png");
			} catch (java.io.IOException e) {//GEN-END:|368-getter|1|368-@java.io.IOException
            e.printStackTrace();
			}//GEN-LINE:|368-getter|2|368-postInit
         // write post-init user code here
		}//GEN-BEGIN:|368-getter|3|
		return imgSearch;
	}
	//</editor-fold>//GEN-END:|368-getter|3|

	//<editor-fold defaultstate="collapsed" desc=" Generated Getter: okCommand ">//GEN-BEGIN:|371-getter|0|371-preInit
	/**
	 * Returns an initiliazed instance of okCommand component.
	 * @return the initialized component instance
	 */
	public Command getOkCommand() {
		if (okCommand == null) {//GEN-END:|371-getter|0|371-preInit
         // write pre-init user code here
			okCommand = new Command("Ok", Command.OK, 0);//GEN-LINE:|371-getter|1|371-postInit
         // write post-init user code here
		}//GEN-BEGIN:|371-getter|2|
		return okCommand;
	}
	//</editor-fold>//GEN-END:|371-getter|2|

	//<editor-fold defaultstate="collapsed" desc=" Generated Getter: cmdDeleteSearch ">//GEN-BEGIN:|375-getter|0|375-preInit
	/**
	 * Returns an initiliazed instance of cmdDeleteSearch component.
	 * @return the initialized component instance
	 */
	public Command getCmdDeleteSearch() {
		if (cmdDeleteSearch == null) {//GEN-END:|375-getter|0|375-preInit
         // write pre-init user code here
			cmdDeleteSearch = new Command("Delete Search", Command.SCREEN, 0);//GEN-LINE:|375-getter|1|375-postInit
         // write post-init user code here
		}//GEN-BEGIN:|375-getter|2|
		return cmdDeleteSearch;
	}
	//</editor-fold>//GEN-END:|375-getter|2|

	//<editor-fold defaultstate="collapsed" desc=" Generated Getter: cmdClearCache ">//GEN-BEGIN:|377-getter|0|377-preInit
	/**
	 * Returns an initiliazed instance of cmdClearCache component.
	 * @return the initialized component instance
	 */
	public Command getCmdClearCache() {
		if (cmdClearCache == null) {//GEN-END:|377-getter|0|377-preInit
         // write pre-init user code here
			cmdClearCache = new Command("Clear cache", Command.SCREEN, 0);//GEN-LINE:|377-getter|1|377-postInit
         // write post-init user code here
		}//GEN-BEGIN:|377-getter|2|
		return cmdClearCache;
	}
	//</editor-fold>//GEN-END:|377-getter|2|





	//<editor-fold defaultstate="collapsed" desc=" Generated Getter: alertSaveSearchConfirm ">//GEN-BEGIN:|382-getter|0|382-preInit
	/**
	 * Returns an initiliazed instance of alertSaveSearchConfirm component.
	 * @return the initialized component instance
	 */
	public Alert getAlertSaveSearchConfirm() {
		if (alertSaveSearchConfirm == null) {//GEN-END:|382-getter|0|382-preInit
         // write pre-init user code here
			alertSaveSearchConfirm = new Alert("Confirmation", "Save search results timeline for future use?", null, AlertType.CONFIRMATION);//GEN-BEGIN:|382-getter|1|382-postInit
			alertSaveSearchConfirm.addCommand(getCmdYes());
			alertSaveSearchConfirm.addCommand(getCmdNo());
			alertSaveSearchConfirm.setCommandListener(this);
			alertSaveSearchConfirm.setTimeout(Alert.FOREVER);//GEN-END:|382-getter|1|382-postInit
         // write post-init user code here
		}//GEN-BEGIN:|382-getter|2|
		return alertSaveSearchConfirm;
	}
	//</editor-fold>//GEN-END:|382-getter|2|

	//<editor-fold defaultstate="collapsed" desc=" Generated Getter: cmdTemplates ">//GEN-BEGIN:|388-getter|0|388-preInit
	/**
	 * Returns an initiliazed instance of cmdTemplates component.
	 * @return the initialized component instance
	 */
	public Command getCmdTemplates() {
		if (cmdTemplates == null) {//GEN-END:|388-getter|0|388-preInit
         // write pre-init user code here
			cmdTemplates = new Command("Templates", Command.ITEM, 0);//GEN-LINE:|388-getter|1|388-postInit
         // write post-init user code here
		}//GEN-BEGIN:|388-getter|2|
		return cmdTemplates;
	}
	//</editor-fold>//GEN-END:|388-getter|2|

	//<editor-fold defaultstate="collapsed" desc=" Generated Getter: cmdOpenUrls ">//GEN-BEGIN:|390-getter|0|390-preInit
	/**
	 * Returns an initiliazed instance of cmdOpenUrls component.
	 * @return the initialized component instance
	 */
	public Command getCmdOpenUrls() {
		if (cmdOpenUrls == null) {//GEN-END:|390-getter|0|390-preInit
         // write pre-init user code here
			cmdOpenUrls = new Command("Open URLs", Command.ITEM, 0);//GEN-LINE:|390-getter|1|390-postInit
         // write post-init user code here
		}//GEN-BEGIN:|390-getter|2|
		return cmdOpenUrls;
	}
	//</editor-fold>//GEN-END:|390-getter|2|

	//<editor-fold defaultstate="collapsed" desc=" Generated Getter: cmdSaveAsTemplate ">//GEN-BEGIN:|392-getter|0|392-preInit
	/**
	 * Returns an initiliazed instance of cmdSaveAsTemplate component.
	 * @return the initialized component instance
	 */
	public Command getCmdSaveAsTemplate() {
		if (cmdSaveAsTemplate == null) {//GEN-END:|392-getter|0|392-preInit
         // write pre-init user code here
			cmdSaveAsTemplate = new Command("Save as Template", Command.ITEM, 0);//GEN-LINE:|392-getter|1|392-postInit
         // write post-init user code here
		}//GEN-BEGIN:|392-getter|2|
		return cmdSaveAsTemplate;
	}
	//</editor-fold>//GEN-END:|392-getter|2|

	//<editor-fold defaultstate="collapsed" desc=" Generated Getter: cmdRetweet ">//GEN-BEGIN:|395-getter|0|395-preInit
	/**
	 * Returns an initiliazed instance of cmdRetweet component.
	 * @return the initialized component instance
	 */
	public Command getCmdRetweet() {
		if (cmdRetweet == null) {//GEN-END:|395-getter|0|395-preInit
         // write pre-init user code here
			cmdRetweet = new Command("Retweet", Command.ITEM, 0);//GEN-LINE:|395-getter|1|395-postInit
         // write post-init user code here
		}//GEN-BEGIN:|395-getter|2|
		return cmdRetweet;
	}
	//</editor-fold>//GEN-END:|395-getter|2|

	//<editor-fold defaultstate="collapsed" desc=" Generated Getter: cmdDeleteTweet ">//GEN-BEGIN:|397-getter|0|397-preInit
	/**
	 * Returns an initiliazed instance of cmdDeleteTweet component.
	 * @return the initialized component instance
	 */
	public Command getCmdDeleteTweet() {
		if (cmdDeleteTweet == null) {//GEN-END:|397-getter|0|397-preInit
         // write pre-init user code here
			cmdDeleteTweet = new Command("Delete tweet", Command.ITEM, 0);//GEN-LINE:|397-getter|1|397-postInit
         // write post-init user code here
		}//GEN-BEGIN:|397-getter|2|
		return cmdDeleteTweet;
	}
	//</editor-fold>//GEN-END:|397-getter|2|



    /**
     * Returns a display instance.
     * @return the display instance.
     */
    public Display getDisplay () {
        return Display.getDisplay(this);
    }

    /**
     * Exits MIDlet.
     */
   public void exitMIDlet() {
      switchDisplayable(null, null);

      // Requirement 14: When quitting if outbox contains items, application
      // shall be minimized.
      if (isOutboxEmpty()) {
         destroyApp(true);
         notifyDestroyed();
      } else {
         m_runningInBackground = true;
      }
   }

   /**
    * Called when MIDlet is started.
    * Checks whether the MIDlet have been already started and initialize/starts or resumes the MIDlet.
    */
   public void startApp() {
      if (midletPaused) {
         resumeMIDlet();
      } else {
         initialize();
         startMIDlet();
      }
      updateOutboxMainWindowItem();
      updateSearchTimelinesList();
      updateDownloadProgressIcons();
      if (!midletPaused) {
         /// If it's configured to start downloading updates when app starts,
         /// then download them
         if (Settings.GetInstance().GetSettings().m_onStartDownloadUpdates) {
            receiveAllTimelines();
         }
      }

      midletPaused = false;
   }

   /**
    * Called when MIDlet is paused.
    */
   public void pauseApp() {
      midletPaused = true;
   }

   /**
    * Called to signal the MIDlet to terminate.
    * @param unconditional if true, then the MIDlet has to be unconditionally terminated and all resources has to be released.
    */
   public void destroyApp(boolean unconditional) {
      m_app.Close();
   }

   /**
    * Functon sends request to web server to post an update
    */
   public void PostUpdate() {
      // Post update
      final String updateText = getTxtboxUpdate().getString();
      m_app.PostUpdate(updateText, m_replyToId);
      m_replyToId = 0;

      // Clear content of update form
      getTxtboxUpdate().setString("");
   }

    /**
     * Function saves settings from config form to config storage
     */
   private void UpdateConfig() {
      Settings.SettingsRec settingsRec = Settings.GetInstance().GetSettings();
      settingsRec.m_name = getFrmConfigTxtUserName().getString();
      settingsRec.m_password = getFrmConfigTxtPassword().getString();
      settingsRec.m_onStartNewTweet = getFrmConfigOnStartGroup().isSelected(0);
      settingsRec.m_onStartDownloadUpdates = getFrmConfigOnStartGroup().isSelected(1);
      Settings.GetInstance().ApplySettings(settingsRec);
   }

   
   public void TWASC_OnUpdateStarted(TwitterUpdate i_update) {
      m_app.GetOutbox().AddItem(i_update);
      updateOutboxMainWindowItem();
   }

   /**
    * Callback method. Called by Twitter accessor class to notify user about
    * asynchronous http access operation status (errors, success and so on).
    * See TwitterPosterStatusCallback.TWASC_OnUpdatePosted
    * @param i_status in, status string
    */
   public void TWASC_OnUpdatePosted(TwitterUpdate i_update) {
      // Delete item from outbox
      m_app.GetOutbox().DeleteItem(i_update.m_id);

      // Update outbox item on the main screen
      updateOutboxMainWindowItem();
      updateOutboxListDisplayable();

      destroyAppIfMinimizedAndOutboxEmpty();
   }

   /**
    * See TwitterPosterStatusCallback.TWASC_OnError
    */
   public void TWASC_OnUpdateError(TwitterUpdate i_update, String i_error) {
      getAlertError().setString(i_error);
      switchDisplayable(null, getAlertError());

      m_app.GetOutbox().DeleteItem(i_update.m_id);

      saveUpdateToDrafts(i_update.m_text, i_update.m_replyToId);

      // Update outbox
      updateOutboxMainWindowItem();
      updateOutboxListDisplayable();

      destroyAppIfMinimizedAndOutboxEmpty();
   }

   private void loadReadmeText() {
      if (getFrmReadmeText().getText() != null) {
         // Text already loaded, exiting
         return;
      }

      InputStream is = this.getClass().getResourceAsStream(c_readmeFileName);
      StringBuffer sb = new StringBuffer();
      byte[] buf = new byte[64];
      int len = 0;
      try {
         while ((len = is.read(buf)) != -1) {
            sb.append(new String(buf, 0, len));
         }

         getFrmReadmeText().setText(sb.toString());
         is.close();
      } catch (IOException ex) {
         ex.printStackTrace();
      }
   }

    /**************************************************************************
     *  Draft functions
     **************************************************************************/
   private void saveUpdateToDrafts(String i_updateText, long i_replyToId) {
      String updateText = i_updateText;
      // Clear update displayable textbox
      getTxtboxUpdate().setString("");

      if (i_updateText.length() == 0) {
         // Save only not empty drafts
         return;
      }

      // Create new update object
      TwitterUpdate update = TwitterUpdate.CreateNew(updateText, i_replyToId);
      m_app.GetDrafts().AddItem(update);

      // Update drafts displayable and item in main menu
      updateDraftsMainWindowItem();
      updateDraftsDisplayable(true);
   }

   private void editDraftUpdate() {
      final int selDraftNum = getListDrafts().getSelectedIndex();
      TwitterUpdate update = m_app.GetDrafts().ElementAt(selDraftNum);
      getTxtboxUpdate().setString(update.m_text);
      m_replyToId = update.m_replyToId;
      deleteDraftItem();
   }

   /**
    * Updates drafts items displayable - window title and items
    * @param updateListItems if true then updates items in list, otherwise
    * only title
    */
   private void updateDraftsDisplayable(boolean updateListItems) {
      // Settint displayable title
      //
      final int draftItemCount = m_app.GetDrafts().GetItemCount();
      final String titleText = c_draftsListDisplayableTitle +
         (draftItemCount > 0 ? " (" + draftItemCount + ")" : "");
      getListDrafts().setTitle(titleText);

      if (!updateListItems) {
         // All done, no need to update list items
         return;
      }

      // Delete all items
      getListDrafts().deleteAll();

      // Fill list with drafts items
      for (int i = 0; i < draftItemCount; ++i) {
         TwitterUpdate update = m_app.GetDrafts().ElementAt(i);
         getListDrafts().append(update.m_text, null);
      }
   }

   /**
    * Function updates drafts IU - drafts title in the main form
    */
   private void updateDraftsMainWindowItem() {
      final int draftItemCount = m_app.GetDrafts().GetItemCount();
      final String titleText = c_draftsListDisplayableTitle + (draftItemCount > 0 ? " (" + draftItemCount + ")" : "");
      //getListUpdates().setTitle(titleText);
      ListSelectionKeeper keeper = new ListSelectionKeeper(getListMainForm());
      setTextAndIcon(getListMainForm(), c_DraftsItemPos, titleText, imgDrafts);
      keeper.Restore();
   }

   /**
    * Function deletes currently selected draft item
    */
   private void deleteDraftItem() {
      final int sel = getListDrafts().getSelectedIndex();
      if (-1 == sel) {
         // Nothing selected
         return;
      }

      final TwitterUpdate update = m_app.GetDrafts().ElementAt(sel);
      m_app.GetDrafts().DeleteItem(update.m_id);
      updateDraftsMainWindowItem();

      getListDrafts().delete(sel);    // Delete item from drafts list displayable
      updateDraftsDisplayable(false); // Update drafts displayable title
   }
    
    /***************************************************************************
     * Outbox functions
     **************************************************************************/

   /**
    * If the application is running in background and ontbox is empty, then
    * destroy the application.
    */
   private void destroyAppIfMinimizedAndOutboxEmpty() {
      // Requirement 14: When quitting if outbox contains items, application
      // shall be minimized.
      // When items have been sent, the application shall be closed.
      //
      if (isOutboxEmpty() && m_runningInBackground) {
         destroyApp(true);
         notifyDestroyed();
      }
   }

   /**
    * Function updates text of Outbox main frame item (according to the number of items
    * in the outpox)
    */
   private void updateOutboxMainWindowItem() {
      final int itemCount = m_app.GetOutbox().GetItemCount();
      String text = c_outboxListDisplayableTitle + (itemCount > 0 ? " (" + itemCount + ")" : "");
      ListSelectionKeeper keeper = new ListSelectionKeeper(getListMainForm());
      setTextAndIcon(getListMainForm(), c_OutboxItemPos, text, imgOutbox);
      keeper.Restore();
   }

   /**
    * Get number of items in the outbox
    * @return number of items in the outbox
    */
   private boolean isOutboxEmpty() {
      return m_app.GetOutbox().GetItemCount() == 0;
   }
    
   /**
    * Function fills outbox list according to the content of outbox
    */
   private void updateOutboxListDisplayable() {
      // Settint displayable title
      //
      final int itemCount = m_app.GetOutbox().GetItemCount();
      String titleText = c_outboxListDisplayableTitle + (itemCount > 0 ? " (" + itemCount + ")" : "");
      getListOutbox().setTitle(titleText);

      // Filling displayable list
      //

      // Delete all items
      getListOutbox().deleteAll();

      // Fill list with outbox items
      for (int i = 0; i < itemCount; ++i) {
         TwitterUpdate update = m_app.GetOutbox().ElementAt(i);
         getListOutbox().append(update.m_text, null);
      }
   }

    /***************************************************************************
     * Timelines functions
     **************************************************************************/

   /**
    * Downloads all timelines
    */
   private void receiveAllTimelines() {
      m_app.DownloadAll();
      updateTimelinesWindowTitle();
      updateDownloadProgressIcons();
   }

   private void showHomeTimeline() {
      m_currentTimeline = m_app.GetHomeTimeline();
      updateListTimeline();
      goToListTimeline();
   }

   private void showRepliesTimeline() {
      m_currentTimeline = m_app.GetRepliesTimeline();
      updateListTimeline();
      goToListTimeline();
   }

   private void showDirectInboxTimeline() {
      m_currentTimeline = m_app.GetDirectInboxTimeline();
      updateListTimeline();
      goToListTimeline();
   }

   private void showDirectOutboxTimeline() {
      m_currentTimeline = m_app.GetDirectSentTimeline();
      updateListTimeline();
      goToListTimeline();
   }

   private void showSearchTimeline(int i_searchTimelineNo) {
      m_currentTimeline = m_app.GetSearchManager().GetTimeline(i_searchTimelineNo).m_timeline;
      updateListTimeline();
      goToListTimeline();
   }
  
   private void updateTimelinesWindowTitle() {
      final String title = m_app.IsDownloading() ? "Downloading Timelines..." :
         "Timelines";
      getListTimelines().setTitle(title);
   }

   // Return @username title for Replies folder
   String getRepliesSubfolderTitle() {
      return "@" + Settings.GetInstance().GetSettings().m_name;
   }

   /**
    * Updates items in Timelines view - shows number of unread items in ().
    */
   private void updateTimelinesItemTitles() {
      final int unreadCounts[] = {
         m_app.GetHomeTimeline().GetUnreadItemCount(),
         m_app.GetRepliesTimeline().GetUnreadItemCount(),
         m_app.GetDirectInboxTimeline().GetUnreadItemCount(),
         m_app.GetDirectSentTimeline().GetUnreadItemCount(),
      };

      final boolean downloading[] = {
         m_app.GetHomeTimeline().Downloading(),
         m_app.GetRepliesTimeline().Downloading(),
         m_app.GetDirectInboxTimeline().Downloading(),
         m_app.GetDirectSentTimeline().Downloading(),
      };

      ListSelectionKeeper keeper = new ListSelectionKeeper(getListTimelines());

      final int searchCount = m_app.GetSearchManager().GetTimelineCount();
      for (int i = 0; i < c_timelinesItemTitles.length + searchCount; i++) {
         int unreadCount = 0;
         String title = "";
         Image image = null;
         if (i < c_timelinesItemTitles.length) {
            // Regular timelines
            unreadCount = unreadCounts[i];
            title = i == 1 ? getRepliesSubfolderTitle() : c_timelinesItemTitles[i];
            image = downloading[i] ? getImgDownload() : c_timelinesItemImages[i];
         } else {
            // Search timelines
            final int searchNum = i - c_timelinesItemTitles.length;
            DownloadableCollection collection =
               m_app.GetSearchManager().GetTimeline(searchNum).m_timeline;
            unreadCount = collection.GetUnreadItemCount();
            title = collection.GetName();
            image = collection.Downloading() ? getImgDownload() : getImgSearch();
         }

         final String text = title + (unreadCount > 0 ?
            " (" + String.valueOf(unreadCount) + ")" : "");
         setTextAndIcon(getListTimelines(), i, text, image);
      }
      keeper.Restore();
   }

   private void updateSearchTimelinesList() {
      final SearchManager searchManager = m_app.GetSearchManager();
      final int count = searchManager.GetTimelineCount();
      final int listItemCount = getListTimelines().size();
      for (int i = 0; i < count; ++i) {
         final int pos = c_firstSearchTimelinePos + i;
         final String text = searchManager.GetTimeline(i).m_search;
         if (listItemCount <= pos) {
            getListTimelines().insert(pos, text, getImgSearch());
         } else {
            getListTimelines().set(pos, text, getImgSearch());
         }
      }

      // delete items that are old
      final int neededItemCount = c_firstSearchTimelinePos + count;
      while (getListTimelines().size() > neededItemCount) {
         getListTimelines().delete(neededItemCount);
      }
   }

   void updateMainWindowTimelinesItem() {
      final int unreadItemCount = m_app.GetUnreadItemCount();
      final String friendsItemText = c_timelinesTitle + (unreadItemCount > 0 ?
         " (" + String.valueOf(unreadItemCount) + ")" : "");
      ListSelectionKeeper keeper = new ListSelectionKeeper(getListMainForm());
      setTextAndIcon(getListMainForm(), c_timelinesItemPos, friendsItemText,
         m_app.IsDownloading() ? getImgDownload() : getImgFriends());
      keeper.Restore();
   }

   private void addSearchTimeline() {
      final String search = getTxtboxSearch().getString();
      if (0 == search.length()) {
         // Do nothing if no search text is entered.
         return;
      }
      
      final int pos = m_app.GetSearchManager().AddSearchTimeline(search);
      updateSearchTimelinesList();
      getListTimelines().setSelectedIndex(pos + c_firstSearchTimelinePos, true);
      
      DownloadableCollection timeline = m_app.GetSearchManager().GetTimeline(pos).m_timeline;
      timeline.Download();
      showSearchTimeline(pos);
   }

   /**
    * @brief Deletes currently selected search timeline
    */
   private void deleteSearchTimeline() {
      final int pos = getListTimelines().getSelectedIndex();
      if (pos >= c_firstSearchTimelinePos) {
         m_app.GetSearchManager().Delete(pos - c_firstSearchTimelinePos);
         updateSearchTimelinesList();

         // Keep selection at the old position
         final int newPos = pos >= getListTimelines().size()? pos - 1 : pos;
         getListTimelines().setSelectedIndex(newPos, true);
      }
   }

   /***************************************************************************
   * Timeline window function
   **************************************************************************/

   /**
    * @brief Downloads current timeline (the one currently
    *        selected in UI)
    */
   private void receive() {
      m_lastTweetBeforeDownloadOlder = 0;
      m_currentTimeline.Download();
      showDownloadingProgressInTimelineWindow();
      updateDownloadProgressIcons();
   }

   private void viewTimelineNextPage() {
      // Remember id of last item
      final int count = m_currentTimeline.GetItemCount();
      m_lastTweetBeforeDownloadOlder = (0 == count) ? 0 :
         m_currentTimeline.ElementAt(count - 1).m_id;

      // Download older tweets
      m_currentTimeline.DownloadEarlier();
      showDownloadingProgressInTimelineWindow();
      updateDownloadProgressIcons();
   }

   /**
    * @brief Sets timeline window title (like "Friends (12)")
    */
   private void updateListTimelineTitle() {
      final int unread = m_currentTimeline.GetUnreadItemCount();
      final String title = m_currentTimeline.GetName() +
         (unread > 0 ? // number of unread items
            " (" + String.valueOf(unread) + ")" : "");
      getListTimeline().setTitle(title);
   }

   private void updateListTimeline() {
      if (m_currentTimeline.Downloading())
      {
         showDownloadingProgressInTimelineWindow();
         return;
      }

      updateListTimelineTitle();

      // Filling displayable list
      //

      // Delete all items
      getListTimeline().deleteAll();

      // Fill list with outbox items
      final int itemCount = m_currentTimeline.GetItemCount();
      for (int i = 0; i < itemCount; ++i) {
         TwitterUpdate update = m_currentTimeline.ElementAt(i);
         String text = update.m_author + ": " + update.m_text;
         Image img = update.m_read ? getImgTweetRead() : getImgTweet();
         getListTimeline().append(text, img);
      }
         
      if (itemCount > 0) {
         getListTimeline().append(c_nextPage, null);
      }
   }

   private void markAllTweetsRead(boolean i_read) {
      ListSelectionKeeper keeper = new ListSelectionKeeper(getListTimeline());
      for (int i = 0; i < m_currentTimeline.GetItemCount(); ++i) {
         m_currentTimeline.ElementAt(i).MarkAsRead(i_read);
         // update read icon
         final String text = getListTimeline().getString(i);
         Image img = i_read ? getImgTweetRead() : getImgTweet();
         setTextAndIcon(getListTimeline(), i, text, img);
      }
      keeper.Restore();
      updateListTimelineTitle();
   }

   public void DCSC_OnDownloadFinished(UpdateCollection i_timeline) {
      if (m_currentTimeline == i_timeline)
      {
         updateListTimeline();
      }
      updateTimelinesItemTitles();
      updateTimelinesWindowTitle();
      updateDownloadProgressIcons();

      // If we had progress view displayed, close it and display
      // timeline window
      if (getDisplay().getCurrent() == getFrmDownloadProgress()) {
         switchDisplayable(null, getListTimeline());
      }

      // If we remembered last tweet when loading previous page, keep this
      // selection
      if (0 != m_lastTweetBeforeDownloadOlder) {
         final int pos = m_currentTimeline.FindTweet(m_lastTweetBeforeDownloadOlder);
         if (pos >= 0 && pos <  m_currentTimeline.GetItemCount() - 1) {
            // Item presents and it's not last, set selection after it
            getListTimeline().setSelectedIndex(pos + 1, true);
         }
      }
   }

   public void DCSC_OnDownloadStep(UpdateCollection i_timeline, int i_persent) {
      if (m_currentTimeline != i_timeline)
      {
         return;
      }
      if (Gauge.INDEFINITE == getFrmDownloadProgressBar().getMaxValue()) {
         getFrmDownloadProgressBar().setMaxValue(100);
      }
      String text = "Downloading...";
      getFrmDownloadProgressBar().setLabel(text);
      getFrmDownloadProgressBar().setValue(i_persent);
   }

   /**
    * @brief function deletes all items from timeline window and
    *        switches to download progress window.
    *        It also updates current download status on gauge.
    */
   private void showDownloadingProgressInTimelineWindow() {
      if (!m_currentTimeline.Downloading()) {
         // Current timeline is not downloading so return
         return;
      }

      if (getDisplay().getCurrent() == getListTimeline()) {
         switchDisplayable(null, getFrmDownloadProgress());
      }

      // Show connection progress on gauge
      final int progress = m_currentTimeline.GetDownloadProgress();
      if (0 == progress) {
         // Delete all items from timeline window
         getListTimeline().deleteAll();
         // We are still connecting
         getFrmDownloadProgressBar().setLabel("Connecting...");
         getFrmDownloadProgressBar().setMaxValue(Gauge.INDEFINITE);
         getFrmDownloadProgressBar().setValue(Gauge.CONTINUOUS_RUNNING);
      } else {
         // Show current progress
         getFrmDownloadProgressBar().setLabel("Downloading...");
         getFrmDownloadProgressBar().setMaxValue(100);
         getFrmDownloadProgressBar().setValue(progress);
      }
   }

   private void goToListTimeline() {
      if (m_currentTimeline.Downloading()) {
         switchDisplayable(null, getFrmDownloadProgress());
      } else {
         switchDisplayable(null, getListTimeline());
      }
   }

   private void updateDownloadProgressIcons() {
      // Timelines item in the main window
      updateMainWindowTimelinesItem();

      // Friends timeline
      setIcon(getListTimelines(), 0, m_app.GetHomeTimeline().Downloading() ?
         getImgDownload() : getImgFriends());

      // Replies timeline
      setIcon(getListTimelines(), 1, m_app.GetRepliesTimeline().Downloading() ?
         getImgDownload() : getImgReplies());

      // Friends timeline
      setIcon(getListTimelines(), 2, m_app.GetDirectInboxTimeline().Downloading() ?
         getImgDownload() : getImgDirect());

      // Friends timeline
      setIcon(getListTimelines(), 3, m_app.GetDirectSentTimeline().Downloading() ?
         getImgDownload() : getImgDirectOutbox());

      for (int i = 0; i < m_app.GetSearchManager().GetTimelineCount(); ++i) {
         final int pos = c_firstSearchTimelinePos + i;
         setIcon(getListTimelines(), pos, 
            m_app.GetSearchManager().GetTimeline(i).m_timeline.Downloading() ?
               getImgDownload() : getImgSearch());
      }
   }

   private void setIcon(List i_listForm, int i_itemNo, Image i_image) {
      if (i_listForm.getImage(i_itemNo) == i_image) {
         // item already has this image assigned.
         return;
      }
      final String text = i_listForm.getString(i_itemNo);
      i_listForm.set(i_itemNo, text, i_image);
   }

   /**
    * Sets list item text and selection
    * Function is optimized for SE to avoid bug with 2 icons for one item
    */
   private void setTextAndIcon(List i_listForm, int i_itemNo, String i_text,
      Image i_image) {
      // Prevents problem with two images on SE K300i
      //i_listForm.setSelectedIndex(i_itemNo, true);
      i_listForm.set(i_itemNo, i_text, i_image);
   }

  private void viewTweet(DownloadableCollection i_timeline, int i_tweetNo) {
      // update read icon
      final String text = getListTimeline().getString(i_tweetNo);
      ListSelectionKeeper keeper = new ListSelectionKeeper(getListTimeline());
      setTextAndIcon(getListTimeline(), i_tweetNo, text, getImgTweetRead());
      keeper.Restore();

      // Prepare timeline details form to show
      TwitterUpdate update = i_timeline.ElementAt(i_tweetNo);
      getFrmTweetDetails().setTitle(DateUtils.FormatDate(update.m_date));
      getTxtTweetDetailsText().setLabel(update.m_author);
      getTxtTweetDetailsText().setText(" " + update.m_text);

      //show tweet being replied
      if (0 == update.m_replyToId) {
         getTxtInReplyTo().setLabel("");
         getTxtInReplyTo().setText("");
      } else {
         getTxtInReplyTo().setLabel(c_inReplyToTitle);
         uTweetMeApp.FoundTweet foundTweet = m_app.FindTweet(update.m_replyToId);
         if (null == foundTweet) {
            getTxtInReplyTo().setText("Not Loaded");
         } else {
            TwitterUpdate updateReplied = foundTweet.m_collection.ElementAt(
               foundTweet.m_pos);
            final String inReplyToUser = updateReplied.m_author;
            getTxtInReplyTo().setText(inReplyToUser);
         }
      }

      update.MarkAsRead(true);
      m_currentTweetNo = i_tweetNo;

      // Show timeline details form
      switchDisplayable(null, getFrmTweetDetails());

      /// We might try to see tweet from not current timeline. In this case
      /// we need to make this timeline current.
      if (i_timeline != m_currentTimeline) {
         // change timeline
         m_currentTimeline = i_timeline;
         // Update timeline view
         updateListTimeline();
      }
      // set selection to current tweet, in case if it's not there
      getListTimeline().setSelectedIndex(i_tweetNo, true);
   }

   private void markViewedTweetUnread() {
      // update read icon
      final String text = getListTimeline().getString(m_currentTweetNo);
      ListSelectionKeeper keeper = new ListSelectionKeeper(getListTimeline());
      setTextAndIcon(getListTimeline(), m_currentTweetNo, text, getImgTweet());
      keeper.Restore();

      // uopdate tweet
      TwitterUpdate update = m_currentTimeline.ElementAt(m_currentTweetNo);
      update.MarkAsRead(false);
   }

   private void clearTimeline() {
      m_currentTimeline.Clear();
      updateListTimeline();
      updateTimelinesItemTitles();
   }

   private void replyToTweet() {
      final String author = getTxtTweetDetailsText().getLabel();
      getTxtboxUpdate().setString("@" + author + " ");
      switchDisplayable(null, getTxtboxUpdate());

      TwitterUpdate update = m_currentTimeline.ElementAt(m_currentTweetNo);
      m_replyToId = update.m_id;

      // Update numbers in titles
      updateTimelinesItemTitles();
      updateTimelinesWindowTitle();
      updateMainWindowTimelinesItem();
   }

   private void retweet() {
      final String author = getTxtTweetDetailsText().getLabel();
      final String text = getTxtTweetDetailsText().getText();

      // author + text because we have " " in the beginning of text already
      String retweetText = "RT @" + author + text;
      if (retweetText.length() > TwitterUpdate.m_maxLongTweetTextLen) {
         retweetText = retweetText.substring(0, TwitterUpdate.m_maxLongTweetTextLen);
      }

      getTxtboxUpdate().setString(retweetText);
      switchDisplayable(null, getTxtboxUpdate());

      m_replyToId = 0;  // Retweet shouldn't have reference to original tweet
                        // otherwise it will not be visible to the people who
                        // don't follow original tweet author.

      // Update numbers in titles
      updateTimelinesItemTitles();
      updateTimelinesWindowTitle();
      updateMainWindowTimelinesItem();
   }

   private void replyDirectToTweet() {
      final String author = getTxtTweetDetailsText().getLabel();
      getTxtboxUpdate().setString("d " + author + " ");
      switchDisplayable(null, getTxtboxUpdate());
      m_replyToId = 0; // Direct replies are not supported

      // Update numbers in titles
      updateTimelinesItemTitles();
      updateTimelinesWindowTitle();
      updateMainWindowTimelinesItem();
   }


   /**
    * @brief Is called when user clicked "in reply to" label on the
    *        tweet details form.
    */
   private void showReply() {
      TwitterUpdate update = m_currentTimeline.ElementAt(m_currentTweetNo);
      uTweetMeApp.FoundTweet foundTweet = m_app.FindTweet(update.m_replyToId);
      if (null == foundTweet) {
         // This must be "Update not loaded"
         return;
      }
      viewTweet(foundTweet.m_collection, foundTweet.m_pos);
   }

   /**
    * Method is executed when user is leaving timeline screen
    * If this is a search timeline which has been just created, then
    * we  prompt to delete this timeline or keep it.
    */
   private void leaveListTimeline() {
      switchDisplayable(null, getListMainForm());
   }

   public void OnSelectTemplate(String i_templateText) {
      final String text = getTxtboxUpdate().getString() + i_templateText;
      getTxtboxUpdate().setString(text);
      switchDisplayable(null, getTxtboxUpdate());
   }

   public void OnSelectCancelled() {
      switchDisplayable(null, getTxtboxUpdate());
   }

   public void OnEditCancelled() {
      switchDisplayable(null, getListMainForm());
   }

   /**
    * Create new tweet bases on template
    */
   public void OnCreateNewTweet(String i_templateText) {
      m_displayableToReturnFromEdit = getListMainForm();
      getTxtboxUpdate().setString(i_templateText);
      switchDisplayable(null, getTxtboxUpdate());
   }

   /**
    * Launching URL in phone browser is cancelled by user
    */
   public void OnLaunchCancelled() {
      switchDisplayable(null, getFrmTweetDetails());
   }

   /**
    * URL should be launched in the phone browser
    */
   public void OnUrlSelected(String i_url) {
      try {
         platformRequest(i_url);
         exitMIDlet();
      } catch (ConnectionNotFoundException ex) {
         ex.printStackTrace();
      }
   }

   /**
    * Given text should be saved as a new template
    * @param i_text in, new template text
    */
   private void saveAsTemplate(String i_text) {
      if (0 == i_text.length()) {
         // Don't save empty text as template
         return;
      }

      uTweetMe.UI.TemplateWindowManager.GetInstance().SaveAsTempate(i_text);

      // Update numbers in titles
      updateTimelinesItemTitles();
      updateTimelinesWindowTitle();
      updateMainWindowTimelinesItem();
   }

   /**
    * @brief Function is called when tweet is deleted. Shows timeline window.
    * @param i_tweetId in, deleted tweet id
    */
   public void OnDeleteTweet(long i_tweetId) {
      m_app.DeleteTweetFromContainers(i_tweetId);
      final int pos = getListTimeline().getSelectedIndex();
      updateListTimeline();
      final int size = getListTimeline().size();
      if (size > 0) {
         final int newPos = pos >= size ? size - 1 : pos;
         getListTimeline().setSelectedIndex(newPos, true);
      }
      Alert alert = new Alert("Info", "Tweet deleted", null, AlertType.CONFIRMATION);
      switchDisplayable(alert, getListTimeline());
   }

   /**
    * @brief Function is called when tweet deleting is cancelled by user. Just
    *        go back to tweet details window
    */
   public void OnActionCancelled(String i_message) {
      Alert alert = null;
      if (null != i_message && 0 != i_message.length()) {
         alert = new Alert("Error", i_message, null, AlertType.ERROR);
      }
      switchDisplayable(alert, getFrmTweetDetails());
   }

	/**
	 * @brief Creates text to show as a privew for tweet posting.
	 *		If Tweet is longer than 140 characters, displays warning
	 *		that it will be posted via TwiGu.ru service.
	 * @param i_textAboutToTweet - tweet text
	 * @return Text for preview
	 */
	private String getPreviewText(String i_textAboutToTweet) {
		final int tweetLength = i_textAboutToTweet.length();
		if (tweetLength <= TwitterUpdate.m_maxUpdateTextLen) {
			return i_textAboutToTweet;
		}
		final String msg = replace("#chars#", Integer.toString(tweetLength),
			c_longTweetWarning);
		return i_textAboutToTweet + msg;
	}

	private String replace(String needle, String replacement, String haystack) {
		String result = "";
		int index = haystack.indexOf(needle);
		if (index == 0) {
			result = replacement + haystack.substring(needle.length());
			return replace(needle, replacement, result);
		} else if (index > 0) {
			result = haystack.substring(0, index) + replacement + haystack.substring(index + needle.length());
			return replace(needle, replacement, result);
		} else {
			return haystack;
		}
	}
}