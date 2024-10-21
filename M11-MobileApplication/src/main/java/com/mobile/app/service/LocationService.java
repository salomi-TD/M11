package com.mobile.app.service;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mobile.app.entity.Location;
import com.mobile.app.repository.LocationRepository;
import com.mobile.app.util.MElevenUtil;

@Service
public class LocationService
{

	private static final Logger LOG = LoggerFactory.getLogger(LocationService.class);

	@Autowired
	private LocationRepository locationRepository;

	public Integer createLocation(final Location data)
	{
		final Location location = new Location();
		location.setEmpId(data.getEmpId());
		location.setLatitude(MElevenUtil.getDoubleValue(data.getLatitude()));
		location.setLongitude(MElevenUtil.getDoubleValue(data.getLongitude()));

		final Location savedLocation = locationRepository.save(location);
		return savedLocation.getLocationId();
	}

	public void updateLocation(final Location data)
	{
		try
		{
			final Location existingLocation = locationRepository.findByEmpId(data.getEmpId());
			if (Objects.nonNull(existingLocation))
			{
				existingLocation.setLatitude(MElevenUtil.getDoubleValue(data.getLatitude()));
				existingLocation.setLongitude(MElevenUtil.getDoubleValue(data.getLongitude()));
				locationRepository.save(existingLocation);
			}
			else
			{
				createLocation(data);
			}
		}
		catch (final Exception e)
		{
			LOG.error("updateLocation() - Exception occurred. Message: [{}]", e.getMessage(), e);
		}
	}

	public Location getLocation(final Integer locationId)
	{
		try
		{
			final Location existingLocation = locationRepository.findByEmpId(locationId);
			if (Objects.nonNull(existingLocation))
			{
				return existingLocation;
			}
		}
		catch (final Exception e)
		{
			LOG.error("getLocation() - Exception occurred. Message: [{}]", e.getMessage(), e);
		}
		return null;
	}

}