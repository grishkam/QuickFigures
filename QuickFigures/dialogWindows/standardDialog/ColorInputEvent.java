package standardDialog;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JPanel;

import applicationAdapters.CanvasMouseEventWrapper;

public class ColorInputEvent extends ComponentInputEvent {
	
	private Color font;
	public CanvasMouseEventWrapper event;

	
	public ColorInputEvent(JPanel sourcePanel, Component component, Color number) {
		this.setSourcePanel(sourcePanel);
		this.setComponent(component);
		this.setColor(number);
	}

	

	public Color getColor() {
		return font;
	}

	public void setColor(Color number) {
		this.font = number;
	}



	
}
