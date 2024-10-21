package com.mobile.app.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Optional;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.mobile.app.entity.FoodBusinessOperator;
import com.mobile.app.repository.FboRepo;

import io.micrometer.common.util.StringUtils;

@Service
public class FileService
{

	private static final Logger LOG = LoggerFactory.getLogger(FileService.class);

	@Value("${file.storage.path}")
	private String fileStoragePath;

	@Autowired
	private FboRepo fboRepo;

	public String saveFileToDirectory(final String fileName, final String uploadDirectory, final String encodedData)
	{
		try
		{
			if (StringUtils.isNotBlank(encodedData))
			{
				final byte[] decodedData = Base64.decodeBase64(encodedData);
				final Path filePath = Path.of(fileStoragePath, uploadDirectory, fileName);
				final Path directoryPath = filePath.getParent();
				if (!Files.exists(directoryPath))
				{
					Files.createDirectories(directoryPath);
				}
				Files.write(filePath, decodedData, StandardOpenOption.CREATE);
				return Path.of(uploadDirectory, fileName).toString();
			}
		}
		catch (final IOException e)
		{
			LOG.info("saveFileToDirectory() - Failed to save the image. ", e.getMessage());
		}
		catch (final Exception e)
		{
			LOG.error("saveFileToDirectory() - Exception occurred. Message: [{}]", e.getMessage(), e);
		}
		return null;
	}

	public void deleteImagesById(final Integer id)
	{
		try
		{
			final Optional<FoodBusinessOperator> fbo = fboRepo.findById(id);
			if (fbo.isPresent())
			{
				final FoodBusinessOperator employee = fbo.get();
				final String imagePathsString = employee.getPhotos();
				if (StringUtils.isNotBlank(imagePathsString))
				{
					final String[] imagePaths = imagePathsString.split(",");
					for (final String imagePath : imagePaths)
					{
						final Path filePath = Paths.get(fileStoragePath, imagePath);

						if (Files.exists(filePath) && !Files.isDirectory(filePath))
						{
							Files.delete(filePath);
						}
						else
						{
							LOG.info("File not found: " + imagePath);
						}
					}
				}

			}
		}
		catch (final Exception e)
		{
			LOG.error("deleteImagesById() - Exception occurred. Message: [{}]", e.getMessage(), e);
		}
	}

}