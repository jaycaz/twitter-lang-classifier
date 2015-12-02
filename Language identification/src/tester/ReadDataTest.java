package tester;

import dataReader.ReadData;

import java.io.BufferedReader;

import static org.junit.Assert.assertFalse;

public class ReadDataTest {

    ReadData reader;

    @org.junit.Before
    public void setUp() throws Exception {
        reader = new ReadData();
    }

    @org.junit.After
    public void tearDown() throws Exception {

    }

    @org.junit.Test
    public void testGetInputMap() throws Exception {
//        HashMap<Language, ArrayList<ArrayList<String>>> devMap = reader.getInputMap("_dev");
//        assertFalse("Dev data not successfully retrieved", devMap.isEmpty());
//
//        HashMap<Language, ArrayList<ArrayList<String>>> trainMap = reader.getInputMap("_train");
//        assertFalse("Train data not successfully retrieved", trainMap.isEmpty());
//
//        HashMap<Language, ArrayList<ArrayList<String>>> testMap = reader.getInputMap("_test");
//        assertFalse("Test data not successfully retrieved", testMap.isEmpty());
    }

    @org.junit.Test
    public void testGetLangReader() throws Exception {
        BufferedReader frenchReader = reader.getLangReader("fra");
        String frenchLine = frenchReader.readLine();
        assertFalse("Reader for language data not successfully retrieved", frenchLine.isEmpty());
    }

    @org.junit.Test
    public void testGetInputWords() throws Exception {

    }

    @org.junit.Test
    public void testGetInputSentences() throws Exception {

    }
}