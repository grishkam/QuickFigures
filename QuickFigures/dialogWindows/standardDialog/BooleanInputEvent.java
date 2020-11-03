package standardDialog;

import java.awt.Component;

import javax.swing.JPanel;

public class BooleanInputEvent extends ComponentInputEvent {
	private boolean bool=false;
	
	
	public BooleanInputEvent(JPanel panel, Component component, boolean number) {
		this.setSourcePanel(panel);
		this.setComponent(component);
		setBool(number);
		
	}

	public boolean getBool() {
		return bool;
	}

	public void setBool(boolean bool) {
		this.bool = bool;
	}

	
}
