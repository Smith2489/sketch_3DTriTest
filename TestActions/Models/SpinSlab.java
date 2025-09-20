package TestActions.Models;

import Actions.ObjectActions.ModelAction;
import Renderer.Objects.Physics.Physics;

public class SpinSlab extends ModelAction{
    private boolean keyLocked = false;
    private int spinSpeed = 2;
    private float[] startPosition = {0, 0, 0};
    private float[] scale = new float[3];
    public void init(){
        startPosition[0] = getPos()[0];
        startPosition[1] = getPos()[1];
        startPosition[2] = getPos()[2];
        Physics.fluidDensity = 0.25f;
        Physics.gravityAcceleration = 0.005f;
        physics.terminalVelocity = 0.5f;
        physics.setGravityVelocity();
        scale = getScale();
    }
    public void perform(){
        physics.applyGravity();
        if(keyPressed()){
          if(key('r')){
            hardSetPosition(startPosition);
            physics.setGravityVelocity();
          }
          if(!keyLocked){
            keyLocked = true;
            switch(key()){
              case '5':
                if(Math.abs(spinSpeed) == 2)
                  spinSpeed = 0;
                else
                  spinSpeed = 2;
                break;
              case '6':
                if(Math.abs(spinSpeed) == 2)
                  spinSpeed = 0;
                else
                  spinSpeed = -2;
                break;
            }
          }
        }
        else
          keyLocked = false;
        float[] tempScale = getScale();
        if(keyPressed()){
          if(key('8')){
            if(tempScale[0] < 10)
              scale[0]+=0.5;
          }
          else if(key('3')){
            if(scale[0] > 0.5)
              scale[0]-=0.5;
          }
          scale[1] = scale[0];
          scale[2] = scale[0];
          hardSetScale(scale);
        }
        addToRotation(spinSpeed*speed, (byte)2);
        addToRotation(spinSpeed*speed+0.0001f, (byte)1);
        float[] tempRot = getRotDegrees();
        float[] tempPos = getPos();
        if(tempRot[2] > 360)
          rotateMinus360((byte)2);
        if(tempRot[2] < 0)
          rotatePlus360((byte)2);
        if(tempRot[1] > 360)
          rotateMinus360((byte)1);
        if(tempRot[1] < 0)
          rotatePlus360((byte)1);

        if(tempPos[0] > 100000)
          hardSetPosition(100000, tempPos[1], tempPos[2]);
        else if(tempPos[0] < -100000)
          hardSetPosition(-100000, tempPos[1], tempPos[2]);

        if(tempPos[1] > 100000)
          hardSetPosition(tempPos[0], 100000, tempPos[2]);
        else if(tempPos[1] < -100000)
          hardSetPosition(tempPos[0], -100000, tempPos[2]);

        if(tempPos[2] > 100000)
          hardSetPosition(tempPos[0], tempPos[1], 100000);
        else if(tempPos[2] < -100000)
          hardSetPosition(tempPos[0], tempPos[1], -100000);
    }
}