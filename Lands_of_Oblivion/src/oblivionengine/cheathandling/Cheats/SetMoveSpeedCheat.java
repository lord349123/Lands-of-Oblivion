/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package oblivionengine.cheathandling.Cheats;

import oblivionengine.Game;
import oblivionengine.cheathandling.Cheat;


/**
 *
 * @author Tobi
 */
public class SetMoveSpeedCheat extends Cheat{
    
    public SetMoveSpeedCheat(){
        super.identifier = "Set move speed";
        super.paramNumber = 1;
    }
    
    @Override
    protected void executeCheat(Game game, double params[]) throws Throwable{
        game.mapState.getPlayer().setMoveSpeed(((float) params[0]));
        System.out.println("New move speed: " + params[0]);
    }
}
