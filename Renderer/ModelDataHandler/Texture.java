package Renderer.ModelDataHandler;

public class Texture{
    private Graphic img = new Graphic();
    private byte flags = 0; //bit 0 = hasRemoval, bit1 = tile
    //r = replace pixels and do not draw areas where specified by graphic
    //m = multiply pixels and do not draw areas where specified by graphic
    //k = replace pixels; areas that are not to be replaced will be fill colour
    //u = multiply pixels; areas that are not to be replaced will be fill colour
    private char pixelMode = 'r';

    public Texture(String fileName){
        img = new Graphic(fileName);
        pixelMode = 'r';
    }

    public void setImage(String fileName){
        img.setImage(fileName);
    }

    public int returnWidth(){
        return img.returnWidth();
    }

    public int returnHeight(){
        return img.returnHeight();
    }

    public int[] returnPixels(){
        return img.returnPixels();
    }

    public int returnInvisColour(){
        return img.returnInvisColour((byte)0);
    }

    public boolean shouldDrawPixel(int pixelIndex){
        return img.shouldDrawPixel(pixelIndex);
    }
    public void setMode(char newMode){
        if(newMode >= 65 && newMode <= 90)
            newMode+=32;
        if(newMode == 'm' || newMode == 'k' || newMode == 'u')
            pixelMode = newMode;
        else
            pixelMode = 'r';
    }

    public char getMode(){
        return pixelMode;
    }

    public void setHasRemoval(boolean hasRemoval){
        if(hasRemoval)
            flags|=1;
        else
            flags&=-2;
    }
    public void tile(){
        flags|=2;
    }
    public void position(){
        flags&=-3;
    }

    public boolean hasRemoval(){
        return (flags & 1) == 1;
    }

    //If true, then the texture will be repeated across the polygon
    //If false, then the texture will be positioned on the polygon, but not repeated
    public boolean isTiled(){
        return (flags & 2) == 2;
    }

    public void copy(Object o){
        if(o instanceof Texture){
            Texture t = (Texture)o;
            img.copy(t.img);
            flags = t.flags;
            pixelMode = t.pixelMode;
        }
    }

    public void copy(Texture t){
        img.copy(t.img);
        flags = t.flags;
        pixelMode = t.pixelMode;
    }

    public boolean equals(Object o){
        if(o instanceof Texture){
            Texture t = (Texture)o;
            boolean isEqual = img.equals(t.img);
            isEqual&=(flags == t.flags);
            isEqual&=(pixelMode == t.pixelMode);
            return isEqual;
        }
        return false;
    }

    public boolean equals(Texture t){
        boolean isEqual = img.equals(t.img);
        isEqual&=(flags == t.flags);
        isEqual&=(pixelMode == t.pixelMode);
        return isEqual;
    }
}
