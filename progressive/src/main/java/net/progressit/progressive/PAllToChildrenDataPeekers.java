package net.progressit.progressive;

import com.google.common.collect.Sets;

import net.progressit.progressive.PComponent.PDataPeekers;

public class PAllToChildrenDataPeekers<T> extends PDataPeekers<T>{

	public PAllToChildrenDataPeekers() {
		super((data)->Sets.newHashSet(), (data)->Sets.newHashSet(data));
	}

}
