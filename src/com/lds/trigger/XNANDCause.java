package com.lds.trigger;

public class XNANDCause extends Cause
{
	private Cause cause1, cause2;
	
	public XNANDCause (Cause cause1, Cause cause2)
	{
		this.cause1 = cause1;
		this.cause2 = cause2;
	}
	
	@Override
	public boolean isTriggered ()
	{
		if (cause1.isTriggered() == cause2.isTriggered())
		{
			return true;
		}
		else
		{
			return false;
		}
	}
}