package mygame;

import com.jme3.renderer.RenderManager;
import oblivionengine.Game;
import oblivionengine.appstates.MapState;

/**
 * test
 * @author normenhansen
 */
public class Main extends Game {
    
    //Objektvariablen
    
    //--------------------------------------------------------------------------
    //Konstruktoren
    
    //--------------------------------------------------------------------------
    //Getter und Setter
    
    //--------------------------------------------------------------------------
    //Klasseninterne Methoden

    public static void main(String[] args) {
       Main game = new Main();
       game.start();
    }

    @Override
    public void simpleInitApp() { 
        //Den MapState initialisieren und Tastendrücke aktivieren
        MapState mapState = new MapState();
        initMapState(mapState);
        mapState.activateKeys(true);
        mapState.activateCursor(true);
        mapState.activateFogFilter(true);
    }

    @Override
    public void simpleUpdate(float tpf) {
        
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
}