package com.mobile.app.service;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import io.micrometer.common.util.StringUtils;

@Service
public class CryptoService
{

	private static final String SECRET_KEY = "abcdefghijklmnop";

	private static final Logger LOG = LoggerFactory.getLogger(CryptoService.class);

	public String encryptData(final String data)
	{
		if (StringUtils.isNotBlank(data))
		{
			try
			{
				final Cipher cipher = Cipher.getInstance("AES");
				cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8), "AES"));
				return Base64.getEncoder().encodeToString(cipher.doFinal(data.getBytes(StandardCharsets.UTF_8)));
			}
			catch (final InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException
							| BadPaddingException e)
			{
				LOG.error("encryptData() - Exception occurred. Message: [{}]", e.getMessage(), e);
			}
			catch (final Exception e)
			{
				LOG.error("encryptData() - Exception occurred. Message: [{}]", e.getMessage(), e);
			}
		}
		return null;
	}

	public String decryptData(final String encryptedData)
	{
		if (StringUtils.isNotBlank(encryptedData))
		{
			try
			{
				final Cipher cipher = Cipher.getInstance("AES");
				cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8), "AES"));
				final byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
				return new String(decryptedBytes, StandardCharsets.UTF_8);
			}
			catch (final InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException
							| BadPaddingException e)
			{
				LOG.error("decryptData() - Exception occurred. Message: [{}]", e.getMessage(), e);
			}
			catch (final Exception e)
			{
				LOG.error("decryptData() - Exception occurred. Message: [{}]", e.getMessage(), e);
			}
		}
		return null;
	}
}
