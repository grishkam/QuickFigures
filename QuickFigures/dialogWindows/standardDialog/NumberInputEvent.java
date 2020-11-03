package standardDialog;

import java.awt.Component;

import javax.swing.JPanel;

public class NumberInputEvent extends ComponentInputEvent {

	private double number;
	private float[] numbers;

	public NumberInputEvent(JPanel panel, Component component, double number) {
		this.setSourcePanel(panel);
		this.setComponent(component);
		this.setNumber(number);
	}

	public NumberInputEvent(JPanel panel, Component component, float[] numbers) {
		this.setSourcePanel(panel);
		this.setComponent(component);
		this.setNumber(numbers[0]);
		this.numbers=numbers;
	}
	
	public NumberInputEvent(JPanel panel, Component component, double num, float[] numbers) {
		this.setSourcePanel(panel);
		this.setComponent(component);
		this.setNumber(num);
		this.numbers=numbers;
	}

	public double getNumber() {
		return number;
	}
	public float[] getNumbers() {
		if(numbers==null)
			return new float[] {(float) number};
		return numbers;
	}
	

	public void setNumber(double number) {
		this.number = number;
	}




}
