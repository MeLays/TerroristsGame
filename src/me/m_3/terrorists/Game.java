/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.m_3.terrorists;

import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import java.util.HashMap;

/**
 *
 * @author seelo
 */
public class Game {
    
    Main main;
    
    HashMap<String,Vector3f> userLocations = new HashMap<>();
    HashMap<String,Quaternion> userRotations = new HashMap<>();
    HashMap<String,Vector3f> userMovements = new HashMap<>();
    
    public Game(Main main){
        this.main = main;
    }
    
    RigidBodyControl landscape;
    Spatial scene;
    
    String weapon = "Pistol";
    
    public void setupEnvironment(Vector3f location, String map){
        //Set up controls
        main.getFlyByCamera().setMoveSpeed(100);
        CapsuleCollisionShape capsuleShape = new CapsuleCollisionShape(1.5f, 6f, 1);
        main.player = new CharacterControl(capsuleShape, 0.05f);
        main.player.setJumpSpeed(20);
        main.player.setFallSpeed(30);
        
        //Load map
        scene = main.getAssetManager().loadModel("Scenes/"+map+".j3o");
        main.getRootNode().attachChild(scene);
        main.getCamera().setLocation(GameFunctions.translateToCameraLocation(location));
        
        CollisionShape sceneShape = CollisionShapeFactory.createMeshShape(scene);
        landscape = new RigidBodyControl(sceneShape, 0);
        scene.addControl(landscape);
        
        main.bulletAppState.getPhysicsSpace().add(landscape);
        main.bulletAppState.getPhysicsSpace().add(main.player);
        
        main.player.setGravity(new Vector3f(0,-30f,0));
        main.player.setPhysicsLocation(location);
                    
        //Weapon
        showWeapon();
    }
    
    Spatial weapon_model = null;
    
    public void showWeapon(){
        if (weapon_model == null){
            weapon_model = main.getAssetManager().loadModel("Models/"+weapon+"/"+weapon+".j3o");
            main.player3d.attachChild(weapon_model);
        }
        weapon_model.setLocalTranslation(new Vector3f(-3,-2,2));
    }
    
    public void updateUser(String username , Vector3f location , Quaternion rotation , Vector3f movement){
        this.userLocations.put(username, location);
        this.userRotations.put(username, rotation);
        this.userMovements.put(username, movement);
    }
    
}
