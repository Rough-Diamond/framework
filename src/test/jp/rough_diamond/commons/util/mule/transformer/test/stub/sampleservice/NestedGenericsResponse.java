
package jp.rough_diamond.commons.util.mule.transformer.test.stub.sampleservice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for nestedGenericsResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="nestedGenericsResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="return" type="{http://sample.rough_diamond.jp/}string2ArrayOfChildBeanMap" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "nestedGenericsResponse", propOrder = {
    "_return"
})
public class NestedGenericsResponse {

    @XmlElement(name = "return")
    protected String2ArrayOfChildBeanMap _return;

    /**
     * Gets the value of the return property.
     * 
     * @return
     *     possible object is
     *     {@link String2ArrayOfChildBeanMap }
     *     
     */
    public String2ArrayOfChildBeanMap getReturn() {
        return _return;
    }

    /**
     * Sets the value of the return property.
     * 
     * @param value
     *     allowed object is
     *     {@link String2ArrayOfChildBeanMap }
     *     
     */
    public void setReturn(String2ArrayOfChildBeanMap value) {
        this._return = value;
    }

}
