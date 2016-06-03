package jp.rough_diamond.commons.util.mule.transformer.test.stub.sampleservice;

import jp.rough_diamond.commons.util.mule.transformer.AbstractObjectToJAXBElement;

public class ObjectToJAXBElement_NestedGenerics extends AbstractObjectToJAXBElement {
    public final static ObjectToJAXBElement_NestedGenerics INSTANCE = new ObjectToJAXBElement_NestedGenerics();

    @Override
    protected String getOperation() {
        return "nestedGenerics";
    }

    @Override
    protected Class<?> getPortType() {
        return SampleServicePortType.class;
    }
}
