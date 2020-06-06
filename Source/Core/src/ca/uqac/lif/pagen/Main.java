package ca.uqac.lif.pagen;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import ca.uqac.lif.pagen.CliParser.Argument;
import ca.uqac.lif.pagen.CliParser.ArgumentMap;
import ca.uqac.lif.synthia.Picker;
import ca.uqac.lif.synthia.random.RandomIntervalFloat;
import ca.uqac.lif.synthia.util.Constant;
import ca.uqac.lif.synthia.util.ElementPicker;
import ca.uqac.lif.synthia.random.PoissonInteger;
import ca.uqac.lif.synthia.random.RandomBoolean;
import ca.uqac.lif.synthia.random.RandomFloat;
import ca.uqac.lif.synthia.random.RandomInteger;

public class Main 
{
	public static void main(String[] args) throws FileNotFoundException 
	{
		// Program defaults
		int seed = -1;
		int min_depth = 4;
		int max_depth = 5;
		float p_degree = 2.2f;
		float p_misalignment = 0.1f;
		String type = "html";
		boolean quiet = false;

		// Override by CLI parameters
		CliParser parser = setupParser();
		ArgumentMap arg_map = parser.parse(args);
		if (arg_map == null || arg_map.containsKey("help"))
		{
			parser.printHelp("Random DOM tree generator", System.out);
			System.exit(1);
		}
		if (arg_map.hasOption("min-depth"))
		{
			min_depth = Integer.parseInt(arg_map.get("min_depth").trim());
		}
		if (arg_map.hasOption("max-depth"))
		{
			max_depth = Integer.parseInt(arg_map.get("max_depth").trim());
		}
		if (arg_map.hasOption("degree"))
		{
			p_degree = Float.parseFloat(arg_map.get("degree").trim());
		}
		if (arg_map.hasOption("seed"))
		{
			seed = Integer.parseInt(arg_map.get("seed").trim());
		}
		if (arg_map.hasOption("misalign"))
		{
			p_misalignment = Float.parseFloat(arg_map.get("misalign").trim());
		}
		if (arg_map.hasOption("type"))
		{
			type = arg_map.get("type");
		}

		// Initialize RNGs and seed
		RandomInteger depth = new RandomInteger(min_depth, max_depth); // 6-22
		PoissonInteger degree = new PoissonInteger(p_degree);
		RandomIntervalFloat width = new RandomIntervalFloat(5, 20);
		RandomIntervalFloat height = new RandomIntervalFloat(5, 10);
		RandomFloat float_source = new RandomFloat();
		RandomInteger row_size = new RandomInteger(0, 10);
		RandomInteger column_size = new RandomInteger(0, 10);
		RandomBoolean misalignment = new RandomBoolean(p_misalignment);
		RandomInteger misalignment_shift = new RandomInteger(2, 10);
		ElementPicker<LayoutManager> layout = new ElementPicker<LayoutManager>(float_source);
		Picker<String> color = new ColorPicker(new RandomInteger(128, 255));
		if (seed >= 0)
		{
			depth.setSeed(seed);
			degree.setSeed(seed + 1);
			float_source.setSeed(seed + 2);
			width.setSeed(seed + 3);
			height.setSeed(seed + 4);
			row_size.setSeed(seed + 5);
			column_size.setSeed(seed + 6);
			misalignment_shift.setSeed(seed + 7);
			misalignment.setSeed(seed + 8);
		}

		// Setup box picker
		HorizontalFlowLayout hfl_1 = new HorizontalFlowLayout();
		hfl_1.setAlignmentFault(misalignment, misalignment_shift);
		HorizontalFlowLayout hfl_2 = new HorizontalFlowLayout(row_size);
		hfl_2.setAlignmentFault(misalignment, misalignment_shift);
		VerticalFlowLayout vfl_1 = new VerticalFlowLayout(column_size);
		vfl_1.setAlignmentFault(misalignment, misalignment_shift);
		layout.add(hfl_1, 0.2);
		layout.add(hfl_2, 0.4);
		layout.add(vfl_1, 0.4);
		RandomBoxPicker box_picker = new RandomBoxPicker(degree, depth, layout, width, height);
		Box b = box_picker.pick();

		BoxRenderer renderer = null;
		if (type.compareToIgnoreCase("html") == 0)
		{
			renderer = new HtmlRenderer(color);
		}
		else if (type.compareToIgnoreCase("opl") == 0)
		{
			renderer = new OplRenderer(hfl_1.getConstraints(), hfl_2.getConstraints(), vfl_1.getConstraints());
		}
		else if (type.compareToIgnoreCase("opl") == 0)
		{
			renderer = new OplRenderer(hfl_1.getConstraints(), hfl_2.getConstraints(), vfl_1.getConstraints());
		}
		else if (type.compareToIgnoreCase("dot") == 0)
		{
			renderer = new DotRenderer();
		}
		if (!quiet)
		{
			System.err.println("Tree size:                " + b.getSize());
			System.err.println("Horizontal misalignments: " + (hfl_1.getMisalignmentCount() + hfl_2.getMisalignmentCount()));
			System.err.println("Vertical misalignments:   " + (vfl_1.getMisalignmentCount()));
		}
	}

	public static void renderToFile(Box b, BoxRenderer r, String filename) throws FileNotFoundException
	{
		PrintStream ps = new PrintStream(new FileOutputStream(new File(filename)));
		r.render(ps, b);
		ps.close();
	}

	protected static CliParser setupParser()
	{
		CliParser parser = new CliParser();
		parser.addArgument(new Argument().withLongName("type").withShortName("t").withDescription("Output file of type t (html, dot, opl)"));
		parser.addArgument(new Argument().withLongName("seed").withShortName("s").withArgument("x").withDescription("Initialize RNG with seed s"));
		parser.addArgument(new Argument().withLongName("misalign").withShortName("m").withArgument("x").withDescription("Set misalignment probability to p (in [0,1])"));
		parser.addArgument(new Argument().withLongName("min-depth").withShortName("d").withArgument("x").withDescription("Set minimum document depth to x"));
		parser.addArgument(new Argument().withLongName("max-depth").withShortName("D").withArgument("x").withDescription("Set maximum document depth to x"));
		parser.addArgument(new Argument().withLongName("degree").withShortName("g").withArgument("x").withDescription("Set degree to Poisson distribution with parameter x"));
		parser.addArgument(new Argument().withLongName("quiet").withShortName("q").withDescription("Don't print generation stats to stderr"));
		parser.addArgument(new Argument().withLongName("help").withShortName("?").withDescription("Show command line usage"));
		return parser;
	}

}
