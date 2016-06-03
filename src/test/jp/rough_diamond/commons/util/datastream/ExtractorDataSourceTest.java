package jp.rough_diamond.commons.util.datastream;

import static org.junit.Assert.assertEquals;
import jp.rough_diamond.commons.entity.Unit;
import jp.rough_diamond.commons.extractor.Extractor;
import jp.rough_diamond.commons.extractor.Order;
import jp.rough_diamond.commons.extractor.Property;
import jp.rough_diamond.commons.testdata.NumberingLoader;
import jp.rough_diamond.commons.testdata.UnitLoader;
import jp.rough_diamond.commons.testing.DataLoadingTestCase;
import jp.rough_diamond.commons.testing.Loader;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ExtractorDataSourceTest {
	@Before
	public void setUp() throws Exception {
		DataLoadingTestCase.setUpDB();
		Loader.load(UnitLoader.class);
		Loader.load(NumberingLoader.class);
	}

	@After
	public void tearDown() throws Exception {
		DataLoadingTestCase.cleanUpDB();
	}

	@Test
	public void testQueryOnece() throws Exception {
		Extractor ex = new Extractor(Unit.class);
		ex.addOrder(Order.asc(new Property(Unit.ID)));
		DataSource<Unit> ds = new ExtractorDataSource<Unit>(ex);
		long[] ids = {1L,2L,3L,4L,5L};
		int index = 0;
		for(Unit unit : ds) {
			assertEquals("IDが誤っています。", ids[index++], unit.getId().longValue());
		}
		assertEquals("件数が誤っています。", 5, index);
	}
	
	@Test
	public void testQueryMulti() throws Exception {
		//複数回発行してるかはログ見ないとわかんないよorz
		Extractor ex = new Extractor(Unit.class);
		ex.addOrder(Order.asc(new Property(Unit.ID)));
		DataSource<Unit> ds = new ExtractorDataSource<Unit>(ex, true, 2);
		long[] ids = {1L,2L,3L,4L,5L};
		int index = 0;
		for(Unit node : ds) {
			assertEquals("IDが誤っています。", ids[index++], node.getId().longValue());
		}
		assertEquals("件数が誤っています。", 5, index);
	}
	
	@Test
	public void testQueryLimitAndOffset() throws Exception {
		Extractor ex = new Extractor(Unit.class);
		ex.addOrder(Order.asc(new Property(Unit.ID)));
		ex.setOffset(1);
		ex.setLimit(3);
		DataSource<Unit> ds = new ExtractorDataSource<Unit>(ex, true, 2);
		long[] ids = {2L,3L,4L};
		int index = 0;
		for(Unit node : ds) {
			assertEquals("IDが誤っています。[" + index + "]", ids[index++], node.getId().longValue());
		}
		assertEquals("件数が誤っています。", 3, index);
	}
}
