package com.example.data;

import java.util.List;

import javax.sql.DataSource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import static org.junit.Assert.*;

import com.example.config.RootConfig;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes=RootConfig.class)
@ActiveProfiles("local")
public class LocalDataBaseTests {
	
	@Autowired
	DigitImagesRepository repository;
	
	@Autowired
	Uploader uploader;

	//@Test
	public void operationsTest(){
		byte[] data=new byte[]{1,0,0,0,1,0};
		DigitImage dImage = new DigitImage(0,10,data);
		
		
		repository.deleteAll();
		repository.insertADigitImage(dImage);
		
		List<DigitImage> imageList = repository.fetchDigitImages(1);
		DigitImage rs=imageList.get(0);
		
		assertEquals(dImage.id, rs.id);
		assertEquals(dImage.type, rs.type);
		assertArrayEquals(dImage.data,rs.data);
	}
	
	
	@Test
	public void uploadTest(){
		String dataFile="src/main/resources/data/t10k-images.idx3-ubyte";
		String labelFile="src/main/resources/data/t10k-labels.idx1-ubyte";
		int num=1000;
		int startId=0;
		
		uploader.upload(repository,dataFile, labelFile, num, startId);
		
		List<DigitImage> imageList = repository.fetchDigitImages(num);
		
		List<DigitImage> res=uploader.readData(dataFile, labelFile, num, startId);
		for (int i=0;i<num;++i){//should use iterator instead
			DigitImage dImage=imageList.get(i);
			DigitImage rs=imageList.get(i);
			assertEquals(dImage.id, rs.id);
			assertEquals(dImage.type, rs.type);
			assertArrayEquals(dImage.data,rs.data);
		}
	}
}
