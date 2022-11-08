package net.progressit.progressive;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * A set of child component configurations, created by the parent PComponent as part of its rendering.
 * 
 * @author theodore.r
 *
 */
@Data
public class PChildrenPlan{
	private final List<PChildPlan> childrenPlan = new ArrayList<>();
	public void addChildPlan(PChildPlan childPlan) {
		childrenPlan.add(childPlan);
	}
}