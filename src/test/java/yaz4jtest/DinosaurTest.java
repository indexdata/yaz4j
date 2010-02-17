package yaz4jtest;

import org.junit.*;
import static org.junit.Assert.*;
import org.yaz4j.*;

/**
 * @author adam
 */
public class DinosaurTest {

    @Test
    public void test() {
        Connection con = new Connection("z3950.loc.gov:7090/voyager", 0);
        assertNotNull(con);
        con.setSyntax("usmarc");
        PrefixQuery pqf = new PrefixQuery("@attr 1=7 0253333490");
        assertNotNull(pqf);
        ResultSet set = con.search(pqf);
        assertNotNull(set);
        Record rec = set.getRecord(0);
        assertNotNull(rec);
        // System.out.println(rec.render());
    }
}
