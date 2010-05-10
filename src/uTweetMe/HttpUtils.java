/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uTweetMe;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
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
    static String Request(String url, String query, String requestMethod, 
            String i_username, String i_password, IDownloadProgress i_progressCallback)
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
            //Log.setState("connecting");
            con = (HttpConnection)Connector.open(url);
            //Log.setState("connected");
            //Log.verbose("opened connection to "+url);
            con.setRequestMethod(requestMethod);
            if (i_username != null && i_password != null && i_username.length() > 0) 
            {
                String userPass = i_username + ":" + i_password;
                userPass = Base64Encode(userPass.getBytes());
                con.setRequestProperty("Authorization", "Basic " + userPass);
            }
            con.setRequestProperty("User-Agent", c_userAgent);
            con.setRequestProperty("Connection", "close");
            /*
            if (!Device.isNokia()) {
            con.setRequestProperty("Host", con.getHost()+":"+con.getPort());
            }
            */
            con.setRequestProperty("Accept", "*/*");
            con.setRequestProperty("X-Twitter-Client", "uTweetMe");
            con.setRequestProperty("X-Twitter-Client-Version", "");
            con.setRequestProperty("X-Twitter-Client-URL", "http://utweetme.navetke.ru");

            if (query.length() > 0) 
            {
                con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                con.setRequestProperty("Content-Length", "" + query.length());
                os = con.openOutputStream();
                //Log.verbose("opened output stream");
                os.write(query.getBytes());
                //Log.verbose("sent query "+query);
                os.close();
                os = null;
                //Log.verbose("closed output stream");
            }

            //Log.setState("sending request");
            status = con.getResponseCode();
            message = con.getResponseMessage();
            //Log.setState("received response");
            //Log.info(status+" "+message);
            //Log.debug("user-agent "+con.getRequestProperty("User-Agent"));
            //Log.verbose("response code "+status+" "+message);
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
        //Log.setState("receiving i_bufToEncode");
        //Log.verbose("opened input stream");
        
        if (!redirected) 
        {
            response = getUpdates(con, is, os, i_progressCallback);
        } 
        else 
        {
            try 
            {
                if (con != null) 
                {
                    con.close();
                    //Log.verbose("closed connection");
                }
                if (os != null) 
                {
                    os.close();
                    //.verbose("closed output stream");
                }
                if (is != null) 
                {
                    is.close();
                    //Log.verbose("closed input stream");
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
    private static String getUpdates(HttpConnection con, InputStream is,
           OutputStream os, IDownloadProgress i_progressCallback) throws IOException {
      StringBuffer stb = new StringBuffer();
      int ch = 0;
      final int c_updateEachNBytes = 500; // Update progress after downloading this number of bytes
      try {
         int n = (int) con.getLength();
         //Log.info("Size: "+n);
         //Log.verbose("reading response");
         if (n != -1) {
            for (int i = 0; i < n; i++) {
               if ((ch = is.read()) != -1) {
                  stb.append((char) ch);
                  //Log.setProgress(i*100/n);
                  if (null != i_progressCallback &&
                          (0 == i % c_updateEachNBytes)) {
                     final boolean interrupt = i_progressCallback.OnProgress(i * 100 / n);
                     // Exit if downloading must be cancelled.
                     if (interrupt) {
                        i_progressCallback.OnDownloadFinished();
                        return "";
                     }
                  }
               }
            }
         } else {
            while ((ch = is.read()) != -1) {
               n = is.available();
               stb.append((char) ch);
            }
         }
      } catch (IOException ioe) {
         throw ioe;
      } finally {
         try {
            if (os != null) {
               os.close();
            //Log.verbose("closed output stream");
            }
            if (is != null) {
               is.close();
            //Log.verbose("closed input stream");
            }
            if (con != null) {
               con.close();
            //Log.verbose("closed connection");
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
