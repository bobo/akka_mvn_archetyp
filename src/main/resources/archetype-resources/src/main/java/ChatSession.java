#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package};

import com.google.inject.Inject;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author bobo
 */
public class ChatSession implements Serializable{

    private String userName;
    private final List<Message> messages = new ArrayList<Message>();
    @Inject
    private ChatServer chatServer;

    public void login() {
        chatServer.init();
        chatServer.login(this);
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public void recieveMessage(Message message) {
        messages.add(message);
    }

    public void sendMessage(String text) {
        Message message = new Message(userName, text);
        chatServer.sendMessage(message);
    }

    public List<Message> getLog() {
        return messages;
    }

    @Override
    public String toString() {
        return "Session: " + userName;
    }

    public void setServer(ChatServer server) {
        this.chatServer = server;
    }
}
