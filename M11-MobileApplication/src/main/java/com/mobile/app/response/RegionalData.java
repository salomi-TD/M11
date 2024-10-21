package com.mobile.app.response;

import java.util.List;

import com.mobile.app.entity.Employee;
import com.mobile.app.entity.VehicleDetails;

public class RegionalData
{

	private List<Employee> ucoList;

	private List<Employee> rucoList;

	private List<Employee> collectionExecutives;

	private List<Employee> drivers;

	private List<VehicleDetails> vehicle;

	private List<String> startAddress;

	public List<Employee> getUcoList()
	{
		return ucoList;
	}

	public void setUcoList(List<Employee> ucoList)
	{
		this.ucoList = ucoList;
	}

	public List<Employee> getRucoList()
	{
		return rucoList;
	}

	public void setRucoList(List<Employee> rucoList)
	{
		this.rucoList = rucoList;
	}

	public List<Employee> getCollectionExecutives()
	{
		return collectionExecutives;
	}

	public void setCollectionExecutives(List<Employee> collectionExecutives)
	{
		this.collectionExecutives = collectionExecutives;
	}

	public List<Employee> getDrivers()
	{
		return drivers;
	}

	public void setDrivers(List<Employee> drivers)
	{
		this.drivers = drivers;
	}

	public List<VehicleDetails> getVehicle()
	{
		return vehicle;
	}

	public void setVehicle(List<VehicleDetails> vehicle)
	{
		this.vehicle = vehicle;
	}

	public List<String> getStartAddress()
	{
		return startAddress;
	}

	public void setStartAddress(List<String> startAddress)
	{
		this.startAddress = startAddress;
	}

}
