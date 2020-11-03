package standardDialog;


public class ObjectEditEvent {
	Object o;
	private String key="";
	public ObjectEditEvent(Object snappingBehaviour) {
		o= snappingBehaviour;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}

	
	
	
}
