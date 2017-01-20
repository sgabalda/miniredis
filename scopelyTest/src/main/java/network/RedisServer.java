package network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.StringTokenizer;

import core.RedisCore;
import core.RedisCoreException;

public class RedisServer {
	
	public static final int PORT=5555;
	private static final RedisCore redis=new RedisCore();
	
	public static final void main(String... args) {
		
		
		System.out.println("Starting redis service");
		try(ServerSocket server=new ServerSocket(PORT);
				
			    ) {
			System.out.println("Listening on port "+PORT);
			while (true) {
				final Socket clientSocket = server.accept();
				System.out.println("Received new client");
				new Thread(new Runnable() {
					@Override
					public void run() {
						manageClient(clientSocket);
						
					}
					
				}).start();
			}
			
		} catch (IOException e) {
			System.err.println("IOException opening port");
			e.printStackTrace();
		}
		
		
	}
	
	private static void manageClient(Socket clientSocket) {
		try (PrintWriter out =
		        new PrintWriter(clientSocket.getOutputStream(), true);
			    BufferedReader in = new BufferedReader(
			        new InputStreamReader(clientSocket.getInputStream()));){
			String inputline;
			while ((inputline = in.readLine()) != null) {
				System.out.println("Received "+inputline);
				String[] tokens=inputline.split(" ");
				if(tokens.length>0) {
					String command=tokens[0];
					System.out.println("Command is "+command);
					switch(command) {
					case "SET":
						//there have to be 2 more tokens
						if(tokens.length==3) {
							out.println(redis.SET(tokens[1], tokens[2]));
						}else if(tokens.length==5 && tokens[3].equals("EX")) {
							//TODO should check for the string being a number
							out.println(redis.SET(tokens[1], tokens[2],Integer.parseInt(tokens[4])));
						}else{
							out.println("Error, SET needs 3 params and an optional EX param");
						}
						break;
	
					case "GET":
						//there have to be 2 more tokens
						if(tokens.length!=2) {
							out.println("Error, GET needs the key");
						}else{
							out.println(redis.GET(tokens[1]));
						}
						break;
					
					case "DEL":
						//there have to be 2 more tokens
						if(tokens.length!=2) {
							out.println("Error, DEL needs the key");
						}else{
							out.println(redis.DEL(tokens[1]));
						}
						break;
					
					case "INCR":
						//there have to be 2 more tokens
						if(tokens.length!=2) {
							out.println("Error, INCR needs the key");
						}else{
							try {
								out.println(redis.INCR(tokens[1]));
							} catch (RedisCoreException e) {
								out.println("ERROR");
								e.printStackTrace();
							}
						}
						break;
					case "DBSIZE":
						//there have to be 2 more tokens
						if(tokens.length!=1) {
							out.println("Error, DBSIZE needs no params");
						}else{
							out.println(redis.DBSIZE());
						}
						break;
					
					case "ZADD":
						//there have to be 2 more tokens
						if(tokens.length!=4) {
							out.println("Error, ZADD needs 3 params: ZADD key score member");
						}else{
							//TODO should check for the string being a number
							try {
								out.println(redis.ZADD(tokens[1],Double.valueOf(tokens[2]),tokens[3], null));
							} catch (NumberFormatException e) {
								out.println("Error, score has to be a number");
								e.printStackTrace();
							} catch (RedisCoreException e) {
								out.println("Error, "+e.getMessage());
								e.printStackTrace();
							}
						}
						break;
					case "ZCARD":
						//there have to be 2 more tokens
						if(tokens.length!=2) {
							out.println("Error, ZCARD needs 1 params: ZCARD key");
						}else{
							//TODO should check for the string being a number
							out.println(redis.ZCARD(tokens[1]));
						}
						break;
					
					case "ZRANK":
						//there have to be 2 more tokens
						if(tokens.length!=3) {
							out.println("Error, ZRANK needs 2 params: ZRANK key");
						}else{
							//TODO should check for the string being a number
							out.println(redis.ZRANK(tokens[1],tokens[2]));
						}
						break;
					case "ZRANGE":
						//there have to be 2 more tokens
						if(tokens.length!=4 && tokens.length!=5) {
							out.println("Error, ZRANGE needs 3 params: ZRANGE key start stop, and optional WITHSCORES");
						}else{
							//TODO should check for the string being a number
							out.println(redis.ZRANGE(tokens[1],Integer.parseInt(tokens[2]),Integer.parseInt(tokens[3]),
									tokens.length==5 && tokens[4].equals("WITHSCORES")));
						}
						break;
					}
				}
				
			}
		} catch (IOException e) {
			System.err.println("IOException reading communication pipeline");
			e.printStackTrace();
		}
	}

}
