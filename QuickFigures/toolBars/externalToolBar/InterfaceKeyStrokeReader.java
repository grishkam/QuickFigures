package externalToolBar;

import java.awt.event.KeyEvent;


public interface InterfaceKeyStrokeReader<ImageWrapper> {
	public boolean keyPressed(ImageWrapper iw, KeyEvent e);
	public boolean keyReleased(ImageWrapper iw, KeyEvent e);
	public boolean keyTyped(ImageWrapper iw, KeyEvent e);
	
}
