import java.util.Collection;

public class ChatRoom {
  private String name;
  private ChatUser owner;
  private Collection<ChatUser> chatUsers;

  public ChatRoom(String name, ChatUser owner, Collection<ChatUser> chatUsers) {
    this.name = name;
    this.owner = owner;
    this.chatUsers = chatUsers;
  }

  public String getName() {
    return name;
  }

  public Collection<ChatUser> getChatUsers() {
    return chatUsers;
  }

  public void addChatUser(ChatUser chatUser) {
    chatUsers.add(chatUser);
  }

  public void sendMessageToRoom(String message, ChatUser sender){
    chatUsers.forEach(
        chatUser -> {
          if (!chatUser.equals(sender)) {
            chatUser.displayMessage(message);
          }
        });
  }
}
