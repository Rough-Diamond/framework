
package jp.rough_diamond.commons.util.mule.transformer.test.stub.sampleservice;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ChildBean complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ChildBean">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="yyy" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="zzz" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ChildBean", namespace = "http://test.transformer.mule.util.commons.rough_diamond.jp", propOrder = {
    "yyy",
    "zzz"
})
public class ChildBean {

    @XmlElementRef(name = "yyy", namespace = "http://test.transformer.mule.util.commons.rough_diamond.jp", type = JAXBElement.class)
    protected JAXBElement<String> yyy;
    @XmlElementRef(name = "zzz", namespace = "http://test.transformer.mule.util.commons.rough_diamond.jp", type = JAXBElement.class)
    protected JAXBElement<String> zzz;

    /**
     * Gets the value of the yyy property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getYyy() {
        return yyy;
    }

    /**
     * Sets the value of the yyy property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setYyy(JAXBElement<String> value) {
        this.yyy = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the zzz property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getZzz() {
        return zzz;
    }

    /**
     * Sets the value of the zzz property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setZzz(JAXBElement<String> value) {
        this.zzz = ((JAXBElement<String> ) value);
    }

}
