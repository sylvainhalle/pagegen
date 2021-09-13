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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A geometric relationship between two or more boxes in a page.
 */
public abstract class LayoutConstraint 
{
	/**
	 * A Boolean value indicating whether the constraint is fulfilled for the
	 * boxes it considers.
	 */
	Boolean m_verdict = null;

	/**
	 * Creates a map from each box property to the set of all constraints
	 * it is involved in.
	 * @param constraints The set of constraints
	 * @return The map
	 */
	public static Map<BoxProperty,Set<LayoutConstraint>> indexProperties(Set<LayoutConstraint> constraints)
	{

		Map<BoxProperty,Set<LayoutConstraint>> constraint_index = new HashMap<BoxProperty,Set<LayoutConstraint>>();
		for (LayoutConstraint c : constraints)
		{
			for (BoxProperty bp : c.getBoxProperties(null))
			{
				Set<LayoutConstraint> set = null;
				if (constraint_index.containsKey(bp))
				{
					set = constraint_index.get(bp);
				}
				else
				{
					set = new HashSet<LayoutConstraint>();
					constraint_index.put(bp, set);
				}
				set.add(c);
			}
		}
		return constraint_index;
	}

	/**
	 * Creates a new empty layout constraint.
	 */
	public LayoutConstraint()
	{
		super();
	}

	/**
	 * Evaluates the constraint on its boxes.
	 * @return {@code true} if the constraint holds, {@code false} if it is
	 * violated
	 */
	public final boolean getVerdict()
	{
		if (m_verdict == null)
		{
			m_verdict = evaluate();
		}
		return m_verdict;
	}

	/**
	 * Determines if a box is involved in the constraint.
	 * @param b The box
	 * @return {@code true} if the box is involved in the constraint,
	 * {@code false} otherwise
	 */
	public abstract boolean involves(Box b);

	/**
	 * Gets the set of boxes involved in the constraint.
	 * @return The set of boxes
	 */
	public abstract Set<Box> getBoxes();

	/**
	 * Gets the set of box properties that are possibly impacted by another
	 * box property in the constraint.
	 * @param bp A box property that is being changed. May be null.
	 * @param g The graph of box dependencies extracted from the page
	 * @return The set of box properties
	 */
	/*@ non_null @*/ public abstract Set<BoxProperty> getBoxProperties(/*@ non_null @*/ BoxDependencyGraph g, /*@ null @*/ BoxProperty bp);

	/**
	 * Gets the set of all box properties involved in the constraint.
	 * @param g The graph of box dependencies extracted from the page.
	 * @return The set of box properties
	 */
	/*@ non_null @*/ public final Set<BoxProperty> getBoxProperties(/*@ non_null @*/ BoxDependencyGraph g)
	{
		return getBoxProperties(g, null);
	}

	/**
	 * Evaluates the constraint on its boxes.
	 * @return {@code true} if the constraint holds, {@code false} if it is
	 * violated
	 */
	protected abstract boolean evaluate();

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

	/**
	 * Layout constraint involving exactly two boxes.
	 */
	public abstract static class BinaryLayoutConstraint extends LayoutConstraint
	{
		/**
		 * The first box.
		 */
		protected Box m_box1;

		/**
		 * The second box.
		 */
		protected Box m_box2;

		/**
		 * Creates a new binary layout constraint.
		 * @param b1 The first box
		 * @param b2 The second box
		 */
		public BinaryLayoutConstraint(Box b1, Box b2)
		{
			super();
			m_box1 = b1;
			m_box2 = b2;
		}

		/**
		 * Gets the first box.
		 * @return The box
		 */
		public Box getFirstBox()
		{
			return m_box1;
		}

		/**
		 * Gets the second box.
		 * @return The box
		 */
		public Box getSecondBox()
		{
			return m_box2;
		}

		@Override
		public boolean involves(Box b)
		{
			return m_box1.equals(b) || m_box2.equals(b);
		}

		@Override
		public Set<Box> getBoxes()
		{
			Set<Box> out = new HashSet<Box>();
			out.add(m_box1);
			out.add(m_box2);
			return out;
		}

		@Override
		public int hashCode()
		{
			return m_box1.hashCode() + m_box2.hashCode();
		}

		@Override
		public boolean equals(Object o)
		{
			if (o == null || !(o instanceof BinaryLayoutConstraint))
			{
				return false;
			}
			BinaryLayoutConstraint blc = (BinaryLayoutConstraint) o;
			return m_box1.equals(blc.m_box1) && m_box2.equals(blc.m_box2);
		}

