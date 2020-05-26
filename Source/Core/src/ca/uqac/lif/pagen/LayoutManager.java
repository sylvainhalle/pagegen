package ca.uqac.lif.pagen;

import java.util.List;

public interface LayoutManager
{
	/**
	 * Positions a list of boxes inside a parent box
	 * @param parent The parent box
	 * @param children The children boxes
	 */
	public void arrange(Box parent, List<Box> children);
}
