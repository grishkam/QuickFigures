package graphicalObjects_BasicShapes;

import java.io.Serializable;

import graphicalObjectHandles.HasHandles;
import graphicalObjects.LayerSpecified;
import utilityClassesForObjects.Named;
import utilityClassesForObjects.ShowsOptionsDialog;
import utilityClassesForObjects.Tagged;

public interface GraphicalObject extends SimpleGraphicalObject, HasHandles, Tagged, Serializable,Named, ShowsOptionsDialog, LayerSpecified {
		

}
