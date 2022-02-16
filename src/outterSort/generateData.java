package outterSort;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class generateData {
    public static void main(String[] args) throws IOException {
        final int MAX=12;
        File f=new File("myInputFile.txt");
        if (f.exists())
            f.delete();
        BufferedWriter bufw=new BufferedWriter(new FileWriter(f));
        Random rand = new Random(); //instance of random class
        int upperbound = 25;
        //generate random values from 0-24
        for (int i=0;i<MAX;++i){
            bufw.write(rand.nextInt(upperbound)+"");
            bufw.newLine();
        }
        bufw.flush();
        bufw.close();
    }
    public static String getRandomString(){
        StringBuilder sb=new StringBuilder();
        Random random=new Random();
        for (int i = 0; i < 8; i++) {
            sb.append((char)(random.nextInt(26)+97));

        }
        sb.append(',');
        for (int i = 0; i <16 ; i++) {
            sb.append((char)(random.nextInt(26)+97));
        }

        return sb.toString();
    }
}
