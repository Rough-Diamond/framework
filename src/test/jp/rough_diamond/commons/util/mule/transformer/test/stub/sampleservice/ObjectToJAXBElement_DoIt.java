package jp.rough_diamond.commons.util.mule.transformer.test.stub.sampleservice;

import jp.rough_diamond.commons.util.mule.transformer.AbstractObjectToJAXBElement;

public class ObjectToJAXBElement_DoIt extends AbstractObjectToJAXBElement {
    public final static ObjectToJAXBElement_DoIt INSTANCE = new ObjectToJAXBElement_DoIt();

    @Override
    protected String getOperation() {
        return "doIt";
    }

    @Override
    protected Class<?> getPortType() {
        return SampleServicePortType.class;
    }
}
