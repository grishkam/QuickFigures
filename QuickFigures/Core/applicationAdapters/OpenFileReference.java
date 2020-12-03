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
package applicationAdapters;

/**An interface representing anything with a title and save path
  that can lead to a file. */
public interface OpenFileReference extends HasScaleInfo {
	public String getTitle();
	public void setTitle(String st);
	
	public String getPath();
	
	
	
	/**True if this is an image file*/
	public boolean containsImage();
	
	/**returns true if the given object represents the same image as this one*/
	public boolean isSameImage(Object o) ;
	
	/**if open files are tracked by id numbers, returns the number*/
	public int getID();
	
	
	
}