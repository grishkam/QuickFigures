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
 * Date Modified: Nov 3, 2022
 * Version: 2022.2
 */
package infoStorage;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Hashtable;

import layout.RetrievableOption;
import logging.IssueLog;
import utilityClasses1.NumberUse;

/**This class helps extract information that is stored in string form*/
public class BasicMetaDataHandler {
	public static  String delimiter=";";//The delimiter separates the parts of the list
	public static String myIndexCode="Greg Channel At Index ";;//"Image Channel Index ";
	public static String myColorCode="Greg Channel Color ";
	Hashtable <String, String> hash=new Hashtable <String, String>();
	String[] ZviChanKey= new String[] {"Channel Name ", " ", "zvi"};
	
	String[] myChanKey= new String[] {myIndexCode, " "};
	
	/**Channel name keys for determining channel colors, can retrieved lut names or channel names
	   */
	static String[][] allNameKeys=new String[][] {
		//new String[] {"DisplaySetting|Channel|DyeName|", " ", "czi"},//for CZI does not work the same way others do
		new String[] {myColorCode, " ", "any", "0"},   //A system created by me
		new String[] {"ChannelDescription|LUTName ", " ", "lif", "0"},   //for lif. not all .lif files have a useful version of this.  possible alternative "HardwareSetting|LDM_Block_Sequential|ATLConfocalSettingDefinition|MultiBand|DyeName "
		new String[] {"LUT Channel ", " name ", "lei", "0"},   //the most reliable key for lei. only tested on one lei file
		new String[] {"Block 2 csLutName", " ", "lei", "0"} ,           // alternate key for lei for LEI files.  block 2 part is consistent between files
		new String[] {"Channel Name ", " ", "zvi", "0"}, //For .zvi
		
		new String[] {"Name #", " ", "nd2", "2"},//for nikon. Name # entry gave confusing results with two cy5

		new String[] {"Nikon Ti2, FilterChanger(Turret-Lo) #", " ", "nd2", "0"}//for nikon. Name # entry gave confusing results with two cy5
	};
	
	
	
	/**the channel id keys known for microscopy formats*/
	String[][] allNumberKeys=new String[][] {
		new String[] {"Information|Image|Channel|Id ", " "},//for CZI
		// possible for LIF but not enough information "HardwareSetting|LDM_Block_Sequential|ATLConfocalSettingDefinition|LUT|Channel "
		
	};
	
	/**exposure time keys known for microscopy formats*/
	String[][] allExposureTimeKeys=new String[][] {
		new String[] {"Exposure Time [ms] ", " "}, //for ZVI files opened with bioformats
		new String[] {"Experiment|AcquisitionBlock|MultiTrackSetup|Track|Channel|DataGrabberSetup|CameraFrameSetup|ExposureTime|", " "},//for CZI
		
		
	};
	
	/**This will return the meta data info entry in a Text
	   Assumes that the string is the format of key=### \n
	   */
	public  String getMetaDataEntry(String linesOfText, String key){
		return getMetaDataEntryFromLine(linesOfText,key);
	}
	
	/**Assuming the data contains key value pairs separated by an = sign, extracts the information*/
	public static String getMetaDataEntryFromLine(String linesOfText, String key) {
		String[] ss2=linesOfText.split("\n");
		for (int i=0; i<ss2.length; i++) {if (ss2[i].startsWith(key+"= ")) return ss2[i];}
		return null;
	}
	
	/**Assuming the data contains key value pairs separated by an = sign, extracts the data entry*/
	public  static String getMetaDataEntry(String[] data, String key){
		for (int i=0; i<data.length; i++) {if (data[i].startsWith(key+"= ")) return data[i];}
		return null;
	}
	/**Assuming the data contains key value pairs separated by an = sign, extracts the value*/
	public  static String getMetaDataEntryValue(String[] ss2, String key){
		for (int i=0; i<ss2.length; i++) {if (ss2[i].startsWith(key+"= ")) return ss2[i].substring(key.length()+1);}
		return null;
	}
	
	

	/**Extracts the integer value with the given key*/
	public  Integer parseMetadataIntvalue(MetaInfoWrapper data, String entryKey ) {
		String output=data.getEntryAsString(entryKey);// getEntryFromInfoAsString(a, b) ;
		if (output==null) return null;
	    Integer r;
	    try {r=Integer.parseInt(output);} catch (NumberFormatException si) {return null; }
	    return r;
	}
	
	/**Extracts the integer array value with the given key*/
	public  int[] parseMetadataIntArrayValue(MetaInfoWrapper data, String entryKey ) {
		String output= getEntryFromInfoAsString(data, entryKey) ;
		if (output==null) return null;
	    return NumberUse.intArrayFromString1(output);
	}
	

	/**Extracts the string array value with the given key*/
	public  String[] parseMetadataStringArrayValue(MetaInfoWrapper a, String b ) {
		String output= getEntryFromInfoAsString(a, b) ;
		if (output==null) return null;
	    return stringArrayFromString(output);
	}
	
