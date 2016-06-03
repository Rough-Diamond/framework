package jp.rough_diamond.commons.util.mule.transformer.test.stub.sampleservice;

import jp.rough_diamond.commons.util.mule.transformer.AbstractObjectToJAXBElement;

public class ObjectToJAXBElement_ListToList extends AbstractObjectToJAXBElement {
    public final static ObjectToJAXBElement_ListToList INSTANCE = new ObjectToJAXBElement_ListToList();

    @Override
    protected String getOperation() {
        return "listToList";
    }

    @Override
    protected Class<?> getPortType() {
        return SampleServicePortType.class;
    }
}
