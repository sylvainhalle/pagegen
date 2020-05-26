package ca.uqac.lif.pagen;

import java.util.HashSet;
import java.util.Set;

import ca.uqac.lif.synthia.Picker;

public abstract class FlowLayout implements LayoutManager
{
	/**
	 * A set of constraints produced by the layout manager
	 */
	protected Set<LayoutConstraint> m_constraints;
	
	/**
	 * The maximum number of elements to flow before moving to the
	 * next line
	 */
	protected Picker<Integer> m_maxElements;
	
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
	public FlowLayout(Picker<Integer> max_elements)
	{
		super();
		m_maxElements = max_elements;
		m_spacing = 2;
		m_constraints = new HashSet<LayoutConstraint>();
	}
	
	@Override
	public Set<LayoutConstraint> getConstraints()
	{
		return m_constraints;
	}
}
