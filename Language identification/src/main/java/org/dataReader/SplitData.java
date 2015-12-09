package org.dataReader;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by May on 11/8/15.
 */
public class SplitData {
    static String[] filenames = {"abk", "afr", "aka", "amh", "amu", "ara", "arg", "asm", "ast", "awa", "aym" ,"aze" ,"bam" ,"bel" ,"ben" ,"bih" ,"bis" ,"bos" ,"bpy" ,"bre" ,"bug" ,"bul" ,"cak" ,"cat" ,"cco" ,"ceb" ,"ces" ,"cha" ,"che" ,"cho" ,"chr" ,"chv" ,"ckb" ,"cor" ,"cos" ,"crh" ,"cym" ,"dan" ,"deu" ,"div" ,"dzo","ell" ,"eml" ,"eng" ,"epo" ,"est" ,"eus" ,"ewe" ,"fao" ,"fas" ,"fij" ,"fin" ,"fra" ,"frp" ,"fry" ,"ful" ,"gla" ,"gle" ,"glg" ,"glv" ,"grn" ,"guj" ,"hat" ,"hau" ,"haw" ,"heb" ,"her" ,"hil", "hin","hrv" ,"hun" ,"hye" ,"ibo" ,"iku" ,"ilo" ,"ind" ,"isl" ,"jac" ,"jav" ,"jpn" ,"kab" ,"kal" ,"kan" ,"kat" ,"kaz" ,"kek" ,"khm" ,"kik" ,"kin" ,"kir" ,"kom" ,"kor" ,"kur" ,"lad" ,"lao" ,"lat" ,"lav" ,"lez" ,"lij" ,"lin" ,"lit" ,"lmo" ,"ltz" ,"lug" ,"mal" ,"mam" ,"mar" ,"min" ,"mkd" ,"mlg" ,"mlt" ,"mon" ,"mri" ,"msa" ,"mus" ,"mya" ,"mzn" ,"nah" ,"nap" ,"nav" ,"ndo" ,"nds" ,"nep" ,"new" ,"nld" ,"nno" ,"nob" ,"nor" ,"nya" ,"oci" ,"ori" ,"orm" ,"pam" ,"pan" ,"pdc" ,"pdt" ,"pms" ,"pol" ,"por" ,"ppl" ,"pus" ,"quc" ,"que" ,"roh" ,"ron" ,"rus" ,"scn" ,"sco" ,"sin" ,"slk" ,"slv" ,"sme" ,"smo" ,"sna" ,"snd" ,"som" ,"spa" ,"sqi" ,"srd" ,"srp" ,"sun" ,"swa" ,"swe" ,"tah" ,"tam" ,"tat" ,"tel" ,"tgk" ,"tgl" ,"tha" ,"tir" ,"ton" ,"tpi" ,"tsn" ,"tum" ,"tur" ,"twi" ,"udm" ,"uig" ,"ukr" ,"urd" ,"usp" ,"uzb" ,"vec" ,"ven" ,"vie" ,"vol" ,"war" ,"wln" ,"wol" ,"xal" ,"xho" ,"yid" ,"yor" ,"zh-yue" ,"zha" ,"zho" ,"zul" };