	/**Extracts the double value with the given key*/
	public  Double parseMetadataDoublevalue(MetaInfoWrapper a, String b ) {
		String output= getEntryFromInfoAsString(a, b) ;
		if (output==null) return null;
	    Double r;
	    try {r=Double.parseDouble(output);} catch (NumberFormatException si) {return null; }
	    return r;
	}
	
	
	/**Extracts the font value with the given key*/
	Font parseMetadataFontvalue(MetaInfoWrapper a, String b) {
		String st =getEntryFromInfoAsString( a,  b);
		return getFont(st);
	}
	/**Extracts the point with the given key*/
	Point parseMetadataPointvalue(MetaInfoWrapper a, String b) {
		String st =getEntryFromInfoAsString( a,  b);
		return getPoint(st);
		
	}
	
	/**Extracts the object with the given key*/
	public Object parseMetadataClassValue(MetaInfoWrapper data, String key, Class<?> objectType) {
		return getObject(getEntryFromInfoAsString( data,key), objectType);

	}
	
	/**this decodes an object of class 'c' from its string representation 'st'*/
	public static Object getObject(String st, Class<?> c) {	
		if (st.equals("null")) return null;
		if (c.equals(String.class)) return st ;
		if (c.equals(Color.class)) return getColor( st) ;
		if (c.equals(Point.class)) return getPoint( st) ;
		if (c.equals(Rectangle.class)) return getRectangle(st) ;
		if (c.equals(Font.class)) return getFont( st) ;
		if (c.equals(int.class)||c.equals(Integer.class)) {
			
			return Integer.parseInt(st);
			}
		
		if (c.equals(double.class)||c.equals(Double.class)) {return Double.parseDouble(st);}
		if (c.equals(float.class)||c.equals(Float.class)) {return Float.parseFloat(st);}
		
		if (c.equals(boolean.class)||c.equals(Boolean.class)) {return Integer.parseInt(st)==1;}
		if (c.isArray()) {
			
			Class<?> type = c.getComponentType();
			
			/**these lines are to select the delimiter*/
			String delit=delimiter;
			String d2 = getDelimitForClass(type);
			if (st.contains(d2)&&!st.contains(delit)) delit=d2;
			
			
			String[] s=st.split(delit);
			
			Object output=Array.newInstance(c.getComponentType(), s.length);
			if (s.length==1) {
				if (st.equals("")) return output;
			}
			
			
			for(int i=0; i<s.length; i++) Array.set(output, i, getObject(s[i],type ));
			return output;
		}

		throw new NullPointerException();
		//return null;
	}	 
	
	/**gets the proper delimiter for arrays of class c*/
	static String getDelimitForClass(Class<?> c) {
		if (c.equals(double.class)||c.equals(Double.class)) {return ",";}
		if (c.equals(float.class)||c.equals(Float.class)) {return ",";}
		if (c.equals(int.class)||c.equals(Integer.class)) {return ",";}
		if (c.equals(float.class)||c.equals(Float.class)) {return ",";}
		
		return delimiter;
	}

	/**Extracts the boolean with the given key*/
	public  Boolean parseMetadataBooleanvalue(MetaInfoWrapper a, String b ) {
		Integer i1=parseMetadataIntvalue(a,  b ) ;
		if (i1==0) return false;
		if (i1==1) return true;
		return null;
	}
	
	
	
	
	/**Method to make a string array into a string and to change it back*/	
	public  String stringFromStringArray(String[] sta) {
		if (sta.length==1) return sta[0];
		if (sta.length==0) return "";
		String output=sta[0];
		for (int i=1; i<sta.length; i++) {output+= delimiter+sta[i];}
		return output;
	}
		public  static String[] stringArrayFromString(String st) {
		String[] eachnumber=st.split(delimiter);
		String[] output=new String[eachnumber.length];
		for (int i=0; i<output.length; i++){
			{output[i]=eachnumber[i].trim();}
		}
		return output;
	}
		
		
		/**This puts all the information about a particular object into a string form so it can be added to the metadata of an image to be retrived later*/
		public static String entryString(Object o) {
			if (o==null) return "";
			String output=o.toString();
			if (output.equals("true")) return "1";
			if (output.equals("false")) return "0";
			if (output.contains("java.awt.")) output=output.replace("java.awt.", "");
			if (o.getClass().isArray()) {
				
				output="";
				int length=Array.getLength(o);
				if (length==0) return "";
				int i=0;
				if (length==1) return entryString(Array.get(o, i));
				output=entryString(Array.get(o, 0));
				for(i=1; i<length; i++ ) {
					Object elementi=Array.get(o, i);
					output+=delimiter+entryString(elementi);
				}
				
			}
			
			return output.replaceAll(" ", " ");
		}
		
		/**stores the array list into the data set*/
		public  void addArrayListEntryToInfo(MetaInfoWrapper img, String name, ArrayList<?> numbers) {
			addEntryToInfo(img, name, numbers.toArray(new Object[numbers.size()]));
		}
		
