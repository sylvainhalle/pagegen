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

import java.io.PrintStream;

import ca.uqac.lif.synthia.Picker;

public abstract class HtmlRenderer extends BoxRenderer
{
	/*@ non_null @*/ protected Picker<String> m_color;
	
	public HtmlRenderer(/*@ non_null @*/ Picker<String> color)
	{
		super();
		m_color = color;
	}
	
	@Override
	public void render(/*@ non_null @*/ PrintStream ps, /*@ non_null @*/ Box b)
	{
		ps.println("<!DOCTYPE html>");
		ps.println("<html>");
		ps.println("<head>");
		ps.println("<style type=\"text/css\">");
		ps.println(".altered {");
		ps.println("  background-color: black !important;");
		ps.println("  outline: red dashed 1px;");
		ps.println("}");
		ps.println(".box {");
		ps.println("  position: absolute;");
		ps.println("}");
		ps.println(".box:hover {");
		ps.println("  opacity: 80%;");
		ps.println("  outline: solid 1px;");
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
	
	protected abstract void toHtml(/*@ non_null @*/ PrintStream ps, /*@ non_null @*/ Box b, String indent);
}
