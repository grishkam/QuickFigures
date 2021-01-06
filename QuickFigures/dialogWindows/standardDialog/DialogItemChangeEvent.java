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
 * Date Modified: Jan 6, 2021
 * Version: 2021.1
 */
package standardDialog;

import java.awt.Component;

/**
an event the indicates a change within a particular dialog
 */
public class DialogItemChangeEvent {

	
	
	private StandardDialog source;
	private Component key;
	private String keyc;
	
	
	public DialogItemChangeEvent(StandardDialog source, Component key) {
		super();
		this.source = source;
		this.key = key;
	}

	



	public StandardDialog getSource() {
		return source;
	}

	public void setSource(StandardDialog source) {
		this.source = source;
	}

	public Component getKey() {
		return key;
	}

	public void setKey(Component key) {
		this.key = key;
	}
	
	public String getStringKey() {
		return keyc;
	}

	public void setStringKey(String key) {
		this.keyc = key;
	}

}
