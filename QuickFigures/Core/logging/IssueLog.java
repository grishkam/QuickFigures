package logging;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import javax.swing.JLabel;

import standardDialog.StandardDialog;



public class IssueLog {
	static MyTextWindow display;
	static MyTextWindow displayProgress;
	static PrintStream debug=null;
	static PrintStream oldErr=null;
	
	public static boolean sytemprint=false;
	static boolean reportProgress=true;
	static boolean reportTypicalFailtures=false;
	//static boolean println=false;
	public static boolean windowPrint=true;
	
	/**ensures that printed stack traces are shown in a visible
	 window. easier to debug*/
	public static void reportAllFail(boolean on) {
		reportProgress=on;
		reportTypicalFailtures=on;
		if (on) {
			
			createDisplayForErr() ;
			oldErr = System.err;
			System.setErr(display.getStream());
			sytemprint=true;
		}else {
			if (oldErr!=null) System.setErr(oldErr);
		}
		
		
	}
	
	/**ensures that printlns from the system output are shown in a visible
	 * window*/
	public static void reportAllEvents() {
		createEventDisplay();
		System.setOut(displayProgress.getStream());
	}
	
	
	static void createDisplayForErr() {
		if (display==null) {
			display=new MyTextWindow("QuickFigures Messages");
			display.setLocation(1200, 400);
			display.setVisible(true);
		} 
	}
	
	public static boolean isWindows() {
		
		return System.getProperty("os.name").toLowerCase().contains("windows");
	}
	
	public static void log(String... sts){	
		createDisplayForErr();
		for(String st:sts) {		
			if (windowPrint)display.appendLine(st);
			if ( sytemprint &&!System.out.equals(display.getStream())  ) System.out.println(st);
		}
		if (!display.isVisible() &&windowPrint)display.setVisible(true);
	}
	
	
	static double lastTime=-1;
	
	public static void logTimeStart(String st ) {
		log("Starteed"+st);
		lastTime=System.currentTimeMillis();
		log(""+System.currentTimeMillis());
	}
	
	public static void logTimefinish(String st ) {
		IssueLog.log("Done "+st);
		IssueLog.logTime();
	}
	
	public static void logTime() {
		if (lastTime==-1) {
			lastTime=System.currentTimeMillis();
			return;
		}
		log(""+System.currentTimeMillis());
		log("Milliseconds "+(System.currentTimeMillis()-lastTime)) ;
		
		
		lastTime=System.currentTimeMillis();
	}
	
	public static void log(Object... sts){	
	
		for (Object o:sts) {
			if (o==null) {log("null"); continue;}
			log(o.toString());
			}
	}
	public static void log(int... sts){
		if (sts.length==0) return;
		
		String st="";
		st+=sts[0];
		if (sts.length==1) {log(st); return;}
		for(int i=1; i<sts.length; i++) st+=","+sts[i];
		log(st);
	}
	
	
	public static void log(Throwable... ees){
		String show="";
		for(Throwable ee: ees) {
		for (Object e: ee.getStackTrace()) show+='\n'+e.toString();
		log(ee+show);
		}
	}
	
	public static void log(String st, Throwable ee){
		log(st);
		log(ee);
	}

	public static void showMessage(String s) {
		StandardDialog d = new StandardDialog();
		d.setModal(true);
		d.setLayout(new GridBagLayout());
		int y=1;
		
			GridBagConstraints c = new GridBagConstraints();
			c.gridy=y; y++;
			d.add(new JLabel(s), c);
			
		d.removeOptionsTab();
		d.setHideCancel(true);
		d.setWindowCentered(true);
		d.showDialog();
	}
	
	public static void showMessages(Iterable<String> string) {
		StandardDialog d = new StandardDialog();
		d.setModal(true);
		d.setLayout(new GridBagLayout());
		int y=1;
		for(String s: string) {
			GridBagConstraints c = new GridBagConstraints();
			c.gridy=y; y++;
			d.add(new JLabel(s), c);
			}
		d.removeOptionsTab();
		d.setHideCancel(true);
		d.setWindowCentered(true);
		d.showDialog();
	}
	
	public static void createEventDisplay() {
		if(displayProgress==null)
		displayProgress=new MyTextWindow("Montage Wizard Activity Log");
		displayProgress.setVisible(true);
		displayProgress.setLocation(1100, 700);
	}
	
	
	public static void log2(String... sts){
		if (!reportProgress) return ;
		if (displayProgress==null) {
			createEventDisplay();
		} else
			for(String st:sts) {displayProgress.appendLine(st);}
		if (!displayProgress.isVisible())displayProgress.setVisible(true);
			displayProgress.setVisible(true);
	}
	
	 public static String[] getFieldsValues(Object o) {
		 	Field[] fields=o.getClass().getSuperclass().getDeclaredFields() ;
			String[] output=new String[fields.length];
			log("class  "+o.getClass().getName()+" summary");
			for(int i=0; i<fields.length; i++) {
				log("Field entry "+i+" is");
				try {
					log(fields[i].getName()+"="+fields[i].getInt(o) );
	
					//output[i]=fields[i].getName()+"="+fields[i].get(fields[i].get(o)) ;
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					
				}
				
			}
			
			return output;
			
		}
	 
	 public static String[] getNonStaticNumberFieldValues(Object o, boolean superClass) {
		 	
		 Field[] fields=o.getClass().getDeclaredFields() ;
		 if (superClass) fields=o.getClass().getSuperclass().getDeclaredFields() ;
		 log("class  "+o.getClass().getSuperclass().getName()+" summary");
			String[] output=new String[fields.length];
			
			for(int i=0; i<fields.length; i++) {
				boolean stat=Modifier.isStatic(fields[i].getModifiers());
				log(fields[i].getType()+"");
				try {
					
					if (!stat) log(fields[i].getName()+"="+fields[i].getInt(o) );
				} catch (Throwable e) {
					// TODO Auto-generated catch block
					try {
						log(fields[i].getName()+"="+fields[i].getDouble(o) );
					} catch (IllegalArgumentException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (IllegalAccessException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				} 
				
			}
			
			return output;
			
		}
	 
	
	public static void log3(String... string) {
		if (!reportTypicalFailtures) return;
		for(String st: string)debugStream().println(st);
	}
	
	public static PrintStream debugStream() {
		if (debug!=null) return debug;
		return System.out;
	}
	
	public static PrintStream getWarningStream() {
		if (display==null) return null;
		return display.getStream();
	}
	
	public static boolean isDebugMode() {
		return debugStream()==System.out;
	}
	 
	

}