		/**stores the object inthe data set*/
		public  void addEntryToInfo(MetaInfoWrapper data, String nameOfKey, Object number) {
			if (number.getClass()==ArrayList.class) try {if (number instanceof ArrayList<?> )
				addArrayListEntryToInfo(data, nameOfKey, (ArrayList<?>) number) ; return;} catch (Throwable e) {}
			
			try{addEntryToInfo(data, nameOfKey, entryString(number)) ;} catch (Throwable t) {IssueLog.log(" problem adding entry to metadata", t);}
		}
		
		
		
		/**Extracts the file path with the given key*/
		public  String getFileNameFromMetaData(MetaInfoWrapper imp, String key) {
			return new File(getEntryFromInfoAsString(imp, key)).getName();
		}
		


		/**if the string ends with .tif, does nothing, else adds .tif to the string*/
		public String addTifToName(String name) {
			if (name.endsWith(".tif")) return name;
			return name+".tif";
		}
		
		/**Stores an array of points*/
		void addEntryToInfo(MetaInfoWrapper data, String name, Point[] numbers) {
			int[] intsx=new int[numbers.length];
			int[] intsy=new int[numbers.length];
			for (int i=0; i<numbers.length; i++) {
				intsx[i]=(int)numbers[i].getX();
				intsy[i]=(int)numbers[i].getY();
			}
			addEntryToInfo(data, name+"X", intsx);
			addEntryToInfo(data, name+"Y", intsy);
		}
		
		/**returns the array of stored points with the given key*/
		Point[] parseMetadataPointArrayvalue(MetaInfoWrapper data, String kayName) {
			int[] intsx=parseMetadataIntArrayValue(data,  kayName+"X") ;
			int[] intsy=parseMetadataIntArrayValue(data,  kayName+"Y") ;
			Point[] numbers=new Point[intsx.length];
			for (int i=0; i<numbers.length; i++) { 
				numbers[i]=new Point(intsx[i], intsy[i]);
			}
			return numbers;
		}
		
		 
		/**Depending on a channel name, returns an appropriate color
		 * @param name
		 * @return the channel color that is appropirate
		 */
		public static Color determineNewChannelColor(String name) {
			Color newColor=null;
			if(name==null) return null;
			name=name.trim().toLowerCase();
			if (name.equals("texasred")||name.equals("mcherry"))
				newColor=Color.red;
			if (name.contains("egfp")||name.contains("488"))
				newColor=Color.green;
			if (name.contains("yfp"))
				newColor=Color.yellow;
			if (name.contains("rfp")||name.contains("568")||name.contains("mplum")||name.contains("mcherry"))
				newColor=Color.red;
			if (name.equals("dapi")||name.contains("ebfp"))
				newColor=Color.blue;
			if (name.equals("brightfield"))
				newColor=Color.white;
			if (name.equals("dic") ||name.equals("tl dic"))
				newColor=Color.white;
			if (name.equals("cy5"))
				newColor=Color.magenta;
			
			Color color1 = getColor(name);//finds a color based on standard names "Red, Green, blue..."
			
			if(color1!=null)
				newColor=color1;
			return newColor;
		}
		
		/**takes the output of the toString method for class color and returns a color*/
		 public static Color getColor(String st) {		
			 
			    String lowerCase = st.toLowerCase();
				if (lowerCase.equals("white")) {return Color.white;}
				if (lowerCase.equals("black")) {return Color.black;}
				if (lowerCase.equals("red")) {return Color.red;}
				if (lowerCase.equals("blue")) {return Color.blue;}
				if (lowerCase.equals("green")) {return Color.green;}
				if (lowerCase.equals("pink")) {return Color.pink;}
				if (lowerCase.equals("yellow")) {return Color.yellow;}
				if (lowerCase.equals("orange")) {return Color.orange;}
				if (lowerCase.equals("cyan")) {return Color.cyan;}
				if (lowerCase.equals("magenta")) {return Color.magenta;}
				if (lowerCase.equals("grey")) {return Color.gray;}
				if (lowerCase.equals("gray")) {return Color.gray;}
				if (lowerCase.equals("light grey")) {return Color.lightGray;}
				if (lowerCase.equals("dark grey")) {return Color.darkGray;}
				if (lowerCase.equals("light gray")) {return Color.lightGray;}
				if (lowerCase.equals("dark gray")) {return Color.darkGray;}
			 
			    if (st.contains("Color[")) {
			    		return getColorFromImplied(st, "Color");
			    
			    }
			    
			    if (st.contains("rgb(")) {
			    	
			    	Color parsedC = getColorFromImplied(st, "rgb");
			    	
			    	
		    		return parsedC;
		    
		    }
			    
			    if (st.startsWith("#")) st=st.substring(1);
			    if (st.trim().length()==6) {
			    	CharSequence st1 = st.subSequence(0, 2);
			    	CharSequence st2 = st.subSequence(2, 4);
			    	CharSequence st3 = st.subSequence(4, 6);
			    	int r = Integer.parseInt(st1+"", 16);
			    	int g = Integer.parseInt(st2+"", 16);
			    	int b = Integer.parseInt(st3+"", 16);
			    	
			    	return new Color(r,g,b);
			    }
			    
			    return null;
			  }
		 
