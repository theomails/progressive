package net.progressit.progressive;

import java.util.Set;

import net.progressit.progressive.PComponent.PDataPeekers;

public class PAllToSelfDataPeekers<T> extends PDataPeekers<T>{

	public PAllToSelfDataPeekers() {
		super((data)->Set.of(data), (data)->Set.of());
	}

}
