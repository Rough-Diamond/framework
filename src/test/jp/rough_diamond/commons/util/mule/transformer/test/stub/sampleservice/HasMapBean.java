
package jp.rough_diamond.commons.util.mule.transformer.test.stub.sampleservice;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for HasMapBean complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="HasMapBean">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="map" type="{http://sample.rough_diamond.jp/}string2stringMap" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "HasMapBean", namespace = "http://test.transformer.mule.util.commons.rough_diamond.jp", propOrder = {
    "map"
})
public class HasMapBean {

    @XmlElementRef(name = "map", namespace = "http://test.transformer.mule.util.commons.rough_diamond.jp", type = JAXBElement.class)
    protected JAXBElement<String2StringMap> map;

    /**
     * Gets the value of the map property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String2StringMap }{@code >}
     *     
     */
    public JAXBElement<String2StringMap> getMap() {
        return map;
    }

    /**
     * Sets the value of the map property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String2StringMap }{@code >}
     *     
     */
    public void setMap(JAXBElement<String2StringMap> value) {
        this.map = ((JAXBElement<String2StringMap> ) value);
    }

}
