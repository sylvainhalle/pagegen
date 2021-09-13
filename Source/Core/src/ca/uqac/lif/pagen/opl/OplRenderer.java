/*
    A random DOM tree generator
    Copyright (C) 2020-2021 Sylvain Hallé

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
package ca.uqac.lif.pagen.opl;

import java.io.PrintStream;
import java.util.HashSet;
import java.util.Set;

import ca.uqac.lif.pagen.BoxRenderer;
import ca.uqac.lif.pagen.LayoutConstraint;
import ca.uqac.lif.pagen.LayoutConstraint.Contained;
import ca.uqac.lif.pagen.LayoutConstraint.Disjoint;
import ca.uqac.lif.pagen.LayoutConstraint.HorizontallyAligned;
import ca.uqac.lif.pagen.LayoutConstraint.VerticallyAligned;

/**
 * A renderer that prints a page as set of real variables and linear
 * constraints, in the output format of the CPLEX solver.
 */
public abstract class OplRenderer extends BoxRenderer
{
	/**
	 * A set of constraints that applies to some of the boxes. 
	 */
	/*@ non_null @*/ protected Set<LayoutConstraint> m_constraints;
	
	/**
	 * The number of variables resulting from the modeling of the page and
	 * its constraints.
	 */
	protected int m_numVariables;
	
	/**
	 * The number of distinct constraints produced by the renderer.
	 */
	protected int m_numConstraints;
	
	@SafeVarargs
	public OplRenderer(Set<LayoutConstraint> ... constraints)
	{
		super();
		m_constraints = new HashSet<LayoutConstraint>();
		for (Set<LayoutConstraint> c : constraints)
		{
			m_constraints.addAll(c);
		}
	}
	
	/**
	 * Adds a set of constraints to apply to some of the boxes.
	 * @param constraints The set of constraints
	 * @return This renderer
	 */
	public OplRenderer addConstraints(Set<LayoutConstraint> constraints)
	{
		m_constraints.addAll(constraints);
		return this;
	}
	
	/**
	 * Renders a layout constraint. This method simply dispatches the control to
	 * another method depending on the type of layout constraint to be rendered.
	 * @param ps The print stream where the constraint is to be printed
	 * @param c The constraint
	 */
	protected void render(/*@ non_null @*/ PrintStream ps, /*@ non_null @*/ LayoutConstraint c)
	{
		if (c instanceof VerticallyAligned)
		{
			renderVerticallyAligned(ps, (VerticallyAligned) c);
		}
		else if (c instanceof HorizontallyAligned)
		{
			renderHorizontallyAligned(ps, (HorizontallyAligned) c);
		}
		else if (c instanceof Disjoint)
		{
			renderDisjoint(ps, (Disjoint) c);
		}
		else if (c instanceof Contained)
		{
			renderContained(ps, (Contained) c);
		}
	}
	
	/**
	 * Gets the count of variables resulting from the modeling of the page and
	 * its constraints.
	 * @return The number of variables
	 */
	/*@ pure @*/ public int getVariableCount()
	{
		return m_numVariables;
	}
	
	/**
	 * Gets the number of distinct constraints produced by the renderer.
	 * @return The number of constraints
	 */
	/*@ pure @*/ public int getConstraintCount()
	{
		return m_numConstraints;
	}
	
	/**
	 * Renders a vertically-aligned layout constraint.
	 * @param ps The print stream where the constraint is to be printed
	 * @param c The constraint
	 */
	protected abstract void renderVerticallyAligned(/*@ non_null @*/ PrintStream ps, /*@ non_null @*/ VerticallyAligned c);
	
	/**
	 * Renders a horizontally-aligned layout constraint.
	 * @param ps The print stream where the constraint is to be printed
	 * @param c The constraint
	 */
	protected abstract void renderHorizontallyAligned(/*@ non_null @*/ PrintStream ps, /*@ non_null @*/ HorizontallyAligned c);
	
	/**
	 * Renders a disjointness layout constraint.
	 * @param ps The print stream where the constraint is to be printed
	 * @param c The constraint
	 */
	protected abstract void renderDisjoint(/*@ non_null @*/ PrintStream ps, /*@ non_null @*/ Disjoint c);
	
	/**
	 * Renders a containment layout constraint.
	 * @param ps The print stream where the constraint is to be printed
	 * @param c The constraint
	 */
	protected abstract void renderContained(/*@ non_null @*/ PrintStream ps, /*@ non_null @*/ Contained c);
}
