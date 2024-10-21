package com.mobile.app.util;

import java.text.SimpleDateFormat;

public class MElevenUtil
{

	public static final SimpleDateFormat dateFormatddMMyyyy = new SimpleDateFormat("dd-MM-yyyy");

	public static final SimpleDateFormat dateFormatMMyyyy = new SimpleDateFormat("MM-yyyy");

	public static final SimpleDateFormat dateFormatYYYYMMdd = new SimpleDateFormat("yyyy-MM-dd");

	public static final SimpleDateFormat timeFormatHHmm = new SimpleDateFormat("HH:mm");

	public static final String DAILY = "daily";

	public static final String WEEKLY = "weekly";

	public static final String MONTHLY = "monthly";

	public static final String SOMETHING_WENT_WRONG = "Something went wrong.";

	public static Double getDoubleValue(final Double myDouble)
	{
		if (myDouble == null || myDouble.isNaN())
		{
			return 0.0;
		}
		return myDouble;
	}

	public static Boolean getBooleanValue(final Boolean myBoolean)
	{
		if (myBoolean == null)
		{
			return Boolean.FALSE;
		}
		return myBoolean;
	}

	public static Integer getIntegerValue(final Integer myInteger)
	{
		if (myInteger == null)
		{
			return 0;
		}
		return myInteger;
	}

}
