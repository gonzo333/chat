import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

class ConnectionHandler implements Runnable {
  private Socket clientSocket;
  private BufferedReader messageReader;
  private PrintWriter messageWriter;
  private ChatUser chatUser;
  private ChatRoom chatRoom;

  public ConnectionHandler(Socket clientSocket) {
    this.clientSocket = clientSocket;
  }

  @Override
  public void run() {
    try {
      createReaderAndWriterForSocket();
      getNickFromUser();
      printlHelp();
      listenMessage();
    } catch (IOException e) {
      shutDown();
    }
  }

  public void sendMessage(String message) {
    messageWriter.println(message);
  }

  public void shutDown() {
    try {
      clientSocket.close();
      messageReader.close();
      messageWriter.close();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private void createReaderAndWriterForSocket() throws IOException {
    messageWriter = new PrintWriter(clientSocket.getOutputStream(), true);
    messageReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
  }

  private void getNickFromUser() throws IOException {
    messageWriter.println("Podaj nick: ");
    chatUser = new ChatUser(messageReader.readLine());

    messageWriter.println("Wybierz opcję: ");
    messageWriter.println("1 - dołącz do ogólnego pokoju");
    messageWriter.println("2 - stwórz własny pokój");
    messageWriter.println("3 - dołącz do istniejącego pokoju");

    switch (messageReader.readLine()) {
      case "1":
        messageWriter.println("Czatujesz na kanale ogolnym");
        broadcast(chatUser.getNick() + " dołączył do czata");
        break;
      case "2":
        messageWriter.println("TODO - stworz wlasny pokoj");
        chatRoom = new ChatRoom(messageReader.readLine(),chatUser, List.of(chatUser));
        break;
      case "3":
        messageWriter.println("TODO - dolacz do pokoju");
        break;
      default:
        messageWriter.println("TODO - niezana opcja");
        messageReader.readLine();
    }
  }

  private void printlHelp() {
    messageWriter.println("----------- HELP START---------");
    messageWriter.println("|| /quit - opuszczenie czata");
    messageWriter.println("|| /nick nowy_nick - zmiana linku");
    messageWriter.println("----------- HELP STOP---------");
  }

  private void listenMessage() throws IOException {
    String message;
    while ((message = messageReader.readLine()) != null) {
      if (message.startsWith("/nick")) {
        changeNick(message.split(" ", 2)[1]);
      } else if (message.startsWith("/quit")) {
        broadcast(chatUser.getNick() + " opuścił konwersację.");
        shutDown();
      } else {
        broadcast(chatUser.getNick() + ": " + message);
      }
    }
  }

  private void changeNick(String nick) {
    broadcast(this.chatUser.getNick() + " zmienił nick na " + nick);
    this.chatUser.setNick(nick);
    messageWriter.println("Poprawnie zmieniono nick na: " + nick);
  }

  private void broadcast(String message) {
    ChatServer.getActiveConnectionList().stream()
        .forEach(connectionHandler -> connectionHandler.sendMessage(message));
  }
}