		 /**parses a color that is encodes withing the string
		  * assumes a format Color[r=x, g=y, b=z]*/
		 private static Color getColorFromImplied(String st, String method) {
			 String[] colors=getArgsFromImpliedMethod(st, method);
			   int r=0; int g=0; int b=0;
			   try{r=Integer.parseInt(getMetaDataEntryValue(colors, "r"));} catch (Exception e) {}
			   try{g=Integer.parseInt(getMetaDataEntryValue(colors, "g"));} catch (Exception e) {}
			   try{b=Integer.parseInt(getMetaDataEntryValue(colors, "b"));} catch (Exception e) {}
			   
			   
			   /**if the colors are not in the format of r=, g=, b= but instead in order*/
			   if (!colors[0].contains("=")) {
				   r=Integer.parseInt(colors[0]);
				   g=Integer.parseInt(colors[1]);
				   b=Integer.parseInt(colors[2]);
				   
			   }
			   
			 return  new Color(r,g,b);
			 
		 }
		 
		 
		 
			/**takes the output of the toString method for class Point and returns a point*/
		public static Point getPoint(String st) {		 
			    if (st.contains("Point[")) {
			   String[] c=getArgsFromImpliedMethod(st, "Point");
			   int x=0; int y=0; 
			   try{x=Integer.parseInt(getMetaDataEntryValue(c, "x"));} catch (Exception e) {}
			   try{y=Integer.parseInt(getMetaDataEntryValue(c, "y"));} catch (Exception e) {}		  
			   return  new Point(x,y);
			    }
			    if (st.contains("x=")) {
					   String[] c=st.split(" ");
					   int x=0; int y=0; 
					   try{x=Integer.parseInt(getMetaDataEntryValue(c, "x"));} catch (Exception e) {}
					   try{y=Integer.parseInt(getMetaDataEntryValue(c, "y"));} catch (Exception e) {}		  
					   return  new Point(x,y);
					    }
			    
			    
			    return null;
			  }
		
		
			/**undoes the output of the toString method for class Rectangle*/
		 static Rectangle getRectangle(String st) {		 
			    if (st.contains("Rectangle[")) {
			   String[] c=getArgsFromImpliedMethod(st, "Rectangle");
			   int x=0; int y=0; int width=0; int height=0; 
			   try{x=Integer.parseInt(getMetaDataEntryValue(c, "x"));} catch (Exception e) {}
			   try{y=Integer.parseInt(getMetaDataEntryValue(c, "y"));} catch (Exception e) {}	
			   try{width=Integer.parseInt(getMetaDataEntryValue(c, "width"));} catch (Exception e) {}
			   try{height=Integer.parseInt(getMetaDataEntryValue(c, "height"));} catch (Exception e) {}	
			   return  new Rectangle(x,y, width, height);
			    }
			    return null;
			  }
		 
			/**undoes the output of the toString method for class Font*/
		 static Font getFont(String st) {		 
			    return getFont(defaultfont, st);
			  } 
		 static Font defaultfont=new Font("Arial", Font.PLAIN, 20);
		 
		 
			/**undoes the output of the toString method for class Font
			 * if that works returns the font described in the string
			 * if that fails, returns the default font given*/
		 public static Font getFont(Font defaultfont, String st) {	
			 if (st==null) return defaultfont;
			 if (defaultfont==null) defaultfont=BasicMetaDataHandler.defaultfont;
			    if (st.contains("Font[")) {
			   String[] c=getArgsFromImpliedMethod(st, "Font");
			   int size=defaultfont.getSize(); String family=defaultfont.getFamily(); String style="Plain"; if(defaultfont.isBold()) style="bold"; if(defaultfont.isItalic()) style="italic";
			   try{size=Integer.parseInt(getMetaDataEntryValue(c, "size"));} catch (Exception e) {  }
			  try{
				 String s =getMetaDataEntryValue(c, "style");
				 if(s!=null) style=s;
				  } catch (Exception e) {}	
			  try{
				  String s=getMetaDataEntryValue(c, "family");
				  if(s!=null) family=s;
			  } catch (Exception e) {}	
			  
			  return  Font.decode(family+"-"+style.toUpperCase()+"-"+size);
			    } else return defaultfont;
			   // IssueLog.log("not font found");
			   // return null;
			  } 
		 
		 /**Assuming that a string contains values enclosed in brackets and separeated by commas
		    returns the content as a string array*/
		 public static String[] getArgsFromImpliedMethod(String st, String methodName) {
			 if (st.contains("["))
			 return getArgsFromImpliedMethod(st, methodName, "[", "]");
			 if (st.contains("(")) 
				 return getArgsFromImpliedMethod(st, methodName, "(", ")");
			 else return getArgsFromImpliedMethod(st, methodName, "<", ">");
		 }
		 
		 /**Assuming that a string contains values enclosed in brackets and separeated by commas
		    returns the content as a string array*/
		 public static String[] getArgsFromImpliedMethod(String st, String methodName, String startBracket, String endBracket) {
		
			 String delimit=",";
			 
			   if (st.contains(methodName+startBracket)) {
				    int i=st.indexOf(methodName+startBracket);
				    int i2=st.indexOf(endBracket, i);
				    
				   
				  
				   String output= st.substring(i+methodName.length()+1, i2);
				 
				 
				  
				  String[] array = output.split(delimit);
				   
				 
				  return array;
				    }
			   return null;
		 }
		 
