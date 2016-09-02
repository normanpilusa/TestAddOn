package org.openoffice.testaddon;
import java.io.*;
import java.util.*;
/**
 *
 * @author N.D
 * modified Norman Pilusa
 */
public class SpellChecker {

    public static String check(String word) throws IOException {

        //System.out.print("Enter a sentence to spellcheck=> ");
        ///Scanner scn = new Scanner(System.in);
        String searchstring, input = word;//scn.nextLine();
        //long lStartTime = System.currentTimeMillis();

        String misW = preprocessin(input);
        if (misW.equals("")) {
            //System.out.println("All words were correctly spelled");
            //xtext.insertString(xtext.getEnd(), "All words correctly spelled", true);
            return null;
        } else {
            StringTokenizer ErrorDWords = new StringTokenizer(misW);
            int numberoftokens = ErrorDWords.countTokens();
            for (int i = 0; i < numberoftokens; i++) {
                searchstring = ErrorDWords.nextToken();
                boolean check = errordetection(searchstring);
                if (check) {
                    //System.out.println(searchstring + " is correctly spelled");
                    //xtext.insertString(xtext.getEnd(), searchstring + " is correctly spelled", true);
                    return null;
                } else {
                    //System.out.println(searchstring + " is wrongly spelled");
                    //xtext.insertString(xtext.getEnd(), searchstring + " is wrongly spelled", true);
                    return searchstring;
                }

            }
        }
        //long lEndTime = System.currentTimeMillis();
        //long difference = lEndTime - lStartTime;
        //System.out.println("Elapsed milliseconds: " + difference);
        return null;
    }

    //Preprocesssing algorithm 
    public static String preprocessin(String str) throws IOException {
        String searchstring, store;
        StringBuilder builder = new StringBuilder();
        StringTokenizer sTok = new StringTokenizer(str.toLowerCase());

        try {
            int numberoftokens = sTok.countTokens();
            int count;
            for (int i = 0; i < numberoftokens; i++) {
                searchstring = sTok.nextToken();
                count = search(searchstring, "ukwebalanacorpus.txt");
                if (count == 0) {
                    builder.append(searchstring);
                    builder.append(" ");
                }
            }
        } catch (FileNotFoundException e) {
            System.err.format("File does not exist\n");
        }
        store = builder.toString();
        return store;
    }

    //Searches for a string in a text file
    public static int search(String search, String filePath) throws FileNotFoundException {
        boolean found = false;
        int count = 0;
        String searchn;
        java.io.File file;
        file = new java.io.File(filePath);

        Scanner input = new Scanner(file);
        while (found != true && input.hasNext()) {
            searchn = input.next();
            found = search.toLowerCase().equals(searchn.toLowerCase());
            if (found) {
                count++;
            }
        }
        return count;
    }

    public static boolean errordetection(String str) throws IOException {
        String searchstring;
        boolean found = false;
        int fre = 0;
        double calFre = 0, threshold = 0.003;
        int totalW = 50000;

        if (str.length() < 3) {
            found = true;
        } else {

            StringTokenizer sTok = new StringTokenizer(str.toLowerCase());
            int numberoftokens = sTok.countTokens();
            try {
                
                for (int i = 0; i < numberoftokens; i++) {
                    searchstring = sTok.nextToken();
                    int pos = 0;
                    while (pos < searchstring.length()) {
                        str = searchstring.substring(pos, pos + 3);
                        fre = 1;

                        calFre = (double) fre / (double) totalW;

                        if (calFre < threshold) {
                            found = false;
                            pos = searchstring.length();
                            i = numberoftokens;
                        } else {
                            found = true;

                        }

                        if (pos + 3 >= searchstring.length()) {
                            pos = searchstring.length();
                        }
                        pos++;
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return found;
    }
 
}