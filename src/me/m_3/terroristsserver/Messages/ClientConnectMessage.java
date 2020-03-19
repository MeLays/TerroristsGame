/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.m_3.terroristsserver.Messages;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

/**
 * A Packet sent to the Server to initiate a connection.
 * 
 */
@Serializable
public class ClientConnectMessage extends AbstractMessage{
    
    public String playerName = "Player";
    public String gameVersion = "0.001";
    
    public ClientConnectMessage(){
        
    }
    
    public ClientConnectMessage(String playerName , String gameVersion){
        this.playerName = playerName;
        this.gameVersion = gameVersion;
    }
}
