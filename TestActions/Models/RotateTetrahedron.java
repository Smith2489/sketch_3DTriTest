package TestActions.Models;
import Actions.ObjectActions.*;
public class RotateTetrahedron extends ModelAction{
    public void init(){
      
    }
    public void perform(){
      addToRotation(1.5f*speed, (byte)0);
      addToRotation(-3*speed, (byte)1);
      float[] tempRot = getRot();
      if(tempRot[1] < 0)
        addToRotation(360, (byte)1);
      if(tempRot[0] >= 360)
        addToRotation(-360, (byte)0);
    }
  }