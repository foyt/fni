package fi.foyt.fni.api;

import java.util.Arrays;
import java.util.List;

public class ApiResult<T> {

  public ApiResult(T response, List<Message> messages) {
    this.response = response;
    this.messages = messages;
  }
  
  public ApiResult(T result, Message message) {
    this(result, Arrays.asList(message));
  }
  
  public ApiResult(T result) {
    this(result, (List<Message>) null);
  }
  
  public T getResponse() {
    return response;
  }
  
  public void setResponse(T response) {
    this.response = response;
  }
  
  public List<Message> getMessages() {
    return messages;
  }
  
  public void setMessages(List<Message> messages) {
    this.messages = messages;
  }
  
  private T response;
  private List<Message> messages;
}
