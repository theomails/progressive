package net.progressit.progressive;

/**
 * A marker interface within which different event listeners methods are expected to be held. 
 * Actual dispatch will happen based on event class in the method signature, via Google Event Bus.
 * 
 * @author theodore.r
 *
 */
public interface PEventListener{}