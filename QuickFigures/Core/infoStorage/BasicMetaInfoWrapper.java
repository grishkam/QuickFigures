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
package infoStorage;

import java.io.Serializable;

import utilityClasses1.NumberUse;

/**A class that keeps a set of key value pairs stored in some way 
 (method depends on the subclass). 
 Multiple subclasses of this class are used for a variety of purposes
  */
public abstract class BasicMetaInfoWrapper implements MetaInfoWrapper, Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;



	public abstract void replaceInfoMetaDataEntry(String b, String entryC) ;
	public abstract String getEntryAsString(String b) ;
	public abstract void removeEntry(String entryname);


	/**this method switched the number or value for two metadata keys.*/
	public  void switchMetaDataEntries(String b, String c){
		if (b==null||c==null) return;
		String entryB=getEntryAsString( b ) ;
		String entryC=getEntryAsString(c ) ;

		if (entryB==null || entryC==null) return;
		replaceInfoMetaDataEntry( b, entryC);
		replaceInfoMetaDataEntry(c, entryB);
	}
	

	/**returns the entry as an integer*/
	@Override
	public Integer getEntryAsInt(String entryname) {
		String output=getEntryAsString( entryname) ;
		if (output==null) return null;
	    Integer r;
	    try {r=Integer.parseInt(output);} catch (NumberFormatException si) {return null; }
	    return r;
	}

	/**returns the entry as a double*/
	@Override
	public Double getEntryAsDouble(String entryname) {
		String output=getEntryAsString(entryname) ;
		
		if (output==null) return null;
		if (output.endsWith("px")) {output=output.substring(0, output.length()-2);
		
		}
		
	    Double r;
	    try {r=Double.parseDouble(output);} catch (NumberFormatException si) {return null; }
	    return r;
	}
	
	/**returns the entry as a string array*/
	public  String[] parseMetadataStringArrayValue(String b ) {
		String output= getEntryAsString( b) ;
		if (output==null) return null;
	    return BasicMetaDataHandler.stringArrayFromString(output);
	}
	/**returns the entry as an int array*/
	public  int[] parseMetadataIntArrayValue(String b ) {
		String output= getEntryAsString( b) ;
		if (output==null) return null;
	    return NumberUse.intArrayFromString1(output);
	}

	/**returns the entry as a string*/
	protected String getMetaDataEntry(String ss, String b) {
		return BasicMetaDataHandler.getMetaDataEntryFromLine(ss, b);
	}
	
	/**returns the entry as something depending on class c*/
	@Override
	public Object getEntryAsDestringedClass(String b, Class<?> c) {
		return BasicMetaDataHandler.getObject(getEntryAsString( b), c);
	}
	
}
