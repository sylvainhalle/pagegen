package ca.uqac.lif.pagen;

import java.util.List;

public class HorizontalFlowLayout extends FlowLayout
{
	/**
	 * Creates a new horizontal flow layout manager
	 * @param max_elements The maximum number of elements to 
	 * flow before moving to the next line. Set to 0 to flow all elements
	 * on the same line
	 */
	public HorizontalFlowLayout(int max_elements)
	{
		super(max_elements);
	}
	
	/**
	 * Creates a new horizontal flow layout manager
	 */
	public HorizontalFlowLayout()
	{
		this(0);
	}
	
	@Override
	public void arrange(Box parent, List<Box> children)
	{
		if (children.isEmpty())
		{
			return;
		}
		float p_padding = parent.getPadding();
		float x = p_padding, y = p_padding, max_y = 0;
		float bounding_w = 0, bounding_h = 0;
		int n = 0;
		for (Box b : children)
		{
			parent.addChild(b);
			b.shiftX(x);
			b.shiftY(y);
			bounding_w = Math.max(bounding_w, x + b.getWidth());
			x += b.getWidth() + m_spacing;
			bounding_h = Math.max(bounding_h, y + b.getHeight());
			max_y = Math.max(max_y, b.getHeight());
			n++;
			if (m_maxElements > 0 && n == m_maxElements)
			{
				n = 0;
				x = p_padding;
				y += max_y + m_spacing;
				max_y = 0;
			}
		}
		parent.setWidth(bounding_w + p_padding);
		parent.setHeight(bounding_h + p_padding);
	}
}
