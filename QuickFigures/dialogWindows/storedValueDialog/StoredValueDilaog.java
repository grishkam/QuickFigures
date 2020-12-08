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
package storedValueDialog;

import java.lang.reflect.Field;

import gridLayout.RetrievableOption;
import logging.IssueLog;
import standardDialog.StandardDialog;
import standardDialog.booleans.BooleanInputEvent;
import standardDialog.booleans.BooleanInputListener;
import standardDialog.booleans.BooleanInputPanel;
import standardDialog.numbers.NumberInputEvent;
import standardDialog.numbers.NumberInputListener;
import standardDialog.numbers.NumberInputPanel;

/**Created this class to that generating dialogs to change simple options
 * would not take much effort.
 * when given an object in which some fields are annotated with the RetrievableOption annotation (@see RetrievableOption)
  creates a dialog that allows a user to change those fields. a programmer only has to include an
  annotations*/
public class StoredValueDilaog extends StandardDialog{

	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public StoredValueDilaog(Object of) {
		this.setWindowCentered(true);
		 addFieldsForObject(this, of);
		
		 return;
		
	}

	/**
	 looks for annotated fields in the object and add items to the dialog for each 
	 field with the RetrievableOption annotation on them
	 */
	public static void addFieldsForObject(StandardDialog d, Object of) {
		Class<?> c=of.getClass();
		 try{
		 while (c!=Object.class) {
		 for (Field f: c.getDeclaredFields()) {
			 RetrievableOption o= f.getAnnotation( RetrievableOption.class);
			if (o!=null) try {
				f.setAccessible(true);
				
				if(f.getType()==boolean.class) {
					addBooleanField(d, of, f, o);
				}
				if (f.getType()==double.class) {
					addNumberField(d, of, f, o);
				}
				
				
			} catch (Throwable e) {
			
			} 
		 }
		 c=c.getSuperclass();
		 }
		 } catch (Exception e) {IssueLog.logT(e);}
	}

	private static void addNumberField(StandardDialog dialog, Object of, Field f, RetrievableOption o) {
		String label = o.label();
		int[] range = o.minmax();
		boolean useSlider=false;
		if (range.length>=2 && range[0]<range[1]) useSlider=true;
		try {
			NumberInputPanel panel=null;
			if (!useSlider)
			{ panel= new NumberInputPanel(label, f.getDouble(of));}
			else 
				{ panel = new NumberInputPanel(label, f.getDouble(of), range[0], range[1]);}
			new NumberInput(panel,f, of);
			
			dialog.add(o.key(), panel);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 
	 */
	public static void addBooleanField(StandardDialog d, Object of, Field f, RetrievableOption o)
			throws IllegalAccessException {
		String label = o.label();
		d.add(o.key(), new BooleanObjectInput(label, of, f));
	}
	
	/**Class changes a specific field in a specific object in response to a number input*/
	public static class NumberInput implements NumberInputListener {

		private Field field;
		private Object object;

		public NumberInput(NumberInputPanel panel, Field f, Object of) {
			panel.addNumberInputListener(this);
			this.field=f;
			this.object=of;
		}
		
		@Override
		public void numberChanged(NumberInputEvent ne) {
			try {
				field.set(object, ne.getNumber());
			} catch (Exception e) {
				IssueLog.logT(e);
			} 
		}}
	
	
	/**A boolean input panel that changes a boolean field every time the checkbox is changed*/
	public static class BooleanObjectInput extends BooleanInputPanel implements BooleanInputListener {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private Object object;
		private Field f2;

		public BooleanObjectInput(String label, Object of, Field f) throws IllegalArgumentException, IllegalAccessException {
			super(label, f.getBoolean(of));
			this.object=of;
			this.f2=f;
			this.addBooleanInputListener(this);
		}

		@Override
		public void booleanInput(BooleanInputEvent bie) {
			try {
				f2.set(object, bie.getBool());
			} catch (Exception e) {
				IssueLog.logT(e);
			} 
			
		}

	}
	
	public static void main(String[] args) {
		
	}

}
