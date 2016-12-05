package com.example.web;

import static org.springframework.web.bind.annotation.RequestMethod.*;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.Cloud;
import org.springframework.cloud.app.ApplicationInstanceInfo;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.service.ServiceInfo;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.example.cloud.Microservice;
import com.example.cloud.MicroservicesManager;
import com.example.data.AbstractUploader;
import com.example.data.Conversation;
import com.example.data.ConversationRepository;
import com.example.data.DigitImagesRepository;
import com.example.data.Uploader;

@Controller
@RequestMapping("/")
public class HomeController {
	
  @Autowired
  ConversationRepository conversationRepository;
	
  @Autowired 
  DigitImagesRepository repository;
  
  @Autowired
  AbstractUploader uploader;
  
  @Autowired
  private DiscoveryClient discoveryClient;
  
  @Autowired
  Communicator communicator;
  
  @Autowired
  ShownResults shownResults;
  
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
  
  /*
  @ResponseBody
  @RequestMapping("/service-instances/{applicationName}")
  public String serviceInstancesByApplicationName(
          @PathVariable String applicationName) {
	  String result="hello: ";
      List<String> list=this.discoveryClient.getServices();
      for (String s: list){
      	result+=s+" "+this.discoveryClient.getInstances(s).get(index);
      }
      return result;
  }*/
  
  @RequestMapping(method = GET)
  public String home(Model model) {
	int [] a= new int[]{1,12,3};
	
	String appId=cloud.getApplicationInstanceInfo().getAppId();
	Map appProps=cloud.getApplicationInstanceInfo().getProperties();
	model.addAttribute("appId",appId);
	
	
	
	List<ServiceInfo> serviceInfos=cloud.getServiceInfos();
	List<String> servicesIds=new LinkedList<String>();
	for (ServiceInfo serviceInfo: serviceInfos){
		servicesIds.add(serviceInfo.getId());
	}
	
	model.addAttribute("services",servicesIds);
	
	microservicesManager.update();
	
	model.addAttribute("microservices",microservicesManager.getMicroservices().values());
	
	model.addAttribute("hello", "cute");
	model.addAttribute("request",new Request());
	//model.addAttribute("history", history);
	model.addAttribute("history", conversationRepository.getAll());
    return "home";
  }
  
 
  
  
  @RequestMapping(params={"upload"})
  public String upload(Request request,BindingResult bindingResult,Model model){
	  /*
	   * do somthing about user's request.*/
	  System.out.println(request.content);
	  String current=this.microservicesManager.getCurrentMicroservice();
	  String targetUrl=this.microservicesManager.getMicroservices().get(current).URI;
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
	  String reply="";
	  try{
		  reply=restTemplate.getForObject(uri, Reply.class).toString();
	  }catch(Exception e){
		  reply="I'm sorry...There is somthing wrong...";
		  e.printStackTrace();
	  }
	  history.addConversation(new Conversation(request.content, reply));
	  conversationRepository.addConversation(new Conversation(request.content, reply));
	  System.out.println(reply);
	  return "redirect:/";
  }
  
  @RequestMapping(value="/image/{id}")
  public void getUserImage(HttpServletResponse response , @PathVariable("id") int id){
	  /*
	   * this is copied from internet.*/
	   response.setContentType("image/png");
	   try {
		   repository.getADigitImage(id).writeToOutputStream(response.getOutputStream());
	   } catch (IOException e) {
		   e.printStackTrace();
	   }
  }
  
  
  @RequestMapping(value="/chatterbotTest")
  @ResponseBody
  public String chatterbotTest(){
	  	URI uri = UriComponentsBuilder
	  			.fromHttpUrl("http://127.0.0.1:8088/chat")
				.queryParam("request", "What's your name?").build().toUri();
	  	System.out.println(uri.toString());
		return restTemplate.getForObject(uri, Reply.class).toString(); 
  }
  
  @RequestMapping(value="/uploadTest")
  public void uploadTest(HttpServletResponse response){
	System.out.println("r"+repository);
	uploader.uploadTest(repository);
	response.setContentType("text/plain");
	try {
		OutputStream os=response.getOutputStream();
		os.write(("upload test"+(uploader==null)).getBytes());
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	//uploader.uploadTest();
  }
  
   @ResponseBody
   @RequestMapping(value="/communicatorTest")
   public String comunicatorTest(){
	   Result result=communicator.getResult("test");
	   System.out.println(result.getAccuracy());
	   return String.format("acc:%f", result.getAccuracy());
   }
   
   @ResponseBody
   @RequestMapping(value="/shownResultsTest")
   public String shownResultTest(){
	   shownResults.getShownResults();
	   System.out.println(shownResults.getAccuracy());
	 //is there a issue about async because only using .accuracy give us a wrong answer. at least one time getAccuracy is needed.
	   return String.format("acc:%f", shownResults.getAccuracy());
   }

}
