/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.m_3.terroristsserver.game;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.network.HostedConnection;
import com.jme3.network.Message;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import me.m_3.terroristsserver.Main;
import me.m_3.terroristsserver.Messages.LocationChangeBroadcast;
import me.m_3.terroristsserver.Messages.WelcomeMessage;

/**
 *
 * @author seelo
 */
public final class Game {
    
    public HashMap<String, User> users = new HashMap<>();
    Main main;
    
    String map = "DeveloperMap1";
    
    ArrayList<Vector3f> map_spawns = new ArrayList<>();
    
    public Game(Main main){
        this.main = main;
        buildMapIndex();
    }
    
    public void userConnect(HostedConnection source, String username){
        User user = new User(username);
        user.position = map_spawns.get(0);
        Message welcomeMsg = new WelcomeMessage(true, "Developer Test Server" , map , map_spawns.get(0) , false);
        source.send(welcomeMsg);
        
        String user_id = UUID.randomUUID().toString();
        source.setAttribute("id", user_id);
        
        users.put(user_id, user);
        
        //Send user locations
        HashMap<String, Vector3f> locs = new HashMap<>();
        for (User user2 : this.users.values()){
            if (user2.position == null){
                user2.position = new Vector3f(0,0,0);
            }
            locs.put(user2.username, user2.position);
        }
        HashMap<String, Quaternion> rotations = new HashMap<>();
        for (User user2 : this.users.values()){
            if (user2.rotation == null){
                user2.rotation = new Quaternion();
            }
            rotations.put(user2.username, user2.rotation);
        }
        HashMap<String, Vector3f> movements = new HashMap<>();
        for (User user2 : this.users.values()){
            if (user2.movement == null){
                user2.movement = new Vector3f(0,0,0);
            }
            System.out.println(user2.movement);
        }
        LocationChangeBroadcast msg = new LocationChangeBroadcast(locs ,  rotations , movements);
        source.send(msg);
    }
    
    public void buildMapIndex(){
        
        map_spawns = new ArrayList<>();
        
        Spatial scene = main.getAssetManager().loadModel("Scenes/"+map+".j3o");
        Node rootNode = (Node) scene;
        for (Spatial child : rootNode.getChildren()){
            if (child.getName().startsWith("Spawn_")){
                map_spawns.add(child.getWorldTranslation());
            }
        }
    }
    
    public void updateLocations(){
        HashMap<String, Vector3f> locs = new HashMap<>();
        for (User user : this.users.values()){
            locs.put(user.username, user.position);
        }
        HashMap<String, Quaternion> rotations = new HashMap<>();
        for (User user : this.users.values()){
            rotations.put(user.username, user.rotation);
        }
        HashMap<String, Vector3f> movements = new HashMap<>();
        for (User user : this.users.values()){
            movements.put(user.username, user.movement);
        }
        LocationChangeBroadcast msg = new LocationChangeBroadcast(locs , rotations , movements);
        main.server.broadcast(msg);
    }
    
}
