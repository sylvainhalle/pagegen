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

public class BoxProperty
{
	public static enum Property {X, Y, W, H}
	
	/**
	 * The box whose property is being considered.
	 */
	protected Box m_box;
	
	/**
	 * The property of the box.
	 */
	protected Property m_property;
	
	/**
	 * Creates a new box property.
	 * @param b The box whose property is being considered
	 * @param p The property of the box
	 */
	public BoxProperty(Box b, Property p)
	{
		super();
		m_box = b;
		m_property = p;
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
}
