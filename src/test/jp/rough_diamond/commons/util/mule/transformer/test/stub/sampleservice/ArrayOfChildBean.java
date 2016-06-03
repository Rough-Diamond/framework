
package jp.rough_diamond.commons.util.mule.transformer.test.stub.sampleservice;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfChildBean complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfChildBean">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ChildBean" type="{http://test.transformer.mule.util.commons.rough_diamond.jp}ChildBean" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfChildBean", namespace = "http://test.transformer.mule.util.commons.rough_diamond.jp", propOrder = {
    "childBean"
})
public class ArrayOfChildBean {

    @XmlElement(name = "ChildBean", nillable = true)
    protected List<ChildBean> childBean;

    /**
     * Gets the value of the childBean property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the childBean property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getChildBean().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ChildBean }
     * 
     * 
     */
    public List<ChildBean> getChildBean() {
        if (childBean == null) {
            childBean = new ArrayList<ChildBean>();
        }
        return this.childBean;
    }

}
