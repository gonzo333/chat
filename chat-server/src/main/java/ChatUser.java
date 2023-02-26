import enums.ChatOperationType;
import enums.ChatRoomChoose;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Optional;

public class ChatUser implements Runnable {
  private final Socket clientSocket;
  private final ChatServer chatServer;
  private BufferedReader messageReader;
  private PrintWriter messageWriter;
  private String nick;

  public ChatUser(Socket clientSocket, ChatServer chatServer) {
    this.clientSocket = clientSocket;
    this.chatServer = chatServer;
  }

  public String getNick() {
    return nick;
  }

  @Override
  public void run() {
    try {
      createReaderAndWriter();
      getNickFromUser();
      chooseRoomOption();
      printlHelp();
      listenMessage();
    } catch (IOException e) {
      destroyUser();
    }
  }

  public void destroyUser() {
    try {
      clientSocket.close();
      messageReader.close();
      messageWriter.close();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private void createReaderAndWriter() throws IOException {
    messageWriter = new PrintWriter(clientSocket.getOutputStream(), true);
    messageReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
  }

  private void getNickFromUser() throws IOException {
    messageWriter.println("Podaj nick: ");
    nick = messageReader.readLine();
  }

  private void chooseRoomOption() throws IOException {
    displayRoomOptions();
    ChatRoomChoose chatRoomChoose = ChatRoomChoose.valueOf(messageReader.readLine());
    switch (chatRoomChoose) {
      case GENERAL -> {
        chatServer.addChatUserToChatRoomByName(ChatServer.GENERAL_ROOM_NAME, this);
        messageWriter.println("Czatujesz na kanale ogolnym");
      }
      case CREATE -> {
        messageWriter.println("Podaj nazwe pokoju i zatwierdz enter, aby stworzyc pokoj");
        chatServer.createChatRoom(messageReader.readLine(), this);
        messageWriter.println("Gratulacje stworzyles pokoj");
      }
      case JOIN -> {
        messageWriter.println("Podaj nazwe pokoju do ktorego chcesz dolaczyc");
        chatServer.addChatUserToChatRoomByName(messageReader.readLine(), this);
        messageWriter.println("Poprawnie dolaczyles do pokoju");
      }
      default -> {
        messageWriter.println("Niezana opcja");
        chooseRoomOption();
      }
    }
  }

  private void displayRoomOptions() {
    messageWriter.println("Wybierz opcję: ");
    messageWriter.println("1 - dołącz do ogólnego pokoju");
    messageWriter.println("2 - stwórz własny pokój");
    messageWriter.println("3 - dołącz do istniejącego pokoju");
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
      ChatMessageOperation chatMessageOperation;
      ChatOperationType chatOperationType = ChatOperationType.valueOf("message");
      switch (chatOperationType) {
        case CHANGE_NICK -> chatMessageOperation = new ChatOperationChangeNick();

        case QUIT -> chatMessageOperation = new ChatOperationQuit();

        default -> chatMessageOperation = new ChatOperationSendMessage();
      }
      chatMessageOperation.makeOperation(this, message);
    }
  }

  public void changeNick(String newNick) {
    sendMessage(nick + " zmienił nick na " + newNick);
    nick = newNick;
    messageWriter.println("Poprawnie zmieniono nick na: " + newNick);
  }

  public void sendMessage(String message) {
    Optional<ChatRoom> userChatRoom = chatServer.getUserRoom(this);
    userChatRoom.ifPresent(chatRoom -> chatRoom.sendMessageToRoom(message, this));
  }

  public void displayMessage(String message) {
    messageWriter.println(message);
  }
}