		 /**Assuming that the string contains */
		 public static Integer[] getIntArgsFromImpliedMethod(String st, String methodName) {
			   if (st.contains(methodName+"[")) {
				    String[] outputs=getArgsFromImpliedMethod(st, methodName);
				    Integer[] outputsint=new Integer[outputs.length];
				    for(int i=0; i<outputs.length; i++) {
				    	 try {outputsint[i]=Integer.parseInt(outputs[i]);} catch (Exception e) {
				    		 outputsint[i]=null;
				    	 }
				    }
				    }
			   return null;
		 }
		 
		 public static String removeImpliedMethod(String st, String methodName) {
			   if (st.contains(methodName+"[")) {
				    int i=st.indexOf(methodName+"[");
				    int i2=st.indexOf("]", i);
				   String outputargs= st.substring(i+methodName.length()+1, i2);
				  return st.replace(methodName+"["+outputargs+"]", "");
				    }
			   return st;
		 }
		 
		
		 
		 public void addFieldsToMetaData(MetaInfoWrapper imp, Object o, boolean superClass, String subsetWith) {
			 Field[] fields=o.getClass().getDeclaredFields() ;
			 if (superClass) fields=o.getClass().getSuperclass().getDeclaredFields() ;
			 
				for(int i=0; i<fields.length; i++) {
					boolean stat=Modifier.isStatic(fields[i].getModifiers());
					boolean exclud=(subsetWith!=null && !fields[i].getName().contains(subsetWith));
					if (stat||exclud) continue;
					String typename=fields[i].getType().getName();
					
					boolean isInt=typename.equals("int");
					boolean isDouble=typename.equals("double");
					boolean isString=typename.equals("String");
					boolean isBoolean=typename.equals("boolean");
					boolean isPoint=fields[i].getType().equals(Point.class);
					boolean isFont=fields[i].getType().equals(Font.class);
					
					//log(fields[i].getType()+"");
					try {
						if (isInt)  addEntryToInfo( imp, fields[i].getName()+"=", fields[i].getInt(o));
						if (isDouble) addEntryToInfo( imp,fields[i].getName()+"=", fields[i].getDouble(o));
						if (isString) addEntryToInfo( imp,fields[i].getName()+"=", ""+fields[i].get(o));
						if (isBoolean) addEntryToInfo( imp, fields[i].getName()+"=", fields[i].getBoolean(o)?1:0);
						if (isFont) addEntryToInfo( imp,fields[i].getName()+"=", (Font)fields[i].get(o));
						if (isPoint) addEntryToInfo( imp,fields[i].getName()+"=", (Point)fields[i].get(o));
						//if (isBoolean) addEntryToInfo( imp, fields[i].getName()+"=", fields[i].getBoolean(o)?1:0);
						//log(+"="+ );
						//log(fields[i].getName()+"="+fields[i].getDouble(o) );
					} catch (Exception e) {
						IssueLog.logT(e);
					} 
					
				}
		 }
		 
		 public void getFieldsFromMetaData(MetaInfoWrapper imp, Object o, boolean superClass, String subsetWith) {
			 Field[] fields=o.getClass().getDeclaredFields() ;
			 if (superClass) fields=o.getClass().getSuperclass().getDeclaredFields() ;
			 
				for(int i=0; i<fields.length; i++) {
					boolean stat=Modifier.isStatic(fields[i].getModifiers());
					boolean exclud=(subsetWith!=null && !fields[i].getName().contains(subsetWith));
					if (stat||exclud) continue;
					String typename=fields[i].getType().getName();
					
					boolean isInt=fields[i].getType().equals(int.class);
					boolean isDouble=fields[i].getType().equals(double.class);
					boolean isString=typename.equals("String");
					boolean isBoolean=typename.equals("boolean");
					boolean isPoint=fields[i].getType().equals(Point.class);
					boolean isFont=fields[i].getType().equals(Font.class);
					
					//log(fields[i].getType()+"");
					try {
						if (isInt)  fields[i].setInt(o, parseMetadataIntvalue(imp, fields[i].getName()))	;
						if (isDouble) fields[i].setDouble(o, parseMetadataDoublevalue(imp, fields[i].getName()))	;
						if (isBoolean) fields[i].setBoolean(o, parseMetadataIntvalue(imp, fields[i].getName())==1);	
						if (isString||isPoint||isFont) fields[i].set(o, getEntryFromInfoAsString(imp, fields[i].getName()));	
						//log(+"="+ );
						//log(fields[i].getName()+"="+fields[i].getDouble(o) );
					} catch (Exception e) {
					//	IssueLog.log(e);
					} 
					
				}
		 }
		 
