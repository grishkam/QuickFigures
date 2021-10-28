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
 * Date Modified: April 24, 2021
 * Version: 2021.2
 */
package storedValueDialog;

import java.lang.reflect.Field;
import java.util.Arrays;

import layout.RetrievableOption;
import logging.IssueLog;
import standardDialog.StandardDialog;
import standardDialog.booleans.BooleanInputEvent;
import standardDialog.booleans.BooleanInputListener;
import standardDialog.booleans.BooleanInputPanel;
import standardDialog.choices.ChoiceInputEvent;
import standardDialog.choices.ChoiceInputListener;
import standardDialog.choices.ChoiceInputPanel;
import standardDialog.numbers.NumberInputEvent;
import standardDialog.numbers.NumberInputListener;
import standardDialog.numbers.NumberInputPanel;
import standardDialog.strings.StringInputEvent;
import standardDialog.strings.StringInputListener;
import standardDialog.strings.StringInputPanel;

/**Created this class to that generating dialogs to change simple options
 * would not take much effort.
 * when given an object in which some fields are annotated with the RetrievableOption annotation (@see RetrievableOption)
  creates a dialog that allows a user to change those fields. a programmer only has to include an
  annotations*/
public class StoredValueDilaog extends StandardDialog{


	private static final long serialVersionUID = 1L;
	
	/**creates a dialog */
	public StoredValueDilaog(String item, Object of) {
		this.setTitle(item);
		this.setWindowCentered(true);
		 addFieldsForObject(this, of);
		
		 return;
		
	}
	
	public StoredValueDilaog(Object of) {
		this("", of);
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
				
				if(o.choices().length>1) {
					String[] theChoices = o.choices();
					addChoice(d, of, f, o, theChoices);
					
				}else {
				 
				if(f.getType()==boolean.class) {
					addBooleanField(d, of, f, o);
				}
				if (f.getType()==double.class) {
					addNumberField(d, of, f, o);
				}
				
				if (f.getType()==String.class) {
					
					addStringField(d, of, f, o);
				}
				}
				
				/**need option to add a choice*/
				
			} catch (Throwable e) {
			
			} 
		 }
		 c=c.getSuperclass();
		 }
		 } catch (Exception e) {IssueLog.logT(e);}
	}

	/**Adds a choice field to a dialog
	 * @param d
	 * @param of
	 * @param f
	 * @param o
	 * @param theChoices
	 */
	private static void addChoice(StandardDialog d, Object of, Field f, RetrievableOption o, String[] theChoices) {
		int startIndex=1;
		try {
		if (f.getType()==int.class) {
				startIndex=f.getInt(of);
			} 
		if (f.getType()==String.class) {
			startIndex=Arrays.binarySearch(theChoices, f.get(of).toString());
		} 
		 ChoiceInputPanel panel = new  ChoiceInput(of, f, o, startIndex);
		 d.add(o.key(), panel);
		
		}catch (Throwable e) {
			IssueLog.logT(e);
		}
		
		
		
	}

	/**Adds a numeric field to a dialog*/
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
			IssueLog.logT(e);
		}
	}

	/**
	 
	 */
	public static void addBooleanField(StandardDialog d, Object of, Field f, RetrievableOption o)
			throws IllegalAccessException {
		String label = o.label();
		d.add(o.key(), new BooleanObjectInput(label, of, f));
	}
	
	public static void addStringField(StandardDialog d, Object of, Field f, RetrievableOption o)
			throws IllegalAccessException {
		String label = o.label();
		d.add(o.key(), new StringInput(label, of, f));
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
	
	/**Class changes a specific field in a specific object in response to a number input*/
	public static class StringInput extends StringInputPanel implements StringInputListener {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private Field field;
		private Object object;

		public StringInput(String label, Object of, Field f) throws IllegalArgumentException, IllegalAccessException {
			super(label, ""+ f.get(of));
			addStringInputListener(this);
			this.field=f;
			this.object=of;
		}
		
		@Override
		public void stringInput(StringInputEvent  ne) {
			try {
				field.set(object, ne.getInputString());
			} catch (Exception e) {
				IssueLog.logT(e);
			} 
		}}
	
	/**Class changes a specific field in a specific object in response to a choice input*/
	public static class ChoiceInput extends ChoiceInputPanel implements ChoiceInputListener {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private Field field;
		private Object object;

		public ChoiceInput( Object of, Field f, RetrievableOption o, int startIndex) throws IllegalArgumentException, IllegalAccessException {
			super(o.label(), o.choices(), startIndex);
			addChoiceInputListener(this);
			this.field=f;
			this.object=of;
		}
		
		

		@Override
		public void valueChanged(ChoiceInputEvent ne) {
			try {
				field.set(object, (int)ne.getChoiceIndex());
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
	
	

}
