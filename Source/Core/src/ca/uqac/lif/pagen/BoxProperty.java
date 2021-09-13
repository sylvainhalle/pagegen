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

import java.util.HashMap;
import java.util.Map;

public class BoxProperty implements Comparable<BoxProperty>
{
	/**
	 * The possible properties that can be modeled about a box.
	 */
	public static enum Property {X, Y, W, H, DX, DY, DW, DH}
	
	/**
	 * The box whose property is being considered.
	 */
	protected Box m_box;
	
	/**
	 * The property of the box.
	 */
	protected Property m_property;
	
	/**
	 * A map used to store references to already instantiated box properties. Its
	 * goal is to avoid creating multiple instances of {@link BoxProperty}
	 * objects referring to the same box and the same property.
	 */
	protected static final Map<BoxProperty,BoxProperty> s_propertyPool = new HashMap<BoxProperty,BoxProperty>();
	
	/**
	 * Gets an instance of a box property.
	 * @param b The box whose property is being considered
	 * @param p The property of the box
	 */
	public static BoxProperty get(Box b, Property p)
	{
		BoxProperty bp = new BoxProperty(b, p);
		if (s_propertyPool.containsKey(bp))
		{
			bp = s_propertyPool.get(bp);
		}
		else
		{
			s_propertyPool.put(bp, bp);
		}
		return bp;
	}
	
	/**
	 * Creates a new box property.
	 * @param b The box whose property is being considered
	 * @param p The property of the box
	 */
	protected BoxProperty(Box b, Property p)
	{
		super();
		m_box = b;
		m_property = p;
	}
	
	/*@ pure non_null @*/ public BoxProperty getDelta()
	{
		switch (m_property)
		{
		case X:
			return get(m_box, Property.DX);
		case Y:
			return get(m_box, Property.DY);
		case H:
			return get(m_box, Property.DH);
		case W:
			return get(m_box, Property.DW);
		default:
			return this;
		}
	}
	
	/*@ pure non_null @*/ public BoxProperty getAbsolute()
	{
		switch (m_property)
		{
		case DX:
			return get(m_box, Property.X);
		case DY:
			return get(m_box, Property.Y);
		case DH:
			return get(m_box, Property.H);
		case DW:
			return get(m_box, Property.W);
		default:
			return this;
		}
	}
	
	/*@ pure non_null @*/ public Box getBox()
	{
		return m_box;
	}
	
	/*@ pure non_null @*/ public Property getProperty()
	{
		return m_property;
	}
	
	@Override
	public String toString()
	{
		return m_box.getId() + "_" + m_property;
	}
	
	@Override
	public int hashCode()
	{
		return m_box.getId();
	}
	
	@Override
	public boolean equals(Object o)
	{
		if (o == null || !(o instanceof BoxProperty))
		{
			return false;
		}
		BoxProperty bp = (BoxProperty) o;
		return bp.m_box.equals(m_box) && bp.m_property == m_property;
	}

	@Override
	public int compareTo(BoxProperty bp)
	{
		int comp = m_box.compareTo(bp.getBox());
		if (comp != 0)
		{
			return comp;
		}
		if (m_property == bp.m_property)
		{
			return 0;
		}
		switch (m_property)
		{
		case DX:
			return -1;
		case DY:
			return bp.m_property == Property.DX ? 1 : -1;
		case DH:
			return (bp.m_property == Property.DX || bp.m_property == Property.DY) ? 1 : -1;
		case DW:
			return (bp.m_property == Property.DX || bp.m_property == Property.DY
				|| bp.m_property == Property.DH) ? 1 : -1;
		case X:
			return (bp.m_property == Property.DX || bp.m_property == Property.DY
				|| bp.m_property == Property.DH || bp.m_property == Property.DW) ? 1 : -1;
		case Y:
			return (bp.m_property == Property.DX || bp.m_property == Property.DY
				|| bp.m_property == Property.DH || bp.m_property == Property.DW
				|| bp.m_property == Property.X) ? 1 : -1;
		case W:
			return (bp.m_property == Property.DX || bp.m_property == Property.DY
				|| bp.m_property == Property.DH || bp.m_property == Property.DW
				|| bp.m_property == Property.X || bp.m_property == Property.Y) ? 1 : -1;
			default:
				return 1;
		}
	}
	
}
