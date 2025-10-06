package TestActions.Models;
import Actions.ObjectActions.*;
public class RotateTetrahedron extends ModelAction{
    public void init(){
      
    }
    public void perform(){
      addToRotation(1.5f*frameRateNorm(), (byte)0);
      addToRotation(-3*frameRateNorm(), (byte)1);
      float[] tempRot = getRotDegrees();
      if(tempRot[1] < 0)
        rotatePlus360((byte)1);
      if(tempRot[0] >= 360)
        rotateMinus360((byte)0);
    }
  }