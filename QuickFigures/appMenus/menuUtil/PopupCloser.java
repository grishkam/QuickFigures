/*******************************************************************************
 * Copyright (c) 2021 Gregory Mazo
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *******************************************************************************/
/**
 * Author: Greg Mazo
 * Date Modified: Jan 6, 2021
 * Version: 2023.1
 */
package menuUtil;

import java.awt.AWTEvent;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;

import javax.swing.JMenu;
import javax.swing.JPopupMenu;

/**this class closes popup menus that are not being used.
  for some reason every popup stayed open even after the user
  clicked on another application. added this to fix the 'issue'*/
public class PopupCloser {
	

	private JPopupMenu _Popup;
	JMenu menu;
	
	public boolean removeAfterDone=true;
	public boolean dontCloseAfterMenuClick=true;
	
	
	public PopupCloser(JPopupMenu p) {
		set_Popup(p);
		armPopup();
	}
	
	public PopupCloser(JMenu p) {
		menu=p;
		armPopup();
	}

	/**sets up a lsitener that will close the popup*/
	public void armPopup()
	{
		
	    if(get_Popup() != null)
	    {
	        Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener()
	        {
	            @Override
	            public void eventDispatched(AWTEvent event) {
	            		
	                if(event instanceof MouseEvent)
	                {
	                    MouseEvent m = (MouseEvent)event;
	                  
	                    if ( withinMenu(m)) {return;}
	              
	                    if(m.getID() == MouseEvent.MOUSE_CLICKED)
	                    {/**closes the menu when user clicks somewhere else*/
		                    	if (dontCloseAfterMenuClick&&_Popup!=null) {
		                    		if (m.getSource()==_Popup) return;
		                    		if (m.getSource()==_Popup.getInvoker()) return;
		                    	}
		                        get_Popup().setVisible(false);
		                       if (removeAfterDone) Toolkit.getDefaultToolkit().removeAWTEventListener(this);
	                    }
	                }
	                
	                /**closes the menu when the user activates another window*/
	                if(event instanceof WindowEvent)
	                {
	                	
		                    WindowEvent we = (WindowEvent)event;
		                   if ( we.getComponent()==menu||we.getComponent()==_Popup) {return;}
		                    if(we.getID() == WindowEvent.WINDOW_DEACTIVATED || we.getID() == WindowEvent.WINDOW_STATE_CHANGED)
		                    {
		                        get_Popup().setVisible(false);
		                        if (removeAfterDone)   Toolkit.getDefaultToolkit().removeAWTEventListener(this);
		                    }
	                }
	            }

				/**
				 * @param m
				 * @return
				 */
				boolean withinMenu(MouseEvent m) {
					return m.getComponent()==menu||m.getComponent()==_Popup||m.getComponent() instanceof JMenu;
				}

	        }, AWTEvent.MOUSE_EVENT_MASK | AWTEvent.WINDOW_EVENT_MASK);

	    }
	}


	public JPopupMenu get_Popup() {
		if (menu!=null) return menu.getPopupMenu();
		return _Popup;
	}


	public void set_Popup(JPopupMenu _Popup) {
		this._Popup = _Popup;
	}
}
