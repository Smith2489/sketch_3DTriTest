package Renderer.Objects.SceneEntities.SceneObjects;
import Actions.ObjectActions.*;
import Maths.Extensions.*;
import Maths.LinearAlgebra.*;
//import Renderer.ScreenDraw.MVP;
import Renderer.Objects.Parents.*;
//Class for abstracting away light object data
public class Light extends SceneEntity{
    //Position if point light, rotation for directional light, both for spot light
    private static final char[] VALID_TYPES = {'p', 'd', 's'};

    //Intensity
    //  0: diffuse
    //  1: specular
    private static float ambientIntensity = 1;
    private float[] intensities = {1, 1};
    //p = point, d = directional, s = spotlight
    private char lightType = 'p';
    //0 = ambient colour, 1 = diffuse colour, 2 = specular colour
    private static float[] ambientColour = {1, 1, 1};
    private float[][] lightColour = {{1, 1, 1}, {1, 1, 1}};
    //0 = inner spread, 1 = outer spread
    private float[] spotlightSpread = {0, -1};
    public Light(){
        super();
        intensities[0] = 1;
        intensities[1] = 1;
        lightColour[0][0] = 1;
        lightColour[0][1] = 1;
        lightColour[0][2] = 1;
        lightColour[1][0] = 1;
        lightColour[1][1] = 1;
        lightColour[1][2] = 1;
        lightType = 'p';
        spotlightSpread[0] = 0;
        spotlightSpread[1] = -1;
    }
    public Light(float[] newPosition){
        super(newPosition);
        intensities[0] = 1;
        intensities[1] = 1;
        lightColour[0][0] = 1;
        lightColour[0][1] = 1;
        lightColour[0][2] = 1;
        lightColour[1][0] = 1;
        lightColour[1][1] = 1;
        lightColour[1][2] = 1;
        lightType = 'p';
        spotlightSpread[0] = 0;
        spotlightSpread[1] = -1;
    }
    public Light(float newX, float newY, float newZ){
        super(newX, newY, newZ);
        intensities[0] = 1;
        intensities[1] = 1;
        lightColour[0][0] = 1;
        lightColour[0][1] = 1;
        lightColour[0][2] = 1;
        lightColour[1][0] = 1;
        lightColour[1][1] = 1;
        lightColour[1][2] = 1;
        lightType = 'p';
        spotlightSpread[0] = 0;
        spotlightSpread[1] = -1;
    }


    public void addAction(LightAction newAction){
        if(newAction != null){
            newAction.setIntensities(intensities);
            newAction.setColour(lightColour);
            super.appendAction(newAction);
            actionList.add(newAction);
        }
        else
            System.out.println(NULL_ACTION);
    }
    public static void setAmbientIntensity(float newAmbient){
        ambientIntensity = Math.max(0, newAmbient);
    }
    public void setDiffuseIntensity(float diffuseIntensity){
        intensities[0] = Math.max(0, diffuseIntensity);
    }
    public void setSpecularIntensity(float specularIntensity){
        intensities[1] = Math.max(0, specularIntensity);
    }
    public static float returnAmbientIntensity(){
        return ambientIntensity;
    }
    public float returnDiffuseIntensity(){
        return intensities[0];
    }
    public float returnSpecularIntensity(){
        return intensities[1];
    }
    public char returnType(){
        return lightType;
    }
    public void setType(char newType){
        if(newType >= 65 && newType <= 90)
            newType+=32;
        boolean isValid = true;
        for(int i = 0; i < VALID_TYPES.length; i++){
            if(newType == VALID_TYPES[i]){
                isValid = true;
                break;
            }
            else
                isValid = false;
        }
        if(!isValid){
            System.out.println("ERROR: NOT A VALID TYPE (MUST BE 'p', 'd', 's')");
            System.exit(-1);
        }
        else
            lightType = newType;
    }
    public void setInnerSpread(float spread){
        if(spread < 0)
            spread = 0;
        else if(spread > 180)
            spread = 180;
        spotlightSpread[0] = (float)Math.cos(MathExtentions.DEGS_TO_RADS*spread);
    }
    public float returnInnerSpread(){
        return spotlightSpread[0];
    }

    public void setOuterSpread(float spread){
        if(spread < 0)
            spread = 0;
        else if(spread > 180)
            spread = 180;
        spotlightSpread[1] = (float)Math.cos(MathExtentions.DEGS_TO_RADS*spread);
    }
    public float returnOuterSpread(){
        return spotlightSpread[1];
    }

