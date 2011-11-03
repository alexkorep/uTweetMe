/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uTweetMe.UI;

import javax.microedition.lcdui.*;
import org.netbeans.microedition.lcdui.WaitScreen;
import org.netbeans.microedition.util.SimpleCancellableTask;
import uTweetMe.LoginManager;
import uTweetMe.UTweetMIDlet;

/**
 * @author user
 */
public class XAuthLogin implements CommandListener {

    private UTweetMIDlet midlet;

    //<editor-fold defaultstate="collapsed" desc=" Generated Fields ">//GEN-BEGIN:|fields|0|
    private Form frmOAuthUsername;
    private TextField txtUserName;
    private TextField txtPassword;
    private Alert alertLoginSuccess;
    private WaitScreen waitScreenLogin;
    private Command itemCommand;
    private Command cmdLogin;
    private SimpleCancellableTask taskLogin;
    //</editor-fold>//GEN-END:|fields|0|
    private final LoginManager m_loginManager;
    //</editor-fold>

    /**
     * The OAuthLogin constructor.
     * @param midlet the midlet used for getting
     */
    public XAuthLogin (UTweetMIDlet midlet, LoginManager i_loginManager) {
        this.midlet = midlet;
        m_loginManager = i_loginManager;
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
         uTweetMe.Settings.SettingsRec settings =
            uTweetMe.Settings.GetInstance().GetSettings();

       getTxtUserName().setString(settings.m_name);
       getTxtPassword().setString(settings.m_password);
//GEN-LINE:|0-initialize|1|0-postInitialize
        // write post-initialize user code here
    }//GEN-BEGIN:|0-initialize|2|
    //</editor-fold>//GEN-END:|0-initialize|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: switchDisplayable ">//GEN-BEGIN:|2-switchDisplayable|0|2-preSwitch
    /**
     * Switches a current displayable in a display. The <code>display</code> instance is taken from <code>getDisplay</code> method. This method is used by all actions in the design for switching displayable.
     * @param alert the Alert which is temporarily set to the display; if <code>null</code>, then <code>nextDisplayable</code> is set immediately
     * @param nextDisplayable the Displayable to be set
     */
    public void switchDisplayable(Alert alert, Displayable nextDisplayable) {//GEN-END:|2-switchDisplayable|0|2-preSwitch
        // write pre-switch user code here
       Display display = getDisplay();//GEN-BEGIN:|2-switchDisplayable|1|2-postSwitch
       if (alert == null) {
          display.setCurrent(nextDisplayable);
       } else {
          display.setCurrent(alert, nextDisplayable);
       }//GEN-END:|2-switchDisplayable|1|2-postSwitch
        // write post-switch user code here
    }//GEN-BEGIN:|2-switchDisplayable|2|
    //</editor-fold>//GEN-END:|2-switchDisplayable|2|
	 //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: frmOAuthUsername ">//GEN-BEGIN:|11-getter|0|11-preInit
    /**
     * Returns an initiliazed instance of frmOAuthUsername component.
     * @return the initialized component instance
     */
    public Form getFrmOAuthUsername() {
       if (frmOAuthUsername == null) {//GEN-END:|11-getter|0|11-preInit
			 // write pre-init user code here
          frmOAuthUsername = new Form("User Name", new Item[] { getTxtUserName(), getTxtPassword() });//GEN-BEGIN:|11-getter|1|11-postInit
          frmOAuthUsername.addCommand(getCmdLogin());
          frmOAuthUsername.setCommandListener(this);//GEN-END:|11-getter|1|11-postInit
			 // write post-init user code here
       }//GEN-BEGIN:|11-getter|2|
       return frmOAuthUsername;
    }
    //</editor-fold>//GEN-END:|11-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: commandAction for Displayables ">//GEN-BEGIN:|4-commandAction|0|4-preCommandAction
    /**
     * Called by a system to indicated that a command has been invoked on a particular displayable.
     * @param command the Command that was invoked
     * @param displayable the Displayable where the command was invoked
     */
    public void commandAction(Command command, Displayable displayable) {//GEN-END:|4-commandAction|0|4-preCommandAction
             // write pre-action user code here
       if (displayable == frmOAuthUsername) {//GEN-BEGIN:|4-commandAction|1|15-preAction
          if (command == cmdLogin) {//GEN-END:|4-commandAction|1|15-preAction
               // write pre-action user code here
             switchDisplayable(null, getWaitScreenLogin());//GEN-LINE:|4-commandAction|2|15-postAction
                // write post-action user code here
          }//GEN-BEGIN:|4-commandAction|3|26-preAction
       } else if (displayable == waitScreenLogin) {
          if (command == WaitScreen.FAILURE_COMMAND) {//GEN-END:|4-commandAction|3|26-preAction
             // write pre-action user code here
//GEN-LINE:|4-commandAction|4|26-postAction
             // write post-action user code here
          } else if (command == WaitScreen.SUCCESS_COMMAND) {//GEN-LINE:|4-commandAction|5|25-preAction
             // write pre-action user code here
//GEN-LINE:|4-commandAction|6|25-postAction
             // write post-action user code here
             switchDisplayable(getAlertLoginSuccess(), midlet.getListMainForm());
          }//GEN-BEGIN:|4-commandAction|7|4-postCommandAction
       }//GEN-END:|4-commandAction|7|4-postCommandAction
             // write post-action user code here
    }//GEN-BEGIN:|4-commandAction|8|
    //</editor-fold>//GEN-END:|4-commandAction|8|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: itemCommand ">//GEN-BEGIN:|12-getter|0|12-preInit
    /**
     * Returns an initiliazed instance of itemCommand component.
     * @return the initialized component instance
     */
    public Command getItemCommand() {
       if (itemCommand == null) {//GEN-END:|12-getter|0|12-preInit
                 // write pre-init user code here
          itemCommand = new Command("Item", Command.ITEM, 0);//GEN-LINE:|12-getter|1|12-postInit
                 // write post-init user code here
       }//GEN-BEGIN:|12-getter|2|
       return itemCommand;
    }
    //</editor-fold>//GEN-END:|12-getter|2|
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: cmdLogin ">//GEN-BEGIN:|14-getter|0|14-preInit
    /**
     * Returns an initiliazed instance of cmdLogin component.
     * @return the initialized component instance
     */
    public Command getCmdLogin() {
       if (cmdLogin == null) {//GEN-END:|14-getter|0|14-preInit
            // write pre-init user code here
          cmdLogin = new Command("Login", "Login to Twitter", Command.ITEM, 0);//GEN-LINE:|14-getter|1|14-postInit
            // write post-init user code here
       }//GEN-BEGIN:|14-getter|2|
       return cmdLogin;
    }
    //</editor-fold>//GEN-END:|14-getter|2|





    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: txtUserName ">//GEN-BEGIN:|19-getter|0|19-preInit
    /**
     * Returns an initiliazed instance of txtUserName component.
     * @return the initialized component instance
     */
    public TextField getTxtUserName() {
       if (txtUserName == null) {//GEN-END:|19-getter|0|19-preInit
          // write pre-init user code here
          txtUserName = new TextField("Username", "", 100, TextField.ANY);//GEN-LINE:|19-getter|1|19-postInit
          // write post-init user code here
       }//GEN-BEGIN:|19-getter|2|
       return txtUserName;
    }
    //</editor-fold>//GEN-END:|19-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: txtPassword ">//GEN-BEGIN:|20-getter|0|20-preInit
    /**
     * Returns an initiliazed instance of txtPassword component.
     * @return the initialized component instance
     */
    public TextField getTxtPassword() {
       if (txtPassword == null) {//GEN-END:|20-getter|0|20-preInit
          // write pre-init user code here
          txtPassword = new TextField("Password", "", 50, TextField.ANY);//GEN-LINE:|20-getter|1|20-postInit
          // write post-init user code here
       }//GEN-BEGIN:|20-getter|2|
       return txtPassword;
    }
    //</editor-fold>//GEN-END:|20-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: alertLoginSuccess ">//GEN-BEGIN:|21-getter|0|21-preInit
    /**
     * Returns an initiliazed instance of alertLoginSuccess component.
     * @return the initialized component instance
     */
    public Alert getAlertLoginSuccess() {
       if (alertLoginSuccess == null) {//GEN-END:|21-getter|0|21-preInit
          // write pre-init user code here
          alertLoginSuccess = new Alert("Login", "Logged in successfully.", null, null);//GEN-BEGIN:|21-getter|1|21-postInit
          alertLoginSuccess.setTimeout(Alert.FOREVER);//GEN-END:|21-getter|1|21-postInit
          // write post-init user code here
       }//GEN-BEGIN:|21-getter|2|
       return alertLoginSuccess;
    }
    //</editor-fold>//GEN-END:|21-getter|2|
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: waitScreenLogin ">//GEN-BEGIN:|22-getter|0|22-preInit
    /**
     * Returns an initiliazed instance of waitScreenLogin component.
     * @return the initialized component instance
     */
    public WaitScreen getWaitScreenLogin() {
       if (waitScreenLogin == null) {//GEN-END:|22-getter|0|22-preInit
          // write pre-init user code here
          waitScreenLogin = new WaitScreen(getDisplay());//GEN-BEGIN:|22-getter|1|22-postInit
          waitScreenLogin.setTitle("Login");
          waitScreenLogin.setCommandListener(this);
          waitScreenLogin.setText("Logging in, please wait...");
          waitScreenLogin.setTask(getTaskLogin());//GEN-END:|22-getter|1|22-postInit
          // write post-init user code here
       }//GEN-BEGIN:|22-getter|2|
       return waitScreenLogin;
    }
    //</editor-fold>//GEN-END:|22-getter|2|
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: taskLogin ">//GEN-BEGIN:|27-getter|0|27-preInit
    /**
     * Returns an initiliazed instance of taskLogin component.
     * @return the initialized component instance
     */
    public SimpleCancellableTask getTaskLogin() {
       if (taskLogin == null) {//GEN-END:|27-getter|0|27-preInit
          // write pre-init user code here
          taskLogin = new SimpleCancellableTask();//GEN-BEGIN:|27-getter|1|27-execute
          taskLogin.setExecutable(new org.netbeans.microedition.util.Executable() {
             public void execute() throws Exception {//GEN-END:|27-getter|1|27-execute
             // write task-execution user code here
                String username = getTxtUserName().getString();
                String password = getTxtPassword().getString();
                if (m_loginManager.Login(username, password))
                {
                   getAlertLoginSuccess().setString("Login succesfull");
                }
                else
                {
                   getAlertLoginSuccess().setString("Wrong Username or Password");
                }

             }//GEN-BEGIN:|27-getter|2|27-postInit
          });//GEN-END:|27-getter|2|27-postInit
          // write post-init user code here
       }//GEN-BEGIN:|27-getter|3|
       return taskLogin;
    }
    //</editor-fold>//GEN-END:|27-getter|3|

    /**
     * Returns a display instance.
     * @return the display instance.
     */
    public Display getDisplay () {
        return Display.getDisplay(midlet);
    }

    /**
     * Exits MIDlet.
     * Note you have to implement proper MIDlet destroying.
     */
    public void exitMIDlet() {
        switchDisplayable (null, null);
        // midlet.destroyApp(true);
        midlet.notifyDestroyed();
    }

}
