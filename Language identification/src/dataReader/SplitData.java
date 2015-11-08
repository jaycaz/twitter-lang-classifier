package dataReader;
import java.io.*;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by May on 11/8/15.
 */
public class SplitData {
    static String[] filenames = {"abk", "afr", "aka", "amh", "amu", "ara", "arg", "asm", "ast", "awa", "aym" ,"aze" ,"bam" ,"bel" ,"ben" ,"bih" ,"bis" ,"bos" ,"bpy" ,"bre" ,"bug" ,"bul" ,"cak" ,"cat" ,"cco" ,"ceb" ,"ces" ,"cha" ,"che" ,"cho" ,"chr" ,"chv" ,"ckb" ,"cor" ,"cos" ,"crh" ,"cym" ,"dan" ,"deu" ,"div" ,"dzo","ell" ,"eml" ,"eng" ,"epo" ,"est" ,"eus" ,"ewe" ,"fao" ,"fas" ,"fij" ,"fin" ,"fra" ,"frp" ,"fry" ,"ful" ,"gla" ,"gle" ,"glg" ,"glv" ,"grn" ,"guj" ,"hat" ,"hau" ,"haw" ,"heb" ,"her" ,"hil", "hin","hrv" ,"hun" ,"hye" ,"ibo" ,"iku" ,"ilo" ,"ind" ,"isl" ,"jac" ,"jav" ,"jpn" ,"kab" ,"kal" ,"kan" ,"kat" ,"kaz" ,"kek" ,"khm" ,"kik" ,"kin" ,"kir" ,"kom" ,"kor" ,"kur" ,"lad" ,"lao" ,"lat" ,"lav" ,"lez" ,"lij" ,"lin" ,"lit" ,"lmo" ,"ltz" ,"lug" ,"mal" ,"mam" ,"mar" ,"min" ,"mkd" ,"mlg" ,"mlt" ,"mon" ,"mri" ,"msa" ,"mus" ,"mya" ,"mzn" ,"nah" ,"nap" ,"nav" ,"ndo" ,"nds" ,"nep" ,"new" ,"nld" ,"nno" ,"nob" ,"nor" ,"nya" ,"oci" ,"ori" ,"orm" ,"pam" ,"pan" ,"pdc" ,"pdt" ,"pms" ,"pol" ,"por" ,"ppl" ,"pus" ,"quc" ,"que" ,"roh" ,"ron" ,"rus" ,"scn" ,"sco" ,"sin" ,"slk" ,"slv" ,"sme" ,"smo" ,"sna" ,"snd" ,"som" ,"spa" ,"sqi" ,"srd" ,"srp" ,"sun" ,"swa" ,"swe" ,"tah" ,"tam" ,"tat" ,"tel" ,"tgk" ,"tgl" ,"tha" ,"tir" ,"ton" ,"tpi" ,"tsn" ,"tum" ,"tur" ,"twi" ,"udm" ,"uig" ,"ukr" ,"urd" ,"usp" ,"uzb" ,"vec" ,"ven" ,"vie" ,"vol" ,"war" ,"wln" ,"wol" ,"xal" ,"xho" ,"yid" ,"yor" ,"zh-yue" ,"zha" ,"zho" ,"zul" };
    //static String[] filenames = {"abk"};
    static final String Path = "data/";
    static final String extension = ".txt";
    static int num_paragraphs = 0, maxParagraphs = 1000;

    public static void main(String []args) {


        try {

            for (int i = 0; i < filenames.length; i++) {
                //For Each File
                BufferedReader br = new BufferedReader(new FileReader(Path + filenames[i] + extension));

                int num_lines =  countLines(Path + filenames[i] + extension);
                int train_num = (int)(num_lines*0.6);
                int test_num = (int)(num_lines*0.85);
                String sCurrentLine;

                BufferedWriter bw = new BufferedWriter(new FileWriter(Path + filenames[i] + "_train" + extension));


                while ((sCurrentLine = br.readLine()) != null) {


                    num_paragraphs++;
                    if(num_paragraphs == train_num)
                    {
                        //Change to test data after 60% of paragraphs are read.
                        bw.close();
                        bw = new BufferedWriter(new FileWriter(Path + filenames[i] + "_test" + extension));

                    }
                    if (num_paragraphs == test_num)
                    {
                        //Change to dev data after 85% of paragraphs are read.
                        bw.close();
                        bw = new BufferedWriter(new FileWriter(Path + filenames[i] + "_dev" + extension));
                    }

                    bw.write(sCurrentLine);

                }
                bw.close();



                num_paragraphs = 0;

            }

        } catch (Exception e) {
            System.out.println(e.fillInStackTrace());
        }


    }



    public static int countLines(String filename) throws IOException {
        InputStream is = new BufferedInputStream(new FileInputStream(filename));
        try {
            byte[] c = new byte[1024];
            int count = 0;
            int readChars = 0;
            boolean empty = true;
            while ((readChars = is.read(c)) != -1) {
                empty = false;
                for (int i = 0; i < readChars; ++i) {
                    if (c[i] == '\n') {
                        ++count;
                    }
                }
            }
            return (count == 0 && !empty) ? 1 : count;
        } finally {
            is.close();
        }
    }


}


