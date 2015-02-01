/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package oblivionengine;

import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Node;
import com.jme3.terrain.geomipmap.TerrainLodControl;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.terrain.heightmap.AbstractHeightMap;
import com.jme3.terrain.heightmap.HillHeightMap;
import com.jme3.texture.Texture;
import java.util.ArrayList;

/**
 *
 * @author To
 */
public class Map extends Node{
    
    //Objektvariablen
    
    private TerrainQuad terrain;
    private float size;   //Wird nur mit einem Wert belegt, wenn man eine ebene Fläche im Kostruktor erzeugt
    private boolean isUndergroundTextureRepetition = false;
    private ArrayList<Building> buildings;
    private ArrayList<Structure> structures;
    private AmbientLight ambientLight;
    private DirectionalLight sunLight;
    private float gravity;
    private Player player;
    private BulletAppState bulletAppState;

    //--------------------------------------------------------------------------
    //Konstruktoren
    
    private Map(String name){
        super(name);
        
        //Physik
        bulletAppState = new BulletAppState();
        activatePhysics(true);
        
        //Player
        player = new Player(1f, 2, 200);
        attachChild(player.getPlayerNode());
        bulletAppState.getPhysicsSpace().add(player);
        
        setAmbientLight(true);
        setSunLight(true);
        setSkyColor(ColorRGBA.Cyan);
        setGravity(-19.62f);
    }
    
    
    public Map(TerrainQuad terrain) {
        this(terrain, "Map");
    }
    

    public Map(TerrainQuad terrain, String name) {
        this(name);
        this.terrain = terrain;
        this.attachChild(terrain);
        
        RigidBodyControl undergroundPhysic = new RigidBodyControl(0);
        this.terrain.addControl(undergroundPhysic);  
        bulletAppState.getPhysicsSpace().add(undergroundPhysic);
    }
    
    
    /*
     * Generiert direkt eine zufällige hügelige Fläche
     */
    public Map(float posX, float posZ, float size, String name){
        this(name);
        this.size = size;
        
        
        /*
         * /Terain erstellen
         */
        //Heightmap
        AbstractHeightMap heightMap = null;
        try{
            heightMap = new HillHeightMap((int)(size+1), 100, 50f, 100f, System.currentTimeMillis());
            heightMap.load();
        } catch(Exception e){e.printStackTrace();};
        HeightMapFilter filter = new HeightMapFilter(heightMap);
        filter.manipulateEdge();
        
        //Terain
        terrain = new TerrainQuad("terrain", 65, (int)size+1, filter.getHeightMap());
        terrain.setShadowMode(ShadowMode.CastAndReceive);
        TerrainLodControl lodControl = new TerrainLodControl(terrain, Game.game.getCamera());
        terrain.addControl(lodControl);
        this.attachChild(terrain);
        
        /*
         * Material
         */
        Material mat_terrain = new Material(Game.game.getAssetManager(), "Common/MatDefs/Terrain/HeightBasedTerrain.j3md");
        float grassScale = 64;
        float dirtScale = 16;
        float rockScale = 128;
        
        Texture grass = Game.game.getAssetManager().loadTexture("Textures/Gras.png");
        grass.setWrap(Texture.WrapMode.Repeat);
        mat_terrain.setTexture("region1ColorMap", grass);
        mat_terrain.setVector3("region1", new Vector3f(200, 1, 128));

        // DIRT texture
        Texture dirt = Game.game.getAssetManager().loadTexture("Textures/Erde.png");
        dirt.setWrap(Texture.WrapMode.Repeat);
        mat_terrain.setTexture("region2ColorMap", dirt);
        mat_terrain.setVector3("region2", new Vector3f(0, 20, dirtScale));

        // ROCK texture
        Texture rock = Game.game.getAssetManager().loadTexture("Textures/Stein.png");
        rock.setWrap(Texture.WrapMode.Repeat);
        mat_terrain.setTexture("region3ColorMap", rock);
        mat_terrain.setVector3("region3", new Vector3f(198, 260, rockScale));
        mat_terrain.setTexture("region4ColorMap", dirt);
        mat_terrain.setVector3("region4", new Vector3f(198, 260, rockScale));
        mat_terrain.setTexture("slopeColorMap", rock);
        mat_terrain.setFloat("slopeTileFactor", 32);
        mat_terrain.setFloat("terrainSize", (int)size+1);
        terrain.setMaterial(mat_terrain);
        
        //Anfangsposition des Players
        player.warp(new Vector3f(0, terrain.getHeight(Vector2f.ZERO), 0));
        
        
        //Physik
        RigidBodyControl undergroundPhysic = new RigidBodyControl(0);
        this.terrain.addControl(undergroundPhysic);  
        bulletAppState.getPhysicsSpace().add(undergroundPhysic);
        
        //Blumen
        initTrees(500, "Models/Landschaft/Baum.j3o");
    }
    
    
    //Generiert eine ebene Fläche am Koordinatenursprung
    public Map(float size, String name){
        this(0, 0, size, name);
    }
    
