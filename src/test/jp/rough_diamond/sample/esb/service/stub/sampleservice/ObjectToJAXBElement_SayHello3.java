package jp.rough_diamond.sample.esb.service.stub.sampleservice;

import jp.rough_diamond.commons.util.mule.transformer.AbstractObjectToJAXBElement;

public class ObjectToJAXBElement_SayHello3 extends AbstractObjectToJAXBElement {
    public final static ObjectToJAXBElement_SayHello3 INSTANCE = new ObjectToJAXBElement_SayHello3();

    @Override
    protected String getOperation() {
        return "sayHello3";
    }

    @Override
    protected Class<?> getPortType() {
        return SampleServicePortType.class;
    }
}
