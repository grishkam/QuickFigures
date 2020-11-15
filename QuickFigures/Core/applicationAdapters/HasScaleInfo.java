package applicationAdapters;

import utilityClassesForObjects.ScaleInfo;

/**implemented by any object that contains information regarding the
  number of pixels that corresponds to a physical unit distance*/
public interface HasScaleInfo {
	public ScaleInfo getScaleInfo();
	public void setScaleInfo( ScaleInfo scaleInfo);
}
