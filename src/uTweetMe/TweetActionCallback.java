package uTweetMe;

/**
 * Callback interface, used by TweetActionPerformer for notifying users about 
 * action status.
 */
public interface TweetActionCallback
{
    /**
     * Fired when action is done
     */
    public void OnTweetActionDone();
    
    /**
     * Fired when error occured during action
     * @param i_error error text string
     */
    public void OnTweetActionError(String i_error);
}
