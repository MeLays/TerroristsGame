package me.m_3.terrorists;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.network.Client;
import com.jme3.network.Message;
import com.jme3.network.Network;
import com.jme3.network.serializing.Serializer;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.FXAAFilter;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Line;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeContext;
import com.jme3.ui.Picture;
import java.io.IOException;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.m_3.terroristsserver.Messages.ClientConnectMessage;
import me.m_3.terroristsserver.Messages.ClientDisconnectBroadcast;
import me.m_3.terroristsserver.Messages.ClientLocationMessage;
import me.m_3.terroristsserver.Messages.LocationChangeBroadcast;
import me.m_3.terroristsserver.Messages.WelcomeMessage;

/**
 * This is the Main Class of your Game. You should only do initialization here.
 * Move your Logic into AppStates or Controls
 * @author normenhansen
 */
public class Main extends SimpleApplication implements ActionListener{

    public static void main(String[] args) {
        Main app = new Main();
        AppSettings settings = new AppSettings(true);
        settings.setTitle("Terrorists");
        settings.setMinResolution(1280, 720);
        settings.setResizable(true);
        settings.setResolution(1280, 720);
        settings.setVSync(true);
        app.setPauseOnLostFocus(false);
        app.setDisplayStatView(false);
        app.setSettings(settings);
        app.start(JmeContext.Type.Display);
    }
    
    Client client;
    ClientListener clientListener;
    
    Game game;
    
    public String username = UUID.randomUUID().toString();
    
    CharacterControl player;
    BulletAppState bulletAppState;
    
    private Vector3f walkDirection = new Vector3f();
    private boolean left = false, right = false, up = false, down = false;
    //Temporary vectors used on each frame.
    //They here to avoid instanciating new vectors on each frame
    private Vector3f camDir = new Vector3f();
    private Vector3f camLeft = new Vector3f();
    
    public Node player3d;
    public Node online_player_nodes;
    
    boolean isShooting = false;
    
