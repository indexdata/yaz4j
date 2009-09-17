package yaz4jtest;
import org.junit.*;
import static org.junit.Assert.*;
import java.util.*;

public class ConnectionTest {
	@Test
	public void testConnection() {
		org.yaz4j.Connection con = new org.yaz4j.Connection("z3950.indexdata.dk:210/gils", 0);
		assertNotNull(con);
		org.yaz4j.PrefixQuery pqf = new org.yaz4j.PrefixQuery("@attr 1=4 utah");
		con.Search(pqf);
	}
}
