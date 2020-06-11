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

public abstract class LayoutConstraint 
{
	protected Set<Box> m_boxes;
	
	public LayoutConstraint()
	{
		super();
		m_boxes = new HashSet<Box>();
	}

	/**
	 * Adds a box subjected to a constraint
	 * @param b The box
	 * @return This constraint
	 */
	public LayoutConstraint add(Box b)
	{
		m_boxes.add(b);
		return this;
	}
	
	@Override
	public String toString()
	{
		StringBuilder out = new StringBuilder();
		out.append(getName());
		boolean first = true;
		for (Box b : m_boxes)
		{
			if (first)
			{
				first = false;
			}
			else
			{
				out.append(",");
			}
			out.append(b.getId());
		}
		return out.toString();
	}
	
	/**
	 * Gets the name of the constraint
	 * @return The name of the constraint
	 */
	protected abstract String getName();
	
	/**
	 * Determines if a constraint is valid
	 * @return <tt>true</tt> if the constraint is valid, <tt>false</tt> otherwise
	 */
	protected boolean isValid()
	{
		return true;
	}
	
	public static class HorizontallyAligned extends LayoutConstraint
	{
		@Override
		protected String getName()
		{
			return "Horizontally aligned: ";
		}
		
		@Override
		protected boolean isValid()
		{
			return m_boxes.size() >= 2;
		}
	}
	
	public static class VerticallyAligned extends LayoutConstraint
	{
		@Override
		protected String getName()
		{
			return "Vertically aligned: ";
		}
		
		@Override
		protected boolean isValid()
		{
			return m_boxes.size() >= 2;
		}
	}
	
	public static class Disjoint extends LayoutConstraint
	{
		@Override
		protected String getName()
		{
			return "Disjoint: ";
		}
		
		@Override
		protected boolean isValid()
		{
			return m_boxes.size() >= 2;
		}
	}
	
	public static class Contained extends LayoutConstraint
	{
		@Override
		protected String getName()
		{
			return "Contained within: ";
		}
		
		@Override
		protected boolean isValid()
		{
			return m_boxes.size() >= 2;
		}
	}
}