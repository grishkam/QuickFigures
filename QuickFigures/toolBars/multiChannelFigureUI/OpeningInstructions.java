/**
 * Author: Greg Mazo
 * Date Modified: Feb 19, 2023
 * Copyright (C) 2023 Gregory Mazo
 * 
 */
/**
 
 * 
 */
package multiChannelFigureUI;

/**
 An object that carries information on that do if no image file is avialble
 */
public class OpeningInstructions {

	public static enum IF_PATH_NOT_FOUND {
		RETURN_NULL, USER_WILL_SELECT_FILE, CHOOSE_IMAGE, USE_ACTIVE_IMAGE;
	}
	
	public String path;
	public IF_PATH_NOT_FOUND method;
	
	public OpeningInstructions(String path, IF_PATH_NOT_FOUND what) {
		this.path=path;
		method=what;
	}
}
