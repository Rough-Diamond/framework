package jp.rough_diamond.commons.util.datastream;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;


//���̃e�X�g�̓^�C�~���O�̊m�F�������������Ȃ̂ŏd�v�Ȃ̂͏o�͌��ʁB
public class AsyncDataSourceTest {
	@Test
	@SuppressWarnings("serial")
	public void Producer�̕��������������ꍇ() throws Exception {
		List<Integer> list = new ArrayList<Integer>(Arrays.asList(new Integer[]{1,2,3,4,5})){
			@Override
			public Integer get(int index) {
				Integer ret = super.get(index);
				System.out.println("producer:" + ret);
				System.out.flush();
				return ret;
			}

		};
		System.out.println(list);
		DataSource<Integer> ds = new AsyncDataSource<Integer>(new SimpleDataSource<Integer>(list));
		int sum = 0;
		for(Integer i : ds) {
			System.out.println("consumer:" + i);
			System.out.flush();
			Thread.sleep(100);
			sum+= i;
		}
		Assert.assertEquals(15, sum);
	}

	@Test
	@SuppressWarnings("serial")
	public void Consumer�̕��������������ꍇ() throws Exception {
		List<Integer> list = new ArrayList<Integer>(Arrays.asList(new Integer[]{1,2,3,4,5})){
			@Override
			public Integer get(int index) {
				Integer ret = super.get(index);
				System.out.println("producer:" + ret);
				System.out.flush();
				try {
					Thread.sleep(100);
				} catch(InterruptedException e) {
					throw new RuntimeException(e);
				}
				return ret;
			}
		};
		System.out.println(list);
		DataSource<Integer> ds = new AsyncDataSource<Integer>(new SimpleDataSource<Integer>(list));
		int sum = 0;
		for(Integer i : ds) {
			System.out.println("consumer:" + i);
			System.out.flush();
			sum+= i;
		}
		Assert.assertEquals(15, sum);
	}

	@Test
	@SuppressWarnings({ "unchecked", "serial" })
	public void ������DataSource��񓯊��Ɏ�荞��() throws Exception {
		List<Integer> list1 = new ArrayList<Integer>(Arrays.asList(new Integer[]{1,2,3,4,5})) {
			@Override
			public Integer get(int index) {
				Integer ret = super.get(index);
				System.out.println("producer:" + ret);
				System.out.flush();
				try {
					Thread.sleep(100);
				} catch(InterruptedException e) {
					throw new RuntimeException(e);
				}
				return ret;
			}
		};
		List<Integer> list2 = new ArrayList<Integer>(Arrays.asList(new Integer[]{6,7,8,9,10}));
		DataSource<Integer> ds = new AsyncDataSource<Integer>(
				new SimpleDataSource<Integer>(list1), new SimpleDataSource<Integer>(list2));
		int sum = 0;
		for(Integer i : ds) {
			System.out.println("consumer:" + i);
			System.out.flush();
			sum+= i;
		}
		Assert.assertEquals(55, sum);
	}
}
