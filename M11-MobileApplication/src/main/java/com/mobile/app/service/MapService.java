package com.mobile.app.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.ortools.Loader;
import com.google.ortools.constraintsolver.Assignment;
import com.google.ortools.constraintsolver.FirstSolutionStrategy;
import com.google.ortools.constraintsolver.RoutingDimension;
import com.google.ortools.constraintsolver.RoutingIndexManager;
import com.google.ortools.constraintsolver.RoutingModel;
import com.google.ortools.constraintsolver.RoutingSearchParameters;
import com.google.ortools.constraintsolver.main;
import com.mobile.app.repository.VehicleRepo;
import com.mobile.app.response.FboListResponse;
import com.mobile.app.response.FboResponse;
import com.mobile.app.response.RegionResponse;

@Service
public class MapService
{

	private static final Logger LOG = LoggerFactory.getLogger(MapService.class);

	@Autowired
	private RegionService regionService;

	@Value("${google.map.apiKey}")
	private String apiKey;

	@Autowired
	private ReportService reportService;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private VehicleRepo vehicleRepo;

	public Map<String, Object> getAddressCoordinates(final Integer empId)
	{

		final Map<String, Object> coordinates = new HashMap<>();
		final RegionResponse regionResponse = regionService.getRegionByEmpId(empId);
		final Integer noOfVehicles = vehicleRepo.findByRegionAndAvailable(regionResponse.getRegionId(), true).size();

		try
		{
			final List<String> address = getFboAddressByRegion(empId);
			coordinates.put("addresses", address);
			coordinates.put("routes", calculateDistanceMatrix(getDistanceMatrixForBatch(address, address), noOfVehicles));
		}
		catch (final Exception e)
		{
			LOG.error("getAddressCoordinates() - Exception occurred. Message: [{}]", e.getMessage(), e);
		}
		return coordinates;
	}

	public List<List<Integer>> calculateDistanceMatrix(final long[][] distanceMatrix, final Integer noOfVehicles)
	{
		List<List<Integer>> routes = new ArrayList<>();

		try
		{
			if (noOfVehicles != 0)
			{
				routes = route(distanceMatrix, noOfVehicles, 0);
			}
		}
		catch (final Exception e)
		{
			LOG.error("calculateDistanceMatrix() - Exception occurred. Message: [{}]", e.getMessage(), e);
		}
		return routes;
	}

	public List<List<Integer>> route(final long[][] distanceMatrix, final Integer noOfVehicles, final Integer depot)
	{
		Loader.loadNativeLibraries();
		// Instantiate the data problem.

		// Create Routing Index Manager
		final RoutingIndexManager manager = new RoutingIndexManager(distanceMatrix.length, noOfVehicles, depot);

		// Create Routing Model.
		final RoutingModel routing = new RoutingModel(manager);

		// Create and register a transit callback.
		final int transitCallbackIndex = routing.registerTransitCallback((long fromIndex, long toIndex) -> {
			// Convert from routing variable Index to user NodeIndex.
			int fromNode = manager.indexToNode(fromIndex);
			int toNode = manager.indexToNode(toIndex);
			return distanceMatrix[fromNode][toNode];
		});

		// Define cost of each arc.
		routing.setArcCostEvaluatorOfAllVehicles(transitCallbackIndex);

		// Add Distance constraint.
		routing.addDimension(transitCallbackIndex, 0, 100000, true, // start cumul to zero
						"Distance");
		final RoutingDimension distanceDimension = routing.getMutableDimension("Distance");
		distanceDimension.setGlobalSpanCostCoefficient(100);

		// Setting first solution heuristic.
		final RoutingSearchParameters searchParameters = main.defaultRoutingSearchParameters().toBuilder()
						.setFirstSolutionStrategy(FirstSolutionStrategy.Value.PATH_CHEAPEST_ARC).build();

		// Solve the problem.
		final Assignment solution = routing.solveWithParameters(searchParameters);

		// Print solution on console.
		return getRouteMatrix(noOfVehicles, routing, manager, solution);
	}

	static List<List<Integer>> getRouteMatrix(final Integer noOfVehicles, final RoutingModel routing,
					final RoutingIndexManager manager, final Assignment solution)
	{
		final List<List<Integer>> routes = new ArrayList<>();

		for (int i = 0; i < noOfVehicles; ++i)
		{
			final List<Integer> routeNodes = new ArrayList<>();
			long index = routing.start(i);

			while (!routing.isEnd(index))
			{
				routeNodes.add(manager.indexToNode(index));
				index = solution.value(routing.nextVar(index));
			}
			routeNodes.add(manager.indexToNode(index));
			routes.add(routeNodes);
		}

		return routes;
	}

	private long[][] getDistanceMatrixForBatch(final List<String> origins, final List<String> destinations)
					throws JsonMappingException, JsonProcessingException, RestClientException
	{
		final long[][] distanceMatrix = new long[destinations.size()][destinations.size()];

		// String originsString = String.join("|", origins);
		final String destinationsString = String.join("|", destinations);
		final ObjectMapper objectMapper = new ObjectMapper();

		for (int i = 0; i < origins.size(); i++)
		{

			final String apiUrl = String.format(
							"https://maps.googleapis.com/maps/api/distancematrix/json?origins=%s&destinations=%s&mode=driving&key=%s",
							origins.get(i), destinationsString, apiKey);
			final JsonNode jsonNode = objectMapper.readTree(restTemplate.getForObject(apiUrl, String.class));

			for (int j = 0; j < destinations.size(); j++)
			{
				final JsonNode distanceNode = jsonNode.get("rows").get(0).get("elements").get(j).get("distance").get("value");
				distanceMatrix[i][j] = distanceNode.asInt();
			}
		}
		return distanceMatrix;
	}

	public List<String> getFboAddressByRegion(final Integer empId)
	{
		final List<String> address = new ArrayList<>();

		final RegionResponse regionResponse = regionService.getRegionByEmpId(empId);
		address.add(regionResponse.getAddress());

		final FboListResponse fboListResponse = reportService.getRucoDailyFollowupDetailsOfFbo(empId);
		final List<FboResponse> list = fboListResponse.getFboList();

		if (!CollectionUtils.isEmpty(list))
		{
			for (final FboResponse fboResponse : list)
			{
				address.add(fboResponse.getAddress());
			}
		}
		return address;
	}

}
