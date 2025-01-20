package com.s13sh.ecommerce.helper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

@Component
public class CloudinaryHelper {

	@Value("${cloudinary.cloud_name}")
	private String cloudName;
	@Value("${cloudinary.key}")
	private String key;
	@Value("${cloudinary.secret}")
	private String secret;

	@SuppressWarnings("unchecked")
	public String saveImage(MultipartFile image) {
		Cloudinary cloudinary = new Cloudinary(
				ObjectUtils.asMap("cloud_name", cloudName, "api_key", key, "api_secret", secret, "secure", true));

		Map<String, Object> resume = new HashMap<>();
		try {
			Map<String, Object> uploadOptions = new HashMap<>();
			uploadOptions.put("folder", "Products");
			resume = cloudinary.uploader().upload(image.getBytes(), uploadOptions);
		} catch (IOException e) {			
		}
		
		return (String) resume.get("url");
	}

}
