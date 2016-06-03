/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.commons.util.mule.transformer.test;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.namespace.QName;

public class ObjectFactory {
    private final static QName _AcceptAcceptId_QNAME = new QName("http://vo.edi.isken.co.jp", "acceptId");
    private final static QName _AcceptDetails_QNAME = new QName("http://vo.edi.isken.co.jp", "details");
    private final static QName _AcceptDetailsItemId_QNAME = new QName("http://vo.edi.isken.co.jp", "itemId");

    public JAXBElementBean createJAXElementBean() {
		return new JAXBElementBean();
	}

    @XmlElementDecl(namespace = "http://vo.edi.isken.co.jp", name = "acceptId", scope = JAXBElementBean.class)
    public JAXBElement<String> createJAXBElementBeanAcceptId(String value) {
        return new JAXBElement<String>(_AcceptAcceptId_QNAME, String.class, JAXBElementBean.class, value);
    }

    /**
     * Create an instance of {@link AcceptDetails }
     * 
     */
    public Details createDetails() {
        return new Details();
    }

    /**
     * Create an instance of {@link ArrayOfAcceptDetails }
     * 
     */
    public ArrayOfDetails createArrayOfDetails() {
        return new ArrayOfDetails();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfAcceptDetails }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://vo.edi.isken.co.jp", name = "details", scope = JAXBElementBean.class)
    public JAXBElement<ArrayOfDetails> createJAXBElementBeanDetails(ArrayOfDetails value) {
        return new JAXBElement<ArrayOfDetails>(_AcceptDetails_QNAME, ArrayOfDetails.class, JAXBElementBean.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Long }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://vo.edi.isken.co.jp", name = "itemId", scope = Details.class)
    public JAXBElement<Long> createDetailsItemId(Long value) {
        return new JAXBElement<Long>(_AcceptDetailsItemId_QNAME, Long.class, Details.class, value);
    }
}
