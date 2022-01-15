package net.progressit.progressive;

import java.util.Set;

import net.progressit.progressive.PComponent.PDataPeekers;

public class PAllToChildrenDataPeekers<T> extends PDataPeekers<T>{

	public PAllToChildrenDataPeekers() {
		super((data)->Set.of(), (data)->Set.of(data));
	}

}
