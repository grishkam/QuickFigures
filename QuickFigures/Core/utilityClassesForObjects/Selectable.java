package utilityClassesForObjects;

public interface Selectable {
	public void select();
	public void deselect();
	public boolean isSelected();

	boolean makePrimarySelectedItem(boolean isFirst);
}
