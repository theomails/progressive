package net.progressit.progressive.helpers;

import java.awt.Container;

import net.progressit.progressive.PPlacers;

/**
 * A simple placer, which takes a container object on placer construction, and then 
 * does simple add/remove of the component into that container when needed.
 *  
 * @author theo
 *
 */
public class PSimpleContainerPlacers extends PPlacers {

	public PSimpleContainerPlacers(Container container) {
		super( (component)->{container.add(component);} , (component)->{container.remove(component);});
	}

}
