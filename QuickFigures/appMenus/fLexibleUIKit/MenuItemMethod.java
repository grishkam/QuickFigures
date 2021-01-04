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
package fLexibleUIKit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**Wanted a simple way to create popup menus for multiple classes without
  writing any more than minimal code for it each time. Also
  wanted to learn about annotations and  reflection in java
  With this annotation on a few method calls within a class
  definition, a popupmenu item can be made to call each method.
  Eliminates the need for writing an action listener each time.
  Might not be efficient but all this introspection make my code
  easier to write.
  */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface MenuItemMethod {
	
	
	/**The text of the Menu items*/
	public String menuText();
	/**Action command*/
	public String menuActionCommand();
	
	
	public boolean inherit() default false;
	
	/**Set if the items goes into a submenu. Use '<' symbol in the string if 
	 * there is a hierarchy of submenus*/
	public String subMenuName() default "";
	
	/**Lower order rank items appear first in the GUI*/
	public int orderRank() default 0;
	
	/**set to a string if you want to invoke another method before adding this to 
	  a list of actions. Java reflect getMethod will be used to find the method.
	  If it returns null or false, the menu item will not be included*/
	public String permissionMethod() default "";
	
	

}