    @Override
    public void simpleInitApp() {
        
        try {
            
            this.game = new Game(this);
            
            viewPort.setBackgroundColor(new ColorRGBA(0.7f, 0.8f, 1f, 1f));
            
            FilterPostProcessor fpp = new FilterPostProcessor(assetManager);
            FXAAFilter FXAA = new FXAAFilter();
            FXAA.setSubPixelShift(5.0f);
            FXAA.setReduceMul(5.0f);
            FXAA.setVxOffset(5.0f);
            fpp.addFilter(FXAA);
            FXAA.setEnabled(true);
            viewPort.addProcessor(fpp);
            
            /** Set up Physics */
            bulletAppState = new BulletAppState();
            stateManager.attach(bulletAppState);
            
            client = Network.connectToServer("localhost", 6143);
            initializeSerializables();
            client.start();
            
            //Register Listeners
            this.clientListener = new ClientListener(this);
            
            client.addMessageListener(this.clientListener, WelcomeMessage.class);
            client.addMessageListener(this.clientListener, LocationChangeBroadcast.class);
            client.addMessageListener(this.clientListener, ClientDisconnectBroadcast.class);
            
            //Connect to Server with a ClientConnectMessage
            Message msg = new ClientConnectMessage(username , "0.001");
            client.send(msg);
            
            this.cam.setFrustumPerspective(110, (float)settings.getWidth()/settings.getHeight(), 1f, 1000f);
            
            setUpKeys();
            
            //Setup empty node around player
            player3d = new Node();
            this.getRootNode().attachChild(player3d);
            
            online_player_nodes = new Node();
            this.getRootNode().attachChild(online_player_nodes);
            
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    @Override
    public void simpleUpdate(float tpf) {
        camDir.set(cam.getDirection()).multLocal(0.6f).setY(0);
        camLeft.set(cam.getLeft()).multLocal(0.4f);
        
        walkDirection.set(0, 0, 0);
        if (left) {
            walkDirection.addLocal(camLeft);
        }
        if (right) {
            walkDirection.addLocal(camLeft.negate());
        }
        if (up) {
            walkDirection.addLocal(camDir);
        }
        if (down) {
            walkDirection.addLocal(camDir.negate());
        }
        player.setWalkDirection(walkDirection);
        cam.setLocation(GameFunctions.translateToCameraLocation(player.getPhysicsLocation()));
        
        player3d.setLocalRotation(this.getCamera().getRotation());
        player3d.setLocalTranslation(GameFunctions.translateToCameraLocation(player.getPhysicsLocation()));
        
        if (walkDirection == null){
            walkDirection = new Vector3f(0,0,0);
        }
        
        if (client.isConnected()){
            //Send location update
            ClientLocationMessage msg = new ClientLocationMessage(player.getPhysicsLocation() , cam.getRotation() , walkDirection);
            client.send(msg);
        }
    }

    @Override
    public void simpleRender(RenderManager rm) {
        for (String username : this.game.userLocations.keySet()){
            
            if (online_player_nodes.getChild("player_"+username) != null){
                online_player_nodes.getChild("player_"+username).setLocalTranslation(this.game.userLocations.get(username));
                online_player_nodes.getChild("player_"+username).setLocalRotation(this.game.userRotations.get(username));
                continue;
            }
            
            Spatial person = getAssetManager().loadModel("Models/Player2/Player2.j3o");
            
            Node player = new Node("player_"+username);
            player.setLocalTranslation(this.game.userLocations.get(username));
            player.attachChild(person);
            player.rotate(this.game.userRotations.get(username));
            
            Vector3f from = new Vector3f();
            this.player3d.localToWorld(new Vector3f(-3,-1.3f,2), from);
            
            Line line = new Line(from, this.game.userLocations.get(username));
            Geometry mark = new Geometry("marker2", line);
            Material mark_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            mark_mat.setColor("Color", ColorRGBA.Green);
            mark.setMaterial(mark_mat);
            rootNode.attachChild(mark);
            
            online_player_nodes.attachChild(player);

        }
        

        if (rootNode.getChild("marker") != null){
            rootNode.getChild("marker").removeFromParent();
        }
        
        if (isShooting){
                        
            //Just for visualizing, the real hitscan is on the server side
            CollisionResults results = new CollisionResults();
            Ray ray = new Ray(cam.getLocation(), cam.getDirection());
            
            //Check for players first
            this.online_player_nodes.collideWith(ray, results);
            
            if (results.size() > 0){
                CollisionResult closest = results.getClosestCollision();
                
                Vector3f from = new Vector3f();
                this.player3d.localToWorld(new Vector3f(-3,-1.3f,2), from);
                                
                Line line = new Line(from, closest.getContactPoint());
                Geometry mark = new Geometry("marker", line);
                Material mark_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                mark_mat.setColor("Color", ColorRGBA.Green);
                mark.setMaterial(mark_mat);
                rootNode.attachChild(mark);
            }
            
            else{
                results = new CollisionResults();
                game.scene.collideWith(ray, results);
                if (results.size() > 0){
                    CollisionResult closest = results.getClosestCollision();

                    Vector3f from = new Vector3f();
                    this.player3d.localToWorld(new Vector3f(-3,-1.3f,2), from);

                    Line line = new Line(from, closest.getContactPoint());
                    Geometry mark = new Geometry("marker", line);
                    Material mark_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                    mark_mat.setColor("Color", ColorRGBA.Red);
                    mark.setMaterial(mark_mat);
                    rootNode.attachChild(mark);
                }
            }
        }
        
        //Draw HUD
        
        for (Spatial child : guiNode.getChildren()){
            child.removeFromParent();
        }
        
        //Initialize Crosshair
        Picture pic = new Picture("HUD Picture");
        pic.setImage(assetManager, "Textures/crosshair.png", true);
        pic.setWidth(settings.getWidth()/32);
        pic.setHeight(settings.getWidth()/32);
        pic.setPosition(settings.getWidth()/2 - settings.getWidth()/64, settings.getHeight()/2 - settings.getWidth()/64);
        guiNode.attachChild(pic);
        
    }
    
    private void setUpKeys() {
        inputManager.addMapping("Left", new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping("Up", new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("Down", new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping("Jump", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addMapping("Shoot",new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addListener(this, "Shoot");
        inputManager.addListener(this, "Left");
        inputManager.addListener(this, "Right");
        inputManager.addListener(this, "Up");
        inputManager.addListener(this, "Down");
        inputManager.addListener(this, "Jump");
    }
    
    @Override
    public void destroy() {
        client.close();
        super.destroy();
    }
    
    public void initializeSerializables() {
        Serializer.registerClass(ClientConnectMessage.class);
        Serializer.registerClass(WelcomeMessage.class);
        Serializer.registerClass(ClientLocationMessage.class);
        Serializer.registerClass(LocationChangeBroadcast.class);
        Serializer.registerClass(ClientDisconnectBroadcast.class);
    }

    @Override
    public void onAction(String binding, boolean isPressed, float tpf) {
        if (binding.equals("Left")) {
            left = isPressed;
        } else if (binding.equals("Right")) {
            right= isPressed;
        } else if (binding.equals("Up")) {
            up = isPressed;
        } else if (binding.equals("Down")) {
            down = isPressed;
        } else if (binding.equals("Jump")) {
              if (isPressed && player.onGround()) {
                    player.jump(new Vector3f(0,20f,0));
              }
        }
        
        //Shooting
        if (binding.equals("Shoot") && isPressed) {
            
            isShooting = true;

        }
        else if (binding.equals("Shoot") && !isPressed) {
            isShooting = false;
        }
    }
}
