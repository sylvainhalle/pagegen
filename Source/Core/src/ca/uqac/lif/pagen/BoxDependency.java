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
	 * The box whose property is modified.
	 */
	protected BoxProperty m_from;
	
	/**
	 * The box whose property is influenced by the first box.
	 */
	protected BoxProperty m_to;
	
	/**
	 * Creates a new box dependence.
	 * @param from The box whose property is modified
	 * @param to The box whose property is influenced by the first box
	 */
	public BoxDependency(BoxProperty from, BoxProperty to)
	{
		super();
		m_from = from;
		m_to = to;
	}
	
	@Override
	public String toString()
	{
		return m_from + "->" + m_to; 
	}
	
	@Override
	public int hashCode()
	{
		return m_from.hashCode() + m_to.hashCode();
	}
	
	@Override
	public boolean equals(Object o)
	{
		if (o == null || !(o instanceof BoxDependency))
		{
			return false;
		}
		BoxDependency bd = (BoxDependency) o;
		return bd.m_from.equals(m_from) && bd.m_to.equals(m_to);
	}
}
