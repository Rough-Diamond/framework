/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */

package jp.rough_diamond.commons.util.datastream;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import junit.framework.Assert;

import org.junit.Test;

public class DataSourceUtilsTest {
	@Test
	public void toListでDataSourceからListへの変換がおこなわれている事() throws Exception {
		List<Integer> list = new ArrayList<Integer>(Arrays.asList(new Integer[]{1,2,3,4,5}));
		DataSource<Integer> ds = new SimpleDataSource<Integer>(list); 
		List<Integer> listDest = DataSourceUtils.toList(ds);
		Assert.assertEquals(5, listDest.size());
		Assert.assertEquals(1, listDest.get(0).intValue());
		Assert.assertEquals(2, listDest.get(1).intValue());
		Assert.assertEquals(3, listDest.get(2).intValue());
		Assert.assertEquals(4, listDest.get(3).intValue());
		Assert.assertEquals(5, listDest.get(4).intValue());
	}
	
	@Test
	public void makeIteratorで正しくデータソースが作成される事() throws Exception {
		DataSource<Integer> ds = DataSourceUtils.makeIterator(new IterableLogic<Integer>(){
			private int index = 1;
			@Override
			public Queue<Integer> getNextQueue() {
				if(index < 3) {
					Queue<Integer> queue = new LinkedList<Integer>();
					for(int i = 1 ; i <= index ; i++) {
						queue.add(i);
					}
					index++;
					return queue;
				}
				return null;
			}
		});
		List<Integer> list = DataSourceUtils.toList(ds);
		Assert.assertEquals(3, list.size());
		Assert.assertEquals(1, list.get(0).intValue());
		Assert.assertEquals(1, list.get(1).intValue());
		Assert.assertEquals(2, list.get(2).intValue());
	}
}
