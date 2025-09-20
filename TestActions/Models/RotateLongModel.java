package TestActions.Models;
import Actions.ObjectActions.*;
public class RotateLongModel extends ModelAction{
  public float[] lookAtPoint = {10, 3, 9};
    public void init(){
    }
    public void perform(){
      matrixTransform();
      float[] modelForward = getForward();
      if(keyPressed()){
        switch(key()){
          case 'w':
            addToPosition(0.5f*speed, modelForward);
            break;
          case 's':
            addToPosition(-0.5f*speed, modelForward);
            break;
          case 'd':
            addToRotation(0.5f*speed, (byte)1);
            break;
          case 'a':
            addToRotation(-0.5f*speed, (byte)1);
            break;
          case 'g':
            addToRotation(0.5f*speed, (byte)0);
            break;
          case 'h':
            addToRotation(-0.5f*speed, (byte)0);
            break;
          case '=':
            initPositionShake(0.6f, 50);
            initRotationShake(15, 50);
            break;
        }
      }
      shakePosition();
      shakeRotation();
      lookAt(lookAtPoint);
      float[] tempRot = getRotDegrees();
      if(tempRot[1] < 0)
        rotatePlus360((byte)1);
      else if(tempRot[1] >= 360)
        rotateMinus360((byte)1);
      if(tempRot[0] < 0)
        rotatePlus360((byte)0);
      else if(tempRot[0] >= 360)
        rotateMinus360((byte)0);
    }
  }