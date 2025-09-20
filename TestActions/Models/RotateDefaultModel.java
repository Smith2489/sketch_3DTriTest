package TestActions.Models;
import Actions.ObjectActions.*;
public class RotateDefaultModel extends ModelAction{
    public void init(){
      
    }
    public void perform(){
      addToRotation(0.25f*speed, (byte)0);
      addToRotation(0.25f*speed, (byte)1);
      addToRotation(0.25f*speed, (byte)2);
      float[] tempRot = getRotDegrees();
      if(tempRot[0] >= 360)
        rotateMinus360((byte)0);
      if(tempRot[1] >= 360)
        rotateMinus360((byte)1);
      if(tempRot[2] >= 360)
        rotateMinus360((byte)2);
    }
  }