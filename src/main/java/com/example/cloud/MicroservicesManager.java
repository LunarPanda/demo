package com.example.cloud;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

@Component
@SessionScope
public class MicroservicesManager {
	
	@Value("${spring.application.name}")
	private String appName;
	
	@Autowired
	DiscoveryClient discoveryClient;
	
	Map<String,Microservice> microservices=null;
	String currentMicroservice=null;

	public MicroservicesManager() {
		super();
		microservices=new HashMap<String,Microservice>();
	}
	
	public void update(){
		Set<String> services=new HashSet<String>();
		services.addAll(discoveryClient.getServices());
		
		for (String microservice: microservices.keySet()){
			if (!services.contains(microservice)){
				if (currentMicroservice.equals(microservice)){
					currentMicroservice=null;
				}
				microservices.remove(microservice);
			}
		}
		
		
		for (String s:services){
			if (s.equals(appName)){
				continue;
			
			}
			if (!microservices.containsKey(s)){
				ServiceInstance si=discoveryClient.getInstances(s).get(0);
				microservices.put(s,new Microservice(s,si.getUri().toString()));
			}
		}
		
		
		
		if (microservices.isEmpty()){
			this.pickCurrentMicroservice(null);
		}else{
			if (currentMicroservice==null){
				List<String> microservicesList=new LinkedList<String>();
				microservicesList.addAll(microservices.keySet());
				this.pickCurrentMicroservice(microservicesList.get(0));
			}
		}
	}
	
	public void pickCurrentMicroservice(String current){
		for (Microservice m:microservices.values()){
			m.state=Microservice.STATE_OFF;
		}
		if (current!=null){
			microservices.get(current).state=Microservice.STATE_ON;
		}
		this.currentMicroservice=current;
	}

	public DiscoveryClient getDiscoveryClient() {
		return discoveryClient;
	}

	public void setDiscoveryClient(DiscoveryClient discoveryClient) {
		this.discoveryClient = discoveryClient;
	}

	public Map<String, Microservice> getMicroservices() {
		return microservices;
	}

	public void setMicroservices(Map<String, Microservice> microservices) {
		this.microservices = microservices;
	}

	public String getCurrentMicroservice() {
		return currentMicroservice;
	}

	
	
	
}
