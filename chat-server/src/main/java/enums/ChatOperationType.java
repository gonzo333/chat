package enums;

public enum ChatOperationType {
  CHANGE_NICK("/nick"),
  QUIT("/quit");

  public final String label;

  ChatOperationType(String label) {
    this.label = label;
  }
}
