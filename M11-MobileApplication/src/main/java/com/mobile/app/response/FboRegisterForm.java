package com.mobile.app.response;

import com.mobile.app.entity.FoodBusinessOperator;

public class FboRegisterForm
{

	private FoodBusinessOperator fbo;

	private String image;

	public FoodBusinessOperator getFbo()
	{
		return fbo;
	}

	public void setFbo(FoodBusinessOperator fbo)
	{
		this.fbo = fbo;
	}

	public String getImage()
	{
		return image;
	}

	public void setImage(String image)
	{
		this.image = image;
	}

}
