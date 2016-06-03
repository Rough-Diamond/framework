package jp.rough_diamond.sample.esb.service.stub.sampleservice;

import jp.rough_diamond.commons.util.mule.transformer.AbstractObjectToJAXBElement;

public class ObjectToJAXBElement_SayHello2 extends AbstractObjectToJAXBElement {
    public final static ObjectToJAXBElement_SayHello2 INSTANCE = new ObjectToJAXBElement_SayHello2();

    @Override
    protected String getOperation() {
        return "sayHello2";
    }

    @Override
    protected Class<?> getPortType() {
        return SampleServicePortType.class;
    }
}
