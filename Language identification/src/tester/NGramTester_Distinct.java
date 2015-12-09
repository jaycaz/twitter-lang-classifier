import org.classifier.NGramClassifierDistinct;
import org.dataReader.ReadData;
import org.util.Language;

import java.util.ArrayList;
import java.util.HashMap;

public class NGramTester_Distinct {

    public String dataPath = "";
    static String[] filenames = {"abk", "afr", "aka", "amh", "amu", "ara", "arg", "asm", "ast", "awa", "aym" ,"aze" ,"bam" ,"bel" ,"ben" ,"bih" ,"bis" ,"bos" ,"bpy" ,"bre" ,"bug" ,"bul" ,"cak" ,"cat" ,"cco" ,"ceb" ,"ces" ,"cha" ,"che" ,"cho" ,"chr" ,"chv" ,"ckb" ,"cor" ,"cos" ,"crh" ,"cym" ,"dan" ,"deu" ,"div" ,"dzo","ell" ,"eml" ,"epo" ,"est" ,"eus" ,"ewe" ,"fao" ,"fas" ,"fij" ,"fin"  ,"frp" ,"fry" ,"ful" ,"gla" ,"gle" ,"glg" ,"glv" ,"grn" ,"guj" ,"hat" ,"hau" ,"haw" ,"heb" ,"her" ,"hil", "hin","hrv"  ,"hye" ,"ibo" ,"iku" ,"ilo" ,"ind" ,"isl" ,"jac" ,"jav" ,"jpn" ,"kab" ,"kal" ,"kan" ,"kat" ,"kaz" ,"kek" ,"khm" ,"kik" ,"kin" ,"kir" ,"kom" ,"kor" ,"kur" ,"lad" ,"lao" ,"lat" ,"lav" ,"lez" ,"lij" ,"lin" ,"lit" ,"lmo" ,"ltz" ,"lug" ,"mal" ,"mam" ,"mar" ,"min" ,"mkd" ,"mlg" ,"mlt" ,"mon" ,"mri" ,"msa" ,"mus" ,"mya" ,"mzn" ,"nah" ,"nap" ,"nav" ,"ndo" ,"nds" ,"nep" ,"new"  ,"nno" ,"nob" ,"nor" ,"nya" ,"oci" ,"ori" ,"orm" ,"pam" ,"pan" ,"pdc" ,"pdt" ,"pms"  ,"ppl" ,"pus" ,"quc" ,"que" ,"roh" ,"ron" ,"rus" ,"scn" ,"sco" ,"sin" ,"slk" ,"slv" ,"sme" ,"smo" ,"sna" ,"snd" ,"som"  ,"sqi" ,"srd" ,"srp" ,"sun" ,"swa"  ,"tah" ,"tam" ,"tat" ,"tel" ,"tgk" ,"tgl" ,"tha" ,"tir" ,"ton" ,"tpi" ,"tsn" ,"tum" ,"tur" ,"twi" ,"udm" ,"uig" ,"ukr" ,"urd" ,"usp" ,"uzb" ,"vec" ,"ven" ,"vie" ,"vol" ,"war" ,"wln" ,"wol" ,"xal" ,"xho" ,"yid" ,"yor" ,"zh-yue" ,"zha" ,"zho" ,"zul" };
    public static void main(String []args) {
        int i = 0, max = 10;
        System.out.println("NGramTest is active!");
        ReadData reader = new ReadData();
        NGramClassifierDistinct classifier = new NGramClassifierDistinct();
        System.out.println("Read in Data!");
        for (String language: filenames) {
           // if (i++ == max) break;

            HashMap<Language, ArrayList<String>> TrainingData = reader.getLanguageData("_train", language);

            System.out.println("Training...! " + language );
            classifier.train(TrainingData);

        }
        System.out.println("Finished Training. Now evaluating...!");
        //i = 0;
        for (String language: filenames) {
         //   if (i++ == max) break;
            HashMap<Language, ArrayList<String>> testData = reader.getLanguageData("_test", language);
            classifier.accuracy(testData);

        }


        double taccuracy = classifier.getAccuracy();
        System.out.println("Acccuracy on test: " + taccuracy);
        //double tfscore = org.classifier.f1(testData);
        //System.out.println("F1: " + tfscore);
        //org.classifier.f1ByClass(data);

    }

}
