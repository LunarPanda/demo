package com.example.data;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.awt.Frame;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

@Component
public class Uploader implements AbstractUploader{
	
	//@Autowired
	//DigitImagesRepository repository;//why this auto wire fails
	
	public void upload(DigitImagesRepository repository,String dataFile,String labelFile,int num, int startId){
		//to do: this function should be modified to be async later.
		//System.out.println(this.repository);
		repository.deleteAll();
		List<DigitImage> list=readData(dataFile,labelFile,num,startId);
		for (DigitImage image:list){
			repository.insertADigitImage(image);
		}
	}
	
	
	public List<DigitImage> readData(String dataFile,String labelFile,int num, int startId){// we pass num parameter for simplicity, this could be read form file headers.
		try {
			List<DigitImage> list = new LinkedList<DigitImage>();
			FileInputStream dataFis=new FileInputStream(dataFile);
			FileInputStream labelFis=new FileInputStream(labelFile);
			byte[] cache = new byte[4];
			dataFis.read(cache);//magic
			/*
			System.out.println(Byte.toUnsignedInt(cache[0]));
			System.out.println(Byte.toUnsignedInt(cache[1]));
			System.out.println(Byte.toUnsignedInt(cache[2]));
			System.out.println(Byte.toUnsignedInt(cache[3]));
			*/
			dataFis.read(cache);//num
			dataFis.read(cache);//height
			dataFis.read(cache);//width
			labelFis.read(cache);//magic
			labelFis.read(cache);//num
			
			for (int i=0;i<num;++i){
				byte[] data = new byte[DigitImage.SIZE];
				int type;
				dataFis.read(data);
				type=labelFis.read();
				list.add(new DigitImage(startId+i,type,data));
			}
			
			
			dataFis.close();
			labelFis.close();
			
			return list;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	public void uploadTest(DigitImagesRepository repository){
		String dataFile="src/main/resources/data/t10k-images.idx3-ubyte";
		String labelFile="src/main/resources/data/t10k-labels.idx1-ubyte";
		int num=100;
		int startId=0;
		new Uploader().upload(repository,dataFile,labelFile,num,startId);
	}


	
/*
	public static void main(String[] argv) throws IOException{
		//for test only 
		String dataFile="src/main/resources/data/t10k-images.idx3-ubyte";
		String labelFile="src/main/resources/data/t10k-labels.idx1-ubyte";
		int num=1000;
		int startId=0;
		List<DigitImage> list = new Uploader().readData(dataFile,labelFile,num,startId);
		int testno=300;
		
		byte[] data=list.get(testno).data;
		BufferedImage image = new BufferedImage(28,28,BufferedImage.TYPE_BYTE_GRAY);
		for (int i=0;i<28*28;++i){
			int d=Byte.toUnsignedInt(data[i]);
			//System.out.println(d);
			image.setRGB(i%28,i/28,d*65536+d*256+d);
		}
		System.out.println(list.get(testno).type);
		FileOutputStream fos= new FileOutputStream("test1.png");
		list.get(0).writeToOutputStream(fos);
		new ShowImage(image);
	}
*/
}


class ShowImage extends Frame {
	//This is for test.
	BufferedImage image;
	public ShowImage(BufferedImage image){
		this.setSize(500, 500);
		this.setVisible(true);
		this.image=image;
	}
	
	public void paint(Graphics g){
		g.drawImage(image, 250, 250, this);
	}

}