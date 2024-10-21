package com.mobile.app.service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.mobile.app.entity.FBOTermsConditions;
import com.mobile.app.entity.FboFollowup;
import com.mobile.app.entity.FoodBusinessOperator;
import com.mobile.app.enums.ApprovalType;
import com.mobile.app.repository.FBOTermsConditionsRepo;
import com.mobile.app.repository.FboFollowupRepo;
import com.mobile.app.repository.FboRepo;
import com.mobile.app.repository.WeighmentDataRepo;
import com.mobile.app.request.FboAccountRequest;
import com.mobile.app.response.FboAccountResponse;
import com.mobile.app.response.FboListResponse;
import com.mobile.app.response.FboResponse;
import com.mobile.app.util.MElevenUtil;

import io.micrometer.common.util.StringUtils;

@Service
public class FboService
{

	private static final Logger LOG = LoggerFactory.getLogger(FboService.class);

	@Autowired
	private FboRepo fboRepo;

	@Autowired
	private ModelMapper mapper;

	@Autowired
	private WeighmentDataRepo weightmentDataRepo;

	@Autowired
	private WorkflowService workflowService;

	@Autowired
	private FileService fileService;

	@Autowired
	private FboFollowupRepo followupRepo;

	@Autowired
	private FBOTermsConditionsRepo fboTermsRepo;

	@Autowired
	private CryptoService cryptoService;

	public String createFbo(final FoodBusinessOperator fbo, final String encodedImage)
	{
		try
		{
			if (Objects.nonNull(fbo))
			{
				if (Objects.nonNull(fboRepo.findByContactNo(fbo.getContactNo())))
				{
					return "This mobile number is already registered";
				}

				fboRepo.save(fbo);

				final FoodBusinessOperator createdFbo = fboRepo.findByContactNo(fbo.getContactNo());
				if (Objects.nonNull(createdFbo))
				{
					createdFbo.setPhotos(fileService.saveFileToDirectory("fbo_" + createdFbo.getEnrollmentId() + ".jpg",
									"images", encodedImage));
					fboRepo.save(createdFbo);
					createFollowupEntry(createdFbo);
					createTermsConditions(createdFbo);
					workflowService.createWorkflowApproval(createdFbo, ApprovalType.REGISTER);

					return "Successfully registered with enrollmentId = " + createdFbo.getEnrollmentId();
				}
			}
		}
		catch (final Exception e)
		{
			LOG.error("createFbo() - Exception occurred. Message: [{}]", e.getMessage(), e);
		}
		return MElevenUtil.SOMETHING_WENT_WRONG;
	}

	private void createFollowupEntry(final FoodBusinessOperator fbo)
	{
		final FboFollowup followup = new FboFollowup();
		followup.setFboId(fbo.getEnrollmentId());
		followup.setRucoId(fbo.getAssignedRUCO());
		followup.setActualFollowupDate(new Date());

		followupRepo.save(followup);
	}

	private void createTermsConditions(final FoodBusinessOperator createdFbo)
	{
		final FBOTermsConditions fboTerms = new FBOTermsConditions();
		fboTerms.setFbo(createdFbo.getEnrollmentId());
		fboTerms.setPaymentCostPerKg(MElevenUtil.getDoubleValue(createdFbo.getRateAgreedPerKG()));
		fboTerms.setPaymentCurrency("INR");
		fboTermsRepo.save(fboTerms);
	}

	public void updateFboPassword(final Integer enrollmentId, final String newPassword)
	{
		final FoodBusinessOperator fbo = fboRepo.findById(enrollmentId).orElse(null);

		if (Objects.nonNull(fbo))
		{
			fbo.setPassword(cryptoService.encryptData(newPassword));
			fboRepo.save(fbo);
		}
		else
		{
			throw new IllegalArgumentException("Food Business Operator with enrollmentId " + enrollmentId + " not found.");
		}
	}

	public FboResponse getFboDetails(final Integer enrollmentId)
	{
		final FboResponse fboResponse = new FboResponse();
		try
		{
			FoodBusinessOperator fbo = fboRepo.findById(enrollmentId).orElse(null);

			if (Objects.nonNull(fbo))
			{
				return mapper.map(fbo, FboResponse.class);
			}
		}
		catch (final Exception e)
		{
			fboResponse.setMessage(MElevenUtil.SOMETHING_WENT_WRONG);
			LOG.error("getFboDetails() - Exception occurred. Message: [{}]", e.getMessage(), e);
		}
		return fboResponse;
	}

	public String updateFboDetails(final FoodBusinessOperator fbo)
	{
		try
		{
			final FoodBusinessOperator existingFbo = fboRepo.findById(fbo.getEnrollmentId()).orElse(null);

			if (Objects.nonNull(existingFbo))
			{
				existingFbo.setContactNo(fbo.getContactNo());
				existingFbo.setEmail(fbo.getEmail());
				existingFbo.setGstNo(fbo.getGstNo());
				existingFbo.setFssaiNo(fbo.getFssaiNo());
				existingFbo.setFrequency(fbo.getFrequency());

				fboRepo.save(existingFbo);

				return "Updated Successfully";
			}
		}
		catch (final Exception e)
		{
			LOG.error("updateFboDetails() - Exception occurred. Message: [{}]", e.getMessage(), e);
			return MElevenUtil.SOMETHING_WENT_WRONG;
		}
		return "FBO record not found";
	}

