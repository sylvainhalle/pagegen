A random DOM tree generator
===========================

![An example of a generated page](example.png?raw=true)

This project generates random [Document Object Model](https://en.wikipedia.org/wiki/Document_Object_Model)
trees. Each DOM document is a set of nested boxes, with randomly selected height and width. The picture above shows an example of such a randomly generated document.

The document can be exported to various formats:

- As an HTML page, containing a set of nested &lt;div&gt; elements, absolutely positioned with respect to the viewport
- As a [DOT](https://graphviz.org) file that Graphviz can display as a tree
- As an OPL input file for the IBM [CPLEX](https://www.ibm.com/products/ilog-cplex-optimization-studio) constraint solver

Many parameters of the generator can be configured: minimum/maximum depth, node degree. In addition, some elements can be purposefully **misaligned**, made to **overlap** or to **overflow** their parent container; this can be used e.g. to test software that detect or correct such misalignments (such as [Cornipickle](https://github.com/liflab/cornipickle)). In the picture above, misaligned elements are represented in black with a dashed red border.

You can use a [precompiled release](releases) or compile the project yourself (see below).

Command line options
--------------------

```
Usage java -jar pagen.jar [options]

-q --quiet          Don't print generation stats to stderr
-s --seed x         Initialize RNG with seed s
-t --type x         Output file of type x (html, dot, opl)
-d --min-depth x    Set minimum document depth to x
-D --max-depth x    Set maximum document depth to x
-w --overflow x     Set overflow probability to p (in [0,1])
-g --degree x       Set degree to Poisson distribution with parameter x
-l --overlap x      Set overlap probability to p (in [0,1])
-m --misalign x     Set misalignment probability to p (in [0,1])
-f --flat           Output page as a flat set of divs
-? --help           Show command line usage
-o --output file    Output to file
```

Compiling and Installing
------------------------

First make sure you have the following installed:

- The Java Development Kit (JDK) to compile. The generator was developed and
  tested on version 6 of the JDK, but it is probably safe to use any
  later version.
- [Ant](http://ant.apache.org) to automate the compilation and build process

Download the sources for the generator from
[GitHub](http://github.com/sylvainhalle/pagegen) or clone the repository
using Git:

    git clone git@github.com:sylvainhalle/pagegen.git

### Compiling

Compile the sources by simply typing:

    ant

This will produce a file called `pagen.jar` in the folder. This
file is runnable and stand-alone, or can be used as a library, so it can be
moved around to the location of your choice.

### Dependencies

This project depends on:

- [Synthia](https://github.com/liflab/synthia), a data structure generator

Dependencies can be individually downloaded by typing:

    ant download-deps

About the author
----------------

The generator was written by [Sylvain Hallé](https://leduotang.ca/sylvain),
full professor at [Université du Québec à
Chicoutimi](http://www.uqac.ca), Canada.

<!-- :wrap=soft: -->