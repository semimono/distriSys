Assignment 2: Distributed Content Harvesting Using Thread Pools
Due on 03/11/15 at 16:59
Submitted by Cullen Eason
With ename semimono
And student ID 829904489
For CS 455
Taught by Shrideep Pallickara

Files:
    +-Cullen_Eason_A2.tar            This is the tar file containing all of the files for the program.
    |-----cs455/harvester/Crawler.java
	The program entry.  This is the glue for all other components in the program and contains the global state.
    |-----cs455/harvester/ThreadPool.java
	A generic thread pool class used for crawling.
    |-----cs455/harvester/Task.java
	This class does the crawling of a page.
    |-----cs455/harvester/RemotCrawler.java
	This class refers to an instance of crawler running a different domain.
    |-----cs455/harvester/Page.java
	This class records links to and from a specific URL.

    |-----cs455/harvester/util/OutputGenerator.java
	This class generates the output files once the crawling terminates.

    |-----cs455/harvester/transport/TCPConnection.java
	A bidirectional TCP connection capable of receiving and sending messages.
    |-----cs455/harvester/transport/TCPConnectionsCache.java
	A singleton which keeps track of all opened TCPConnections.
    |-----cs455/harvester/transport/TCPServerThread.java
	A TCP server thread which listens for new connections and creates new TCPConnections as needed.

    |-----cs455/harvester/wireformats/Event.java
	An event.  Could also be referred to as a message, it has some generic methods execute and getBytes.
    |-----cs455/harvester/wireformats/EventFactory.java
	Creates the specific event given a packet taken from a TCP connection using the id at the front of the packet.
    |-----cs455/harvester/wireformats/Protocol.java
	Keeps track of message IDs and provides standard ways of writing and reading certain types of data.
    |-----cs455/harvester/wireformats/*.java
	All of the remaining classes in the wireformats package are specific events with methods for reading and writing
	data from and to a byte stream, also implementing the execute() method which is called when an event is received.

    |-----README.txt
	You're looking at it.  It gives a basic overview of the program.
    |-----makefile
	The makefile for the program.  Key commands are 'all' and 'clean'.

Instructions:
    Compile the program with `make` or `make all`
    Run a crawler with `java -cp jericho-html-3.3.jar:. cs455.harvester.Crawler <port number> <thread count> <root url> <config file>`

Notes:
	You may notice a long wait (half a minute maybe) after crawling finishes before the crawlers acknowledge so.  This is expected.
	Some pages seem to be improperly formatted and jericho outputs errors about this.
	It was somewhat unclear what was supposed to be contained in the output files (a pair of links or a single link) so I just
	output one link per line.
