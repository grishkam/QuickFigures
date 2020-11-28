package storedValueDialog;

import java.lang.reflect.Field;

import gridLayout.RetrievableOption;
import logging.IssueLog;
import standardDialog.BooleanInputEvent;
import standardDialog.BooleanInputListener;
import standardDialog.BooleanInputPanel;
import standardDialog.StandardDialog;

/**Created this class to that generating dialogs to change simple options
 * would not take much effort.
 * when given an object in which some fields are annotated with the RetrievableOption annotation
  creates a dialog that allows a user to change those fields. a programmer only has to include an
  annotations*/
public class StoredValueDilaog extends StandardDialog{

	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public StoredValueDilaog(Object of) {
		this.setWindowCentered(true);
		 addFieldsForObject(of);
		
		 return;
		
	}

	/**
	 looks for annotated fields in the object and add items to the dialog for each 
	 field with the RetrievableOption annotation on them
	 */
	public void addFieldsForObject(Object of) {
		Class<?> c=of.getClass();
		 try{
		 while (c!=Object.class) {
		 for (Field f: c.getDeclaredFields()) {
			 RetrievableOption o= f.getAnnotation( RetrievableOption.class);
			if (o!=null) try {
				f.setAccessible(true);
				
				if(f.getType()==boolean.class) {
					String label = o.label();
					this.add(o.key(), new BooleanObjectInput(label, of, f));
				}
				
				
			} catch (Throwable e) {
			
			} 
		 }
		 c=c.getSuperclass();
		 }
		 } catch (Exception e) {IssueLog.logT(e);}
	}
	
	/**A boolean input panel that changes a boolean field every time the checkbox is changed*/
	public class BooleanObjectInput extends BooleanInputPanel implements BooleanInputListener {

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
