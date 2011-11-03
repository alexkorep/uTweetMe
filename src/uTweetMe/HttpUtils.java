/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uTweetMe;

import com.twitterapime.io.HttpRequest;
import com.twitterapime.io.HttpResponse;
import com.twitterapime.rest.UserAccountManager;
import com.twitterapime.search.LimitExceededException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Hashtable;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;

/**
 * Download progress callback interface
 */
interface IDownloadProgress {
   /**
    * @brief Method is called during download to indicate the progress.
    * @param i_percent in, download progress percents (0..100)
    * @return true if downloading must be interrupted
    */
   public boolean OnProgress(int i_percent);

   /**
    * Is called when download is finished
    */
   public void OnDownloadFinished();
}

/**
 *
 * @author Alexey
 */
public class HttpUtils 
{
    private static final String c_userAgent = "uTweetMe";

    /// @brief if true, then we are being logging in. In this case
    ///        other threads need to wait until this flag gets to false
    private static boolean m_inLoginProgress = false;
    
    /** 
     * Encodes string to URL represntation
     * @param i_string input string
     * @return URL-encoded string
     */
    public static String URLEncode(String i_string) 
    {
        if (i_string == null) 
        {
            return null;
        }
        
        String result = "";
        try 
        {
            result = new String(i_string.getBytes("UTF-8"), "ISO-8859-1");
        } 
        catch (UnsupportedEncodingException e) 
        {
        }

        StringBuffer strbufResult = new StringBuffer();
        try 
        {
            for (int i = 0; i < result.length(); ++i) 
            {
                int b = (int) result.charAt(i);
                if ((b >= 0x30 && b <= 0x39) || 
                    (b >= 0x41 && b <= 0x5A) || 
                    (b >= 0x61 && b <= 0x7A)) 
                {
                    strbufResult.append((char) b);
                } 
                else if (b == 0x20) 
                {
                    strbufResult.append("+");
                } 
                else 
                {
                    strbufResult.append("%");
                    if (b <= 0xf) 
                    {
                        strbufResult.append("0");
                    }
                    strbufResult.append(Integer.toHexString(b));
                }
            }
        } 
        catch (Exception e)
        {
        }
        return strbufResult.toString();
    }
    

    /**
     * Characters that can present in encoded string
     */
    static final char[] encodeChars =
    "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".toCharArray();

    /**
     * @param i_bufToEncode in, buffer of characters to encode
     * @return encoded string
     */
    public static String Base64Encode(byte[] i_bufToEncode) 
    {
        int len = i_bufToEncode.length;
        StringBuffer result = new StringBuffer(i_bufToEncode.length * 3 / 2);

        int start = 0;
        int end = len - 3;
        
        int i = start;
        int n = 0;

        while (i <= end) 
        {
            int d = ((((int) i_bufToEncode[i]) & 0x0ff) << 16)
                    | ((((int) i_bufToEncode[i + 1]) & 0x0ff) << 8)
                    | (((int) i_bufToEncode[i + 2]) & 0x0ff);

            result.append(encodeChars[(d >> 18) & 63]);
            result.append(encodeChars[(d >> 12) & 63]);
            result.append(encodeChars[(d >> 6) & 63]);
            result.append(encodeChars[d & 63]);

            i += 3;

            if (n++ >= 14) 
            {
                n = 0;
                result.append("\r\n");
            }
        }

        if (i == start + len - 2) 
        {
            int d = ((((int) i_bufToEncode[i]) & 0x0ff) << 16)
                    | ((((int) i_bufToEncode[i + 1]) & 255) << 8);

            result.append(encodeChars[(d >> 18) & 63]);
            result.append(encodeChars[(d >> 12) & 63]);
            result.append(encodeChars[(d >> 6) & 63]);
            result.append("=");
        }
        else if (i == start + len - 1) 
        {
            int d = (((int) i_bufToEncode[i]) & 0x0ff) << 16;
            result.append(encodeChars[(d >> 18) & 63]);
            result.append(encodeChars[(d >> 12) & 63]);
            result.append("==");
        }

        return result.toString();
    }