		 public void saveAnnotatedFields(MetaInfoWrapper imp, Object of, String suffix) {
			 if (imp==null) IssueLog.log("cannot add metadata to a null image");
			 Class<?> c=of.getClass();
			 try{
			 while (c!=Object.class) {
			 for (Field f: c.getDeclaredFields()) {
				 RetrievableOption o= f.getAnnotation( RetrievableOption.class);

				if (o!=null) try {
					f.setAccessible(true);
					
					if (o.save()) addEntryToInfo(imp, o.key()+suffix, f.get(of));
				} catch (Exception e) {
					IssueLog.log("problem adding entry "+o.key()+suffix +" reported in save annotated fields method "+'\n' );
					IssueLog.logT(e);
					
				}
			 }
			 c=c.getSuperclass();
			 }
			// IJ.log("method finished");
			 } catch (Exception e) {IssueLog.logT(e);}
			 return;
			
			 
		 }
		 
		 public void loadAnnotatedFields(MetaInfoWrapper imp, Object of, String suffix) {
			 Class<?> c=of.getClass();
			 try{
			 while (c!=Object.class) {
			 for (Field f: c.getDeclaredFields()) {
				 RetrievableOption o= f.getAnnotation( RetrievableOption.class);
				if (o!=null) try {
					f.setAccessible(true);
				if (o.save()) {
					Object value=parseMetadataClassValue(imp,o.key()+suffix, f.getType());
					if (value!=null) f.set(of, value );
				//	IssueLog.log2("loading value from image "+imp+" metadata", o.key()+suffix);
					}
				} catch (Throwable e) {
					IssueLog.log3("meta data entry not found "+o.key());
					//e.printStackTrace();
				} 
			 }
			 c=c.getSuperclass();
			 }
			 } catch (Exception e) {IssueLog.logT(e);}
			
			 return;
			
			 
		 }
		 
		 
		 /**adds a simple entry containing a key value pair (example:  entryName=5) 
		   to the image metadata. This uses the metadata like a hashtable.
		 */
		public  void addEntryToInfo(MetaInfoWrapper img, String name, String number) {
			img.setEntry(name, number);
			/**if (img==null||name==null) return;
			String oldProp=(String) img.getProperty("Info");
			if (oldProp==null) {
					img.setProperty("Info", name+"="+number);
					oldProp=(String) img.getProperty("Info");
				}
			if (!oldProp.contains(name+"=")){
					String newProp=oldProp+"\n"+name+"="+number;
					img.setProperty("Info", newProp);
					}
			else {replaceMetaDataEntry(img, name, number);}
			*/
		}
		


		public  ArrayList<String> getEntryListByNumber(MetaInfoWrapper a, String prefix, String suffix, int begin, int end ) {
			ArrayList<String> output=new ArrayList<String> ();

			for (int j=begin; j<end; j++) {
				String current=getEntryFromInfoAsString(a, prefix+j+suffix);
				output.add(current);
			}
			
			return output;
			
		}
		
		public  ArrayList<Integer> getIntEntryListByNumber(MetaInfoWrapper a, String prefix, String suffix, int begin, int end, String[] filler ) {
			ArrayList<String> strings = getEntryListByNumber(a,prefix, suffix, begin, end);
			ArrayList<Integer> output=new ArrayList<Integer> ();
			for(int i=0; i<strings.size(); i++) {
				String current = strings.get(i);
				if (current==null) output.add(null);
				if (current!=null) try {
					for(int m=0; m<filler.length; m++) { current=current.replace(filler[m], "").trim();}
					output.add(Integer.parseInt(current));
					
				} catch (Throwable t) {t.printStackTrace();}
			}
			 return output;
			
		}
		
		
		public  String getEntryFromInfoAsString(MetaInfoWrapper a, String b ) {
			return a.getEntryAsString(b);
				/**	
		    if (a==null||b==null) return null;
		    Object property=a.getProperty("Info");
		    String ss;
			ss=(String) property;
		    if (ss.equals(null) || ss.equals("") || ss==null) return null;
		    ss=getMetaDataEntry(ss, b);
		    ;
		    try {String output=ss.substring(b.length()+1); return output;} catch (StringIndexOutOfBoundsException si) {IJ.showMessage("out of bounds exception"); throw new NullPointerException(); }
		   */
		}
		
		/**i
		public  void replaceMetaDataEntry(MetaDataWrapper a, String b, String newValue){
			a.replaceInfoMetaDataEntry(b, newValue);
			//createMetadataWrapper(a).replaceInfoMetaDataEntry(b, newValue);
			f (b==null|| newValue==null) return;
			String entry=getMetaDataEntry( (String)a.getProperty("Info"), b);
			if (entry==null) return;
			String newMeta=((String) a.getProperty("Info")).replace(entry, b+"="+newValue);
			a.setProperty("Info", newMeta);
		}*/
		
		
		public  void removeMetaDataEntry(MetaInfoWrapper a, String b){
			a.removeEntry(b);
			//createMetadataWrapper(a).removeInfoMetaDataEntry(b);
			/**if (b==null || a==null) return;
			String entry=getMetaDataEntry( (String)a.getProperty("Info"), b);
			if (entry==null) return;
			String newMeta=((String) a.getProperty("Info")).replace(entry, "");
			a.setProperty("Info", newMeta);*/
		}
		
		
		/**this method switched the number or value for two metadata keys.*/
		public  void switchMetaDataEntries(MetaInfoWrapper a, String b, String c){
			if (a==null||b==null||c==null) return;
			String entryB=getEntryFromInfoAsString(a, b ) ;
			String entryC=getEntryFromInfoAsString(a, c ) ;
			//IJ.showMessage("replacing entries "+entryB +" and " +entryC);
			if (entryB==null || entryC==null) return;
			
			a.setEntry(b, entryC);
			a.setEntry(c, entryB);
			
		}
		
