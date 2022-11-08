package net.progressit.progressive.helpers;

import net.progressit.progressive.PLifecycleHandler;

/**
 * A convenience empty implementation of the PLifecycleHandler which allows sub-classes to only override the required life-cycle hook methods.
 * 
 * @author theodore.r
 *
 */
public class PSimpleLifecycleHandler implements PLifecycleHandler{
	@Override
	public void prePlacement() {
	}
	@Override
	public void postPlacement() {
	}
	@Override
	public void preProps() {
	}
	@Override
	public void postProps() {
	}
	@Override
	public void preRemove() {
	}
	@Override
	public void postRemove() {
	}
}