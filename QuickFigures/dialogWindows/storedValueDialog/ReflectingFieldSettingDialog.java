package storedValueDialog;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;

import standardDialog.BooleanInputPanel;
import standardDialog.ComboBoxPanel;
import standardDialog.NumberInputPanel;
import standardDialog.StandardDialog;
import standardDialog.StringInputPanel;


/**Takes advantage of reflection to create dialogs for arbitrary objects*/
public class ReflectingFieldSettingDialog extends StandardDialog {

	/**
	 * 
	 */
	
	
	private static final long serialVersionUID = 1L;
	private Object o;
	private ArrayList<?> extraObjects=new ArrayList<Object> ();
	private ArrayList<Field> fields=new ArrayList<Field>();
	private HashMap<String, Method> getters=new HashMap<String, Method> ();
	private HashMap<String, Method> setters=new HashMap<String, Method> ();
	private HashMap<Field, FieldSettingCluster> setterData=new HashMap<Field, FieldSettingCluster> ();
	
	public ReflectingFieldSettingDialog(Object o) {
		this.o=o;
		 setUIForAllFields();
		 this.setWindowCentered(true);
	}
	
	public ReflectingFieldSettingDialog(Object o, Field... desiredFields) {
		this.o=o;
		setFeilds(desiredFields);
		this.setWindowCentered(true);
	}
	
	public ReflectingFieldSettingDialog(Object o, String... desiredFields) {
		this.o=o;
		for(String f: desiredFields)  addField(f);
		this.setWindowCentered(true);
	}
	
	void setFeilds(Field... desiredFields) {
		
		if (desiredFields==null ||desiredFields.length==0) 
			 desiredFields = o.getClass().getDeclaredFields();
		
		
		for(Field f: desiredFields)  addFieldToDialog(f);
		
	}
	
	void setUIForAllFields() {
		Class<? extends Object> c = o.getClass();
		
		while (c!=Object.class) {
			Field[] desiredFields = c.getDeclaredFields();
			for(Field f: desiredFields)  addFieldToDialog(f);
			c=c.getSuperclass();
		}
	}
	
	
	private void addField(String sf) {
	
		Class<?> classC = o.getClass();
		addFieldToDialog(findField(classC, sf));
		
		
	}
	
	/**Moves up the class hierarchy untill it finds the given field*/
	public static Field findField(Class<?> c2, String sf) {
		Field f=null;
		Class<?> classC = c2;
		
		while (f==null&&classC!=Object.class) {
				try{f= classC.getDeclaredField(sf);} catch (Throwable t) {}
			
				if (f!=null) { return f;}
				
				classC=classC.getSuperclass();
		}
		
		return null;
		
	}
	
	
	
	public void addItem(String name, String label, Field f, Method getter,Class<?> t ) {
		getters.put(name, getter);
		
		Boolean isFieldPublic=false;
		if (f!=null)isFieldPublic = Modifier.isPublic(f.getModifiers());
		
		
		try {
			
			
			if (t==double.class)  {
				double d=0;
				if (isFieldPublic&&getter==null) d=f.getDouble(o);
				else if (getter!=null) d=(Double) getter.invoke(o);
				
				if (isFieldPublic||getter!=null) {
					NumberInputPanel nip = new NumberInputPanel(label, d , 5);
					add(name, nip);
				}
			}
			
			if (t==int.class)  {
				
				FieldSettingCluster data = setterData.get(f);
				String[] sar = data.getOptions();
				
				int d=0;
				if (isFieldPublic&&getter==null) d=(int)f.getDouble(o);
				else if (getter!=null) d=(Integer) getter.invoke(o);
				
				if (sar==null||sar.length<2)
					{NumberInputPanel nip = new NumberInputPanel(label, d, 1);
					 add(name, nip);
				} else {
					ComboBoxPanel nip = new ComboBoxPanel(label, sar, d);
					add(name, nip);
			}
				
			}
			
			
			if (t==boolean.class) {
				boolean b=false;
				if (isFieldPublic&&getter==null) b=(boolean)f.getBoolean(o);
				else if (getter!=null) b=(Boolean) getter.invoke(o);
				
				if (isFieldPublic||getter!=null) {
					BooleanInputPanel bip=new BooleanInputPanel(label, b);
					add(name, bip);
					}
			}
			
			if (t==String.class) {
				String st="";
				if (isFieldPublic&&getter==null) st=f.get(o).toString();
				else if (getter!=null) st=getter.invoke(o).toString();
				
				if (isFieldPublic||getter!=null) {
					StringInputPanel bip=new StringInputPanel(label, st);
					add(name, bip);
				}
			}
			
			
			if (!isFieldPublic) return;
			
		} catch (Throwable tr) {
			tr.printStackTrace();
		}
	}
	
	
	
	void addFieldToDialog(Field f) {
		if (!fields.contains(f)) fields.add(f);
		;
		setterData.put(f, new FieldSettingCluster(f, o.getClass()));
		Method getter = this.findGetter(f, o.getClass());
		Class<?> t = f.getType();
		String label=makeLabelString(f.getName());
		addItem(f.getName(), label,f,  getter, t);
	}
	
