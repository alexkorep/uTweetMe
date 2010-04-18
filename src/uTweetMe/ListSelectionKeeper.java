/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uTweetMe;

import javax.microedition.lcdui.List;

/**
 *
 * @author Alexey
 */
public class ListSelectionKeeper {
   private int m_sel;
   private List m_list;
   ListSelectionKeeper(List i_list) {
      //m_list = i_list;
      //m_sel = i_list.getSelectedIndex();
   }

   void Restore() {
      /*
      if (m_sel < 0 || m_sel >= m_list.size()) {
         return;
      }
      m_list.setSelectedIndex(m_sel, true);
      */
   }

}
