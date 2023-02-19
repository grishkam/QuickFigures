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
 * Date Created: Mar 23, 2022
 * Date Modified: Mar 23, 2022
 * Version: 2023.1
 */
package appContext;

import java.util.ArrayList;

import figureOrganizer.MultichannelDisplayLayer;

/**
 An interface for classes that wait for a file to be opened
 */
public interface PendingFileOpenActions {

	public static ArrayList<PendingFileOpenActions> pendingList=new  ArrayList<PendingFileOpenActions>();
	
	boolean isActive();

	/**
	 * @param path
	 * @return
	 */
	boolean isTargetFile(String path);

	/**
	 * @param zero
	 */
	void performActionOnImageDisplayLayer(MultichannelDisplayLayer zero);
	
	

}
