/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.m_3.terrorists;

import com.jme3.math.Vector3f;

/**
 *
 * @author seelo
 */
public class GameFunctions {
    
    public static Vector3f translateToCameraLocation(Vector3f location){
        location = location.clone();
        location.addLocal(0, (float) 1.75, 0);
        return location;
    }
    
}
