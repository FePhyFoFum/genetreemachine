genetreemachine
===============
Installation
---------------
genetreemachine is managed by Maven v. 2 (including the dependencies). In order to compile and build treemachine, it is easiest to let Maven v. 2 do the hard work.

On Ubuntu you can install Maven v. 2 with:
sudo apt-get install maven2

Once Maven v. 2 is installed, you can 
	
	git clone git@github.com:FePhyFoFum/genetreemachine.git

then 
	
	sh mvn_cmdline.sh
	
This will compile a jar file in the target directory that has commands for constructing and synthesizing the graph from the command line. 

If you would rather use the neo4j server and the plugins that are written for interacting with the graph over REST calls, you will compile the server plugins. To compile and package what is necessary for the server plugins

	sh mvn_serverplugins.sh
	
The compilation of the server plugins will delete the treemachine jar in the target directory. You can rebuild either just by running those scripts again.

Usage
--------------
To see the help message run:

	java -jar target/genetreemachine-0.0.1-SNAPSHOT-jar-with-dependencies.jar

### Quickstart
