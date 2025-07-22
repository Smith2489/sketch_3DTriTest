package TestActions.Models;
import Actions.ObjectActions.*;
public class RotateRhombohedron extends ModelAction{
    public void init(){
      
    }
    public void perform(){
      addToRotation(-2*speed, (byte)0);
      addToRotation(2*speed, (byte)1);
      float[] tempRot = getRot();
      if(tempRot[0] < 0)
        addToRotation(360, (byte)0);
      if(tempRot[1] >= 360)
        addToRotation(-360, (byte)1);
    }
  }
