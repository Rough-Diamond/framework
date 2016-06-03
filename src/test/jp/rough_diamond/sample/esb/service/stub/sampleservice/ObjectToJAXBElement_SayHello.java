package jp.rough_diamond.sample.esb.service.stub.sampleservice;

import jp.rough_diamond.commons.util.mule.transformer.AbstractObjectToJAXBElement;

public class ObjectToJAXBElement_SayHello extends AbstractObjectToJAXBElement {
    public final static ObjectToJAXBElement_SayHello INSTANCE = new ObjectToJAXBElement_SayHello();

    @Override
    protected String getOperation() {
        return "sayHello";
    }

    @Override
    protected Class<?> getPortType() {
        return SampleServicePortType.class;
    }
}
