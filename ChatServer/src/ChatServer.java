import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

public class ChatServer {
	private static final int PORT = 5555;
	private static final String ACCOUNT_FILE = "account.txt";
	private static Map<String, String> accounts = new HashMap<>();

	public static void main(String[] args) {
		loadAccounts();

		try (ServerSocket serverSocket = new ServerSocket(PORT)) {
			System.out.println("Server is listening on port " + PORT);

			while (true) {
				Socket clientSocket = serverSocket.accept();
				System.out.println("Client connected from " + clientSocket.getInetAddress());

				// Tạo một luồng mới để xử lý mỗi kết nối của client
				new Thread(new ClientHandler(clientSocket)).start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void loadAccounts() {
		try (BufferedReader reader = new BufferedReader(new FileReader(ACCOUNT_FILE))) {
			String line;
			while ((line = reader.readLine()) != null) {
				String[] parts = line.split(" ");
				if (parts.length == 2) {
					accounts.put(parts[0], parts[1]);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static class ClientHandler implements Runnable {
		private Socket clientSocket;

		public ClientHandler(Socket clientSocket) {
			this.clientSocket = clientSocket;
		}

		@Override
		public void run() {
			try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
					BufferedWriter out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
			// PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)
			) {
				out.write("OK Username Require\n");
				out.flush();
				
				String message;
				String userName = "";
				
				while ((message = in.readLine()) != null) {

					String[] parts = message.split(" ", 2);
					String command = parts[0];
					String parameter = parts.length > 1 ? parts[1] : "";

					switch (command) {
					case "USER":
						userName = parameter;
						if (!accounts.containsKey(userName)) {
							sendResponse(out, "ERR Not found user");
						} else {
							sendResponse(out, "OK Password Require");
						}
						break;

					case "PASS":
						if (!accounts.getOrDefault(userName, "").equals(parameter)) {
							sendResponse(out, "ERR Password incorrect");
						} else {
							sendResponse(out, "OK Login successful");
						}
						break;

					case "ECHO":
						System.out.println("echo message " + message);
						sendResponse(out, message);
						break;

					case "LOGOUT":
						System.out.println("Client Logged out");
						return;

					default:
						System.out.println("Unrecognized command: " + message);
						break;
					}
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		private void sendResponse(BufferedWriter out, String response) throws IOException {
		    out.write(response);
		    out.newLine();
		    out.flush();
		}
	}
	
	
}
