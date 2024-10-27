import java.io.*;
import java.net.*;

public class ChatClient {
	private static final String SERVER_ADDRESS = "127.0.0.1";
	private static final int SERVER_PORT = 5555;

	public static void main(String[] args) {
	    try (
	        Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
	        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
	        BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in))
	    ) {
	        runClientSession(in, out, keyboard);
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}

	// Main session handler to process server responses and manage client input
	private static void runClientSession(BufferedReader in, PrintWriter out, BufferedReader keyboard) throws IOException {
	    String response;
	    while ((response = in.readLine()) != null) {
	        if (response.startsWith("ECHO")) {
	            handleEchoResponse(response, out, keyboard);
	        } else {
	            handleServerResponse(response, out, keyboard);
	        }
	    }
	}

	// Handles ECHO responses specifically and manages further user input or exit
	private static void handleEchoResponse(String response, PrintWriter out, BufferedReader keyboard) throws IOException {
	    String message = response.substring(5); // Extract message after "ECHO"
	    System.out.println("Reply from server: " + message);
	    handleUserMessage(out, keyboard);
	}

	// Processes server responses and prompts user input if needed
	private static void handleServerResponse(String response, PrintWriter out, BufferedReader keyboard) throws IOException {
	    switch (response) {
	        case "ERR Not found user":
	            System.out.println("Not found user");
	            promptAndSend("Username:", "USER", out, keyboard);
	            break;

	        case "ERR Password incorrect":
	            System.out.println("Password incorrect");
	            promptAndSend("Username:", "USER", out, keyboard);
	            break;

	        case "OK Username Require":
	            promptAndSend("Username:", "USER", out, keyboard);
	            break;

	        case "OK Password Require":
	            promptAndSend("Password:", "PASS", out, keyboard);
	            break;

	        case "OK Login successful":
	            System.out.println("Login Successful");
	            handleUserMessage(out, keyboard);
	            break;

	        default:
	            System.out.println("Unrecognized server response: " + response);
	            break;
	    }
	}

	// Prompts the user for input and sends the message, managing exit if requested
	private static void handleUserMessage(PrintWriter out, BufferedReader keyboard) throws IOException {
	    String input = promptForInput("Send to server (Enter \"exit\" to halt program): ", keyboard);
	    if (input.equalsIgnoreCase("exit")) {
	        out.println("LOGOUT");
	        System.out.println("Logout successful. Client stopped!");
	        System.exit(0); // Exit the application
	    } else {
	        out.println("ECHO " + input);
	    }
	}

	// Prompts the user for input and reads the input from the keyboard
	private static String promptForInput(String prompt, BufferedReader keyboard) throws IOException {
	    System.out.print(prompt);
	    return keyboard.readLine();
	}

	// Prompts the user and sends a specific command to the server
	private static void promptAndSend(String prompt, String command, PrintWriter out, BufferedReader keyboard) throws IOException {
	    String input = promptForInput(prompt, keyboard);
	    out.println(command + " " + input);
	}
}
