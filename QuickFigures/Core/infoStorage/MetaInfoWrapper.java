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
 * Version: 2021.1
 */
package infoStorage;


/**an interface with methods designed for extraction of stored values
 * the form of those stored values depends on the implementation*/
public interface MetaInfoWrapper {
	
	
	public  void setEntry(String entryKeyName, String value);
	
	/**The Setters*/
	public  String getEntryAsString( String entryname ) ;
	public  Integer getEntryAsInt( String entryname ) ;
	public  Double getEntryAsDouble( String entryname ) ;
	public Object getEntryAsDestringedClass( String b, Class<?> c);
	
	
	public  void removeEntry(String entyname);
	
	
	
}
