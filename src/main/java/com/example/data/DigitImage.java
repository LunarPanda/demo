package com.example.data;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;

public class DigitImage {
	static final int HEIGHT=28;
	static final int WEIGHT=28;
	static final int SIZE=28*28;
	
	int id;
	int type;
	byte[] data;
	
	public DigitImage(int id, int type, byte[] data) {
		super();
		this.id = id;
		this.type = type;
		this.data = data;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public byte[] getData() {
		return data;
	}
	public void setData(byte[] data) {
		this.data = data;
	}
	
	public void writeToOutputStream(OutputStream os){
		BufferedImage image = new BufferedImage(28,28,BufferedImage.TYPE_BYTE_GRAY);
		for (int i=0;i<28*28;++i){
			int d=data[i]&255;
			image.setRGB(i%28,i/28,d*65536+d*256+d);
		}
		try {
			ImageIO.write(image, "png", os);
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
}
