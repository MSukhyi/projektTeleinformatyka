package com.example.r1kma.myapplication;

import java.util.Random;

public class DataBits
{
	protected int n;
	protected int[] bits;
	
	public void generuj(int n)
	{
		this.n=n;
		bits = new int[n];
		Random generator = new Random();
		for (int i=0; i<n; i++)
		{
			bits[i]=generator.nextInt(2);
		}
	}
	
	@Override
	public String toString()
	{
		String str = "";
		for (int i=0; i<n; i++)
		{
			str+=bits[i];
		}
		return str;
	}
	
	public void fromString(String str)
	{
		n = str.length();
		bits = new int[n];
		for (int i=0; i<n; i++)
		{
			if (str.charAt(i)=='1') bits[i] = 1;
			else bits[i] = 0;
		}
	}
	
	public void fromInt(int bity[])
	{
		this.n=bity.length;
		this.bits = new int[n];
		System.arraycopy(bity, 0, this.bits, 0, bity.length);
	}
	
	public int[] getBits()
	{
		return bits;
	}
}