    public void setLightColour(int r, int g, int b, byte index){
        if(index <= 0){
            ambientColour[0] = (r & 0xFF)*0.003921569f;
            ambientColour[1] = (g & 0xFF)*0.003921569f;
            ambientColour[2] = (b & 0xFF)*0.003921569f;
        }
        else{
            lightColour[index][0] = (r & 0xFF)*0.003921569f;
            lightColour[index][1] = (g & 0xFF)*0.003921569f;
            lightColour[index][2] = (b & 0xFF)*0.003921569f;
        }
    }
    public void setLightColour(int colour, byte index){
        colour&=0xFFFFFF;
        if(colour <= 0xFF)
            colour = (colour << 16)|(colour << 8)|colour;
        if(index <= 0){
            ambientColour[0] = ((colour >>> 16) & 0xFF)*0.003921569f;
            ambientColour[1] = ((colour >>> 8) & 0xFF)*0.003921569f;
            ambientColour[2] = (colour & 0xFF)*0.003921569f;
        }
        else{
            lightColour[index-1][0] = ((colour >>> 16) & 0xFF)*0.003921569f;
            lightColour[index-1][1] = ((colour >>> 8) & 0xFF)*0.003921569f;
            lightColour[index-1][2] = (colour & 0xFF)*0.003921569f;
        }
    }
    public void setLightColour(int r, int g, int b){
        ambientColour[0] = (r & 0xFF)*0.003921569f;
        ambientColour[1] = (g & 0xFF)*0.003921569f;
        ambientColour[2] = (b & 0xFF)*0.003921569f;
        lightColour[0][0] = ambientColour[0];
        lightColour[0][1] = ambientColour[1];
        lightColour[0][2] = ambientColour[2];
        lightColour[1][0] = ambientColour[0];
        lightColour[1][1] = ambientColour[1];
        lightColour[1][2] = ambientColour[2];
    }
    public void setLightColour(int colour){
        colour&=0xFFFFFF;
        if(colour <= 0xFF)
            colour = (colour << 16)|(colour << 8)|colour;
        ambientColour[0] = ((colour >>> 16) & 0xFF)*0.003921569f;
        ambientColour[1] = ((colour >>> 8) & 0xFF)*0.003921569f;
        ambientColour[2] = (colour & 0xFF)*0.003921569f;
        lightColour[0][0] = ambientColour[0];
        lightColour[0][1] = ambientColour[1];
        lightColour[0][2] = ambientColour[2];
        lightColour[1][0] = ambientColour[0];
        lightColour[1][1] = ambientColour[1];
        lightColour[1][2] = ambientColour[2];
    }

    public static void setAmbientColour(int r, int g, int b){
        ambientColour[0] = (r & 0xFF)*0.003921569f;
        ambientColour[1] = (g & 0xFF)*0.003921569f;
        ambientColour[2] = (b & 0xFF)*0.003921569f;
    }

    public static void setAmbientColour(int colour){
        colour&=0xFFFFFF;
        if(colour <= 0xFF)
            colour = (colour << 16)|(colour << 8)|colour;
        ambientColour[0] = ((colour >>> 16) & 0xFF)*0.003921569f;
        ambientColour[1] = ((colour >>> 8) & 0xFF)*0.003921569f;
        ambientColour[2] = (colour & 0xFF)*0.003921569f;
    }

    public float[] returnLightDirection(){
        float[] forward = {-modelMatrix.returnData(0, 2), -modelMatrix.returnData(1, 2), -modelMatrix.returnData(2, 2)};
        forward = VectorOperations.vectorNormalization(forward);
        forward[0]-=EPSILON;
        forward[1]-=EPSILON;
        forward[2]-=EPSILON;
        return forward;
    }
    //Returns a specific colour of the light in an integer RGB format
    public int returnLightColour(byte index){
        if(index <= 0)
            return 0xFF000000|((int)(ambientColour[0]*255) << 16)|((int)(ambientColour[1]*255) << 8)|((int)(ambientColour[2]*255));
        else
            return 0xFF000000|((int)(lightColour[index-1][0]*255) << 16)|((int)(lightColour[index-1][1]*255) << 8)|((int)(lightColour[index-1][2]*255));
    }

    public static float[] returnAmbientColour(){
        float[] returnColour = {ambientColour[0], ambientColour[1], ambientColour[2]};
        return returnColour;
    }

    //Returns a deep copy of a light's colour
    public float[][] returnLightColour(){
        float[][] returnColour = {{ambientColour[0], ambientColour[1], ambientColour[2]},
                                  {lightColour[0][0], lightColour[0][1], lightColour[0][2]},
                                  {lightColour[1][0], lightColour[1][1], lightColour[1][2]}};
        return returnColour;
    }

