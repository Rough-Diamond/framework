package jp.rough_diamond.commons.util.mule.transformer.test.stub.sampleservice;

import jp.rough_diamond.commons.util.mule.transformer.AbstractObjectToJAXBElement;

public class ObjectToJAXBElement_HasMapToHasMap extends AbstractObjectToJAXBElement {
    public final static ObjectToJAXBElement_HasMapToHasMap INSTANCE = new ObjectToJAXBElement_HasMapToHasMap();

    @Override
    protected String getOperation() {
        return "hasMapToHasMap";
    }

    @Override
    protected Class<?> getPortType() {
        return SampleServicePortType.class;
    }
}
