package uTweetMe.UI;

/**
 * Interface implemented by templates user
 */
public interface TemplatesCallback {

   /**
    * @brief Function is called by template window when user has selected
    * one of templates for new tweet creation
    * @param i_templateText in, text of template user selected
    */
   public void OnCreateNewTweet(String i_templateText);

   /**
    * @brief Function is called by template window when user has selected
    * one of templates
    * @param i_templateText in, text of template user selected
    */
   void OnSelectTemplate(String i_templateText);

   /**
    * @brief Function is called by template window when user cancelled
    *        template selection
    */
   public void OnSelectCancelled();

   /**
    * @brief Function is called by template window when user cancelled
    *        template editing
    */
   public void OnEditCancelled();

}
