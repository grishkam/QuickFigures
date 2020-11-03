package standardDialog;

import java.awt.Component;

import javax.swing.JPanel;

public class ChoiceInputEvent extends ComponentInputEvent {

	private int number;
	Object chosen;
	
	
	public ChoiceInputEvent(JPanel panel, Component component, int number, Object chosenObject) {
		this.setSourcePanel(panel);
		this.setComponent(component);
		this.setIndex(number);
		chosen=chosenObject;
	}



	public double getNumber() {
		return number;
	}

	public void setIndex(int number) {
		this.number = number;
	}



	
	


}
