package net.progressit.progressive.helpers;

import javafx.scene.layout.Pane;
import net.progressit.progressive.PPlacers;

/**
 * A simple placer, which takes a container object on placer construction, and then 
 * does simple add/remove of the component into that container when needed.
 *  
 * @author theo
 *
 */
public class PSimpleContainerPlacers extends PPlacers {

	public PSimpleContainerPlacers(Pane container) {
		super( (component)->{container.getChildren().add(component);} , (component)->{container.getChildren().remove(component);});
	}

}
