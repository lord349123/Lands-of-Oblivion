/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package oblivionengine.buildings.einwohner;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import oblivionengine.Game;
import oblivionengine.charakter.npc.NPCManager;

/**
 *
 * @author To
 */
public class BuildingHolzhaus extends BuildingHaus{
    //Objektvariablen

    //--------------------------------------------------------------------------
    //Konstruktoren
    public BuildingHolzhaus() {
        super();
        
        if(testRessources(PRICE_HOLZHAUS)){  //Das Gebäude kann nur gebaut werden, wenn genug Ressourcen zur Verfügung stehen
            setSize(SIZE_HOLZHAUS);
            setPRICE(PRICE_HOLZHAUS);
            
            Geometry haus = (Geometry)Game.game.getAssetManager().loadModel("Models/Buildings/Holzhaus.j3o");      
            attachChild(haus);
        }
    }

    //--------------------------------------------------------------------------
    //Getter und Setter
    
    
    //--------------------------------------------------------------------------
    //Klasseninterne Methoden
    @Override
    public void finish() {
        super.finish(); 
        
        setNumberpeople(2);
        NPCManager.addZiviisationsPunkte(1);
    }
    
}
