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
					String command = "";
					String parameter = "";
					

					if (message.split(" ").length > 1) {
						command = message.split(" ", 2)[0];
						parameter = message.split(" ", 2)[1];
					} else {
						System.out.println("message.length " + message.length() + " " + message);
						//System.out.println("else");
					}
					if (command.equalsIgnoreCase("USER")) {
						userName = parameter;
						if (!accounts.containsKey(userName)) {
							out.write("ERR Not found user\n");
							out.flush();
						} else {
							out.write("OK Password Require\n");
							out.flush();
						}
					} else if (command.equalsIgnoreCase("PASS")) {
						System.out.println("Phi check pass " + userName + " " + parameter);
						if (!accounts.get(userName).equals(parameter)) {
							System.out.println("Phi check pass err");
							out.write("ERR Password incorrect\n");
							out.flush();
						} else {
							System.out.println("Phi check pass correct");
							out.write("OK Login successful\n");
							out.flush();
						}
					} else if (command.equalsIgnoreCase( "ECHO")) {
						System.out.println("echo");
						out.write(parameter);
						out.newLine();
						out.flush();
						
					} else if (command.equalsIgnoreCase("LOGOUT")) {
						System.out.println("Logout");
					} else {
						System.out.println("message " + message);
					}
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
