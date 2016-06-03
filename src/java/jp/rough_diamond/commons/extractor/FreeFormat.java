/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */

package jp.rough_diamond.commons.extractor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * �t���[�t�H�[�}�b�g
 * �Ⴆ�Όv�Z���Ȃǂ𒼐ړ����������ꍇ�Ɏg�p����
 */
public class FreeFormat implements Value {
	public final String format;
	public final List<?> values;
	public FreeFormat(String format, List<?> values) {
		format.getClass();		//NOP	NullPointerException�𑗏o���������ꍇ
		this.format = format;
		if(values == null) {
			this.values = Collections.unmodifiableList(new ArrayList<Object>());
		} else {
			this.values = values;
		}
	}
	public FreeFormat(String format, Object... values) {
		this(format, Arrays.asList(values));
	}

	/**
	 * @return
	 */
	public boolean includeSummaryFunction() {
		for(Object o : values) {
			if(o instanceof SummaryFunction) {
				return true;
			} else if(o instanceof FreeFormat) {
				FreeFormat ff = (FreeFormat)o;
				boolean ret = ff.includeSummaryFunction();
				if(ret) return ret;
			}
		}
		return false;
	}
}
