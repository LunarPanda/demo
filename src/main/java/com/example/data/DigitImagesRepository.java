package com.example.data;

import java.util.List;

public interface DigitImagesRepository {
	void insertADigitImage(DigitImage dImage);
	List<DigitImage> fetchDigitImages(int num);
	DigitImage getADigitImage(int id);
	void deleteAll();
}
