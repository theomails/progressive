package net.progressit.progressive;

/**
 * PLifecycleHandler has to be defined as part of the PComponent definition.
 * Zero or more of the lifecycle hooks can be used to initialize / manage the component.
 * 
 * @author theodore.r
 *
 */
public interface PLifecycleHandler {
	public void prePlacement();
	public void postPlacement();
	public void preProps();
	public void postProps();
	public void preRemove();
	public void postRemove();
}