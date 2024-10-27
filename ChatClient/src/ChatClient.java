import java.io.*;
import java.net.*;

public class ChatClient {
	private static final String SERVER_ADDRESS = "127.0.0.1";
	private static final int SERVER_PORT = 5555;

	public static void main(String[] args) {
		try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
				BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
				BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in))) {

			String clientMsg = "";
			String servertMsg = "";
			while (true) {
				servertMsg = in.readLine();
				if(servertMsg == null) {
					System.out.println("continue");
					continue;
				}
				
				if (servertMsg.equalsIgnoreCase("ERR Not found user")) {
					System.out.println("Not found user");
					System.out.print("Username:");
					String username = keyboard.readLine();
					out.println("USER " + username);
				}
				
				else if (servertMsg.equalsIgnoreCase("ERR Password incorrect"))  {
					System.out.println("Password incorrect");
					System.out.print("Username:");
					String username = keyboard.readLine();
					out.println("USER " + username);
				}

				else if (servertMsg.equalsIgnoreCase("OK Username Require")) {
					System.out.print("Username:");
					String username = keyboard.readLine();
					out.println("USER " + username);
				}

				else if (servertMsg.equalsIgnoreCase("OK Password Require")) {
					System.out.print("Password:");
					String password = keyboard.readLine();
					out.println("PASS " + password);
				}

				else if (servertMsg.equalsIgnoreCase("OK Login successful")) {
					System.out.println("Login Successful");
					System.out.print("Send to server(Enter \"exit\" to halt program): ");
					clientMsg = keyboard.readLine();
					out.println("ECHO " + clientMsg);
				}

				else {
					System.out.println("Reply from servert: " + servertMsg);
					System.out.print("Send to server(Enter \"exit\" to halt program): ");
					clientMsg = keyboard.readLine();
					out.println("ECHO " + clientMsg);
				}

				if (clientMsg.equalsIgnoreCase("exit")) {
					out.println("LOGOUT");
					System.out.println("Logout successful. Client stopped!");
					socket.close();
					break;
				}

			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
