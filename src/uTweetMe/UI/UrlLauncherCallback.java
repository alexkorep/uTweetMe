package uTweetMe.UI;

/**
 * @interface UrlLauncherCallback
 * @brief Is used by URL launcher UI class to handle user actions
 */
public interface UrlLauncherCallback {
   /**
    * @brief Function is called when user cancels selecting URL to launch
    */
   public void OnLaunchCancelled();

   /**
    * @brief Function is called when user selected URL to launch in the
    *        phone browser.
    * @param i_url in, URL to launch
    */
   public void OnUrlSelected(String i_url);
}