    public static void main(String []args) {
        Random rand = new Random();
        rand.setSeed(0);
        char[] buf = new char[1024];


        //split for smaller files
        if(splitSmall())
            return;

        int random_array[] = {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2 ,2 , 3 , 3 , 3}; // array with 60, 25, 15 proability

        for (int i = 0; i < filenames.length; i++) {
            //For Each File
            //BufferedReader br = new BufferedReader(new FileReader(Path + filenames[i] + EXTENSION));
            ReadData reader = new ReadData();
            BufferedReader br;
            try {
                br = reader.getLangReader(filenames[i]);
            }
            catch (IOException e) {
                System.out.println("Could not find/open file '" + filenames[i] + ReadData.EXTENSION + "' to split, skipping");
                continue;
            }

            String sCurrentLine;
            FileOutputStream f_train;
            FileOutputStream f_test;
            FileOutputStream f_dev;

            try {
                f_train = new FileOutputStream(ReadData.getLangZipPath(filenames[i], "_train").toFile());
                ZipOutputStream out_train = new ZipOutputStream(new BufferedOutputStream(f_train));
//                ZipEntry entry_train = new ZipEntry(filenames[i] + ReadData.TRAIN + ReadData.EXTENSION);
                out_train.putNextEntry(new ZipEntry(filenames[i] + ReadData.TRAIN + ReadData.EXTENSION));

                f_test = new FileOutputStream(ReadData.getLangZipPath(filenames[i], "_test").toFile());
                ZipOutputStream out_test = new ZipOutputStream(new BufferedOutputStream(f_test));
                out_test.putNextEntry(new ZipEntry(filenames[i] + ReadData.TEST + ReadData.EXTENSION));

                f_dev = new FileOutputStream(ReadData.getLangZipPath(filenames[i], "_dev").toFile());
                ZipOutputStream out_dev = new ZipOutputStream(new BufferedOutputStream(f_dev));
                out_dev.putNextEntry(new ZipEntry(filenames[i] + ReadData.DEV + ReadData.EXTENSION));

                //                out_train.putNextEntry(new ZipEntry());

                //BufferedWriter bw_train = new BufferedWriter(new FileWriter(ReadData.getLangPath(filenames[i], "_train"));
                //BufferedWriter bw_test = new BufferedWriter(new FileWriter(ReadData.getLangPath(filenames[i], "_test"));
                //BufferedWriter bw_dev = new BufferedWriter(new FileWriter(ReadData.getLangPath(filenames[i], "_dev"));

                String line;
                while ((line = br.readLine()) != null) {
                    line = line.concat("\n");
                    byte[] bytes = line.getBytes(StandardCharsets.UTF_8);
                    int rand_int = random_array[rand.nextInt(random_array.length)];

                    switch (rand_int) {
                        case 1:
                            out_train.write(bytes);
                            break;
                        case 3:
                            out_test.write(bytes);
                            break;
                        case 2:
                            out_dev.write(bytes);
                            break;
                    }
                }

                br.close();

                out_train.closeEntry();
                out_test.closeEntry();
                out_dev.closeEntry();

//                f_dev.close();
//                f_test.close();
//                f_train.close();

                out_train.close();
                out_test.close();
                out_dev.close();

                System.out.println("File " + filenames[i] + ReadData.EXTENSION + " successfully split");

            }
            catch (IOException e) {
                System.out.println("Error occurred while splitting file " + filenames[i] + ReadData.EXTENSION + ", skipping");
                continue;
            }
        }
    }

    private static boolean splitSmall() {

        for(String filename: filenames) {
            try {
                BufferedReader br = new BufferedReader(new FileReader("data/" + filename + "_train.txt"));
                String line;
                int count = 0;
                BufferedWriter bw = new BufferedWriter(new FileWriter("smallData/" + filename + "_train.txt"));
                while ((line = br.readLine()) != null) {
                    //write to file the top 100
                    bw.write(line);
                    bw.newLine();

                    if (count++ == 100)
                        break;
                }
                bw.close();
                br.close();

                br = new BufferedReader(new FileReader("data/" + filename + "_test.txt"));
                bw = new BufferedWriter(new FileWriter("smallData/" + filename + "_test.txt"));
                count = 0;
                while ((line = br.readLine()) != null) {
                    //write to file the top 100
                    bw.write(line);
                    bw.newLine();

                    if (count++ == 100)
                        break;
                }
                bw.close();
                br.close();

            }
            catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        return true;
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


    public void splitFile(String filename){
        BufferedReader br;
        Random rand = new Random();
        int random_array[] = {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2 ,2 , 3 , 3 , 3}; // array with 60, 25, 15 proability
        try {
            br = new BufferedReader(new FileReader(new File(filename)));
            BufferedWriter bw_train = new BufferedWriter(new FileWriter(filename + "_train"));
            BufferedWriter bw_test = new BufferedWriter(new FileWriter(filename + "_test"));
            BufferedWriter bw_dev = new BufferedWriter(new FileWriter(filename + "_dev"));

            String line;
            while ((line = br.readLine()) != null) {
                line = line.concat("\n");
                int rand_int = random_array[rand.nextInt(random_array.length)];

                switch (rand_int) {
                    case 1:
                        bw_train.write(line);
                        break;
                    case 3:
                        bw_test.write(line);
                        break;
                    case 2:
                        bw_dev.write(line);
                        break;
                }
            }

            br.close();
            bw_dev.close();
            bw_test.close();
            bw_train.close();


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
//    public static int getRandom(int[] array) {
//        int rnd = new Random().nextInt(array.length);
//        return array[rnd];
//    }

}


