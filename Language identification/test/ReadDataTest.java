import dataReader.ReadData;
import org.junit.Assert;
import util.Language;

import java.util.ArrayList;
import java.util.HashMap;

public class ReadDataTest {

    @org.junit.Before
    public void setUp() throws Exception {

    }

    @org.junit.After
    public void tearDown() throws Exception {

    }

    @org.junit.Test
    public void testGetInputMap() throws Exception {
        ReadData reader = new ReadData();
        HashMap<Language, ArrayList<ArrayList<String>>> map = reader.getInputMap();
        Assert.assertFalse(map.isEmpty());
    }
}