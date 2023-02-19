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
 * Date Modified: Jan 4, 2021
 * Version: 2023.1
 */
package figureFormat;

/**A shutdown hook that saves the preferences of the default directory handler to a file
  if that file is loaded, the working directory used previously would be used again
  Work in progress. will use the prefs file to store options in the future.
  */
public class PrefsShutDownHook implements Runnable{
	
static boolean addedHookAlready=false;
	
	/**Adds the shutdown hook if it has not already need added*/
	public static void addShutdownHook() {
		if (addedHookAlready) return;
		
		Runtime.getRuntime().addShutdownHook(new Thread(new PrefsShutDownHook()));
		addedHookAlready=true;
	}

	@Override
	public void run() {
		DirectoryHandler.getDefaultHandler().savePrefs();
	}
}
