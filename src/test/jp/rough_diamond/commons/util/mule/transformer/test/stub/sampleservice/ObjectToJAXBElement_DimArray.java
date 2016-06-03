package jp.rough_diamond.commons.util.mule.transformer.test.stub.sampleservice;

import jp.rough_diamond.commons.util.mule.transformer.AbstractObjectToJAXBElement;

public class ObjectToJAXBElement_DimArray extends AbstractObjectToJAXBElement {
    public final static ObjectToJAXBElement_DimArray INSTANCE = new ObjectToJAXBElement_DimArray();

    @Override
    protected String getOperation() {
        return "dimArray";
    }

    @Override
    protected Class<?> getPortType() {
        return SampleServicePortType.class;
    }
}