    /*
     * Generiert Baüme
     * @param: number: Anzahl der zu generierenden Bäume
     *         path  : Pfad zum Modell des Baumes
     */
    private void initTrees(int number, String path){
        for (int i = 0; i < number; i++) {
            float posX = (float)Math.random()*this.size;
            float posZ = (float)Math.random()*this.size;
            float height = terrain.getHeight(new Vector2f(posX, posZ));
            
            if(height <5){
                Node tree = (Node)Game.game.getAssetManager().loadModel(path);
                tree.scale(10);
                tree.setLocalTranslation(posX, height+1.5f, posZ);
                terrain.attachChild(tree);
            }
        }
    }
    
    //--------------------------------------------------------------------------
    //Getter und Setter
    
    public TerrainQuad getTerrain() {
        return terrain;
    }

    public ArrayList<Building> getBuildings() {
        return buildings;
    }

    public void addBuildings(Building... buildings) {
        for (Building building : buildings) {
            this.buildings.add(building);
            this.attachChild(building);
        }
    }

    public ArrayList<Structure> getStructures() {
        return structures;
    }

    public void addStructures(Structure... structures) {
        for (Structure structure : structures) {
            this.structures.add(structure);
            this.attachChild(structure);
        }
    }

    public AmbientLight getAmbientLight() {
        return ambientLight;
    }
    
    public void setAmbientLight(boolean isAmbientLight){
        if(ambientLight == null && isAmbientLight){
            ambientLight = new AmbientLight();
            ambientLight.setColor(ColorRGBA.White);
            addLight(ambientLight); 
        } else if(ambientLight != null && !isAmbientLight){
            removeLight(ambientLight);
            ambientLight = null;
        }
    }
    
    public void setAmbientLightColor(ColorRGBA color){
        ambientLight.setColor(color);
    }
    
    public DirectionalLight getSunLight() {
        return sunLight;
    }
    
    public void setSunLight(boolean isSunLight){
        if(sunLight == null && isSunLight){
            sunLight = new DirectionalLight();
            sunLight.setDirection((new Vector3f(-0.5f, -0.5f, -0.5f)).normalizeLocal());
            sunLight.setColor(ColorRGBA.Yellow);
            addLight(sunLight); 
        } else if(sunLight != null && !isSunLight){
            removeLight(sunLight);
            sunLight = null;
        }
    }
    
    public void setSunLightColor(ColorRGBA color){
        sunLight.setColor(color);
    }
    
    public void setSkyColor(ColorRGBA color){
        Game.game.getViewPort().setBackgroundColor(color);
    }
    
    public ColorRGBA getSkyColor(){
        return Game.game.getViewPort().getBackgroundColor();
    }
    
    public float getGravity() {
        return gravity;
    }

    public void setGravity(float gravity) {
        this.gravity = gravity;
    }
    
    public Player getPlayer() {
        return player;
    }
    
    //--------------------------------------------------------------------------
    //Klasseninterne Methoden   
 
    public void activatePhysics(boolean value){
        if(value){
            Game.game.getStateManager().attach(bulletAppState);
        } else{
            Game.game.getStateManager().detach(bulletAppState);
        }
    }
    
     public void activatePhysicsForPlayer(boolean value){
        if(value){
            bulletAppState.getPhysicsSpace().add(player);
        } else{
            bulletAppState.getPhysicsSpace().remove(player);
        }
    }
}
