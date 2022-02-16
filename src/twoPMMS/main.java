package twoPMMS;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


public class main {
    //we can put max 15000 ints into memory
    static final int maxSize = 15000;


    public static void test(File inputFile, File outputFile, File tempFile) throws Exception {

        BufferedReader bufr = new BufferedReader(new FileReader(inputFile));

        String[] heapArray = new String[maxSize];
        String line = null;//Use to store a record read from buffer
        int i = 0;//i indicate how many records are read into the buffer
        List<File> tempFiles = new ArrayList<>();
        int heapSize = 0;
        //replaceSelection begin
        System.out.println("=================Phase1 started======================");
        while ((line = bufr.readLine()) != null) {
            heapArray[i++] = line;
            if (i == maxSize)
                break;
        }
        while (i == maxSize) {
            heapSize = maxSize;
            File newTempFile = File.createTempFile("tempFile", ".txt", tempFile);
            tempFiles.add(newTempFile);
            BufferedWriter bufw = new BufferedWriter(new FileWriter(newTempFile));
            buildHeap(heapArray, heapSize, 0);
            while (heapSize != 0) {
                bufw.write(heapArray[0]);
                bufw.newLine();
                line = bufr.readLine();
                if (line == null)
                    break;
                if (Integer.parseInt(line)>Integer.parseInt(heapArray[0])) {
                    heapArray[0] = line;
                } else {
                    heapArray[0] = heapArray[heapSize - 1];
                    heapArray[heapSize - 1] = line;
                    heapSize--;
                }
                siftDown(heapArray, 0, heapSize);
            }
            if (heapSize != 0) {//file input is completed
                i = i - heapSize;
                while (heapSize != 0) {
                    bufw.write(heapArray[0]);
                    bufw.newLine();
                    heapArray[0] = heapArray[heapSize - 1];
                    heapSize--;
                    siftDown(heapArray, 0, heapSize);
                }
            }
            bufw.close();
        }
        System.out.println("=================Phase1 ended======================");
        System.out.println("=================Phase2 started======================");
        //continue to read the rest data in buffer
        if (i != 0) {
            heapSize = i;
            File newTempFile = File.createTempFile("tempFile.txt", ".txt", tempFile.getParentFile());
            tempFiles.add(newTempFile);
            BufferedWriter bufw = new BufferedWriter(new FileWriter(newTempFile));
            int offset = maxSize - heapSize;
            buildHeap(heapArray, heapSize, offset);
            while (heapSize != 0) {
                bufw.write(heapArray[offset]);
                bufw.newLine();
                heapArray[offset] = heapArray[offset + heapSize - 1];
                heapSize--;
                siftDown(heapArray, offset, heapSize);

            }
            bufw.close();
        }
        //replaceSelection end,all data are sorted into some separate temFile,all temFile are in tempFiles list.
        //release memory
        heapArray = null;
        System.gc();
        //begin MultiWayMergeSort
        multiWayMergeSort(tempFiles, outputFile);

//delete tempFiles
        for (File file : tempFiles) {
            file.delete();
        }
        System.out.println("==============phase2 ended====================");

        //=================================================================
    }


    private static void buildHeap(String heapArray[], int size, int start) {
        for (int i = size / 2 - 1; i >= start; i--) {
            siftDown(heapArray, i, size);
        }
    }

    private static void siftDown(String[] heapArray, int i, int size) {
        int j = 2 * i + 1;
        String temp = heapArray[i];
        while (j < size) {
            if (j < size - 1 && Integer.parseInt(heapArray[j])>Integer.parseInt(heapArray[j + 1])){
                ++j;
            }
            if (Integer.parseInt(temp)>Integer.parseInt(heapArray[j])) {
                heapArray[i] = heapArray[j];
                i = j;
                j = 2 * j + 1;
            } else break;
        }
        heapArray[i] = temp;
    }

    static void multiWayMergeSort(List<File> files, File outputFile) throws IOException {
        int ways = files.size();
        int length_per_run = maxSize / ways;
        Run[] runs = new Run[ways];
        for (int i = 0; i < ways; i++) {
            runs[i] = new Run(length_per_run);
        }
        List<BufferedReader> rList = new ArrayList<>();
        //read files' data into runs' buffer
        for (int i = 0; i < ways; i++) {
            BufferedReader bufr = new BufferedReader(new FileReader(files.get(i)));
            rList.add(i, bufr);
            int j = 0;
            //runs[i].buffer.length!=0
            while ((runs[i].buffer[j] = bufr.readLine()) != null) {
                ++j;
                if (j == length_per_run)
                    break;
            }
            runs[i].length = j;
            runs[i].index = 0;
        }
        System.out.print(length_per_run/ways);
        System.out.println(" passes in total");
        //merge the files and write to outputFile
        int[] ls = new int[ways];//loser tree
        createLoserTree(ls, runs, ways);
        BufferedWriter bufw = new BufferedWriter(new FileWriter(outputFile));
        int liveRuns = ways;
        while (liveRuns > 0) {
            //System.out.println(runs[ls[0]].buffer[runs[ls[0]].index]);
            bufw.write(runs[ls[0]].buffer[runs[ls[0]].index++]);
            bufw.newLine();
            if (runs[ls[0]].index == runs[ls[0]].length) {
                //reload
                int j = 0;
                while ((runs[ls[0]].buffer[j] = rList.get(ls[0]).readLine()) != null) {
                    j++;
                    if (j == length_per_run) {
                        break;
                    }
                }
                runs[ls[0]].length = j;
                runs[ls[0]].index = 0;
            }
            if (runs[ls[0]].length == 0) {
                liveRuns--;
                //String maxString = new String(maxKey);
                String maxString="99999999";
                //maxString += "\n";
                runs[ls[0]].buffer[runs[ls[0]].index] = maxString;
            }
            adjust(ls, runs, ways, ls[0]);
        }
        bufw.flush();
        bufw.close();
        for (BufferedReader bufr : rList) {
            bufr.close();
        }

    }

    private static void createLoserTree(int[] ls, Run[] runs, int n) {
        //ways equals to the number of nodes in loserTree

        for (int i = 0; i < n; i++) {
            ls[i] = -1;
        }
        for (int i = n - 1; i >= 0; i--) {
            adjust(ls, runs, n, i);
        }
    }

    private static void adjust(int[] ls, Run[] runs, int n, int s) {
        int t = (s + n) / 2;
        int temp = 0;
        while (t != 0) {
            if (s == -1)
                break;
            if (ls[t] == -1 || Integer.parseInt(runs[s].buffer[runs[s].index])>Integer.parseInt(runs[ls[t]].buffer[runs[ls[t]].index])) {
                temp = s;
                s = ls[t];
                ls[t] = temp;
            }
            t /= 2;
        }
        ls[0] = s;
    }


    static class Run {
        String[] buffer;
        int length;
        int index;

        Run(int length) {
            this.length = length;
            buffer = new String[length];
        }
    }
    //=================================================================
}
