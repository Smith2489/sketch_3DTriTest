package TestActions.Models;
import Actions.ObjectActions.*;
public class RotateAtFiveDegrees extends ModelAction{
    private int direction = 1;
    public RotateAtFiveDegrees(boolean positive){
      if(positive)
        direction = 1;
      else
        direction = -1;
    }
    public void init(){
      
    }
    public void perform(){
      addToRotation(5*direction*speed, (byte)1);
      float[] tempRot = getRot();
      if(tempRot[1] < 0)
        addToRotation(360, (byte)1);
      else if(tempRot[1] >= 360)
        addToRotation(-360, (byte)1);
    }
  }
