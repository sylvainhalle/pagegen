package ca.uqac.lif.pagen;

import java.util.List;
import java.util.Set;

public interface LayoutManager
{
	/**
	 * Positions a list of boxes inside a parent box
	 * @param parent The parent box
	 * @param children The children boxes
	 */
	public void arrange(Box parent, List<Box> children);
	
	/**
	 * Gets the set of constraints accumulated by the operation of this
	 * layout manager
	 * @return The set of layout constraints
	 */
	public Set<LayoutConstraint> getConstraints();
}
