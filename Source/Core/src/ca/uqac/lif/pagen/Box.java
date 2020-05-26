package ca.uqac.lif.pagen;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Box 
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
	
	/*@ non_null @*/ protected final List<Box> m_children;
	
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
	
	public List<Box> getChildren()
	{
		return m_children;
	}
	
	public void addChild(/*@ non_null @*/ Box r)
	{
		m_children.add(r);
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
}
