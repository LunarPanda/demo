package com.example.data;

public interface AbstractUploader {
	public void upload(DigitImagesRepository repository,String dataFile,String labelFile,int num, int startId);
	public void uploadTest(DigitImagesRepository repository);
}
