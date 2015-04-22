/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package oblivionengine.buildings;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import oblivionengine.Game;

/**
 *
 * @author To
 */
public class BuildingSteinhaus extends BuildingHaus{
    //Objektvariablen
    

    //--------------------------------------------------------------------------
    //Konstruktoren
    public BuildingSteinhaus() {
        super();
        
        if(testRessources(PRICE_STEINHAUS)){  //Das Gebäude kann nur gebaut werden, wenn genug Ressourcen zur Verfügung stehen
            setSize(SIZE_STEINHAUS);
            setPRICE(PRICE_STEINHAUS);
            
            Box boxMesh = new Box(5f,10f,5f); 
            Geometry boxGeo = new Geometry("Colored Box", boxMesh); 
            Material boxMat = new Material(Game.game.getAssetManager(), "Common/MatDefs/Light/Lighting.j3md"); 
            boxMat.setBoolean("UseMaterialColors", true); 
            boxMat.setColor("Ambient", ColorRGBA.Gray); 
            boxMat.setColor("Diffuse", ColorRGBA.Gray); 
            boxGeo.setMaterial(boxMat); 
            attachChild(boxGeo);
        }
    }

    //--------------------------------------------------------------------------
    //Getter und Setter
    
    
    //--------------------------------------------------------------------------
    //Klasseninterne Methoden
    @Override
    public void finishBuilding() {
        super.finishBuilding(); 
        
        setNumberpeople(8);
    }
    
}
