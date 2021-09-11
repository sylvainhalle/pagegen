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

public abstract class OplRenderer extends BoxRenderer
{
	/**
	 * A set of constraints that applies to some of the boxes. 
	 */
	protected Set<LayoutConstraint> m_constraints;
	
	@SafeVarargs
	public OplRenderer(Set<LayoutConstraint> ... constraints)
	{
		super();
		m_constraints = new HashSet<LayoutConstraint>();
		for (Set<LayoutConstraint> set : constraints)
		{
			m_constraints.addAll(set);
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
	
	protected void render(PrintStream ps, LayoutConstraint c)
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
	
	protected abstract void renderVerticallyAligned(PrintStream ps, VerticallyAligned c);
	
	protected abstract void renderHorizontallyAligned(PrintStream ps, HorizontallyAligned c);
	
	protected abstract void renderDisjoint(PrintStream ps, Disjoint c);
	
	protected abstract void renderContained(PrintStream ps, Contained c);

}
