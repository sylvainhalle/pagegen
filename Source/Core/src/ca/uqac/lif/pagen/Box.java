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
package ca.uqac.lif.pagen;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Box implements Comparable<Box>
{
	/**
	 * A counter to give IDs to boxes
	 */
	private static int s_idCount = 0;
	
	/**
	 * A unique ID for this box
	 */
	protected int m_id;
	
	/**
	 * The <i>x</i> position of the top-left corner of this box
	 */
	protected float m_x;
	
	/**
	 * The <i>y</i> position of the top-left corner of this box
	 */
	protected float m_y;
	
	/**
	 * The width of this box
	 */
	protected float m_width;
	
	/**
	 * The height of this box
	 */
	protected float m_height;
	
	/**
	 * The padding to be applied to the inside of this box. Padding applies to
	 * all sides.
	 */
	protected float m_padding;
	
	/**
	 * A tag that can be associated to elements to signal they have been "altered"
	 */
	protected boolean m_altered = false;
	
	/**
	 * The list of children for this node
	 */
	/*@ non_null @*/ protected final List<Box> m_children;
	
	/**
	 * The parent of a given box
	 */
	protected Box m_parent;
	
	/**
	 * Resets the ID counter used for boxes to zero. This is useful when
	 * generating multiple trees, each with their own numbering. 
	 */
	public static void resetIds()
	{
		s_idCount = 0;
	}
	
	/**
	 * Creates a new box with given position and dimensions.
	 * @param x The <i>x</i> position of the top-left corner of this box
	 * @param y The <i>y</i> position of the top-left corner of this box
	 * @param w The width of this box
	 * @param h The height of this box
	 */
	public Box(float x, float y, float w, float h)
	{
		super();
		m_id = s_idCount++;
		m_x = x;
		m_y = y;
		m_width = w;
		m_height = h;
		m_children = new ArrayList<Box>();
		m_padding = 0;
	}
	
	/**
	 * Creates a new box with given position and dimensions.
	 * @param x The <i>x</i> position of the top-left corner of this box
	 * @param y The <i>y</i> position of the top-left corner of this box
	 * @param w The width of this box
	 * @param h The height of this box
	 */
	private Box(int id, float x, float y, float w, float h)
	{
		super();
		m_id = id;
		m_x = x;
		m_y = y;
		m_width = w;
		m_height = h;
		m_children = new ArrayList<Box>();
		m_padding = 0;
	}

	/**
	 * Marks a box as being "altered"
	 */
	public void alter()
	{
		m_altered = true;
	}
	
	/**
	 * Checks if a box is "altered"
	 * @return <tt>true</tt> if the box is altered, <tt>false</tt> otherwise
	 */
	public boolean isAltered()
	{
		return m_altered;
	}
	
	/**
	 * Gets the unique ID given to this box
	 * @return The ID
	 */
	public int getId()
	{
		return m_id;
	}
	
	/**
	 * Gets the size of this tree.
	 * @return The total number of nodes in the tree
	 */
	public int getSize()
	{
		int count = 1;
		for (Box b_c : m_children)
		{
			count += b_c.getSize();
		}
		return count;
	}
	
	public void setPadding(float p)
	{
		m_padding = p;
	}
	
	public void shiftX(float s)
	{
		m_x += s;
		for (Box b : m_children)
		{
			b.shiftX(s);
		}
	}
	
	public void shiftY(float s)
	{
		m_y += s;
		for (Box b : m_children)
		{
			b.shiftY(s);
		}
	}
	
	/**
	 * Gets the x position of this box
	 * @return The x position
	 */
	public float getX()
	{
		return m_x;
	}
	
	/**
	 * Gets the y position of this box
	 * @return The y position
	 */
	public float getY()
	{
		return m_y;
	}
	
	/**
	 * Gets the width of this box
	 * @return The width
	 */
	public float getWidth()
	{
		return m_width;
	}
	
	/**
	 * Gets the height of this box
	 * @return The height
	 */
	public float getHeight()
	{
		return m_height;
	}
	
	/**
	 * Gets the padding associated to this box
	 * @return The padding
	 */
	public float getPadding()
	{
		return m_padding;
	}
	
	/**
	 * Sets the width of the element
	 * @param w The width
	 */
	public void setWidth(float w)
	{
		m_width = w;
	}
	
	/**
	 * Sets the height of the element
	 * @param w The height
	 */
	public void setHeight(float h)
	{
		m_height = h;
	}
	
	/**
	 * Gets the maximum depth of the tree
	 * @return The depth
	 */
	public int getDepth()
	{
		int max_depth = 0;
		for (Box b : m_children)
		{
			max_depth = Math.max(max_depth, b.getDepth());
		}
		return max_depth + 1;
	}
	
	/*@ non_null @*/ public List<Box> getChildren()
	{
		return m_children;
	}
	
	/*@ null @*/ public Box getParent()
	{
		return m_parent;
	}
	
	public void setParent(Box parent)
	{
		m_parent = parent;
	}
	
	public void addChild(/*@ non_null @*/ Box r)
	{
		m_children.add(r);
		r.setParent(this);
		m_width = Math.max(m_width, Math.max(0, r.getX() + r.getWidth()));
		m_height = Math.max(m_height, Math.max(0, r.getY() + r.getHeight()));
	}
	
	public void addChildren(/*@ non_null @*/ Collection<Box> c)
	{
		for (Box b : c)
		{
			addChild(b);
		}
	}
	
	@Override
	public String toString()
	{
		StringBuilder out = new StringBuilder();
		toString(out, "");
		return out.toString();
	}
	
	protected void toString(StringBuilder out, String indent)
	{
		out.append(indent).append("id: ").append(m_id).append(", x: ").append(m_x).append(", y: ").append(m_y).append(", w: ").append(m_width).append(", h: ").append(m_height).append("\n");
		indent += " ";
		for (Box b : m_children)
		{
			b.toString(out, indent);
		}
	}
	
	public Map<Integer,Box> flatten()
	{
		Map<Integer,Box> map = new HashMap<Integer,Box>();
		flatten(map);
		return map;
	}
	
	protected void flatten(Map<Integer,Box> map)
	{
		map.put(m_id, this);
		for (Box b : m_children)
		{
			b.flatten(map);
		}
	}
	
	@Override
	public int hashCode()
	{
		return m_id;
	}
	
	@Override
	public boolean equals(Object o)
	{
		if (o == null || !(o instanceof Box))
		{
			return false;
		}
		return m_id == ((Box) o).getId();
	}

	@Override
	public int compareTo(Box b)
	{
		return m_id - b.m_id;
	}
	
	public static Box trim(Box b)
	{
		Set<Integer> to_include = new HashSet<Integer>();
		visit(b, to_include);
		return copy(b, to_include);
	}

	protected static void visit(Box current, Set<Integer> to_include)
	{
		if (current.isAltered())
		{
			propagate(current, to_include);
		}
		for (Box child : current.getChildren())
		{
			visit(child, to_include);
		}
	}

	protected static void propagate(Box current, Set<Integer> to_include)
	{
		Box parent = current.getParent();
		if (parent == null)
		{
			to_include.add(current.getId());
			return;
		}
		for (Box sibling : parent.getChildren())
		{
			to_include.add(sibling.getId());
		}
		propagate(parent, to_include);
	}

	protected static Box copy(Box current, Set<Integer> to_include) 
	{
		if (!to_include.contains(current.getId()))
		{
			return null;
		}
		Box current_copy = new Box(current.getId(), current.getX(), current.getY(), current.getWidth(), current.getHeight());
		current_copy.setPadding(current.getPadding());
		if (current.isAltered())
		{
			current_copy.alter();
		}
		for (Box child : current.getChildren())
		{
			Box child_copy = copy(child, to_include);
			if (child_copy != null)
			{
				current_copy.addChild(child_copy);
			}
		}
		return current_copy;
	}
}
