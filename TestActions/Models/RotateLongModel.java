package TestActions.Models;
import Actions.ObjectActions.*;
public class RotateLongModel extends ModelAction{
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
      float[] tempRot = getRot();
      if(tempRot[1] < 0)
        addToRotation(360, (byte)1);
      else if(tempRot[1] >= 360)
        addToRotation(-360, (byte)1);
      if(tempRot[0] < 0)
        addToRotation(360, (byte)0);
      else if(tempRot[0] >= 360)
        addToRotation(-360, (byte)0);
    }
  }