/*
    A random DOM tree generator
    Copyright (C) 2020 Sylvain Hallé
    
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published
    by the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.
    
    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.
    
    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package ca.uqac.lif.pagen;

import java.util.HashSet;
import java.util.Set;

import ca.uqac.lif.synthia.Picker;
import ca.uqac.lif.synthia.util.Constant;

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
	 * A coin toss deciding if an element should be misaligned on purpose
	 */
	protected Picker<Boolean> m_injectAlignementFault = new Constant<Boolean>(false);
	
	/**
	 * A picker deciding by how much to misalign an element
	 */
	protected Picker<Integer> m_shiftPicker = new Constant<Integer>(0);
	
	/**
	 * The number of misalignments injected into the pages so far
	 */
	protected int m_misalignmentCount = 0;
	
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
	public FlowLayout(/*@ non_null @*/ Picker<Integer> max_elements)
	{
		super();
		m_maxElements = max_elements;
		m_spacing = 2;
		m_constraints = new HashSet<LayoutConstraint>();
	}
	
	/**
	 * Sets a Boolean picker to determine if an alignment fault is to
	 * be injected.
	 * @param picker The picker
	 * @param shift The amount by which to shift a misaligned element
	 */
	public void setAlignmentFault(Picker<Boolean> picker, Picker<Integer> shift)
	{
		m_injectAlignementFault = picker;
		m_shiftPicker = shift;
	}
	
	/**
	 * Gets the number of misalignments injected into the pages so far
	 * @return The number of misalignments
	 */
	public int getMisalignmentCount()
	{
		return m_misalignmentCount;
	}
	
	@Override
	public Set<LayoutConstraint> getConstraints()
	{
		return m_constraints;
	}
}
