/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.m_3.terroristsserver;

import com.jme3.app.SimpleApplication;
import com.jme3.network.Client;
import com.jme3.network.ClientStateListener;
import com.jme3.network.ConnectionListener;
import com.jme3.network.HostedConnection;
import com.jme3.network.Network;
import com.jme3.network.Server;
import com.jme3.network.serializing.Serializer;
import com.jme3.system.JmeContext;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.m_3.terroristsserver.Messages.ClientConnectMessage;
import me.m_3.terroristsserver.Messages.ClientDisconnectBroadcast;
import me.m_3.terroristsserver.Messages.ClientLocationMessage;
import me.m_3.terroristsserver.Messages.LocationChangeBroadcast;
import me.m_3.terroristsserver.Messages.WelcomeMessage;
import me.m_3.terroristsserver.game.Game;
import me.m_3.terroristsserver.game.User;


/**
 *
 * @author seelo
 */

public class Main extends SimpleApplication implements ConnectionListener{

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Main app = new Main();
        app.start(JmeContext.Type.Headless);
    }
    
    public Game game;
    
    public Server server;
    ServerListener serverListener;
    
    @Override
    public void simpleInitApp() {
        try {
            server = Network.createServer(6143);
            initializeSerializables();
            server.start();
            server.addConnectionListener(this);
            
            this.serverListener = new ServerListener(this);
            
            //Register Listener
            server.addMessageListener(this.serverListener , ClientConnectMessage.class);
            server.addMessageListener(this.serverListener , ClientLocationMessage.class);
            
            //Starting game...
            this.game = new Game(this);
            
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void destroy() {
        server.close();
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
    public void connectionAdded(Server server, HostedConnection conn) {
    }

    @Override
    public void connectionRemoved(Server server, HostedConnection conn) {
        User user = game.users.get((String)conn.getAttribute("id"));
        String username = user.username;
        game.users.remove((String)conn.getAttribute("id"));
        ClientDisconnectBroadcast msg = new ClientDisconnectBroadcast();
        msg.username = username;
        server.broadcast(msg);
    }
    
}