	/**When given a field name, this insets spaces and capitalizaton
	  to make a readable to user version*/
	static String makeLabelString(String input) {
		String output = input.replace("_", " ");
			for(int i=0; i<26; i++) {
			int i1=i+(int) 'A';
			char c1=(char)i1;
			output=output.replaceAll(""+c1, " "+c1);
		}
		
		String c = (""+output.charAt(0)).toUpperCase();
		
		output=c+output.substring(1, output.length());
	
		return output;
	}
	
	void setFieldFromDialog(Object o, Field f) {
		 try {
		
		boolean isPublic=Modifier.isPublic(f.getModifiers());
	
		Class<?> t = f.getType();
		String name=f.getName();	
		Method setter=this.findSetter(f);
		
		/**if this objects lacks the field, don't bother*/
		if (findField(o.getClass(), f.getName())==null) { return;}
		
		if (t==double.class)  {
			double d=0; d=this.getNumber(name);
			if (isPublic&& setter==null)
					f.set(o, d);
			if (setter!=null) {setter.invoke(o, d);}
		}
		
		if (t==int.class)  {
			double d=0; 
			
			FieldSettingCluster data = setterData.get(f);
			String[] sar = data.getOptions();
			if (sar==null||sar.length<2) d=this.getNumber(name);
			else d=this.getChoiceIndex(name);
			
			if (setter!=null) {setter.invoke(o, (int)d);}
			else if (isPublic&& setter==null)
					f.set(o, (int)d);
			
		}
		
	
		if (t==boolean.class) {
			boolean d=this.getBoolean(name);
			if (isPublic&& setter==null)
					f.set(o, d);
			if (setter!=null) {setter.invoke(o, (boolean)d);}
			
		}
		
		if (t==String.class) {
			String st=this.getString(name);
			if (isPublic&& setter==null)
					f.set(o, st);
			if (setter!=null) {setter.invoke(o, (String)st);}
			
		}
			
		
	} catch (Throwable t) {
		t.printStackTrace();
	}
	}
	
protected void afterEachItemChange() {
	for(Field f: fields)setFieldFromDialog(o, f);
	
	for(Object o2: this.getExtraObjects())
		for(Field f: fields)setFieldFromDialog(o2, f);
		
	}




Method findGetter(Field f, Class<?> c) {
	try{
	String getterName =  makeGetterSetterString("get", f.getName());
	if (f.getType()==boolean.class) getterName =  makeGetterSetterString("is", f.getName());
	Method m1 = findMethod(getterName, c);
	if (m1!=null) return m1;

	
	} catch (Exception e) {e.printStackTrace();
	return null;}
	return null;
}

Method findSetter(Field f) {
	try{
	String getterName =  makeGetterSetterString("set", f.getName());
	
	Method m1 = findMethod(getterName, o.getClass(), f.getType());
	if (m1!=null) return m1;

	
	} catch (Exception e) {e.printStackTrace();
	return null;}
	return null;
}


/**returns the method of name name. problem filled one. need recursive alternative*/
Method findMethod(String getterName, Class<?> o,  Class<?>... parameterTypes) {
	Method m1=null;
	Class<?> classC=o;
	while (m1==null&&classC!=Object.class) {
		try{m1= classC.getDeclaredMethod(getterName, parameterTypes);} catch (Throwable t) {}
	
		if (m1!=null) { return m1;}
		
		classC=classC.getSuperclass();
}
	
	return null;
}

static String makeGetterSetterString(String get, String input ) {
	String output = input;
	String c = (""+output.charAt(0)).toUpperCase();
	output=get+c+output.substring(1, output.length());
	return output;
}

public static void main(String[] args) {
	testMe object = new TestMe2();
	ReflectingFieldSettingDialog dialog = new ReflectingFieldSettingDialog(object, new String[] {"x", "theCorrectWayToCode", "options"});
	dialog.showDialog();
	
}

public ArrayList<?> getExtraObjects() {
	return extraObjects;
}

public void setExtraObjects(ArrayList<?> extraObjects) {
	this.extraObjects = extraObjects;
}

class FieldSettingCluster {
	private Field field;
	private UserChoiceField anns;
	Class<?> theClass;
	private Method getter;

	public FieldSettingCluster(Field f, Class<? extends Object> class1) {
		theClass=class1;
		field=f;
		anns = f.getAnnotation(UserChoiceField.class);
		getter =findGetter(f, class1);
	}
	
	public String[] getOptions() {
		if (anns==null) return new String[]{};
		return anns.optionsForUser();
	}
	
}



static class testMe {
	private double x=6;
	private double y=12;
	private int population=18;
	public int theCorrectWayToCode=42;
	public String name="first";
	
	@UserChoiceField(optionsForUser = { "Option 1", "Option 2", "Option 3", "Other"  })
	public int options=2;
	
	
	private boolean performAction=true;
	
	public double getX() {
		return x;
	}
	public void setX(double x) {
		this.x = x;
	}
	public double getY() {
		return y;
	}
	public void setY(double y) {
		this.y = y;
	}
	public int getPopulation() {
		return population;
	}
	public void setPopulation(int population) {
		this.population = population;
	}
	public boolean isPerformAction() {
		return performAction;
	}
	public void setPerformAction(boolean performAction) {
		this.performAction = performAction;
	}
	
}

static class TestMe2 extends testMe {}
	

}