			/**Given an image opened originally from a zvi file, this method 
			   returns a list of each channel's name. example {texasred, eGFP, DAPI}.
			   Seems to work but no longer understand why. needs new testing*/
			public  ArrayList<String> channelNamesInOrder(MetaInfoWrapper select){
				
				/**CZI name system worked poorly so changed it*/
				/**ArrayList<String> cziNames=getCZINames(select);
				if(cziNames!=null)
					return cziNames;*/
				
				return chanOrderBasedInMyIndex(select);	
					
				}

			/**
			 * @param select
			 * @return
			 */
			private ArrayList<String> getCZINames(MetaInfoWrapper select) {
				/**this method reflects an old format of CZI channel names that I have not seen in recent tests. */
				String CZItest=getEntryFromInfoAsString(select, "Information|Image|Channel|Fluor #1 ");
				if (CZItest!=null) {
					
					return CZIChannelNamesInOrder2(select, null);
				}
				
				return null;
			}

			public boolean hasmyIndexSystem(MetaInfoWrapper select) {
				try {String c3=(String) getEntryFromInfoAsString(select, myIndexCode+0+ " " ) ;
				if (c3==null)return false;
				return true;
				} catch (Exception nn) {
					return false;}
				
			}
			
			/**looks for my index system*/
			public ArrayList<String> chanOrderBasedInMyIndex(MetaInfoWrapper select) {
				
				createChanDataIfnotEstablished(select);
				String[] chanKey =findChannelNameKey(select);
				if (chanKey==null)
					chanKey =ZviChanKey;//the default channel key if my own key is not present
				
				
				ArrayList <String> ChannelNames=new ArrayList <String> ();
					String currentChannelName=null;
					int c2;
					/**checks indexes 0 through 7 for channel data. For each of my indices,
					 * returns a number c2. then adds the name of channel c2 to the lists of names
					 * */ 
					for (int j=0; j<7; j++) {
						try {	String c3=(String) getEntryFromInfoAsString(select, myIndexCode+j+ " " ) ;
								currentChannelName=c3.trim();
								c2=Integer.parseInt(currentChannelName);
								
						try {
							//sets 
							currentChannelName=getRealChannelInformationBasedOnMetaData(select, chanKey, c2) ;
							
							ChannelNames.add(currentChannelName);
							
				} catch (Exception nn) {}
			} catch (Exception nn) {}	
				}
					
				return ChannelNames;
			}

			/**Given metadata, and the key for determining which key holds the channel name, returns the name of a certain channel
			 *  Channels here are numbered from 0, 1, 2 onward ()*/
			public String getRealChannelInformationBasedOnMetaData(MetaInfoWrapper select, String[] prefixSuffix,
					int channelNumber) {
				String fullKey = prefixSuffix[0]+(channelNumber+Integer.parseInt(prefixSuffix[3]))+ prefixSuffix[1];
			
				String entryFromInfoAsString = (String) getEntryFromInfoAsString(select, fullKey );
				//IssueLog.log("full key "+fullKey+" for "+channelNumber+" without output "+entryFromInfoAsString);
				return entryFromInfoAsString;
			}

			/**Checks the file for my metadta regarding he channel indeces, creates the info if not already present*/
			public void createChanDataIfnotEstablished(MetaInfoWrapper select) {
				boolean establish=hasmyIndexSystem(select);
				
				if (!establish) 
					createGregIndexSystem(select);
			}
			
			/**To accomadate newer versions of locitools, I need to write this.
			   this will obtain a list of the channel names and their numbers and record their indexes.
			   in the meta data. When QuickFigures reorders channels, these will also be reordered.
			   innitial channel names are derived from the microscope meta data (as it has been placed by locitools).*/
			private void createGregIndexSystem(MetaInfoWrapper select) {
				ArrayList <String> ChannelNames=new ArrayList <String> ();
				ArrayList <Integer> ChannelNums=new ArrayList <Integer> ();
				
				String[] chanKey =findChannelNameKey(select);//finds whichevery channel name key is appropriate for the metadata
				if (chanKey==null)
					chanKey =ZviChanKey;
				
				ArrayList<String> cziNames = this.getCZINames(select);
				if(cziNames!=null) {
					ChannelNames.addAll(cziNames);
					for(int i=0; i<cziNames.size(); i++)  ChannelNums.add(i);
					
				}

				else 
				{
				/**list all the channel numbers that appear in the metadata. And each of their channel anmes */
				for (int j=0; j<6; j++) {
								try {
								try {
									String c1 = getRealChannelInformationBasedOnMetaData(select, chanKey, j) ;
									if (c1!=null) 
										{
										ChannelNames.add(c1);
										ChannelNums.add(j);
										}
								
								} catch (Exception nn) {}
								} catch (Exception nn) {}
							}
				
			}
			
			
				for (int j=0; j<ChannelNames.size(); j++) {
					select.setEntry(myIndexCode+j+ " ", ""+ChannelNums.get(j));
					select.setEntry(myColorCode+ChannelNums.get(j)+ " ", ""+ChannelNames.get(j));
				
				}
				
			}

