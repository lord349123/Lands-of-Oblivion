/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package oblivionengine.charakter.npc;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import java.util.ArrayList;
import oblivionengine.Game;
import oblivionengine.buildings.einwohner.BuildingHaus;
import oblivionengine.charakter.player.Player;

/**
 *
 * @author To
 */
public class NPCControl extends AbstractControl{
    //Objektvariablen
    private RigidBodyControl rigidBody;
    private Node node;
    
    //Laufrichtung & Geschwindigkeit
    private int moveSpeed = 15;
    protected ArrayList<Vector2f> path; 
    protected int pathIndex = 0;
    protected Vector2f walkDirection = new Vector2f(0, 0);
    private float timeChangeDirection = 1;
    private float timerChangeDirection = 0;
    protected boolean isWalkingRandom = true;
    
    //Zuhause des NPCs
    private BuildingHaus home = null;   
    
    //Animation
    private AnimControl animControl;
    private AnimChannel animChannel;
    private static final String ANIM_WALK = "my_animation";
    private static final String ANIM_IDLE = "";
    
    //Liste mit allen Bedürfnissen und die Timer
    private ArrayList<Float> timerBedürfnisse = new ArrayList<Float>();
    
    
    //--------------------------------------------------------------------------
    //Konstruktoren
    public NPCControl(BuildingHaus home) { 
        this.home = home;
        NPCManager.numberNPCs++;
        
        //Timer einstellen
        for (int i = 0; i < NPCManager.getBedürfnisseSek().size(); i++) {
            timerBedürfnisse.add(new Float(0));
        }
    }
    
    
    //--------------------------------------------------------------------------
    //Getter und Setter
    public Spatial getSpatial(){
        return spatial;
    }
    
    public void setLocalTranslation(Vector3f pos){
        spatial.setLocalTranslation(pos);
    }

    public BuildingHaus getHome() {
        return home;
    }

    public void setHome(BuildingHaus home) {
        this.home = home;
    }

    public Node getNode() {
        return node;
    }

    public void setNode(Node node) {
        this.node = node;
    }
    
    public void setWalkDirection(Vector2f walkDirection){
        this.walkDirection = walkDirection;
        
        if(!walkDirection.equals(Vector2f.ZERO))
            rotateSpatialToWalkDirection(walkDirection);
        
        //Animation
        if(!walkDirection.equals(Vector2f.ZERO)){
            animChannel = animControl.createChannel();
            animChannel.setAnim(ANIM_WALK);
        } 
        else{
            //Laufanimation beenden
            animControl.clearChannels();
        }
    }

    public boolean isIsWalkingRandom() {
        return isWalkingRandom;
    }

    public void setIsWalkingRandom(boolean isWalkingRandom) {
        this.isWalkingRandom = isWalkingRandom;
    } 

    public AnimControl getAnimControl() {
        return animControl;
    }

    public void setAnimControl(AnimControl animControl) {
        this.animControl = animControl;
    } 

    public ArrayList<Float> getTimerBedürfnisse() {
        return timerBedürfnisse;
    }

    public ArrayList<Vector2f> getPath() {
        return path;
    }

    public void setPath(ArrayList<Vector2f> path) {
        this.path = path;
        setIsWalkingRandom(false);
        
        //Neuen Richtungsvektor festlegen
        if(path != null && path.size() > 0){
            setWalkDirection(path.get(0).subtract(new Vector2f(spatial.getLocalTranslation().x, spatial.getLocalTranslation().z))); 
            System.out.println("Erster Richtungsvektor: " + walkDirection + " , " + spatial.getLocalTranslation() + "/ " + path.get(0));
        }
    }
    

    //--------------------------------------------------------------------------
    //Klasseninterne Methoden
    
    public void generateModell(){
        node = (Node)(Game.game.getAssetManager().loadModel("Models/Player.j3o"));
        node.setLocalTranslation(home.getLocalTranslation().add(0, 0, 7));
        node.getLocalTranslation().setY(Game.game.mapState.getMap().getTerrain().getHeight(new Vector2f(node.getLocalTranslation().x, node.getLocalTranslation().z)));
        node.scale(2.6f);
        Game.game.mapState.getMap().attachChild(node);
        node.addControl(this);
                
        //Animation vorbereiten
        animControl = spatial.getControl(AnimControl.class);
        
        //Schatten
        node.setShadowMode(ShadowMode.Off);  
    }

