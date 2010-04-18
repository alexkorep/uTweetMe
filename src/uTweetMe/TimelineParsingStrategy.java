/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uTweetMe;

/**
 * Tweet representation in XML should have the following pattern:
 * <statusNodeName>
 *    <textNodeName>tweet text</textNodeName>
 *    <idNodeName>Tweet id</idNodeName>
 *    <dateNodeName>Tweet date</dateNodeName>
 *    <usernameNodeName>user name</usernameNodeName>
 *    <replyIdNodeName>Reply id</replyIdNodeName>
 * </statusNodeName>
 */
public class TimelineParsingStrategy {
   public String m_statusNodeName;
   public String m_textNodeName;
   public String m_idNodeName;
   public String m_dateNodeName;
   public String m_usernameNodeName;
   public String m_replyIdNodeName;

   TimelineParsingStrategy(
           String i_statusNodeName,
           String i_textNodeName,
           String i_idNodeName,
           String i_dateNodeName,
           String i_usernameNodeName,
           String i_ReplyIdNodeName) {
      m_statusNodeName = i_statusNodeName;
      m_textNodeName = i_textNodeName;
      m_idNodeName = i_idNodeName;
      m_dateNodeName = i_dateNodeName;
      m_usernameNodeName = i_usernameNodeName;
      m_replyIdNodeName = i_ReplyIdNodeName;
   }
}
