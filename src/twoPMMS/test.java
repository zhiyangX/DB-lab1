package twoPMMS;

import java.io.File;

public class test {
    public static void main(String[] args) throws Exception {
        long start=System.currentTimeMillis();
        File inputFile=new File("myInputFile.txt");
        File outputFile=new File("outputFile.txt");
        File tempFile=new File("temp");
        if (outputFile.exists())
            outputFile.delete();
        main.test(inputFile,outputFile,tempFile);
        long end=System.currentTimeMillis();
        System.out.print("Total time usage: ");
        System.out.print(String.format("%d",end-start));
        System.out.print(" milliseconds");
    }
}
