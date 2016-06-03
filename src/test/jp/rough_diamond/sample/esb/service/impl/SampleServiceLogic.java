package jp.rough_diamond.sample.esb.service.impl;

import java.util.*;

@SuppressWarnings("all")
public class SampleServiceLogic implements jp.rough_diamond.sample.esb.service.SampleService {
	@Override
	public void sayHello() {
		System.out.println("say Hello.");
	}

	@Override
	public String sayHello2() {
		return "Hello World.";
	}

	@Override
	public String sayHello3(String name) {
		return "Hello " + name + ".";
	}
}