    private static boolean login(String i_username, String i_password) 
            throws IOException, LimitExceededException {
       LoginManager loginManager = LoginManager.GetInstance();
       while (m_inLoginProgress) {
         // wait until we get logged in
       }

       m_inLoginProgress = true;
       if (loginManager.IsLoggedIn()) {
          m_inLoginProgress = false;
          return true;
       }
       boolean result = loginManager.Login(i_username, i_password);
       m_inLoginProgress = false;
       return result;
    }

    
    /**
     * @param url
     * @param query
     * @param requestMethod
     * @param i_username
     * @param i_password
     * @return
     * @throws java.io.IOException
     * @throws java.lang.Exception
     */
    static String Request(String url, String query, Hashtable bodyParameters,
            String requestMethod,
            String i_username, String i_password, IDownloadProgress i_progressCallback)
            throws IOException, Exception 
    {
       if (0 != i_username.length()) {
          if (!login(i_username, i_password)) {
             throw new IOException("Cannot login: invalid username or password.");
          }
       }
       
       LoginManager loginManager = LoginManager.GetInstance();
       UserAccountManager userAccountMngr = loginManager.GetUserAccountManager();
       String fullUrl = url + (query.length() == 0 ? "" : "?" + query);
       HttpRequest request = userAccountMngr.createRequest(fullUrl);
       request.setMethod(requestMethod.equals("POST") ? HttpConnection.POST : HttpConnection.GET);

       // Set body parameters
       //
       if (null != bodyParameters)
       {
          Enumeration keys = bodyParameters.keys();
          String key;
          while (keys.hasMoreElements()) {
             key = (String) keys.nextElement();
             request.setBodyParameter(
                     key, (String) bodyParameters.get(key));
          }
       }

       HttpResponse resp = request.send();
       InputStream is = resp.getStream();

       // todo: Replace all staff below till input stream to HttpRequest class
       // usage and XAuthSigner to sign the request.

        if (null != i_progressCallback)
        {
           i_progressCallback.OnProgress(0);
        }

        String response = getUpdates(is, i_progressCallback);

        return response;
    }


        /**
     * @param url
     * @param query
     * @param requestMethod
     * @param i_username
     * @param i_password
     * @return
     * @throws java.io.IOException
     * @throws java.lang.Exception
     */
    static String RequestPublicTimeline(String url, String query, String requestMethod,
            IDownloadProgress i_progressCallback)
            throws IOException, Exception
    {
        String response = "";
        int status = -1;
        String message = null;
        int depth = 0;
        boolean redirected = false;

        String auth = null;
        InputStream is = null;
        OutputStream os = null;
        HttpConnection con = null;
        //final String platform = Device.getPlatform();

        if (null != i_progressCallback)
        {
           i_progressCallback.OnProgress(0);
        }

        while (con == null)
        {
            con = (HttpConnection)Connector.open(url);
            con.setRequestMethod(requestMethod);
            con.setRequestProperty("User-Agent", c_userAgent);
            con.setRequestProperty("Connection", "close");
            con.setRequestProperty("Accept", "*/*");
            con.setRequestProperty("X-Twitter-Client", "uTweetMe");
            con.setRequestProperty("X-Twitter-Client-Version", "");
            con.setRequestProperty("X-Twitter-Client-URL", "http://utweetme.navetke.ru");

            if (query.length() > 0)
            {
                con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                con.setRequestProperty("Content-Length", "" + query.length());
                os = con.openOutputStream();
                os.write(query.getBytes());
                os.close();
                os = null;
            }

            status = con.getResponseCode();
            message = con.getResponseMessage();
            switch (status)
            {
                case HttpConnection.HTTP_OK:
                case HttpConnection.HTTP_NOT_MODIFIED:
                    break;
                case HttpConnection.HTTP_MOVED_TEMP:
                case HttpConnection.HTTP_TEMP_REDIRECT:
                case HttpConnection.HTTP_MOVED_PERM:
                    if (depth > 2)
                    {
                        throw new IOException("Too many redirect");
                    }
                    redirected = true;
                    url = con.getHeaderField("location");
                    //Log.verbose("redirected to "+url);
                    con.close();
                    con = null;
                    //Log.verbose("closed connection");
                    depth++;
                    break;
                case 100:
                    throw new IOException("unexpected 100 Continue");
                default:
                    con.close();
                    con = null;
                    //Log.verbose("closed connection");
                    throw new IOException("Communication error: "+status+" "+message);
            }
        }

        is = con.openInputStream();

        if (!redirected)
        {
            response = getUpdates(is, i_progressCallback);
        }
        else
        {
            try
            {
                if (con != null)
                {
                    con.close();
                }
                if (os != null)
                {
                    os.close();
                }
                if (is != null)
                {
                    is.close();
                }
            }
            catch (IOException ioe)
            {
                throw ioe;
            }
        }

        return response;
    }

    /**
     * Downloads string data from given HTTP connection
     * @param con in, connection to read from, used to get data length
     * @param is in, input stream, data is read from it
     * @param os in, output stream, not sure why it's needed
     * @param i_progressCallback in, callback to notify about progress,
     *        might be null!
     */
    private static String getUpdates(InputStream is, IDownloadProgress i_progressCallback) throws IOException {
      StringBuffer stb = new StringBuffer();
      int ch = 0;
      try {
         while ((ch = is.read()) != -1) {
            stb.append((char) ch);
         }
      } catch (IOException ioe) {
         throw ioe;
      } finally {
         try {
            if (is != null) {
               is.close();
            //Log.verbose("closed input stream");
            }
         } catch (IOException ioe) {
            throw ioe;
         }
      }
      if (null != i_progressCallback) {
         i_progressCallback.OnDownloadFinished();
      }
      return stb.toString();
   }
}
