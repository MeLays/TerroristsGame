/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.m_3.terrorists;

import com.jme3.math.Vector3f;
import com.jme3.network.Client;
import com.jme3.network.Message;
import com.jme3.network.MessageListener;
import com.jme3.network.serializing.Serializer;
import me.m_3.terroristsserver.Messages.ClientDisconnectBroadcast;
import me.m_3.terroristsserver.Messages.LocationChangeBroadcast;
import me.m_3.terroristsserver.Messages.WelcomeMessage;

/**
 *
 * @author seelo
 */
public class ClientListener implements MessageListener<Client> {
    
    Main main;
    
    public ClientListener(Main main){
        this.main = main;
    }
    
    @Override
    public void messageReceived(Client source, Message msg) {
        if (msg instanceof WelcomeMessage){
            WelcomeMessage welcomeMsg = (WelcomeMessage) msg;
            if (welcomeMsg.connected){
                
                System.out.println("Connected. Playing on Map: " + welcomeMsg.map);
                System.out.println("SERVER: " + welcomeMsg.welcomeText);
                
                main.game.setupEnvironment(welcomeMsg.location, welcomeMsg.map);
                
            }
            else{
                //TODO: Connection denied
            }
        }
        if (msg instanceof LocationChangeBroadcast){
            LocationChangeBroadcast locationChange = (LocationChangeBroadcast) msg;
            for (String username : locationChange.locations.keySet()){
                if (username.equalsIgnoreCase(main.username)) continue;
                main.game.updateUser(username, locationChange.locations.get(username),
                        locationChange.rotations.get(username),
                        locationChange.movements.get(username));
            }
        }
        if (msg instanceof ClientDisconnectBroadcast){
            ClientDisconnectBroadcast disconnectMsg = (ClientDisconnectBroadcast) msg;
            String username = disconnectMsg.username;
            main.game.userLocations.remove(username);
            main.game.userRotations.remove(username);
            main.game.userMovements.remove(username);
            if (main.online_player_nodes.getChild("player_"+username) != null){
                main.online_player_nodes.getChild("player_"+username).removeFromParent();
            }
        } 
    }
  
}
