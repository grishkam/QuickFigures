package objectDialogs;

import java.util.ArrayList;

import graphicalObjects.HasTextInsets;
import utilityClasses1.ArraySorter;

public class TextInsetsDialog extends GraphicItemOptionsDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected HasTextInsets s;
	
	ArrayList<HasTextInsets> array=new 	ArrayList<HasTextInsets>();

	public TextInsetsDialog(HasTextInsets s) {
	this.s=s;	
	 addOptionsToDialog();
	}
	
	public TextInsetsDialog(ArrayList<?> items, boolean backgroundShapes) {
		setArray(items);
		if (backgroundShapes) setArrayToTextBackGround(items);
		s=new ArraySorter<HasTextInsets>().getFirstNonNull(array);
		if (s!=null)
		addOptionsToDialog();
		}
	
	
	
	public void setArray(ArrayList<?> items) {
		array=new 	ArrayList<HasTextInsets>();
		for(Object i: items) {
			if (i instanceof HasTextInsets) {
				array.add((HasTextInsets) i);
			}
		}
		
	}
	
	public void setArrayToTextBackGround(ArrayList<?> items) {
		array=new 	ArrayList<HasTextInsets>();
		for(Object i: items) {
			if (i instanceof HasTextInsets) {
				array.add(((HasTextInsets) i));
			}
		}
		
	}
	
	protected void addOptionsToDialog() {
		this.addInsetToDialog(s.getInsets());
		
	}
	
	
	protected void setItemsToDiaog() {
		setItemsToDiaog(s);
		for(HasTextInsets s: array) {
			setItemsToDiaog(s);
		}
}
	
	protected void setItemsToDiaog(HasTextInsets s) {
		s.setInsets(this.getInsetsPanelFromDialog());
}
}
