/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.commons.util.mule.transformer;

import jp.rough_diamond.commons.util.PropertyUtils;

import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractTransformer;

abstract public class PropertyBaseTransformer extends AbstractTransformer {
	
	@Override
	protected Object doTransform(Object src, String encoding) throws TransformerException {
		Object dest = newTransformObject();
		PropertyUtils.copyProperties(src, dest);
		afterProcess(src, dest, encoding);
		return dest;
	}

	protected void afterProcess(Object src, Object dest, String encoding) throws TransformerException { }
	
	abstract protected Object newTransformObject();
}
