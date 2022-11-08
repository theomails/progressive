package net.progressit.progressive;

import java.util.Set;

import com.google.common.eventbus.EventBus;

import net.progressit.progressive.helpers.PComponentHelper;

public abstract class PLeafComponent <T, U> extends PComponent<T, U>{

	public PLeafComponent(PPlacers placers, EventBus globalBus) {
		super(placers, globalBus);
	}

	@Override
	protected Set<Object> partitionDataForSelf(T data) {
		return PComponentHelper.setWithAllData(data);
	}

	@Override
	protected Set<Object> partitionDataForChildren(T data) {
		return PComponentHelper.setWithNoData();
	}

	@Override
	protected PChildrenPlan renderChildrenPlan(T data) {
		return PComponentHelper.emptyChildrenPlan();
	}

}