		@Override
		public String toString()
		{
			StringBuilder out = new StringBuilder();
			out.append(getName()).append(m_box1.getId()).append(",").append(m_box2.getId());
			return out.toString();
		}

		@Override
		protected boolean isValid()
		{
			return m_box1 != null && m_box2 != null && m_box1.getId() != m_box2.getId();
		}
	}

	/**
	 * Layout constraint involving an unspecified number of boxes.
	 */
	public abstract static class MultiLayoutConstraint extends LayoutConstraint
	{
		/**
		 * The set of boxes involved in the constraint.
		 */
		protected Set<Box> m_boxes;

		/**
		 * Creates a new empty multi-layout constraint.
		 */
		public MultiLayoutConstraint()
		{
			super();
			m_boxes = new HashSet<Box>();
		}

		@Override
		public boolean involves(Box b)
		{
			return m_boxes.contains(b);
		}

		@Override
		public Set<Box> getBoxes()
		{
			return m_boxes;
		}

		/**
		 * Adds a box subjected to a constraint
		 * @param b The box
		 * @return This constraint
		 */
		public MultiLayoutConstraint add(Box b)
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

		@Override
		public int hashCode()
		{
			int h = 0;
			for (Box b : m_boxes)
			{
				h += b.hashCode();
			}
			return h;
		}

		@Override
		public boolean equals(Object o)
		{
			if (o == null || !(o instanceof MultiLayoutConstraint))
			{
				return false;
			}
			MultiLayoutConstraint mlc = (MultiLayoutConstraint) o;
			if (mlc.m_boxes.size() != m_boxes.size())
			{
				return false;
			}
			for (Box b : m_boxes)
			{
				if (!mlc.m_boxes.contains(b))
				{
					return false;
				}
			}
			return true;
		}
	}

	public static class HorizontallyAligned extends MultiLayoutConstraint
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

		@Override
		public int hashCode()
		{
			return super.hashCode() + 3;
		}

		@Override
		public boolean equals(Object o)
		{
			if (o == null || !(o instanceof HorizontallyAligned))
			{
				return false;
			}
			return super.equals(o);
		}

		@Override
		public Set<BoxProperty> getBoxProperties(BoxDependencyGraph g, BoxProperty bp)
		{
			Set<BoxProperty> props = new HashSet<BoxProperty>();
			Box current = null;
			if (bp != null)
			{
				current = bp.getBox();	
			}
			for (Box b : m_boxes)
			{
				if (!b.equals(current))
				{
					props.add(BoxProperty.get(b, BoxProperty.Property.Y));
				}
			}
			return props;
		}

