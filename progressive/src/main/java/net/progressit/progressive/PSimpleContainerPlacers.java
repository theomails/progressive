package net.progressit.progressive;

import java.awt.Container;

import net.progressit.progressive.PComponent.PPlacers;

public class PSimpleContainerPlacers extends PPlacers {

	public PSimpleContainerPlacers(Container container) {
		super( (component)->{container.add(component);} , (component)->{container.remove(component);});
	}

}
