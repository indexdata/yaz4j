package org.yaz4j;

import java.io.UnsupportedEncodingException;
import java.io.IOException;

public class Yaz4jMain {
    // java -cp ./bin: -Djava.library.path=./libyaz4j org.yaz4j.Yaz4jMain

    public static void main(String[] args) throws UnsupportedEncodingException, IOException {
        Connection conn = new Connection("talisbase.talis.com", 210);
        conn.setDatabaseName("unionm21");
        conn.setUsername("fred");
        conn.setPassword("apple");
        conn.setSyntax("USMarc"); // USMarc, Sutrs, XML, opac, UKMarc

        PrefixQuery query = new PrefixQuery("@attr 1=4 \"pottering\"");
        ResultSet results = conn.Search(query);

        int resultsSize = results.getSize();
        System.out.println("Found " + resultsSize + " records");

        for (int i = 0; i < resultsSize; i++) {
            Record record = results.getRecord(i);
            System.out.write(record.getContent());
        }
        conn.Dispose();
    }
}
