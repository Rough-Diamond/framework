package jp.rough_diamond.commons.util.mule.transformer.test.stub.sampleservice;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

/**
 * This class was generated by Apache CXF 2.1.2
 * Fri Jul 31 21:13:51 JST 2009
 * Generated source version: 2.1.2
 * 
 */
 
@WebService(targetNamespace = "http://sample.rough_diamond.jp/", name = "SampleServicePortType")
@XmlSeeAlso({ObjectFactory.class})
public interface SampleServicePortType {

    @WebResult(name = "return", targetNamespace = "http://sample.rough_diamond.jp/")
    @RequestWrapper(localName = "listToList", targetNamespace = "http://sample.rough_diamond.jp/", className = "jp.rough_diamond.commons.util.mule.transformer.test.stub.sampleservice.ListToList")
    @ResponseWrapper(localName = "listToListResponse", targetNamespace = "http://sample.rough_diamond.jp/", className = "jp.rough_diamond.commons.util.mule.transformer.test.stub.sampleservice.ListToListResponse")
    @WebMethod
    public jp.rough_diamond.commons.util.mule.transformer.test.stub.sampleservice.ArrayOfString listToList(
        @WebParam(name = "arg0", targetNamespace = "http://sample.rough_diamond.jp/")
        jp.rough_diamond.commons.util.mule.transformer.test.stub.sampleservice.ArrayOfChildBean arg0
    );

    @WebResult(name = "return", targetNamespace = "http://sample.rough_diamond.jp/")
    @RequestWrapper(localName = "nestedGenerics", targetNamespace = "http://sample.rough_diamond.jp/", className = "jp.rough_diamond.commons.util.mule.transformer.test.stub.sampleservice.NestedGenerics")
    @ResponseWrapper(localName = "nestedGenericsResponse", targetNamespace = "http://sample.rough_diamond.jp/", className = "jp.rough_diamond.commons.util.mule.transformer.test.stub.sampleservice.NestedGenericsResponse")
    @WebMethod
    public jp.rough_diamond.commons.util.mule.transformer.test.stub.sampleservice.String2ArrayOfChildBeanMap nestedGenerics(
        @WebParam(name = "arg0", targetNamespace = "http://sample.rough_diamond.jp/")
        jp.rough_diamond.commons.util.mule.transformer.test.stub.sampleservice.String2ArrayOfChildBeanMap arg0
    );

    @WebResult(name = "return", targetNamespace = "http://sample.rough_diamond.jp/")
    @RequestWrapper(localName = "mapToMap", targetNamespace = "http://sample.rough_diamond.jp/", className = "jp.rough_diamond.commons.util.mule.transformer.test.stub.sampleservice.MapToMap")
    @ResponseWrapper(localName = "mapToMapResponse", targetNamespace = "http://sample.rough_diamond.jp/", className = "jp.rough_diamond.commons.util.mule.transformer.test.stub.sampleservice.MapToMapResponse")
    @WebMethod
    public jp.rough_diamond.commons.util.mule.transformer.test.stub.sampleservice.String2StringMap mapToMap(
        @WebParam(name = "arg0", targetNamespace = "http://sample.rough_diamond.jp/")
        jp.rough_diamond.commons.util.mule.transformer.test.stub.sampleservice.String2ChildBeanMap arg0
    );

    @WebResult(name = "return", targetNamespace = "http://sample.rough_diamond.jp/")
    @RequestWrapper(localName = "hasMapToHasMap", targetNamespace = "http://sample.rough_diamond.jp/", className = "jp.rough_diamond.commons.util.mule.transformer.test.stub.sampleservice.HasMapToHasMap")
    @ResponseWrapper(localName = "hasMapToHasMapResponse", targetNamespace = "http://sample.rough_diamond.jp/", className = "jp.rough_diamond.commons.util.mule.transformer.test.stub.sampleservice.HasMapToHasMapResponse")
    @WebMethod
    public jp.rough_diamond.commons.util.mule.transformer.test.stub.sampleservice.HasMapBean hasMapToHasMap(
        @WebParam(name = "arg0", targetNamespace = "http://sample.rough_diamond.jp/")
        jp.rough_diamond.commons.util.mule.transformer.test.stub.sampleservice.HasMapBean arg0
    );

    @WebResult(name = "return", targetNamespace = "http://sample.rough_diamond.jp/")
    @RequestWrapper(localName = "dimArray", targetNamespace = "http://sample.rough_diamond.jp/", className = "jp.rough_diamond.commons.util.mule.transformer.test.stub.sampleservice.DimArray")
    @ResponseWrapper(localName = "dimArrayResponse", targetNamespace = "http://sample.rough_diamond.jp/", className = "jp.rough_diamond.commons.util.mule.transformer.test.stub.sampleservice.DimArrayResponse")
    @WebMethod
    public jp.rough_diamond.commons.util.mule.transformer.test.stub.sampleservice.ArrayOfArrayOfString dimArray(
        @WebParam(name = "arg0", targetNamespace = "http://sample.rough_diamond.jp/")
        jp.rough_diamond.commons.util.mule.transformer.test.stub.sampleservice.ArrayOfArrayOfString arg0
    );

    @RequestWrapper(localName = "doIt", targetNamespace = "http://sample.rough_diamond.jp/", className = "jp.rough_diamond.commons.util.mule.transformer.test.stub.sampleservice.DoIt")
    @ResponseWrapper(localName = "doItResponse", targetNamespace = "http://sample.rough_diamond.jp/", className = "jp.rough_diamond.commons.util.mule.transformer.test.stub.sampleservice.DoItResponse")
    @WebMethod
    public void doIt(
        @WebParam(name = "arg0", targetNamespace = "http://sample.rough_diamond.jp/")
        jp.rough_diamond.commons.util.mule.transformer.test.stub.sampleservice.ParentBean arg0
    );
}
