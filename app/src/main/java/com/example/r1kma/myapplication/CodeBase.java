package com.example.r1kma.myapplication;

import java.util.Random;

abstract class CodeBase
{
    protected int[] data;
    protected int[] code;
    protected int[] type;	// 0 - poprawny bit danych, 1 - przekłamany bit danych, 2 - niepewny bit danych, 3 - poprawny bit redundantny, 4 - przekłamany bit redundantny, 5 - niepewny bit redundantny
    protected int errors=0;

    public void setData(int[] dane)
    {
        this.data = new int[dane.length];
        System.arraycopy(dane, 0, this.data, 0, dane.length);
        errors=0;
    }

    public void setData(DataBits d)
    {
        int b[]=d.getBits();
        this.data = new int[b.length];
        System.arraycopy(b, 0, this.data, 0, b.length);
        errors=0;
    }

    public void setData(String str)
    {
        int n = str.length();
        data = new int[n];
        for (int i=0; i<n; i++)
        {
            if (str.charAt(i)=='1') data[i] = 1;
            else data[i] = 0;
        }
        errors=0;
    }

    public void setCode(int[] kod)
    {
        this.code = new int[kod.length];
        this.type = new int[kod.length];
        System.arraycopy(kod, 0, this.code, 0, kod.length);
    }

    public void setCode(DataBits d)
    {
        int b[]=d.getBits();
        this.code = new int[b.length];
        this.type = new int[b.length];
        System.arraycopy(b, 0, this.code, 0, b.length);
        errors=0;
    }

    public void setCode(String str)
    {
        int n = str.length();
        code = new int[n];
        this.type = new int[n];
        for (int i=0; i<n; i++)
        {
            if (str.charAt(i)=='1') code[i] = 1;
            else code[i] = 0;
        }
        errors=0;
    }

    abstract int[] koduj();
    abstract int[] dekoduj();

    public int getDataBitsNumber()
    {
        return data.length;
    }

    public int getControlBitsNumber()
    {
        return code.length-data.length;
    }

    public int getDetectedErrorsNumber()
    {
        return errors;
    }

    public int getFixedErrorsNumber()
    {
        int b=0;
        for (int i=0; i<type.length; i++)
        {
            if (type[i]==1 || type[i]==4) b++;
        }
        return b;
    }

    public String codeToString()
    {
        DataBits d = new DataBits();
        d.fromInt(code);
        return d.toString();
    }

    public String dataToString()
    {
        DataBits d = new DataBits();
        d.fromInt(data);
        return d.toString();
    }

    public int[] getBitTypes()
    {
        return type;
    }

    abstract void fix();

    public void zakloc(int n)
    {
        int l = code.length;
        Random generator = new Random();
        if (n>l) n=l;
        int pozycja;
        int zaklocone=0;
        for (int i=0; i<l; i++) type[i]=0;
        while (zaklocone<n)
        {
            pozycja=generator.nextInt(l);
            if (type[pozycja]==0)
            {
                if (code[pozycja]==1) code[pozycja]=0;
                else code[pozycja]=1;
                type[pozycja]=1;
                zaklocone++;
            }
        }
    }

    public void negate(int pozycja)
    {
        if (pozycja<code.length)
        {
            if (code[pozycja]==1) code[pozycja]=0;
            else code[pozycja]=1;
            if (type[pozycja]==1) type[pozycja]=0;
            else type[pozycja]=1;
        }
    }
}
