package jp.rough_diamond.commons.util.mule.transformer.test.stub.sampleservice;

import jp.rough_diamond.commons.util.mule.transformer.AbstractObjectToJAXBElement;

public class ObjectToJAXBElement_MapToMap extends AbstractObjectToJAXBElement {
    public final static ObjectToJAXBElement_MapToMap INSTANCE = new ObjectToJAXBElement_MapToMap();

    @Override
    protected String getOperation() {
        return "mapToMap";
    }

    @Override
    protected Class<?> getPortType() {
        return SampleServicePortType.class;
    }
}
