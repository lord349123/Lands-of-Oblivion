/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package oblivionengine.appstates;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.BloomFilter;
import com.jme3.post.filters.BloomFilter.GlowMode;
import com.jme3.post.filters.DepthOfFieldFilter;
import com.jme3.post.ssao.SSAOFilter;
import com.jme3.renderer.Camera;
import com.jme3.ui.Picture;
import oblivionengine.Game;
import oblivionengine.Map;

/**
 *
 * @author To
 */
public class MapState extends AbstractAppState implements ActionListener{
    
    //Konstanten
    public static final String LINKS         = "Links laufen";
    public static final String RECHTS        = "Rechts laufen";
    public static final String VORWÄRTS      = "Vorwärts laufen";
    public static final String RÜCKWÄRTS     = "Rückwärts laufen";
    public static final String SPRINGEN      = "Springen";
    
    //--------------------------------------------------------------------------
    //Objektvariablen
    private Map map;    //Ist eine Referenz auf activeMap in der Klasse Game
    private boolean vorwärts = false, rückwärts = false, links = false, rechts = false; //Laufrichtungen
    private Vector3f walkDirection = new Vector3f(0, 0, 0);
    private Picture cursor;
    
    //Filter
    private FilterPostProcessor fpp;
    private DepthOfFieldFilter dofFilter;   //Fokussierung der Kamera
    private SSAOFilter ssaoFilter;          //weiche Schatten
    private BloomFilter bloomFilter;
    
    //--------------------------------------------------------------------------
    //Konstruktoren
    public MapState() {
        //FilterPostProcessor initialisieren
        fpp = new FilterPostProcessor(Game.game.getAssetManager());
        Game.game.getViewPort().addProcessor(fpp);
    }
    
    //--------------------------------------------------------------------------
    //Getter und Setter
    
    public Map getMap() {
        return map;
    }

    public void setMap(Map map) {
        Game.game.getRootNode().detachChild(this.map);
        this.map = map;
        Game.game.setActiveMap(map);
    }  

    //--------------------------------------------------------------------------
    //Klasseninterne Methoden
    
    @Override
    public void initialize(AppStateManager stateManager, Application app){ 
        //Map erstellen
        map = new Map(1000, 1000, "Materials/Boden.j3m", "Testboden");
        Game.game.setActiveMap(map);
        map.setUndergroundTextureRepetition(true);
    }
    
    @Override
    public void update(float tpf){
        Camera cam = Game.game.getCam();
        
        //Fokussierung der Kamera auf ein Objekt aktualisieren
        if(dofFilter != null){
            //Per RayCasting das anvisierte Objekt ermitteln
            Ray ray = new Ray(cam.getLocation(), cam.getDirection());
            CollisionResults results = new CollisionResults();
            int numCollisions = map.collideWith(ray, results);
            if(numCollisions > 0){
                CollisionResult hit = results.getClosestCollision();
                dofFilter.setFocusDistance(hit.getDistance() / 10);
            }
        }
        
        /*
         * Bewegen des Spielers und rotieren der Kamera
         */
        Vector3f vorwärtsRichtung = new Vector3f(cam.getDirection().getX(), 0, cam.getDirection().getZ());
        Vector3f linksRichtung = new Vector3f(cam.getLeft().getX(), 0, cam.getLeft().getZ());
        float speed = map.getPlayer().getSpeed();
        
        //Spieler bewegen
        walkDirection.set(0, 0, 0);
        if(vorwärts){
            walkDirection.addLocal(vorwärtsRichtung.mult(speed));
        } 
        if(rückwärts){
            walkDirection.addLocal(vorwärtsRichtung.mult(speed).negate());
        } 
        if(links){
            walkDirection.addLocal(linksRichtung.mult(speed));
        } 
        if(rechts){
            walkDirection.addLocal(linksRichtung.mult(speed).negate());
        }
        map.getPlayer().setWalkDirection(walkDirection);
        
        //Kamera an die Position des Players setzen
        cam.setLocation(map.getPlayer().getPlayerNode().getLocalTranslation().add(0, 3, 0));
    }
    
    @Override
    public void onAction(String name, boolean isPressed, float tpf){
        switch(name){
            case VORWÄRTS     : vorwärts    = isPressed; break;
            case RÜCKWÄRTS    : rückwärts   = isPressed; break;
            case LINKS        : links       = isPressed; break;
            case RECHTS       : rechts      = isPressed; break;
            case SPRINGEN     : map.getPlayer().jump();  break;
        }
    }
    
