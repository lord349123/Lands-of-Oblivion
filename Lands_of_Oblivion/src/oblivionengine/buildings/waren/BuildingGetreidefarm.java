/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package oblivionengine.buildings.waren;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import oblivionengine.Game;
import static oblivionengine.buildings.Building.testRessources;
import oblivionengine.buildings.Ressourcen;
import oblivionengine.buildings.WorkBuilding;
import oblivionengine.charakter.npc.NPCManager;

/**
 *
 * @author To
 */
public class BuildingGetreidefarm extends WorkBuilding{
    //Objektvariablen
    

    //--------------------------------------------------------------------------
    //Konstruktoren
    public BuildingGetreidefarm() {
        super();
        
         if(testRessources(PRICE_GETREIDEFARM)){  //Das Gebäude kann nur gebaut werden, wenn genug Ressourcen zur Verfügung stehen
            setSize(SIZE_GETREIDEFARM);
            setPRICE(PRICE_GETREIDEFARM);
            
            Box boxMesh = new Box(15f,8f,15f); 
            Geometry boxGeo = new Geometry("Colored Box", boxMesh); 
            Material boxMat = new Material(Game.game.getAssetManager(), "Common/MatDefs/Light/Lighting.j3md"); 
            boxMat.setBoolean("UseMaterialColors", true); 
            boxMat.setColor("Ambient", ColorRGBA.Yellow); 
            boxMat.setColor("Diffuse", ColorRGBA.Yellow); 
            boxGeo.setMaterial(boxMat); 
            attachChild(boxGeo);
         }
    }

    //--------------------------------------------------------------------------
    //Getter und Setter
    
    
    //--------------------------------------------------------------------------
    //Klasseninterne Methoden
    @Override
    public void finish() {
        super.finish(); 
        
        control.setRessource(Ressourcen.Getreide);
        control.addPrice(Ressourcen.Wasser, 1);
        
        NPCManager.addZiviisationsPunkte(2);
    }
}
