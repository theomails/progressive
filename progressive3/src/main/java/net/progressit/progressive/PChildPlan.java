package net.progressit.progressive;

import java.util.Optional;

import lombok.Builder;
import lombok.Data;

/**
 * A single intent of child component configuration, created by the parent PComponent as part of its rendering.
 * 
 * @author theodore.r
 *
 */
@Data
@Builder
public class PChildPlan{
	public final PComponent<?,?> component;
	public final Object props;
	public final Optional<PEventListener> listener;
}