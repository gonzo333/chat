public class ChatOperationQuit implements ChatMessageOperation {
  @Override
  public void makeOperation(ChatUser chatUser, String message) {
    chatUser.sendMessage(chatUser.getNick() + " opuścił konwersację.");
    chatUser.destroyUser();
  }
}
