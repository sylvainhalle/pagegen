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

public class BoxDependency
{
	/**
	 * The box property.
	 */
	/*@ non_null @*/ protected BoxProperty m_property;
	
	/**
	 * The box property that influences the other box property.
	 */
	/*@ non_null @*/ protected BoxProperty m_influencedBy;
	
	/**
	 * Creates a new box dependence.
	 * @param property The box whose property is modified
	 * @param influenced_by The box whose property is influenced by the first box
	 */
	public BoxDependency(/*@ non_null @*/ BoxProperty property, /*@ non_null @*/ BoxProperty influenced_by)
	{
		super();
		m_property = property;
		m_influencedBy = influenced_by;
	}
	
	/*@ pure non_null @*/ public BoxProperty getProperty()
	{
		return m_property;
	}
	
	/*@ pure non_null @*/ public BoxProperty getInfluencedBy()
	{
		return m_influencedBy;
	}
	
	@Override
	public String toString()
	{
		return m_property + "->" + m_influencedBy; 
	}
	
	@Override
	public int hashCode()
	{
		return m_property.hashCode() + m_influencedBy.hashCode();
	}
	
	@Override
	public boolean equals(Object o)
	{
		if (o == null || !(o instanceof BoxDependency))
		{
			return false;
		}
		BoxDependency bd = (BoxDependency) o;
		return bd.m_property.equals(m_property) && bd.m_influencedBy.equals(m_influencedBy);
	}
}
