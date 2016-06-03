
package jp.rough_diamond.commons.util.mule.transformer.test.stub.sampleservice;

/**
 * Please modify this class to meet your needs
 * This class is not complete
 */

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
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

public final class SampleServicePortType_SampleServicePort_Client {

    private static final QName SERVICE_NAME = new QName("http://sample.rough_diamond.jp/", "SampleService");

    private SampleServicePortType_SampleServicePort_Client() {
    }

    public static void main(String args[]) throws Exception {
        URL wsdlURL = SampleService.WSDL_LOCATION;
        if (args.length > 0) { 
            File wsdlFile = new File(args[0]);
            try {
                if (wsdlFile.exists()) {
                    wsdlURL = wsdlFile.toURI().toURL();
                } else {
                    wsdlURL = new URL(args[0]);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
      
        SampleService ss = new SampleService(wsdlURL, SERVICE_NAME);
        SampleServicePortType port = ss.getSampleServicePort();  
        
        {
        System.out.println("Invoking listToList...");
        jp.rough_diamond.commons.util.mule.transformer.test.stub.sampleservice.ArrayOfChildBean _listToList_arg0 = new jp.rough_diamond.commons.util.mule.transformer.test.stub.sampleservice.ArrayOfChildBean();
        jp.rough_diamond.commons.util.mule.transformer.test.stub.sampleservice.ArrayOfString _listToList__return = port.listToList(_listToList_arg0);
        System.out.println("listToList.result=" + _listToList__return);


        }
        {
        System.out.println("Invoking nestedGenerics...");
        jp.rough_diamond.commons.util.mule.transformer.test.stub.sampleservice.String2ArrayOfChildBeanMap _nestedGenerics_arg0 = new jp.rough_diamond.commons.util.mule.transformer.test.stub.sampleservice.String2ArrayOfChildBeanMap();
        jp.rough_diamond.commons.util.mule.transformer.test.stub.sampleservice.String2ArrayOfChildBeanMap _nestedGenerics__return = port.nestedGenerics(_nestedGenerics_arg0);
        System.out.println("nestedGenerics.result=" + _nestedGenerics__return);


        }
        {
        System.out.println("Invoking mapToMap...");
        jp.rough_diamond.commons.util.mule.transformer.test.stub.sampleservice.String2ChildBeanMap _mapToMap_arg0 = new jp.rough_diamond.commons.util.mule.transformer.test.stub.sampleservice.String2ChildBeanMap();
        jp.rough_diamond.commons.util.mule.transformer.test.stub.sampleservice.String2StringMap _mapToMap__return = port.mapToMap(_mapToMap_arg0);
        System.out.println("mapToMap.result=" + _mapToMap__return);


        }
        {
        System.out.println("Invoking hasMapToHasMap...");
        jp.rough_diamond.commons.util.mule.transformer.test.stub.sampleservice.HasMapBean _hasMapToHasMap_arg0 = new jp.rough_diamond.commons.util.mule.transformer.test.stub.sampleservice.HasMapBean();
        jp.rough_diamond.commons.util.mule.transformer.test.stub.sampleservice.HasMapBean _hasMapToHasMap__return = port.hasMapToHasMap(_hasMapToHasMap_arg0);
        System.out.println("hasMapToHasMap.result=" + _hasMapToHasMap__return);


        }
        {
        System.out.println("Invoking dimArray...");
        jp.rough_diamond.commons.util.mule.transformer.test.stub.sampleservice.ArrayOfArrayOfString _dimArray_arg0 = null;
        jp.rough_diamond.commons.util.mule.transformer.test.stub.sampleservice.ArrayOfArrayOfString _dimArray__return = port.dimArray(_dimArray_arg0);
        System.out.println("dimArray.result=" + _dimArray__return);


        }
        {
        System.out.println("Invoking doIt...");
        jp.rough_diamond.commons.util.mule.transformer.test.stub.sampleservice.ParentBean _doIt_arg0 = new jp.rough_diamond.commons.util.mule.transformer.test.stub.sampleservice.ParentBean();
        port.doIt(_doIt_arg0);


        }

        System.exit(0);
    }

}
