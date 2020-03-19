/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.m_3.terroristsserver.Messages;

import com.jme3.math.Vector3f;
import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

/**
 * A Packet sent to the Client after a ConnectMessage has been received.
 * 
 */
@Serializable
public class WelcomeMessage extends AbstractMessage{
 
    public boolean connected = true;
    public String welcomeText = "Welcome to the server.";
    public String map = "DeveloperMap1";
    public Vector3f location = null;
    public boolean spectator = false;
    
    public WelcomeMessage(){
        
    }
    
    public WelcomeMessage(boolean connected , String welcomeText , String map , Vector3f location , boolean spectator){
        this.connected = connected;
        this.welcomeText = welcomeText;
        this.map = map;
        this.location = location;
        this.spectator = spectator;
    }
    
}
