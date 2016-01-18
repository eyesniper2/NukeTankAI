#Nuke's Tank AI for the 2016 Pason Coding Contest

##About:

The Nuke tank AI battle application was built by @DLukeNelson and @eyesniper2 for the Pason coding contest.
This application is based off of the sample code provided by Pason for use in their virtual machine. Getting the API to work with anything else might prove challenging.

The strategy used per friendly tank was to focus on dodging bullets and attacking unfriendly targets as they approach. In order to avoid targeting unfriendly targets through walls a check was used to make sure that the friendly tank had a line of sight to the enemy tank. Before firing there was also a check to make sure friendly targets were not in the way.

##Usage:

To explore the project and make modifications, this project is set up using maven. However to get this project to run in the Linux VM that is provided you will need to not compile the project and use the following steps:

1. Drag your files into the VM to match this structure with its dependencies.
	```
	net
		rayfall
			... Rest of the project ...		
	org
		json
			... Rest of the library ...
	```
2. From the top of the project structure above (The folder that has the org and net folder) run the following command to compile the software:
	- Linux
	```
	javac -cp /usr/local/share/java/zmq.jar:. net/rayfall/TankAI/Client.java
	```

3. The run the software using this command:
	- Linux
	```
	java -Djava.library.path=/usr/local/lib -cp /usr/local/share/java/zmq.jar:. net.rayfall.TankAI.Client <ip> <match-token>
	```

##API:

If you are a student or a competitor looking to get a jump start on their client for this contest please have a look at the API branch.

## Dependencies

[ZeroMQ](http://www.zeromq.org/)
[ZeroMQ Java Binding](http://www.zeromq.org/bindings:java)
[Json](http://www.json.org/java/)
