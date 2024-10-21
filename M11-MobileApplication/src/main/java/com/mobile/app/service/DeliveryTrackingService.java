package com.mobile.app.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.maps.DistanceMatrixApi;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.TravelMode;
import com.mobile.app.entity.WeighmentData;
import com.mobile.app.repository.WeighmentDataRepo;

@Service
public class DeliveryTrackingService
{

	private static final Logger LOG = LoggerFactory.getLogger(DeliveryTrackingService.class);

	@Autowired
	private WeighmentDataRepo weighmentDataRepo;

	@Value("${google.map.apiKey}")
	private String apiKey;

	// Below method can be used if the Driver has no button for "Reached".
	// public boolean trackingDetails(final Integer orderId) {
	// try {
	// final DeliveryTracking tracking = deliveryTrackingRepo.findByPoId(orderId);
	// if (Objects.nonNull(tracking)) {
	// if (tracking.getEndTime() == null) {
	// return false;
	// }
	// }
	// } catch (final Exception e) {
	// LOG.info(e.getMessage());
	// }
	// return true;
	// }

	public double getDistanceTravelled(final Integer orderId)
	{

		final WeighmentData existingData = weighmentDataRepo.findById(orderId).orElse(null);
		final GeoApiContext context = new GeoApiContext.Builder().apiKey(apiKey).build();

		try
		{
			final DistanceMatrix distanceMatrix = DistanceMatrixApi.newRequest(context).origins(existingData.getSourceAddress())
							.destinations(existingData.getDestinationAddress()).mode(TravelMode.DRIVING).await();

			final long distanceInMeters = distanceMatrix.rows[0].elements[0].distance.inMeters;
			return distanceInMeters / 1000.0;
		}
		catch (final Exception e)
		{
			LOG.error("getDistanceTravelled() - Exception occurred. Message: [{}]", e.getMessage(), e);
		}
		return 0;
	}

}
