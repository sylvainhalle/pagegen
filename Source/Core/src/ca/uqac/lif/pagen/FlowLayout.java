package ca.uqac.lif.pagen;

public abstract class FlowLayout implements LayoutManager
{
	/**
	 * The maximum number of elements to flow before moving to the
	 * next line
	 */
	protected int m_maxElements;
	
	/**
	 * The spacing between elements
	 */
	protected float m_spacing;
	
	/**
	 * Creates a new flow layout manager
	 * @param max_elements The maximum number of elements to 
	 * flow before moving to the next line/column. Set to 0 to flow all elements
	 * on the same line/column
	 */
	public FlowLayout(int max_elements)
	{
		super();
		m_maxElements = max_elements;
		m_spacing = 2;
	}
}
