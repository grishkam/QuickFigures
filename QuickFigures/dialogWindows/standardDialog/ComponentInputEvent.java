package standardDialog;

import java.awt.Component;

import javax.swing.JPanel;

public class ComponentInputEvent {

	private JPanel sourcePanel;
	private Component component;
	protected String key;
	
	public Component getComponent() {
		return component;
	}

	public void setComponent(Component component) {
		this.component = component;
	}
	
	public JPanel getSourcePanel() {
		return sourcePanel;
	}

	public void setSourcePanel(JPanel sourcePanel) {
		this.sourcePanel = sourcePanel;
	}
	
	public void setKey(String key) {
		this.key=key;
		
	}
	
	public String getKey() {
		return key;
	}
}
