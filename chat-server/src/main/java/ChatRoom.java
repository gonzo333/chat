import java.util.List;

public class ChatRoom {
  private String name;
  private ChatUser owner;
  private List<ChatUser> chatUsers;

  public ChatRoom(String name, ChatUser owner, List<ChatUser> chatUsers) {
    this.name = name;
    this.owner = owner;
    this.chatUsers = chatUsers;
  }
}