	public String removeFbo(final Integer enrollmentId)
	{
		try
		{
			final FoodBusinessOperator existingFbo = fboRepo.findById(enrollmentId).orElse(null);

			if (Objects.nonNull(existingFbo))
			{
				fboRepo.deleteById(enrollmentId);
				fileService.deleteImagesById(enrollmentId);
				return "Successfully removed";
			}
		}
		catch (final Exception e)
		{
			LOG.error("removeFbo() - Exception occurred. Message: [{}]", e.getMessage(), e);
		}
		return "FBO record not found";
	}

	public FboListResponse getFboList()
	{
		final FboListResponse fboListResponse = new FboListResponse();
		try
		{
			final List<FoodBusinessOperator> fboList = fboRepo.findAll();

			if (!CollectionUtils.isEmpty(fboList))
			{
				final List<FboResponse> fboResponse = new ArrayList<>();

				for (final FoodBusinessOperator fbo : fboList)
				{
					fboResponse.add(mapper.map(fbo, FboResponse.class));
				}
				fboListResponse.setFboList(fboResponse);
			}
		}
		catch (final Exception e)
		{
			LOG.error("getFboList() - Exception occurred. Message: [{}]", e.getMessage(), e);
		}
		return fboListResponse;
	}

	public FboListResponse getFboListAssociatedWithEmployee(final Integer empId)
	{
		final FboListResponse fboListResponse = new FboListResponse();
		try
		{
			final List<FoodBusinessOperator> fboList = fboRepo.findByCreatedByOrAssignedRUCOOrAssignedUCO(empId, empId, empId);
			if (!CollectionUtils.isEmpty(fboList))
			{
				final List<FboResponse> fboResponse = new ArrayList<>();

				for (final FoodBusinessOperator fbo : fboList)
				{
					fboResponse.add(mapper.map(fbo, FboResponse.class));
				}
				fboListResponse.setFboList(fboResponse);
			}
		}
		catch (final Exception e)
		{
			LOG.error("getFboListAssociatedWithEmployee() - Exception occurred. Message: [{}]", e.getMessage(), e);
		}
		return fboListResponse;
	}

	public FboAccountResponse getFboAccountDetails(final Integer enrollmentId)
	{

		final FboAccountResponse accountDetails = new FboAccountResponse();
		try
		{
			final FoodBusinessOperator fbo = fboRepo.findById(enrollmentId).orElse(null);
			if (Objects.nonNull(fbo))
			{
				if (StringUtils.isNotBlank(fbo.getAccountNumber()) && StringUtils.isNotBlank(fbo.getIfscCode()))
				{
					accountDetails.setAccountNumber(cryptoService.encryptData(fbo.getAccountNumber()));
					accountDetails.setIfscCode(fbo.getIfscCode());
				}
				if (StringUtils.isNotBlank(fbo.getUpiId()))
				{
					accountDetails.setUpiId(fbo.getUpiId());
				}
			}
		}
		catch (final Exception e)
		{
			LOG.error("getFboAccountDetails() - Exception occurred. Message: [{}]", e.getMessage(), e);
		}
		return accountDetails;
	}

	public String saveFboAccountDetails(final FboAccountRequest accountRequest)
	{
		try
		{
			final FoodBusinessOperator fbo = fboRepo.findById(accountRequest.getFboId()).orElse(null);
			if (Objects.nonNull(fbo))
			{
				final String accountNo = cryptoService.encryptData(accountRequest.getAccountNumber());
				fbo.setAccountNumber(accountNo);
				fbo.setIfscCode(accountRequest.getIfscCode());
				fboRepo.save(fbo);
				return "Your Bank account details updated successfully";
			}
		}
		catch (final Exception e)
		{
			LOG.error("saveFboAccountDetails() - Exception occurred. Message: [{}]", e.getMessage(), e);
			return MElevenUtil.SOMETHING_WENT_WRONG;
		}
		return "FBO enrollment Id is not found";
	}

	public void createFollowupEntries()
	{
		final List<FoodBusinessOperator> activeFboList = fboRepo.findByActive(true);
		if (!CollectionUtils.isEmpty(activeFboList))
		{
			for (final FoodBusinessOperator fbo : activeFboList)
			{
				if (Objects.nonNull(weightmentDataRepo.findLatestOrderOfFbo(fbo.getEnrollmentId())))
				{
					final Integer frequency = fbo.getFrequency();
					if (frequency != 0 && frequency != null)
					{
						final Date actualFollowupDate = calculateNextFollowupDate(frequency);

						final FboFollowup newFollowupEntry = new FboFollowup();
						newFollowupEntry.setFboId(fbo.getEnrollmentId());
						newFollowupEntry.setActualFollowupDate(actualFollowupDate);
						newFollowupEntry.setRucoId(fbo.getAssignedRUCO());

						followupRepo.save(newFollowupEntry);
					}
				}
			}
		}
	}

	private Date calculateNextFollowupDate(final Integer frequency)
	{
		return Date.from(LocalDate.now().plusDays(frequency).atStartOfDay(ZoneId.systemDefault()).toInstant());
	}

}