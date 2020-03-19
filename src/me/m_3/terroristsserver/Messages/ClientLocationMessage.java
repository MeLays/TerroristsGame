/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.m_3.terroristsserver.Messages;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

/**
 *
 * @author seelo
 */
@Serializable
public class ClientLocationMessage extends AbstractMessage{
    public Vector3f location = null;
    public Quaternion rotation = null;
    public Vector3f movement = null;
    
    public ClientLocationMessage(){
        
    }
    
    public ClientLocationMessage(Vector3f location , Quaternion rotation , Vector3f movement){
        this.location = location;
        this.rotation = rotation;
        this.movement = movement;
    }
}
