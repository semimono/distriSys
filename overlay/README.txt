Assignment 1: Peer to Peer Network Overlay
Due on 02/18/15 at 16:59
Submitted by Cullen Eason
With ename semimono
And student ID 829904489
For CS 455
Taught by Shrideep Pallickara

Files:
    +-Cullen_Eason_A1.tar            This is the tar file containing all of the files for the program.
    |-----cs455/overlay/node/Registry.java
	The main server; it has a main method and allows nodes to connect to it.
    |-----cs455/overlay/node/MessagingNode.java
	The overlay node class; it has a main method and manages the tasks of a node.
    |-----cs455/overlay/node/Node.java
	A container for basic information about nodes, mainly used by Registry.

    |-----cs455/overlay/routing/RoutingEntry.java
	An entry in a routing table, containing node id and address.
    |-----cs455/overlay/routing/RoutingTable.java
	A routing table, used to store all of the nodes a given messaging node connects to.

    |-----cs455/overlay/transport/TCPConnection.java
	A bidirectional TCP connection capable of receiving and sending messages.
    |-----cs455/overlay/transport/TCPConnectionsCache.java
	A singleton which keeps track of all opened TCPConnections.
    |-----cs455/overlay/transport/TCPServerThread.java
	A TCP server thread which listens for new connections and creates new TCPConnections as needed.

    |-----cs455/overlay/util/InteractiveCommandParser.java
	Reads commands from the command line, providing default syntax for usage and the like.
    |-----cs455/overlay/util/StatisticsCollectorAndDisplay.java
	Stores and displays messaging statistics.

    |-----cs455/overlay/wireformats/Event.java
	An event.  Could also be referred to as a message, it has some generic methods execute and getBytes.
    |-----cs455/overlay/wireformats/EventFactory.java
	Creates the specific event given a packet taken from a TCP connection using the id at the front of the packet.
    |-----cs455/overlay/wireformats/Protocol.java
	Keeps track of message IDs and provides standard ways of writing and reading certain types of data.
    |-----cs455/overlay/wireformats/*.java
	All of the remaining classes in the wireformats package are specific events with methods for reading and writing
	data from and to a byte stream, also implementing the execute() method which is called when an event is received.

    |-----README.txt
	You're looking at it.  It gives a basic overview of the program.
    |-----makefile
	The makefile for the program.  Key commands are 'all' and 'clean'.

Instructions:
    Compile the program with `make` or `make all`
    Run the Registry with `java cs455.overlay.node.Registry <port number>`
    Run a Messaging Node with `java cs455.overlay.node.MessagingNode <registry host> <registry port number>`

Notes:
	The wait between when messages are finished being sent and when the registry requests traffix summaries
	is slightly different than in the assignment.  Rather than a hard time of 20 seconds or so, the nodes
	don't report that they've finished until 3 seconds after the last packet they received or relayed. This
	seems to keep the wait shorter while still making it reliable.
	The commandline interface for the Nodes and Registry shows a list of the available commands if h or help
	is entered.  In parenthesis, a shorter name for the command is specified for faster operation.
