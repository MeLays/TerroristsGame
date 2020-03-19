/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.m_3.terroristsserver.Messages;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

/**
 *
 * @author seelo
 */
@Serializable
public class ClientDisconnectBroadcast extends AbstractMessage{
    public String username;
    
    public ClientDisconnectBroadcast(){
        
    }
    
    public ClientDisconnectBroadcast(String username){
        this.username = username;
    }
}
