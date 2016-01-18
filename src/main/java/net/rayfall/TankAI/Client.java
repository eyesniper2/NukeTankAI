package net.rayfall.TankAI;

final class Client
{
	public static Communication comm;
	public static String matchToken;
	public static String clientToken;
	
	class Type
	{
		public static final String CREATE = "create";
		public static final String JOIN = "join";
	}
	

	public static void main(String[] args)
	{
		Client.run(args);
	}

	public static void run(String[] args)
	{
		String ipAddress = null;
		String teamName = "Nuke";
		String password = "nukeisawesome";

		if(args.length != 2) {
			Client.printHelp();
			return;
		}

		ipAddress = args[0];
		matchToken = args[1];

		System.out.println("Starting Nuke AI Battle Tank Client...");

		Command command = new Command();

		// retrieve the command to connect to the server
		String connectCommand = command.getMatchConnectCommand(teamName, password, matchToken);

		// retrieve the communication singleton
		comm = Communication.getInstance(ipAddress, matchToken);

		// send the command to connect to the server
		System.out.println("Connecting to server...");
		clientToken = comm.send(connectCommand, Command.Key.CLIENT_TOKEN);
		System.out.println("Received client token... " + clientToken);
		
		// Check to make sure we are connected
		if (null == clientToken)
		{
			System.out.println("Error: unable to connect!");
			System.exit(-1);
		}

		// the GameInfo object will hold the client's name, token, game type, etc.
		@SuppressWarnings("unused")
		GameInfo gameInfo = new GameInfo(clientToken, teamName);

		// We are now connected to the server. Let's do some stuff:
		System.out.println("Connected to game server!");
		
		System.out.println("Starting heart beat...");
		HeartBeat heartBeat = new HeartBeat();
		heartBeat.launchGameHeartBeat();
		
		
		
		
		System.out.println("Exiting...");
	}

	public static void printHelp()
	{
			System.out.println("usage: Client <ip address> <match-token>");
	}
}
