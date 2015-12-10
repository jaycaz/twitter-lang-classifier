package tester;

/**
 * Created by May on 12/1/15.
 */
public class TwitterTest {
  //  static String[] filenames = {"abk", "afr", "aka", "amh", "amu", "ara", "arg", "asm", "ast", "awa", "aym" ,"aze" ,"bam" ,"bel" ,"ben" ,"bih" ,"bis" ,"bos" ,"bpy" ,"bre" ,"bug" ,"bul" ,"cak" ,"cat" ,"cco" ,"ceb" ,"ces" ,"cha" ,"che" ,"cho" ,"chr" ,"chv" ,"ckb" ,"cor" ,"cos" ,"crh" ,"cym" ,"dan" ,"deu" ,"div" ,"dzo","ell" ,"eml" ,"epo" ,"est" ,"eus" ,"ewe" ,"fao" ,"fas" ,"fij" ,"fin"  ,"frp" ,"fry" ,"ful" ,"gla" ,"gle" ,"glg" ,"glv" ,"grn" ,"guj" ,"hat" ,"hau" ,"haw" ,"heb" ,"her" ,"hil", "hin","hrv"  ,"hye" ,"ibo" ,"iku" ,"ilo" ,"ind" ,"isl" ,"jac" ,"jav" ,"jpn" ,"kab" ,"kal" ,"kan" ,"kat" ,"kaz" ,"kek" ,"khm" ,"kik" ,"kin" ,"kir" ,"kom" ,"kor" ,"kur" ,"lad" ,"lao" ,"lat" ,"lav" ,"lez" ,"lij" ,"lin" ,"lit" ,"lmo" ,"ltz" ,"lug" ,"mal" ,"mam" ,"mar" ,"min" ,"mkd" ,"mlg" ,"mlt" ,"mon" ,"mri" ,"msa" ,"mus" ,"mya" ,"mzn" ,"nah" ,"nap" ,"nav" ,"ndo" ,"nds" ,"nep" ,"new"  ,"nno" ,"nob" ,"nor" ,"nya" ,"oci" ,"ori" ,"orm" ,"pam" ,"pan" ,"pdc" ,"pdt" ,"pms"  ,"ppl" ,"pus" ,"quc" ,"que" ,"roh" ,"ron" ,"rus" ,"scn" ,"sco" ,"sin" ,"slk" ,"slv" ,"sme" ,"smo" ,"sna" ,"snd" ,"som"  ,"sqi" ,"srd" ,"srp" ,"sun" ,"swa"  ,"tah" ,"tam" ,"tat" ,"tel" ,"tgk" ,"tgl" ,"tha" ,"tir" ,"ton" ,"tpi" ,"tsn" ,"tum" ,"tur" ,"twi" ,"udm" ,"uig" ,"ukr" ,"urd" ,"usp" ,"uzb" ,"vec" ,"ven" ,"vie" ,"vol" ,"war" ,"wln" ,"wol" ,"xal" ,"xho" ,"yid" ,"yor" ,"zh-yue" ,"zha" ,"zho" ,"zul" };
    public static void main(String []args){
//        SplitData sp = new SplitData();
//        sp.splitFile("data/twitter_data_labled.tsv");

      /* ReadData reader = new ReadData();
        NGramClassifier classifier = new NGramClassifier();
        HashMap<String, ArrayList<String>> TrainingData;


        while((TrainingData = reader.getNextTweets("data/twitter_train.txt"))!= null){
           classifier.train(TrainingData);
        }





        //TrainingData = ReadData.getInputSentences("_train");
        //classifier.train(TrainingData);


        HashMap<String, ArrayList<String>> testData;


        double sum=0, count=0;
        while((testData = reader.getNextTweets("data/twitter_test.txt"))!= null){
            if(testData.size() == 0)
                continue;
            sum+=classifier.accuracy(testData);
            count+=1;
            //System.out.println(sum + " " + count);
        }
        System.out.println(sum/count);

*/


/*        TwitterNGramClassifier classifier = new TwitterNGramClassifier();

        HashMap<String, ArrayList<String>> TrainingData;
        classifier.train(reader, "data/twitter_train.txt");

        HashMap<String, ArrayList<String>> testData;

        while((testData = reader.getNextTweets("data/twitter_test.txt"))!= null){
            classifier.accuracy(testData);
        }
        double taccuracy = classifier.getAccuracy();
        System.out.println("Acccuracy on test: " + taccuracy);*/

     /*   try {
            BufferedReader br = new BufferedReader(new FileReader("test.txt"));
            String s = br.readLine();
            System.out.println(s );
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
*/
    }




}

