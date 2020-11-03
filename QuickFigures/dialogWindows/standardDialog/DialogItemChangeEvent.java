package standardDialog;

import java.awt.Component;

public class DialogItemChangeEvent {

	
	
	private StandardDialog source;
	private Component key;
	private String keyc;
	
	
	public DialogItemChangeEvent(StandardDialog source, Component key) {
		super();
		this.source = source;
		this.key = key;
	}

	



	public StandardDialog getSource() {
		return source;
	}

	public void setSource(StandardDialog source) {
		this.source = source;
	}

	public Component getKey() {
		return key;
	}

	public void setKey(Component key) {
		this.key = key;
	}
	
	public String getStringKey() {
		return keyc;
	}

	public void setStringKey(String key) {
		this.keyc = key;
	}

}