			/**Given an image opened originally from a zvi file, this method 
			   returns a list of each channel's exposure time.
			   Seems to work but no longer understand why. needs new testing*/
			public  ArrayList<String> getChannelExposuresInOrder(MetaInfoWrapper select){
				
				
				/**new modification to get exposure data from czi. seems to work but will perform more testing*/
				String CZItest=getEntryFromInfoAsString(select, "Information|Image|Channel|Fluor #1 ");
				if (CZItest!=null) {
					
					return CZIChannelNamesInOrder2(select, "Information|Image|Channel|ExposureTime #");
				}
				
				
				
				createChanDataIfnotEstablished(select);
				
				ArrayList <String> ChannelExposures=new ArrayList <String> ();
					String c1;
					int c2;
					for (int j=0; j<6; j++) {
						try {
							String c3=getRealChannelInformationBasedOnMetaData(select, myChanKey,j) ;
								   c1=c3.trim();
								   c2=Integer.parseInt(c1);
						try {
							String[] key1 = this.findAppropriateKey(select, allExposureTimeKeys);
									c1=getRealChannelInformationBasedOnMetaData(select,key1 ,c2 ) ;
									ChannelExposures.add(c1);
									
				} catch (Exception nn) {}
			} catch (Exception nn) {}	
				}
				return ChannelExposures;	
					
				}
			
			
			/**possibly OBSOLETE: Given an image opened originally from a czi file, this method 
			   returns a list of each channel's name. example {texasred, eGFP, DAPI}.
			   If an argument is provided @param intoType then this may return another kind of information
			   Seems to work but not extensively tested. 
			   Takes into account the possibility that the channel IDs are not in order
			   */
			private  ArrayList<String> CZIChannelNamesInOrder2(MetaInfoWrapper select, String infoType){
				if(infoType==null)
					infoType="Information|Image|Channel|Fluor #";//the key for the names of the dyes for each channel.
				
				
				/**obtains the id numbers for each channel used. Those id numbers may start from 0. 
				 * Assumes that the numbers in 'Channel:' section correspond to the location in the stack. 
				 * need more examples to test to be sure this is alays the case*/
				ArrayList<Integer> list = getIntEntryListByNumber(select, "Information|Image|Channel|Id #"," ", 1,6, new String[] {"Channel:"});
				
				
				ArrayList <String> outputChannelNames=new ArrayList <String> ();
					String c1;
					int c2;
					for (int j=0; j<6; j++) {
						//assuming that an image has less than 6 channels, fints each on the list. This 0-6 iteration should be through the order of the channel in the stack
						int ind1 = list.indexOf((j));//returns the location of channel j on the list. For example the channel at position 0 in the stack might be called channel 
						try {String c3=(String) getEntryFromInfoAsString(select, "Information|Image|Channel|Id #"+(ind1+1)+ " " ) ;
						
						c3=c3.replace("Channel:", "");
						c1=c3.trim();
						c2=Integer.parseInt(c1);
						
						try {c1=(String) getEntryFromInfoAsString(select, infoType+(c2+1)+" ") ;
						
						outputChannelNames.add(c1);
						
				} catch (Exception nn) {}
						
						}catch (Throwable t) {}
						
			
						
						
						
				}
					
					
					
					
					
				return outputChannelNames;	
					
				}
			

			

			/**Tests the image metadata to determine if any of the known channel name keys are present*/
			public String[] findChannelNameKey(MetaInfoWrapper handle) {
				
				String[][] targetKeyList = allNameKeys;
				return findAppropriateKey(handle, targetKeyList);
			}

			/**checks a list of keys to determine which key 1) exists in the metadata and 2) can be used to get information about a channel
			 * @param handle
			 * @param targetKeyList
			 * @return
			 */
			protected String[] findAppropriateKey(MetaInfoWrapper handle, String[][] targetKeyList) {
				for(String[] key : targetKeyList) try {
					for(int i=0; i<7; i++) try {
						
					String f = getRealChannelInformationBasedOnMetaData(handle, key, i);
					if(f!=null) return key;
					
					} catch (Throwable t) {}
				}
				catch (Throwable t) {}
				
				
				
				return null;
			}

			public void createChanNamesFor(MetaInfoWrapper mcw, int nChan) {
				for(int i=1; i<=nChan; i++) {
					mcw.setEntry(ZviChanKey[0]+(i-1)+ZviChanKey[1], "channel #"+i);
				}
				
			}
		 
}