		@Override
		public boolean evaluate()
		{
			Box first = null;
			for (Box b : m_boxes)
			{
				if (first == null)
				{
					first = b;
					continue;
				}
				if (first.getY() != b.getY())
				{
					return false;
				}
			}
			return true;
		}
	}

	public static class VerticallyAligned extends MultiLayoutConstraint
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

		@Override
		public int hashCode()
		{
			return super.hashCode() + 5;
		}

		@Override
		public boolean equals(Object o)
		{
			if (o == null || !(o instanceof VerticallyAligned))
			{
				return false;
			}
			return super.equals(o);
		}

		@Override
		public boolean evaluate()
		{
			Box first = null;
			for (Box b : m_boxes)
			{
				if (first == null)
				{
					first = b;
					continue;
				}
				if (first.getX() != b.getX())
				{
					return false;
				}
			}
			return true;
		}

		@Override
		public Set<BoxProperty> getBoxProperties(BoxDependencyGraph g, BoxProperty bp)
		{
			Set<BoxProperty> props = new HashSet<BoxProperty>();
			Box current = null;
			if (bp != null)
			{
				current = bp.getBox();	
			}
			for (Box b : m_boxes)
			{
				if (!b.equals(current))
				{
					props.add(BoxProperty.get(b, BoxProperty.Property.X));
				}
			}
			return props;
		}
	}

	public static class Disjoint extends BinaryLayoutConstraint
	{
		public Disjoint(Box b1, Box b2) 
		{
			super(b1, b2);
		}

		@Override
		public int hashCode()
		{
			return super.hashCode() + 2;
		}

		@Override
		public boolean equals(Object o)
		{
			if (o == null || !(o instanceof Disjoint))
			{
				return false;
			}
			return super.equals(o);
		}

		@Override
		protected String getName()
		{
			return "Disjoint: ";
		}

		@Override
		public Set<BoxProperty> getBoxProperties(BoxDependencyGraph g, BoxProperty bp)
		{
			Set<BoxProperty> props = new HashSet<BoxProperty>();
			if (bp == null)
			{
				props.add(BoxProperty.get(m_box1, BoxProperty.Property.X));
				props.add(BoxProperty.get(m_box1, BoxProperty.Property.Y));
				props.add(BoxProperty.get(m_box1, BoxProperty.Property.W));
				props.add(BoxProperty.get(m_box1, BoxProperty.Property.H));
				props.add(BoxProperty.get(m_box2, BoxProperty.Property.X));
				props.add(BoxProperty.get(m_box2, BoxProperty.Property.Y));
				props.add(BoxProperty.get(m_box2, BoxProperty.Property.W));
				props.add(BoxProperty.get(m_box2, BoxProperty.Property.H));
				return props;
			}
			boolean first = m_box1.equals(bp.getBox());
			Set<BoxDependency> deps = g.getInfluences(bp);
			// Checks if the two boxes influence each other
			for (BoxDependency bd : deps)
			{
				if (first)
				{
					if (bd.getProperty().getBox().equals(m_box2))
					{
						return props;
					}
				}
				else
				{
					if (bd.getProperty().getBox().equals(m_box1))
					{
						return props;
					}
				}
			}
			switch (bp.getProperty())
			{
			case X:
			case W:
				if (first)
				{
					props.add(BoxProperty.get(m_box2, BoxProperty.Property.X));
					props.add(BoxProperty.get(m_box2, BoxProperty.Property.W));
				}
				else
				{
					props.add(BoxProperty.get(m_box1, BoxProperty.Property.X));
					props.add(BoxProperty.get(m_box1, BoxProperty.Property.W));
				}
				break;
			case Y:
			case H:
				if (first)
				{
					props.add(BoxProperty.get(m_box2, BoxProperty.Property.Y));
					props.add(BoxProperty.get(m_box2, BoxProperty.Property.H));
				}
				else
				{
					props.add(BoxProperty.get(m_box1, BoxProperty.Property.Y));
					props.add(BoxProperty.get(m_box1, BoxProperty.Property.H));
				}
				break;
			default:
				props.add(BoxProperty.get(m_box1, BoxProperty.Property.X));
				props.add(BoxProperty.get(m_box1, BoxProperty.Property.Y));
				props.add(BoxProperty.get(m_box1, BoxProperty.Property.W));
				props.add(BoxProperty.get(m_box1, BoxProperty.Property.H));
				props.add(BoxProperty.get(m_box2, BoxProperty.Property.X));
				props.add(BoxProperty.get(m_box2, BoxProperty.Property.Y));
				props.add(BoxProperty.get(m_box2, BoxProperty.Property.W));
				props.add(BoxProperty.get(m_box2, BoxProperty.Property.H));
			}
			return props;
		}

		@Override
		public boolean evaluate()
		{
			float b1_x = m_box1.getX();
			float b1_y = m_box1.getY();
			float b1_w = m_box1.getWidth();
			float b1_h = m_box1.getHeight();
			float b2_x = m_box2.getX();
			float b2_y = m_box2.getY();
			float b2_w = m_box2.getWidth();
			float b2_h = m_box2.getHeight();
			return b1_y + b1_h <= b2_y || b2_y + b2_h <= b1_y || b1_x + b1_w <= b2_x || b2_x + b2_w <= b1_x;			
		}

		/**
		 * Parses a box tree and generates all the disjointness constraints between
		 * all the children of a parent box
		 * @param b The parent box
		 * @return The set of constraints
		 */
		public static Set<LayoutConstraint> addContainmentConstraints(Box b)
		{
			Set<LayoutConstraint> set = new HashSet<LayoutConstraint>();
			addDisjointnessConstraints(b, set);
			return set;
		}

		/**
		 * Recursively parses a box tree and generates all the disjointness
		 * constraints between all the children of a parent box
		 * @param b The parent box
		 * @param set The set of constraints. Constraints will be added into
		 * that set
		 */
		protected static void addDisjointnessConstraints(Box b, Set<LayoutConstraint> set)
		{
			List<Box> children = b.getChildren();
			for (int i = 0; i < children.size(); i++)
			{
				for (int j = i + 1; j < children.size(); j++)
				{
					set.add(new Disjoint(children.get(i), children.get(j)));
				}
				addDisjointnessConstraints(children.get(i), set);
			}
		}
	}

	public static class Contained extends BinaryLayoutConstraint
	{
		public Contained(Box parent, Box child) 
		{
			super(parent, child);
		}

		@Override
		public int hashCode()
		{
			return super.hashCode() + 4;
		}

		@Override
		public boolean equals(Object o)
		{
			if (o == null || !(o instanceof Contained))
			{
				return false;
			}
			return super.equals(o);
		}

		@Override
		protected String getName()
		{
			return "Contained: ";
		}

		@Override
		public String toString()
		{
			return m_box2.getId() + " within " + m_box1.getId();
		}

		@Override
		public Set<BoxProperty> getBoxProperties(BoxDependencyGraph g, BoxProperty bp)
		{
			Set<BoxProperty> props = new HashSet<BoxProperty>();
			if (bp == null)
			{
				props.add(BoxProperty.get(m_box1, BoxProperty.Property.X));
				props.add(BoxProperty.get(m_box1, BoxProperty.Property.Y));
				props.add(BoxProperty.get(m_box1, BoxProperty.Property.W));
				props.add(BoxProperty.get(m_box1, BoxProperty.Property.H));
				props.add(BoxProperty.get(m_box2, BoxProperty.Property.X));
				props.add(BoxProperty.get(m_box2, BoxProperty.Property.Y));
				props.add(BoxProperty.get(m_box2, BoxProperty.Property.W));
				props.add(BoxProperty.get(m_box2, BoxProperty.Property.H));
				return props;
			}
			boolean first = m_box1.equals(bp.getBox());
			Set<BoxDependency> deps = g.getInfluences(bp);
			// Checks if the two boxes influence each other
			for (BoxDependency bd : deps)
			{
				if (first)
				{
					if (bd.getProperty().getBox().equals(m_box2))
					{
						return props;
					}
				}
				else
				{
					if (bd.getProperty().getBox().equals(m_box1))
					{
						return props;
					}
				}
			}
			switch (bp.getProperty())
			{
			case X:
				if (!first)
				{
					props.add(BoxProperty.get(m_box2, BoxProperty.Property.W));
				}
				break;
			case W:
				if (first)
				{
					props.add(BoxProperty.get(m_box2, BoxProperty.Property.W));
				}
				else
				{
					props.add(BoxProperty.get(m_box1, BoxProperty.Property.X));
					props.add(BoxProperty.get(m_box1, BoxProperty.Property.W));
				}
				break;
			case Y:
				if (!first)
				{
					props.add(BoxProperty.get(m_box2, BoxProperty.Property.H));
				}
				break;
			case H:
				if (first)
				{
					props.add(BoxProperty.get(m_box2, BoxProperty.Property.Y));
					props.add(BoxProperty.get(m_box2, BoxProperty.Property.H));
				}
				else
				{
					props.add(BoxProperty.get(m_box1, BoxProperty.Property.H));
				}
				break;
			default:
				props.add(BoxProperty.get(m_box1, BoxProperty.Property.X));
				props.add(BoxProperty.get(m_box1, BoxProperty.Property.Y));
				props.add(BoxProperty.get(m_box1, BoxProperty.Property.W));
				props.add(BoxProperty.get(m_box1, BoxProperty.Property.H));
				props.add(BoxProperty.get(m_box2, BoxProperty.Property.X));
				props.add(BoxProperty.get(m_box2, BoxProperty.Property.Y));
				props.add(BoxProperty.get(m_box2, BoxProperty.Property.W));
				props.add(BoxProperty.get(m_box2, BoxProperty.Property.H));
			}
			return props;
		}

		@Override
		public boolean evaluate()
		{
			float b1_x = m_box1.getX();
			float b1_y = m_box1.getY();
			float b1_w = m_box1.getWidth();
			float b1_h = m_box1.getHeight();
			float b2_x = m_box2.getX();
			float b2_y = m_box2.getY();
			float b2_w = m_box2.getWidth();
			float b2_h = m_box2.getHeight();
			return b1_y <= b2_y && b1_y + b1_h >= b2_y + b2_h && b1_x <= b2_x && b1_x + b1_w >= b2_x + b2_w;			
		}

		/**
		 * Parses a box tree and generates all the containment constraints between
		 * a parent box and its children
		 * @param b The parent box
		 * @return The set of constraints
		 */
		public static Set<LayoutConstraint> addContainmentConstraints(Box b)
		{
			Set<LayoutConstraint> set = new HashSet<LayoutConstraint>();
			addContainmentConstraints(b, set);
			return set;
		}

		/**
		 * Recursively arses a box tree and generates all the containment
		 * constraints between a parent box and its children
		 * @param b The parent box
		 * @param set The set of constraints. Constraints will be added into
		 * that set
		 */
		protected static void addContainmentConstraints(Box b, Set<LayoutConstraint> set)
		{
			for (Box child : b.getChildren())
			{
				set.add(new Contained(b, child));
				addContainmentConstraints(child, set);
			}
		}
	}
}