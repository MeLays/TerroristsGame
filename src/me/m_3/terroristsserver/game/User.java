/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.m_3.terroristsserver.game;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

/**
 *
 * @author seelo
 */
public class User {
    public Vector3f position;
    public Quaternion rotation;
    public Vector3f movement;
    
    public String username;
    
    public int health = 100;
    
    public User(String username){
        this.username = username;
    }
}
