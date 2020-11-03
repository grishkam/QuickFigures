package menuUtil;

import java.awt.AWTEvent;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;

import javax.swing.JMenu;
import javax.swing.JPopupMenu;

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
	                  
	                    if ( m.getComponent()==menu||m.getComponent()==_Popup||m.getComponent() instanceof JMenu) {return;}
	              
	                    if(m.getID() == MouseEvent.MOUSE_CLICKED)
	                    {
	                    	if (dontCloseAfterMenuClick&&_Popup!=null) {
	                    		if (m.getSource()==_Popup) return;
	                    		if (m.getSource()==_Popup.getInvoker()) return;
	                    	}
	                        get_Popup().setVisible(false);
	                       if (removeAfterDone) Toolkit.getDefaultToolkit().removeAWTEventListener(this);
	                    }
	                }
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
