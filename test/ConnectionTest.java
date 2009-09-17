package yaz4jtest;
import org.junit.*;
import static org.junit.Assert.*;
import java.util.*;

public class ConnectionTest {
	@Test
	public void testConnection() {
		org.yaz4j.Connection con = new org.yaz4j.Connection("z3950.indexdata.dk:210/gils", 0);
		assertNotNull(con);
		con.setSyntax("sutrs");
		org.yaz4j.PrefixQuery pqf = new org.yaz4j.PrefixQuery("@attr 1=4 utah");
		assertNotNull(pqf);
		org.yaz4j.ResultSet s = con.Search(pqf);
		assertNotNull(s);
		assertEquals(s.getSize(), 9);
		org.yaz4j.Record rec = s.getRecord(0);
		assertNotNull(rec);
		byte [] content = rec.getContent();
		// first SUTRS record
		assertEquals(content.length, 1940);
		assertEquals(content[0], 103);
	}
}
