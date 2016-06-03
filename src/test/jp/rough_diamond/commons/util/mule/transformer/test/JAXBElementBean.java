/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.commons.util.mule.transformer.test;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Accept", propOrder = {
    "acceptDate",
    "acceptId",
    "details",
})
public class JAXBElementBean {
    @XmlElement(namespace = "http://vo.edi.isken.co.jp")
    protected XMLGregorianCalendar acceptDate;
    @XmlElementRef(name = "acceptId", namespace = "http://vo.edi.isken.co.jp", type = JAXBElement.class)
    protected JAXBElement<String> acceptId;
    @XmlElementRef(name = "details", namespace = "http://vo.edi.isken.co.jp", type = JAXBElement.class)
    protected JAXBElement<ArrayOfDetails> details;

    public JAXBElement<String> getAcceptId() {
		return acceptId;
	}

	public void setAcceptId(JAXBElement<String> acceptId) {
		this.acceptId = acceptId;
	}

	/**
     * Gets the value of the acceptDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getAcceptDate() {
        return acceptDate;
    }

    /**
     * Sets the value of the acceptDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setAcceptDate(XMLGregorianCalendar value) {
        this.acceptDate = value;
    }

    /**
     * Gets the value of the details property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfDetails }{@code >}
     *     
     */
    public JAXBElement<ArrayOfDetails> getDetails() {
        return details;
    }

    /**
     * Sets the value of the details property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfDetails }{@code >}
     *     
     */
    public void setDetails(JAXBElement<ArrayOfDetails> value) {
        this.details = ((JAXBElement<ArrayOfDetails> ) value);
    }
}
