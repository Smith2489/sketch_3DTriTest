package Maths;

public class BitOperations{
    public static byte reverse(byte x){
        byte output = 0;
        for(int i = 0; i < 8; i++){
            output<<=1;
            output|=(x & 1);
            x>>>=1;
        }
        return output;
    }
    public static short reverse(short x){
        short output = 0;
        for(int i = 0; i < 16; i++){
            output<<=1;
            output|=(x & 1);
            x>>>=1;
        }
        return output;
    }
    public static int reverse(int x){
        int output = 0;
        for(int i = 0; i < 32; i++){
            output<<=1;
            output|=(x & 1);
            x>>>=1;
        }
        return output;
    }
    public static long reverse(long x){
        long output = 0;
        for(int i = 0; i < 64; i++){
            output<<=1;
            output|=(x & 1);
            x>>>=1;
        }
        return output;
    }
    public static short interleave(byte x, byte y){
        short output = 0;
        for(int i = 0; i < 16; i+=2){
            output|=((x & 1) << i)|((y & 1) << (i+1));
            x>>>=1;
            y>>>=1;
        }
        return output;
    }
    public static int interleave(short x, short y){
        int output = 0;
        for(int i = 0; i < 32; i+=2){
            output|=((x & 1) << i)|((y & 1) << (i+1));
            x>>>=1;
            y>>>=1;
        }
        return output;
    }

    public static long interleave(int x, int y){
        long output = 0;
        for(int i = 0; i < 64; i+=2){
            output|=((x & 1) << i)|((y & 1) << (i+1));
            x>>>=1;
            y>>>=1;
        }
        return output;
    }
}