    @Override
    protected void controlUpdate(float tpf) {
        //Nur bewegen, wenn eine Bewegungsrichtung defniert wurde
        if(!walkDirection.equals(Vector2f.ZERO)){
            //Spatial bewegen
            Vector3f walkDirection = new Vector3f(this.walkDirection.x, 0, this.walkDirection.y);
            spatial.move(walkDirection.normalize().mult(tpf).mult(3));
            spatial.getLocalTranslation().setY(Game.game.mapState.getMap().getTerrain().getHeight(new Vector2f(spatial.getLocalTranslation().x, spatial.getLocalTranslation().z))); 
        }
        
        //Einem Pfad folgen
        if(path != null && pathIndex < path.size()){
            Vector2f pos = new Vector2f(spatial.getLocalTranslation().x, spatial.getLocalTranslation().z);
            
            if(pos.distance(path.get(pathIndex)) < 2f || (pathIndex < path.size()-1 && pos.distance(path.get(pathIndex)) > 4f)){
                //Neuen Weg festlegen oder anhalten
               setWalkDirection(new Vector2f(path.get(pathIndex)).subtract(pos));
               
               //Nächste Position ansteuern
               pathIndex++;

               if(pathIndex == path.size())
                   path = null;
            } 
        }
        else if(isWalkingRandom){    //Bewegungsrichtung in einem festen Intervall zufällig ändern
            timerChangeDirection += tpf;
            if(timerChangeDirection >= timeChangeDirection){
                timerChangeDirection = 0;
                timeChangeDirection = (float)(Math.random()*8);


                //Stehen bleiben oder in zufällige Richtung laufen
                int i = (int)(Math.random()*2);
                if(i == 0){
                    changeRandomWalkDirection();

                } else if(i == 1){
                    setWalkDirection(new Vector2f(0, 0));
                }
            }
        }
        
        
        //Güter verbrauchen
        consumeProducts(tpf);
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        
    }
    
    
    //Güter werden verbraucht un Bedürfnisse gestillt
    private void consumeProducts(float tpf){
       
        //Sekundäre Bedürfnisse
        for (int i = 0; i < NPCManager.getBedürfnisseSek().size(); i++) {
            //Timer aktualisieren
            float timer = timerBedürfnisse.get(i);
            timer += tpf;
            timerBedürfnisse.set(i, timer);
            
            //Bedürfnis überprüfen und Moral anpassen
            if(timer >= NPCManager.getBedürfnisseSek().get(i).getTime()){
                timer = 0;
                timerBedürfnisse.set(i, timer);

                if(Player.lager.getAnzahlRessourcen(NPCManager.getBedürfnisseSek().get(i).getRessource()) > 0){
                    NPCManager.addMoral(0.01f);
                    Player.lager.addRessourcen(NPCManager.getBedürfnisseSek().get(i).getRessource(), -1);
                }
                else{
                    NPCManager.addMoral(-0.01f);
                }
            } 
        }
        
        //Primäre Bedürfnisse
        for (int i = 0; i < NPCManager.getBedürfnissePrim().size(); i++) {
            //Timer aktualisieren
            float timer = timerBedürfnisse.get(i);
            timer += tpf;
            timerBedürfnisse.set(i, timer);
            
            //Bedürfnis überprüfen und Moral anpassen
            if(timer >= NPCManager.getBedürfnissePrim().get(i).getTime()){
                timer = 0;
                timerBedürfnisse.set(i, timer);

                if(Player.lager.getAnzahlRessourcen(NPCManager.getBedürfnissePrim().get(i).getRessource()) > 0){
                    Player.lager.addRessourcen(NPCManager.getBedürfnissePrim().get(i).getRessource(), -1);
                }
                else{
                    NPCManager.addMoral(-0.03f);
                }
            } 
        }
    }
    
    
    public void changeRandomWalkDirection(){
        float x = (float)Math.random();
        float y = (float)Math.random();
        //Werte per Zufall negieren
        if((int)(Math.random()*2) == 1)
            x *= -1;
        if((int)(Math.random()*2) == 1)
            y *= -1;

        setWalkDirection(new Vector2f(x, y));

        //Spatial rotieren
        rotateSpatialToWalkDirection(this.walkDirection);
    }
    
    
    //Rotiert das Spatial so, dass es immer nach vorne läuft
    public void rotateSpatialToWalkDirection(Vector2f walkDirection){
        Vector2f pos = new Vector2f(spatial.getLocalTranslation().x, spatial.getLocalTranslation().z);
        Vector2f us = new Vector2f(0, 1);           //Startrichtung
        Vector2f ue   = walkDirection.normalize();  //Endrichtung
        
        //Winkel bestimmen auf den das Spatial gedreht werden soll
        float dot = us.dot(ue);
        float angle = (float)Math.acos(dot);
        
        if(walkDirection.x < 0){
            angle *= -1;
        }
        
        //Spatial rotieren
        float[] angles = {0, angle, 0};
        Quaternion quat = new Quaternion(angles);
        spatial.setLocalRotation(quat);
    }
}
