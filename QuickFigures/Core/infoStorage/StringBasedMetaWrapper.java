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
package infoStorage;

import java.io.Serializable;

//import ij.IJ;

/**A class that keeps a set of key value pains in a string. Methods innitially written 
  to modify the info of ImagePlus metadata*/
public class StringBasedMetaWrapper extends BasicMetaInfoWrapper implements MetaInfoWrapper, Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String st="";
	
	public  String getProperty() {
		return st;
	}
	
	public void setProperty(String newProp) {
		st=newProp;
	}
	
	public String getInfo() {
		
		String oldProp=(String) getProperty();
		if (oldProp==null) {
				setProperty("");
				oldProp=(String) getProperty();
			}
		return oldProp;
		
	}
	
	

	/**sets a given entry to value value*/
	@Override
	public void setEntry(String keyName, String value) {
		if (keyName==null) return;
		String oldProp=(String) getProperty();
		if (oldProp==null) {
				setProperty( keyName+"= "+value);
				oldProp=(String) getProperty();
			}
		if (!oldProp.contains(keyName+"= ")){
				String newProp=oldProp+"\n"+keyName+"= "+value;
				setProperty(newProp);
				}
		else {replaceInfoMetaDataEntry(keyName, value);}
		
	}
	

	@Override
	public String getEntryAsString(String entryname) {
		 String b = entryname;
		if (b==null) return null;
		    Object property=getProperty();
		    String ss;
			ss=(String) property;
		    if (ss==null||ss.equals(null) || ss.equals("") ) return null;
		    ss=getMetaDataEntry(ss, b);
		    if (ss==null||ss.equals(null) || ss.equals("") ) return null;
		    ;
		    
		    try {
		    	String output=ss.substring(b.length()+2);
		    	//IssueLog.log("Parsing string "+output);
		    	return output;
		    	} catch (StringIndexOutOfBoundsException si) { throw new NullPointerException(); }
	}
	
	/**replaces the old value of entryname with a new value, similar to set entry but cannot
	 * create a new entry*/
	public void replaceInfoMetaDataEntry(String entryname, String newValue) {
	 String b = entryname;
		if (b==null|| newValue==null) return;
		String entry=getMetaDataEntry( getProperty(), b);
		if (entry==null) return;
		String newMeta=((String) getProperty()).replace(entry, b+"= "+newValue);
		setProperty(newMeta);
	}

	
	
	
	@Override
	public void removeEntry(String entryname) {
		String b = entryname;
		if (b==null ) return;
		String entry=getMetaDataEntry( getProperty(), b);
		if (entry==null) return;
		String newMeta=((String) getProperty()).replace(entry, "");
		setProperty( newMeta);
		
	}


}
