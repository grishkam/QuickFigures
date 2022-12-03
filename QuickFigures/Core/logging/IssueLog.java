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
 * Date Modified: Jan 5, 2021
 * Version: 2022.2
 */
package logging;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import messages.ShowMessage;



public class IssueLog {
	static IssueLogWindow display;
	static IssueLogWindow displayProgress;
	static PrintStream debug=null;
	static PrintStream oldErr=null;
	
	public static boolean sytemprint=false;
	static boolean reportProgress=true;
	static boolean reportTypicalFailtures=false;
	
	public static boolean windowPrint=true;
	public static boolean startChecking=false;
	
	
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
	
	
	
	/**creates a window for issues to be displayed*/
	static void createDisplayForErr() {
		if (display==null) {
			display=new IssueLogWindow("QuickFigures Messages");
			display.setLocation(1200, 400);
			display.setVisible(true);
		} 
	}
	
	/**a useful method to check the operating system*/
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
	public static int countExceptions=0;
	
	/**displays the current time and a start message*/
	public static void logTimeStart(String st ) {
		log("Since last finish it has been Milliseconds "+(System.currentTimeMillis()-lastTime)) ;
		log("Started "+st+" at time ");
		lastTime=System.currentTimeMillis();
		log(""+System.currentTimeMillis());
	}
	/**displays the current time and an end message*/
	public static void logTimeFinish(String st ) {
		IssueLog.log("Done "+st);
		IssueLog.logTime();
	}
	
	/**logs the current time*/
	public static void logTime() {
		if (lastTime==-1) {
			lastTime=System.currentTimeMillis();
			return;
		}
		log(""+System.currentTimeMillis());
		log("Milliseconds "+(System.currentTimeMillis()-lastTime)) ;
		
		
		lastTime=System.currentTimeMillis();
	}
	
	/**logs the objects as strings*/
	public static void log(Object... sts){	
	
		for (Object o:sts) {
			if (o==null) {log("null"); continue;}
			log(o.toString());
			}
	}
	
	/**logs multiple strings divides by commas*/
	public static void log(int... sts){
		if (sts.length==0) return;
		
		String st="";
		st+=sts[0];
		if (sts.length==1) {log(st); return;}
		for(int i=1; i<sts.length; i++) st+=","+sts[i];
		log(st);
	}
	
	/**logs the stack traces*/
	public static void logT(Throwable... e){
		countExceptions++;
		String show="";
		for(Throwable ee: e) {
		for (Object e1: ee.getStackTrace()) show+='\n'+e1.toString();
		log(ee+show);
		}
	}
	
	/**logs the stack trace with a message*/
	public static void log(String st, Throwable ee){
		log(st);
		logT(ee);
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

	/**
	 * @param string
	 */
	public static void showMessage(String string) {
		ShowMessage.showMessages(string);
		
	}
	 
	/**pauses the thread for a number of miliseconds*/
	public static boolean waitMiliseconds(int s) {
		
			try {Thread.sleep(s);}
			catch (Exception e) { 
				IssueLog.logT(e);
				return false;
			}
		
			return true;
	}
	
	/**pauses the thread for a number of miliseconds*/
	public static boolean waitSeconds(int s) {
		return waitMiliseconds(1000*s);
	}



	/**
	 * @param string
	 */
	public static void logOptional(String string) {
		// TODO Auto-generated method stub
		
	}
	

}