    /*
     * wichtige Tasten aktivieren und deaktivieren
     */
    public void activateKeys(boolean value){
        if(value){
            //Generiere Mappings
            Game.game.getInputManager().addMapping(LINKS, new KeyTrigger(KeyInput.KEY_A));
            Game.game.getInputManager().addMapping(RECHTS, new KeyTrigger(KeyInput.KEY_D));
            Game.game.getInputManager().addMapping(VORWÄRTS, new KeyTrigger(KeyInput.KEY_W));
            Game.game.getInputManager().addMapping(RÜCKWÄRTS, new KeyTrigger(KeyInput.KEY_S));
            Game.game.getInputManager().addMapping(SPRINGEN, new KeyTrigger(KeyInput.KEY_SPACE));
            
            //Erstelle Listener mit den entsprechenden Mappings
            Game.game.getInputManager().addListener(this, LINKS, RECHTS, VORWÄRTS, RÜCKWÄRTS, SPRINGEN);
        } else{
            Game.game.getInputManager().deleteMapping(LINKS);
            Game.game.getInputManager().deleteMapping(RECHTS);
            Game.game.getInputManager().deleteMapping(VORWÄRTS);
            Game.game.getInputManager().deleteMapping(RÜCKWÄRTS);
            Game.game.getInputManager().deleteMapping(SPRINGEN);
        }
    }
    
    /*
     * Fokussierung der Kamera auf ein Objekt aktivieren
     */
    public void activateDepthOfFieldFilter(boolean value){
        if(value){
            if(dofFilter == null){
                dofFilter = new DepthOfFieldFilter();
                fpp.addFilter(dofFilter);
            }
        } else{
            if(dofFilter != null){
                fpp.removeFilter(dofFilter);
                dofFilter = null;
            }
        }
    }
    
    public void activateDepthOfFieldFilter(DepthOfFieldFilter dofFilter){
        if(this.dofFilter == null){
            this.dofFilter = dofFilter;
            fpp.addFilter(this.dofFilter);
        }
    }
    
    /*
     * Aktiviert weiche Schatten
     */
    public void activateSSAOFilter(boolean value){
        if(value){
            if(ssaoFilter == null){
                ssaoFilter = new SSAOFilter(1f, 1.5f, 0.1f, 0.1f);
                fpp.addFilter(ssaoFilter);
            }
        } else{
            if(ssaoFilter != null){
                fpp.removeFilter(ssaoFilter);
                ssaoFilter = null;
            }
        }
    }
    
    public void activateSSAOFilter(float sampleRadius, float intensity, float scale, float bias){
        if(ssaoFilter == null){
            ssaoFilter = new SSAOFilter(sampleRadius, intensity, scale, bias);
            fpp.addFilter(ssaoFilter);
        }
    }
    
    public void activateSSAOFilter(SSAOFilter ssaoFilter){
        if(this.ssaoFilter == null){
            this.ssaoFilter = ssaoFilter;
            fpp.addFilter(this.ssaoFilter);
        }
    }
    
    /*
     * Lässt die Szene leuchten
     */
    public void activateBloomFilter(boolean value){
        if(value){
            if(bloomFilter == null){
                bloomFilter = new BloomFilter();
                fpp.addFilter(bloomFilter);
            }
        } else{
            if(bloomFilter != null){
                fpp.removeFilter(bloomFilter);
                bloomFilter = null;
            }
        }
    }
    
    public void activateBloomFilter(GlowMode glowMode){
        if(bloomFilter == null){
            bloomFilter = new BloomFilter(glowMode);
            fpp.addFilter(bloomFilter);
        }
    }
    
    public void activateBloomFilter(BloomFilter bloomFilter){
        if(this.bloomFilter == null){
            this.bloomFilter = bloomFilter;
            fpp.addFilter(this.bloomFilter);
        }
    }
    
    /*
     * Fadenkreuz anzeigen
     */
    public void activateCursor(boolean value) {
        if(value){
            if(cursor == null){
                cursor = new Picture("Cursor");
                cursor.setImage(Game.game.getAssetManager(), "Interface/Cursor.png", true);
                cursor.move(Game.game.getSettings().getWidth()/2-30, Game.game.getSettings().getHeight()/2-30, 0);
                cursor.setWidth(60);
                cursor.setHeight(60);
                Game.game.getGuiNode().attachChild(cursor);
            }
        } else{
            if(cursor != null){
                Game.game.getGuiNode().detachChild(cursor);
                cursor = null;
            }
        }
    }
    
    public void activateCursor(String path) {
        if(cursor == null){
            cursor = new Picture("Cursor");
            cursor.setImage(Game.game.getAssetManager(), path, true);
            cursor.move(Game.game.getSettings().getWidth()/2-30, 0, Game.game.getSettings().getHeight()/2-30);
            cursor.setWidth(60);
            cursor.setHeight(60);
            Game.game.getGuiNode().attachChild(cursor);
        }
    }
}
