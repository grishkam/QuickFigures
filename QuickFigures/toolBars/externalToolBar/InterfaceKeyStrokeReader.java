package externalToolBar;

import java.awt.event.KeyEvent;

/**Interface whose methods determines how some tools react to key events
  multiple tools may use the same instance of key stroke reader*/
public interface InterfaceKeyStrokeReader<ImageType> {
	public boolean keyPressed(ImageType iw, KeyEvent e);
	public boolean keyReleased(ImageType iw, KeyEvent e);
	public boolean keyTyped(ImageType iw, KeyEvent e);
	
}
