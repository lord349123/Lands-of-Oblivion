/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package oblivionengine.maps.tree;

import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import oblivionengine.Game;

/**
 * Lässt einen Baum umfallen, wenn er gefällt wurde und ihn nach einer 
 * festgelegten Zeit verschwinden
 * @author To
 */
public class TreeCutControl extends AbstractControl {
    
    private float timer = 0;
    private float timerTime = 4;
    
    private Spatial tree;
    private Vector3f fallDirection;
    private boolean isCut = false; 
    

    public TreeCutControl(Spatial tree) {
        super();
        
        this.tree = tree;
    }
    

    @Override
    protected void controlUpdate(float tpf) {
        if(isCut){
            
            //Den Baum im Boden verschwinden lassen
            tree.getParent().getControl(RigidBodyControl.class).setLinearVelocity(new Vector3f(0, -0.981f*2, 0));
            
            //Den Baum rotieren, allerdings nur so weit, dass der Spieler nicht nach oben katapultiert wird
            if(timer < 2.2f)
                tree.getParent().getControl(RigidBodyControl.class).setAngularVelocity(fallDirection);
            else
                tree.getParent().getControl(RigidBodyControl.class).setAngularVelocity(Vector3f.ZERO);
            
            //Der Baum soll erst nach einiger Zet verschwinden
            timer += tpf;
            if(timer > timerTime){
                //Baum verschinden lassen
                tree.getParent().removeFromParent();
                Game.game.mapState.getMap().getBulletAppState().getPhysicsSpace().remove(tree.getParent());
            } 
        }
    }
    
    
    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        
    }
    
    
    //Startet den Umfallprozess
    public void fallDown(){
        tree.getParent().getControl(RigidBodyControl.class).setMass(0.00001f);
        isCut = true;
        fallDirection = Game.game.mapState.getPlayer().getSpatial().getWorldRotation().mult(Vector3f.UNIT_X);
    }
}
