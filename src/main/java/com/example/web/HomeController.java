package com.example.web;

import static org.springframework.web.bind.annotation.RequestMethod.*;

import java.net.URI;
import java.util.LinkedList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.Cloud;
import org.springframework.cloud.service.ServiceInfo;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import com.example.cloud.MicroservicesManager;
import com.example.data.Conversation;
import com.example.data.ConversationRepository;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

@Controller
@RequestMapping("/")
public class HomeController {
	
  @Autowired
  ConversationRepository conversationRepository;
  
  @Autowired
  RestTemplate restTemplate;
  
  @Autowired
  History history;
  
  @Autowired 
  Cloud cloud;
  
  @Autowired
  Environment env;
  
  @Autowired
  MicroservicesManager microservicesManager;
  
  
  
  @RequestMapping(method = GET)
  public String home(Model model) {
	String appId=cloud.getApplicationInstanceInfo().getAppId();
	cloud.getApplicationInstanceInfo().getProperties();
	model.addAttribute("appId",appId);

	
	List<ServiceInfo> serviceInfos=cloud.getServiceInfos();
	List<String> servicesIds=new LinkedList<String>();
	for (ServiceInfo serviceInfo: serviceInfos){
		servicesIds.add(serviceInfo.getId());
		System.out.println("NAME:"+serviceInfo.getId());
	}
	
	System.out.println(servicesIds.size());
	
	model.addAttribute("services",servicesIds);
	
	microservicesManager.update();
	
	model.addAttribute("microservices",microservicesManager.getMicroservices().values());
	
	model.addAttribute("hello", "cute");
	model.addAttribute("request",new Request());
	//model.addAttribute("history", history);
	model.addAttribute("history", conversationRepository.getAll());
    return "home";
  }
  
  @HystrixCommand(fallbackMethod = "fallBack")
  @RequestMapping(params={"upload"})
  public String upload(Request request,BindingResult bindingResult,Model model){
	  /*
	   * do somthing about user's request.*/
	  System.out.println(request.content);
	  String current=this.microservicesManager.getCurrentMicroservice();
	  String targetUrl=null;
	  try{
		  targetUrl=this.microservicesManager.getMicroservices().get(current).URI;
	  }catch(NullPointerException e){
		  e.printStackTrace();
		  String reply="No services available...";
		  conversationRepository.addConversation(new Conversation(request.content, reply));
		  return "redirect:/";
	  }
	  String [] profiles=env.getActiveProfiles();
	  for (String profile:profiles){
		  if (profile.equals("cloud")){//strip port
			  int index=targetUrl.lastIndexOf(":");
			  targetUrl=targetUrl.substring(0, index);
			  break;
		  }
	  }
	  
	  URI uri = UriComponentsBuilder
	  			.fromHttpUrl(String.format("%s/chat",targetUrl))
				.queryParam("request", request.content).build().toUri();
	  System.out.println("Send to "+targetUrl);
	  String reply=restTemplate.getForObject(uri, Reply.class).toString();
	  history.addConversation(new Conversation(request.content, reply));
	  conversationRepository.addConversation(new Conversation(request.content, reply));
	  System.out.println(reply);
	  return "redirect:/";
  }
  
  public String fallBack(Request request,BindingResult bindingResult,Model model){
	  String reply="You are welcome...(There is something wrong!)";
	  history.addConversation(new Conversation(request.content, reply));
	  conversationRepository.addConversation(new Conversation(request.content, reply));	  
	  System.out.println("fallback:"+reply);
	  return "redirect:/";
  }
  
  

  @ResponseBody
  @RequestMapping("hello")
  public String home() {
     return "hello from web-db.";
  }
  
  @ResponseBody
  @RequestMapping("cloud")
  public String cloud(){
	  String infor="";
	  infor+=cloud.getApplicationInstanceInfo().getAppId();
	  System.out.println("cloud: "+infor);
	  return infor;
  }
  
  @RequestMapping("cloud/infos")
  public String info(Model model){
	  model.addAttribute("cloudInfos",cloud.getCloudProperties());
	  return "infos";
  }
  
  @RequestMapping(value="/microservice/{name}")
  public String setMicroservice(@PathVariable("name") String name){
	  for (String microservice: microservicesManager.getMicroservices().keySet()){
		  if (microservice.equals(name)){
			  System.out.println("pick: "+name);
			  microservicesManager.pickCurrentMicroservice(microservice);
			  break;
		  }
	  }
	  return "redirect:/";
  }
  

}
