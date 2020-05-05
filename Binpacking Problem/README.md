## Binpacking

This source lays down the infrastructure to solve bin-packing problems in a
variety of different manners.  Currently, we have implemented the first-fit
descending approximation as well as an old and an improved branch-and-bound
implementation.

### How to run

To run this project, the user needs a jar file.  You can download the latest
release with this command:

	wget https://github.com/ajwock/Binpacking/releases/download/beta-v1.0/Binpacking.jar

From there, the program can be run like this:

	java -jar Binpacking.jar filename [-q|v] [--silent|runtime]
									[-simplebnb|-fastbnb|-ffapprox]
									
This program generates some solution to a binpacking instance contained in 
the file specified as filename.  The type of algorithm used to produce a
solution can be specified using (default) -simplebnb, -fastbnb, or -ffapprox.

REQUIRED:

	filename	Name of the file containing the binpacking instance.

OPTIONS:

	-q			Suppress solution output.

	-v			Show branching tree info if appropriate.

	--silent	Suppress all output.

	--runtime	Suppress output except for program runtime.

	-simplebnb	Use old branch and bound algorithm.

	-fastbnb	Use new, faster branch and bound algorithm.

	-ffapprox	Use first-fit descending approximation.

### Other tools

We used a couple of other tools to generate instances and performance data.

Instance Generator
Usage:

	python testfiles/generateInstance.py output capacityPercentage numberOfItems

Randomly generates an instance with numberOfItems number of items and a capacity
equal to numberOfItems * capacityPercentage / 100.  capacityPercentage was designed
this way to facilitate data generation with differenct characteristics.

REQUIRED:

	output				The name of the file to generate.

	capacityPercentage	The capacity of the instance will be capacityPercentage%
							of the numberOfItems.
	
	numberOfItems		The number of items to generate within the instance.

Data Generator
Usage:

	datagenerator.sh output maxSize stepSize dataPerSize capacityPercentage hueristic

Generates program runtime data on random instances of various sizes.
The amount and type of data is determined by a set of required arguments.

For each size <- n * stepSize where size < maxSize and n >= 1, dataPerSize datapoints
are generated where each instance used has a capacity of size * capacityRatio / 100.

To set up this script to run locally, you need to change line 1 of runscript.sh so
that it points to your copy of Binpacking.jar.

REQUIRED:

	output				The name of the file to generate
	
	maxSize				The maximum instance size to generate.	

	stepSize			Number of instance sizes to skip between instances.
	
	dataPerSize			Number of data points to take at each instance size.
	
	capacityPercentage	The capacity of the instance will be capacityPercentage%
							of the numberOfItems. 
	
	hueristic			Hueristic argument to test with:  -simplebnb, -fastbnb
							-ffapprox.
							
# Data Structures
							