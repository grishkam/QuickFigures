package standardDialog;

import java.awt.Component;

import javax.swing.JPanel;

public class StringInputEvent extends ComponentInputEvent{

	String st="";

	
	public StringInputEvent(JPanel panel, Component component, String number) {
		this.setSourcePanel(panel);
		this.setComponent(component);
		st=number;
		
	}

	
	
}
