
package jp.rough_diamond.commons.util.mule.transformer.test.stub.sampleservice;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for ParentBean complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ParentBean">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="array" type="{http://sample.rough_diamond.jp/}ArrayOfString" minOccurs="0"/>
 *         &lt;element name="boolean1" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="cal" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="child" type="{http://test.transformer.mule.util.commons.rough_diamond.jp}ChildBean" minOccurs="0"/>
 *         &lt;element name="date" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="int1" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="list" type="{http://sample.rough_diamond.jp/}ArrayOfString" minOccurs="0"/>
 *         &lt;element name="xxx" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ParentBean", namespace = "http://test.transformer.mule.util.commons.rough_diamond.jp", propOrder = {
    "array",
    "boolean1",
    "cal",
    "child",
    "date",
    "int1",
    "list",
    "xxx"
})
public class ParentBean {

    @XmlElementRef(name = "array", namespace = "http://test.transformer.mule.util.commons.rough_diamond.jp", type = JAXBElement.class)
    protected JAXBElement<ArrayOfString> array;
    protected Boolean boolean1;
    @XmlElementRef(name = "cal", namespace = "http://test.transformer.mule.util.commons.rough_diamond.jp", type = JAXBElement.class)
    protected JAXBElement<XMLGregorianCalendar> cal;
    @XmlElementRef(name = "child", namespace = "http://test.transformer.mule.util.commons.rough_diamond.jp", type = JAXBElement.class)
    protected JAXBElement<ChildBean> child;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar date;
    @XmlElementRef(name = "int1", namespace = "http://test.transformer.mule.util.commons.rough_diamond.jp", type = JAXBElement.class)
    protected JAXBElement<Integer> int1;
    @XmlElementRef(name = "list", namespace = "http://test.transformer.mule.util.commons.rough_diamond.jp", type = JAXBElement.class)
    protected JAXBElement<ArrayOfString> list;
    @XmlElementRef(name = "xxx", namespace = "http://test.transformer.mule.util.commons.rough_diamond.jp", type = JAXBElement.class)
    protected JAXBElement<String> xxx;

    /**
     * Gets the value of the array property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfString }{@code >}
     *     
     */
    public JAXBElement<ArrayOfString> getArray() {
        return array;
    }

    /**
     * Sets the value of the array property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfString }{@code >}
     *     
     */
    public void setArray(JAXBElement<ArrayOfString> value) {
        this.array = ((JAXBElement<ArrayOfString> ) value);
    }

    /**
     * Gets the value of the boolean1 property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isBoolean1() {
        return boolean1;
    }

    /**
     * Sets the value of the boolean1 property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setBoolean1(Boolean value) {
        this.boolean1 = value;
    }

    /**
     * Gets the value of the cal property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public JAXBElement<XMLGregorianCalendar> getCal() {
        return cal;
    }

    /**
     * Sets the value of the cal property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public void setCal(JAXBElement<XMLGregorianCalendar> value) {
        this.cal = ((JAXBElement<XMLGregorianCalendar> ) value);
    }

    /**
     * Gets the value of the child property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ChildBean }{@code >}
     *     
     */
    public JAXBElement<ChildBean> getChild() {
        return child;
    }

    /**
     * Sets the value of the child property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ChildBean }{@code >}
     *     
     */
    public void setChild(JAXBElement<ChildBean> value) {
        this.child = ((JAXBElement<ChildBean> ) value);
    }

    /**
     * Gets the value of the date property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDate() {
        return date;
    }

    /**
     * Sets the value of the date property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDate(XMLGregorianCalendar value) {
        this.date = value;
    }

    /**
     * Gets the value of the int1 property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Integer }{@code >}
     *     
     */
    public JAXBElement<Integer> getInt1() {
        return int1;
    }

    /**
     * Sets the value of the int1 property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Integer }{@code >}
     *     
     */
    public void setInt1(JAXBElement<Integer> value) {
        this.int1 = ((JAXBElement<Integer> ) value);
    }

    /**
     * Gets the value of the list property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfString }{@code >}
     *     
     */
    public JAXBElement<ArrayOfString> getList() {
        return list;
    }

    /**
     * Sets the value of the list property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfString }{@code >}
     *     
     */
    public void setList(JAXBElement<ArrayOfString> value) {
        this.list = ((JAXBElement<ArrayOfString> ) value);
    }

    /**
     * Gets the value of the xxx property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getXxx() {
        return xxx;
    }

    /**
     * Sets the value of the xxx property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setXxx(JAXBElement<String> value) {
        this.xxx = ((JAXBElement<String> ) value);
    }

}
