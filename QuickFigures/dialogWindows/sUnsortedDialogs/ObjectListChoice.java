/*******************************************************************************
 * Copyright (c) 2021 Gregory Mazo
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
/**
 * Author: Greg Mazo
 * Date Modified: April 17, 2022
 * Version: 2023.1
 */
package sUnsortedDialogs;

import java.util.ArrayList;

import standardDialog.StandardDialog;
import standardDialog.choices.ChoiceInputPanel;

/**A dialog that is used to select from a list of choices*/
public class ObjectListChoice<T> extends StandardDialog {

	public ObjectListChoice(String title) {
		super(title);
		this.setModal(true);
		this.setWindowCentered(true);
		super.setTabName("Pick from the list");
	}

	 ArrayList<T> array;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public T select(String prompt, ArrayList<T> oos) {
		if (oos.size()==1) return oos.get(0);
		if (oos.size()==0) return null;
		
		addChoice(prompt, oos, 0);
		
		this.showDialog();
		
		int ind=this.getChoiceIndex(prompt);
		return oos.get(ind);
		
	}
	
	/**Creates the most generic choice that applies to all types of objects*/
	public static Object selectObject(String prompt, ArrayList<?> items) {
		ArrayList<Object> o = new ArrayList<Object>(); 
		o.addAll(items);
		return new ObjectListChoice<Object>(prompt).select(prompt, o);
	}
	
	public ArrayList<T> selectMany(String prompt, ArrayList<T> oos, int howMany) {
		
		if (oos.size()==0) return null;
		
		for(int i=0; i<howMany; i++)
			addChoice(prompt+" "+(1+i), oos, i);
		
		this.showDialog();
		
		ArrayList<T> output=new ArrayList<T>();
		for(int i=0; i<howMany; i++)
			{
			int ind=this.getChoiceIndex(prompt+" "+(1+i));
			if (ind==oos.size()) {
				output.add(null);
			} else
			if (ind==-1) output.add(null); else
			 output.add(oos.get(ind));
			 
			}
		
		return output;
		
	}
	
	void addChoice(String prompt, ArrayList<T> oos, int starting) {
		
		if (oos.size()==0) return ;
		this.array=oos;
		String[] Items=new String[oos.size()+1];
		for(int i=0; i<oos.size()+1; i++) {
			if (i>=oos.size()) {
				Items[i]="none";
				continue;
			}
			T t=oos.get(i);
			if (t!=null) Items[i]=t.toString(); else Items[i]="";
		}
		
		if (starting>oos.size()) starting=oos.size();
		this.add(prompt, new ChoiceInputPanel(prompt, Items, starting));
		
	}
	
}
