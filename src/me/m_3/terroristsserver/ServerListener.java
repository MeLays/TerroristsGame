/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.m_3.terroristsserver;

import com.jme3.network.HostedConnection;
import com.jme3.network.Message;
import com.jme3.network.MessageListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.m_3.terroristsserver.Messages.ClientConnectMessage;
import me.m_3.terroristsserver.Messages.ClientLocationMessage;
import me.m_3.terroristsserver.game.User;

/**
 *
 * @author seelo
 */
public class ServerListener implements MessageListener<HostedConnection>{

    Main main;
    
    public ServerListener(Main main){
        this.main = main;
    }
    
    @Override
    public void messageReceived(HostedConnection source, Message m) {
        if (m instanceof ClientConnectMessage){
            ClientConnectMessage msg = (ClientConnectMessage) m;
            Logger.getLogger(Main.class.getName()).log(Level.INFO, "{0} connected with username {1}", new Object[]{source.getAddress(), msg.playerName});
        
            //TODO: Check if there is space for user/password correct etc
            
            main.game.userConnect(source, msg.playerName);
        }
        
        else if (m instanceof ClientLocationMessage){
            ClientLocationMessage msg = (ClientLocationMessage) m;
            User user = main.game.users.get((String)source.getAttribute("id"));
            user.position = msg.location;
            user.rotation = msg.rotation;
            user.movement = msg.movement;
            main.game.updateLocations();
        }
    }
    
}
