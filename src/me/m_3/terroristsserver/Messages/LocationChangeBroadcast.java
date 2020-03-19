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
import java.util.HashMap;

/**
 *
 * @author seelo
 */
@Serializable
public class LocationChangeBroadcast extends AbstractMessage{
    
    public HashMap<String, Vector3f> locations = new HashMap<>();
    public HashMap<String, Quaternion> rotations = new HashMap<>();
    public HashMap<String, Vector3f> movements = new HashMap<>();

    
    public LocationChangeBroadcast(){
        
    }
    
    public LocationChangeBroadcast(HashMap<String, Vector3f> locs,
            HashMap<String, Quaternion> rotations,
            HashMap<String, Vector3f> movements){
        this.locations = locs;
        this.rotations = rotations;
        this.movements = movements;
    }
    
}
