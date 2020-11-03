package standardDialog;

import java.awt.Component;
import java.awt.Font;

import javax.swing.JPanel;

public class FontInputEvent extends ComponentInputEvent {
	
	private Font font;

	
	public FontInputEvent(JPanel sourcePanel, Component component, Font number) {
		this.setSourcePanel(sourcePanel);
		this.setComponent(component);
		this.setNumber(number);
	}

	

	public Font getNumber() {
		return font;
	}

	public void setNumber(Font number) {
		this.font = number;
	}



	
}
