package com.mobile.app.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
@CrossOrigin(origins = "http://localhost:8081")
@RestController
public class FileController
{

	private static final Logger LOG = LoggerFactory.getLogger(FileController.class);

	@Value("${file.storage.path}")
	private String fileStoragePath;

	@GetMapping("/image")
	public ResponseEntity<String> getImageBase64(@RequestParam final String imageName)
	{
		try
		{
			final String imagePath = fileStoragePath + "/" + imageName;
			final File file = new File(imagePath);
			final byte[] imageData = Files.readAllBytes(file.toPath());

			final String base64Image = Base64.getEncoder().encodeToString(imageData);

			final HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.TEXT_PLAIN);

			return new ResponseEntity<>(base64Image, headers, HttpStatus.OK);
		}
		catch (final IOException ioe)
		{
			LOG.error("getImageBase64() - IOException occurred. Message: [{}]", ioe.getMessage(), ioe);
		}
		catch (final Exception e)
		{
			LOG.error("getImageBase64() - Exception occurred. Message: [{}]", e.getMessage(), e);
		}

		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

}
