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

import static org.junit.Assert.*;

import java.util.Set;

import org.junit.Test;

import ca.uqac.lif.pagen.BoxProperty.Property;
import ca.uqac.lif.pagen.LayoutConstraint.HorizontallyAligned;
import ca.uqac.lif.pagen.LayoutConstraint.VerticallyAligned;

public class LayoutConstraintTest
{
	@Test
	public void testVertical1()
	{
		Box B = new Box(10, 8, 30, 30);
		Box C = new Box(50, 8, 20, 20);
		LayoutConstraint va = new VerticallyAligned().add(B).add(C);
		assertFalse(va.getVerdict());
		BoxDependencyGraph g = new BoxDependencyGraph();
		Set<BoxProperty> set = va.getBoxProperties(g, BoxProperty.get(B, Property.X));
		assertEquals(1, set.size());
	}
	
	@Test
	public void testHorizontal1()
	{
		Box B = new Box(10, 8, 30, 30);
		Box C = new Box(50, 8, 20, 20);
		LayoutConstraint va = new HorizontallyAligned().add(B).add(C);
		assertTrue(va.getVerdict());
		BoxDependencyGraph g = new BoxDependencyGraph();
		Set<BoxProperty> set = va.getBoxProperties(g, BoxProperty.get(B, Property.Y));
		assertEquals(1, set.size());
	}
}
