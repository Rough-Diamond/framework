package jp.rough_diamond.sample.esb.service;

import java.util.*;

import javax.jws.WebService;
import jp.rough_diamond.framework.es.EnterpriseService;
import jp.rough_diamond.framework.es.ServiceConnecter;

@WebService(
    serviceName="SampleService",
    name="SampleServicePortType",
    portName="SampleServicePort"
)
@SuppressWarnings("all")
public interface SampleService extends EnterpriseService {
    @ServiceConnecter(serviceName="SampleServiceConnector_sayHello")
    public void sayHello(
    );
    @ServiceConnecter(serviceName="SampleServiceConnector_sayHello2")
    public String sayHello2(
    );
    @ServiceConnecter(serviceName="SampleServiceConnector_sayHello3")
    public String sayHello3(
            String name
    );
}