    public float[][] returnDiffuseAndSpecularColour(){
        float[][] returnColour = {{lightColour[0][0], lightColour[0][1], lightColour[0][2]},
                                  {lightColour[1][0], lightColour[1][1], lightColour[1][2]}};
        return returnColour;
    }

    public float spreadBrightness(float dot){
        if(dot < spotlightSpread[1])
            return 0;
        if(dot >= spotlightSpread[0])
            return 1;
        float spreadDiff = spotlightSpread[1] - spotlightSpread[0];
        if(Math.abs(spreadDiff) <= EPSILON){
            if(dot < spotlightSpread[0])
                return 0;
            return 1;
        }
        return (spotlightSpread[1] - dot)/spreadDiff;
    }

    public void copy(Object o){
        if(o instanceof Light){
            Light l = (Light)o;
            super.copy(l);
            lightColour[0][0] = l.lightColour[0][0];
            lightColour[0][1] = l.lightColour[0][1];
            lightColour[0][2] = l.lightColour[0][2];
            lightColour[1][0] = l.lightColour[1][0];
            lightColour[1][1] = l.lightColour[1][1];
            lightColour[1][2] = l.lightColour[1][2];
            spotlightSpread[0] = l.spotlightSpread[0];
            spotlightSpread[1] = l.spotlightSpread[1];
            lightType = l.lightType;
            intensities[0] = l.intensities[0];
            intensities[1] = l.intensities[1];
        }
    }
    public void copy(Light l){
        super.copy(l);
        lightColour[0][0] = l.lightColour[0][0];
        lightColour[0][1] = l.lightColour[0][1];
        lightColour[0][2] = l.lightColour[0][2];
        lightColour[1][0] = l.lightColour[1][0];
        lightColour[1][1] = l.lightColour[1][1];
        lightColour[1][2] = l.lightColour[1][2];
        spotlightSpread[0] = l.spotlightSpread[0];
        spotlightSpread[1] = l.spotlightSpread[1];
        lightType = l.lightType;
        intensities[0] = l.intensities[0];
        intensities[1] = l.intensities[1];
    }

    public boolean equals(Object o){
        if(o instanceof Light){
            Light l = (Light)o;
            boolean isEquals = super.equals(l);
            isEquals&=(Math.abs(lightColour[0][0]-l.lightColour[0][0]) <= EPSILON);
            isEquals&=(Math.abs(lightColour[0][1]-l.lightColour[0][1]) <= EPSILON);
            isEquals&=(Math.abs(lightColour[0][2]-l.lightColour[0][2]) <= EPSILON);
            isEquals&=(Math.abs(lightColour[1][0]-l.lightColour[1][0]) <= EPSILON);
            isEquals&=(Math.abs(lightColour[1][1]-l.lightColour[1][1]) <= EPSILON);
            isEquals&=(Math.abs(lightColour[1][2]-l.lightColour[1][2]) <= EPSILON);
            isEquals&=(Math.abs(intensities[0] - l.intensities[0]) <= EPSILON);
            isEquals&=(Math.abs(intensities[1] - l.intensities[1]) <= EPSILON);
            isEquals&=(Math.abs(spotlightSpread[0] - l.spotlightSpread[0]) <= EPSILON);
            isEquals&=(Math.abs(spotlightSpread[1] - l.spotlightSpread[1]) <= EPSILON);
            isEquals&=(lightType == l.lightType);
            return isEquals;
        }
        else
            return false;
    }

    public boolean equals(Light l){
        boolean isEquals = super.equals(l);
        isEquals&=(Math.abs(lightColour[0][0]-l.lightColour[0][0]) <= EPSILON);
        isEquals&=(Math.abs(lightColour[0][1]-l.lightColour[0][1]) <= EPSILON);
        isEquals&=(Math.abs(lightColour[0][2]-l.lightColour[0][2]) <= EPSILON);
        isEquals&=(Math.abs(lightColour[1][0]-l.lightColour[1][0]) <= EPSILON);
        isEquals&=(Math.abs(lightColour[1][1]-l.lightColour[1][1]) <= EPSILON);
        isEquals&=(Math.abs(lightColour[1][2]-l.lightColour[1][2]) <= EPSILON);
        isEquals&=(Math.abs(intensities[0] - l.intensities[0]) <= EPSILON);
        isEquals&=(Math.abs(intensities[1] - l.intensities[1]) <= EPSILON);
        isEquals&=(Math.abs(spotlightSpread[0] - l.spotlightSpread[0]) <= EPSILON);
        isEquals&=(Math.abs(spotlightSpread[1] - l.spotlightSpread[1]) <= EPSILON);
        isEquals&=(lightType == l.lightType);
        return isEquals;
    }
}