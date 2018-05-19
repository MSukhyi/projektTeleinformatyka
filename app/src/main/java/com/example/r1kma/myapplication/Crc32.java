package com.example.r1kma.myapplication;

public class Crc32 extends CodeBase
{
	protected long klucz=0x104C11DB7L;	// CRC-32
	
	private int xor(int a, int b)
	{
		if (a==b) return 0;
		else return 1;
	}
	
	private int[] liczCrc(int bity[])
	{
		int n = bity.length;
		int crc[] = new int[32];
		int temp[] = new int[n+32];
		System.arraycopy(bity, 0, temp, 32, n);		// kopiowanie danych (z przesunięciem o 16 bitów)
		int tklucz[] = new int[33];			// klucz jako tablica bitów
		for (int i=0; i<33; i++)
		{
			if ((klucz&(1<<i))==0) tklucz[i]=0;
			else tklucz[i]=1;
		}
		
		// liczenie CRC
		for (int start=n+31; start>31; start--)
		{
			if (temp[start]==1)
			{
				for (int i=0; i<33; i++)
				{
					temp[start-i]=xor(temp[start-i], tklucz[32-i]);
				}
			}
		}
		
		System.arraycopy(temp, 0, crc, 0, 32);		
		return crc;
	}
	
	@Override
	int[] koduj()
	{
		int n = data.length;
		int l = n+32;
		code = new int[l];
		type = new int[l];
		System.arraycopy(data, 0, code, 32, n);		// kopiowanie danych (z przesunięciem o 16 bitów)
		int [] crc = liczCrc(data);
		System.arraycopy(crc, 0, code, 0, 32);
		for (int i=0; i<32; i++) type[i] = 3;
		for (int i=32; i<l; i++) type[i] = 0;
		
		return code;
	}

	@Override
	int[] dekoduj()
	{
		int l = code.length;
		int n = l-32;
		data = new int[n];
		for (int i=0; i<n; i++) data[i] = code[i+32];
		
		return data;
	}

	@Override
	void fix()
	{
		int l = code.length;
		//int n = l-32;
		//if (dane==null | dane.length!=kod.length-16) dane = new int[n];
		type = new int[l];
		int crc[] = liczCrc(code);
		boolean ok = true;
		for (int i=0; i<32 && ok; i++) if (crc[i]!=0) ok = false;
		if (ok)
		{
			for (int i=0; i<32; i++) type[i] = 3;
			for (int i=32; i<l; i++) type[i] = 0;
		}
		else
		{
			errors++;
			for (int i=0; i<32; i++) type[i] = 5;
			for (int i=32; i<l; i++) type[i] = 2;
		}
	}	
}
