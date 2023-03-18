package net.progressit.progressive;

import com.google.common.collect.Sets;

import net.progressit.progressive.PComponent.PDataPeekers;

public class PAllToSelfDataPeekers<T> extends PDataPeekers<T>{

	public PAllToSelfDataPeekers() {
		super((data)->Sets.newHashSet(data), (data)->Sets.newHashSet());
	}

}
