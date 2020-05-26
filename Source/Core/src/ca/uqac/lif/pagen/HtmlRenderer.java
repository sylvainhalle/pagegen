package ca.uqac.lif.pagen;

import java.io.PrintStream;

import ca.uqac.lif.synthia.Picker;

public class HtmlRenderer
{
	/*@ non_null @*/ protected Picker<String> m_color;
	
	public HtmlRenderer(/*@ non_null @*/ Picker<String> color)
	{
		super();
		m_color = color;
	}
	
	public void toHtml(/*@ non_null @*/ PrintStream ps, /*@ non_null @*/ Box b)
	{
		ps.println("<!DOCTYPE html>");
		ps.println("<html>");
		ps.println("<head>");
		ps.println("<style type=\"text/css\">");
		ps.println(".box {");
		ps.println("  position: absolute;");
		ps.println("}");
		ps.println(".box:hover {");
		ps.println("  opacity: 80%;");
		ps.println("  border: solid 1px;");
		ps.println("  margin: -1px;");
		ps.println("}");
		ps.println("</style>");
		ps.println("</head>");
		ps.println("<body>");
		ps.println("<div style=\"position:absolute;left:0px;top:0px\">");
		toHtml(ps, b, "");
		ps.println("</div>");
		ps.println("</body>");
		ps.println("</html>");
	}
	
	public void toHtml(/*@ non_null @*/ PrintStream ps, /*@ non_null @*/ Box b, String indent)
	{
		ps.print(indent);
		ps.print("<div class=\"box\" style=\"left:");
		ps.print(b.getX());
		ps.print("px;top:");
		ps.print(b.getY());
		ps.print("px;width:");
		ps.print(b.getWidth());
		ps.print("px;height:");
		ps.print(b.getHeight());
		ps.print("px;background-color:");
		ps.print(m_color.pick());
		ps.println("\">");
		ps.println("</div>");
		String new_indent = indent + " ";
		for (Box b_c : b.getChildren())
		{
			toHtml(ps, b_c, new_indent);
		}
		ps.print(indent);		
	}
}
