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
      float[] tempRot = getRotDegrees();
      if(tempRot[1] < 0)
        rotatePlus360((byte)1);
      else if(tempRot[1] >= 360)
        rotateMinus360((byte)1);
    }
  }
