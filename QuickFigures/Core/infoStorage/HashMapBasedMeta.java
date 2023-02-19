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
 * Version: 2023.1
 */
package infoStorage;

import java.util.HashMap;

/** 
 * a subclass of @see BasicMetaIndoWrapper that allows 
 * storage of information into hashmap using the classes in this package*/
public class HashMapBasedMeta extends BasicMetaInfoWrapper  {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	HashMap<String, String> map =new HashMap<String, String>();
	
	
	public HashMapBasedMeta() {}
	public HashMapBasedMeta(String st) {
		if (st==null)st="";
		String[] sts = st.split(""+'\n');
		for(String s: sts) {
			String[] keyval = s.split("=");
			if (keyval.length>1) {
				map.put(keyval[0], keyval[1]);
			}
		}
	}
	
	public String toAString() {
		String output="";
		for(String key: map.keySet()) {
			if (key==null) continue;
			output+='\n'+key+"="+map.get(key);
		}
		return output;
	}
	
	public HashMapBasedMeta(StringBasedMetaWrapper st) {
		this(st.getProperty());
	}

	@Override
	public void setEntry(String entryname, String number) {
		map.put(entryname, number);
		
	}

	@Override
	public void replaceInfoMetaDataEntry(String b, String entryC) {
		map.put(b, entryC);
		
	}

	@Override
	public String getEntryAsString(String b) {
		// TODO Auto-generated method stub
		return map.get(b);
	}

	@Override
	public void removeEntry(String entryname) {
		map.remove(entryname);
		
	}
	
	

}
