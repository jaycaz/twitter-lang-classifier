import org.classifier.NGramChunkClassify;
import org.dataReader.ReadData;
import org.util.Language;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by May on 11/20/15.
 */
public class NGramChunkTest {

    public String dataPath = "";
    static String[] filenames = {"abk", "afr", "aka", "amh", "amu", "ara", "arg", "asm", "ast", "awa", "aym" ,"aze" ,"bam" ,"bel" ,"ben" ,"bih" ,"bis" ,"bos" ,"bpy" ,"bre" ,"bug" ,"bul" ,"cak" ,"cat" ,"cco" ,"ceb" ,"ces" ,"cha" ,"che" ,"cho" ,"chr" ,"chv" ,"ckb" ,"cor" ,"cos" ,"crh" ,"cym" ,"dan" ,"deu" ,"div" ,"dzo","ell" ,"eml" ,"epo" ,"est" ,"eus" ,"ewe" ,"fao" ,"fas" ,"fij" ,"fin"  ,"frp" ,"fry" ,"ful" ,"gla" ,"gle" ,"glg" ,"glv" ,"grn" ,"guj" ,"hat" ,"hau" ,"haw" ,"heb" ,"her" ,"hil", "hin","hrv"  ,"hye" ,"ibo" ,"iku" ,"ilo" ,"ind" ,"isl" ,"jac" ,"jav" ,"jpn" ,"kab" ,"kal" ,"kan" ,"kat" ,"kaz" ,"kek" ,"khm" ,"kik" ,"kin" ,"kir" ,"kom" ,"kor" ,"kur" ,"lad" ,"lao" ,"lat" ,"lav" ,"lez" ,"lij" ,"lin" ,"lit" ,"lmo" ,"ltz" ,"lug" ,"mal" ,"mam" ,"mar" ,"min" ,"mkd" ,"mlg" ,"mlt" ,"mon" ,"mri" ,"msa" ,"mus" ,"mya" ,"mzn" ,"nah" ,"nap" ,"nav" ,"ndo" ,"nds" ,"nep" ,"new"  ,"nno" ,"nob" ,"nor" ,"nya" ,"oci" ,"ori" ,"orm" ,"pam" ,"pan" ,"pdc" ,"pdt" ,"pms"  ,"ppl" ,"pus" ,"quc" ,"que" ,"roh" ,"ron" ,"rus" ,"scn" ,"sco" ,"sin" ,"slk" ,"slv" ,"sme" ,"smo" ,"sna" ,"snd" ,"som"  ,"sqi" ,"srd" ,"srp" ,"sun" ,"swa"  ,"tah" ,"tam" ,"tat" ,"tel" ,"tgk" ,"tgl" ,"tha" ,"tir" ,"ton" ,"tpi" ,"tsn" ,"tum" ,"tur" ,"twi" ,"udm" ,"uig" ,"ukr" ,"urd" ,"usp" ,"uzb" ,"vec" ,"ven" ,"vie" ,"vol" ,"war" ,"wln" ,"wol" ,"xal" ,"xho" ,"yid" ,"yor" ,"zh-yue" ,"zha" ,"zho" ,"zul" };
    //static String[] filenames = {"fra"};
        public static void main(String []args) {
            int i = 0, max = 4;
            System.out.println("NGramChnkTest is active!");
            ReadData reader = new ReadData();
            NGramChunkClassify classifier = new NGramChunkClassify();
            System.out.println("Read in Data!");
            HashMap<Language, ArrayList<String>> TrainingData;
            for (String language: filenames) {

                    System.out.println("Training...! " + language);
//                    System.out.println(classifier.nGramProb.containsKey(new Language(language)) + classifier.nGramProb.keySet().toString() + new Language(language).toString() + classifier.nGramProb.get(new Language(language)));
                    classifier.train(reader, language);

               // org.classifier.retainTop(language);

            }
            System.out.println("Finished Training. Now evaluating...!");
            i = 0;
            HashMap<Language, ArrayList<String>> testData;
            for (String language: filenames) {
                //   if (i++ == max) break;
                //while((testData = reader.getNextChunk("_test", language))!= null)
                testData = reader.getNextChunk("_test", language);
                    classifier.accuracy(testData);
            }

            double taccuracy = classifier.getAccuracy();
            System.out.println("Acccuracy on test: " + taccuracy);

            

    }

}
