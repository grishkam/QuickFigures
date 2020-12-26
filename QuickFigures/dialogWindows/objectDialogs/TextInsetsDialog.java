/*******************************************************************************
 * Copyright (c) 2020 Gregory Mazo
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *******************************************************************************/
package objectDialogs;

import java.util.ArrayList;

import graphicalObjects_SpecialObjects.HasTextInsets;
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
