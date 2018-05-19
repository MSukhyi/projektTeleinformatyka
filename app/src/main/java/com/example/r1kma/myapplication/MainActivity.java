package com.example.r1kma.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

//NE PRACUJE KOLOROWANIE BITOW!!!!!!!!!!!!!!!!!!!!!!!
//NE dobawlena enable/disable przciskow i pol -> koduje tilky "Bit parzystosci"

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public RadioGroup radGroupTypKodow;
    public RadioButton parityRadButton, hammingRadButton, crcRadButton;
    public EditText etDaneWejsc, etZakodow, etZakodowPoKorek, etDaneWyjsc, etZakloc;
    public TextView tvPrzeslBityDanych, tvBityNadm, tvBledyWykr, tvBledySkoryg, tvBledyNiewykr;
    public Spinner spinRodzCRC;
    public ArrayAdapter<String> adapterRodzCRC;
    public List<String> listRodzCRC;
    public Spinner spinLiczbBit;
    public ArrayAdapter<String> adapterLiczbBit;
    public List<String> listLiczbBit;
    public Button btnGeneruj, btnKoduj, btnZakloc, btnDekoduj, btnWyczysc;
    public DataBits inputBits;
    public Parity parityTransmitter;
    public Hamming hammingTransmitter;
    public Crc crcTransmitter;
    public Parity parityReceiver;
    public Hamming hammingReceiver;
    public Crc crcReceiver;
    public CodeBase transmitter;
    public CodeBase receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputBits = new DataBits();
        parityTransmitter = new Parity();
        hammingTransmitter = new Hamming();
        crcTransmitter = new Crc();
        parityReceiver = new Parity();
        hammingReceiver = new Hamming();
        crcReceiver = new Crc();
        transmitter = parityTransmitter;
        receiver = parityReceiver;

        radGroupTypKodow = (RadioGroup) findViewById(R.id.radGroupTypKodow);
        parityRadButton = (RadioButton) findViewById(R.id.parityRadButton);
        hammingRadButton = (RadioButton) findViewById(R.id.hammingRadButton);
        crcRadButton = (RadioButton) findViewById(R.id.crcRadButton);

        spinRodzCRC = (Spinner) findViewById(R.id.spinRodzCRC);
        listRodzCRC = new ArrayList<String>();
        listRodzCRC.add("CRC-12");
        listRodzCRC.add("CRC-16");
        listRodzCRC.add("CRC-32");
        adapterRodzCRC = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, listRodzCRC);
        adapterRodzCRC.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinRodzCRC.setAdapter(adapterRodzCRC);

        etDaneWejsc = (EditText) findViewById(R.id.etDaneWejsc);
        etZakodow = (EditText) findViewById(R.id.etZakodow);
        etZakodowPoKorek = (EditText) findViewById(R.id.etZakodowPoKorek);
        etDaneWyjsc = (EditText) findViewById(R.id.etDaneWyjsc);
        etZakloc = (EditText) findViewById(R.id.etZakloc);

        tvPrzeslBityDanych = (TextView) findViewById(R.id.tvPrzeslBityDanych);
        tvBityNadm = (TextView) findViewById(R.id.tvBityNadm);
        tvBledyWykr = (TextView) findViewById(R.id.tvBledyWykr);
        tvBledySkoryg = (TextView) findViewById(R.id.tvBledySkoryg);
        tvBledyNiewykr = (TextView) findViewById(R.id.tvBledyNiewykr);

        spinLiczbBit = (Spinner) findViewById(R.id.spinLiczbBit);
        listLiczbBit = new ArrayList<String>();
        listLiczbBit.add("8");
        listLiczbBit.add("16");
        listLiczbBit.add("24");
        listLiczbBit.add("32");
        listLiczbBit.add("40");
        listLiczbBit.add("48");
        listLiczbBit.add("56");
        listLiczbBit.add("64");
        adapterLiczbBit = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, listLiczbBit);
        adapterLiczbBit.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinLiczbBit.setAdapter(adapterLiczbBit);

        btnGeneruj = (Button) findViewById(R.id.btnGeneruj);
        btnGeneruj.setOnClickListener(this);
        btnKoduj = (Button) findViewById(R.id.btnKoduj);
        btnKoduj.setOnClickListener(this);
        btnZakloc = (Button) findViewById(R.id.btnZakloc);
        btnZakloc.setOnClickListener(this);
        btnDekoduj = (Button) findViewById(R.id.btnDekoduj);
        btnDekoduj.setOnClickListener(this);
        btnWyczysc = (Button) findViewById(R.id.btnWyczysc);
        btnWyczysc.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnGeneruj:
                int n = Integer.parseInt(spinLiczbBit.getSelectedItem().toString());
                inputBits.generuj(n);
                etDaneWejsc.setText(inputBits.toString());
                break;
            case R.id.btnKoduj:
                String str = etDaneWejsc.getText().toString();
                if (str.length() == 0) {
                    str = "00000000";
                    etDaneWejsc.setText(str);
                } else if (str.length() % 8 != 0) {
                    String temp = "";
                    int zeros = 8 - str.length() % 8;
                    for (int i = 0; i < zeros; i++) temp += "0";
                    str = temp + str;
                    etDaneWejsc.setText(str);
                }
                transmitter.setData(str);
                transmitter.koduj();
                etZakodow.setText(transmitter.codeToString());
                transmitter.setCode(transmitter.codeToString());
                break;
            case R.id.btnZakloc:
                transmitter.zakloc(Integer.parseInt(etZakloc.getText().toString()));
                etZakodow.setText(transmitter.codeToString());
                break;
            case R.id.btnDekoduj:
                transmitter.setCode(etZakodow.getText().toString());
                int errors = countErrors();
                transmitter.fix();
                etZakodowPoKorek.setText(transmitter.codeToString());
                //colorFixedBits(transmitter.getBitTypes());
                transmitter.dekoduj();
                etDaneWyjsc.setText(transmitter.dataToString());
                tvPrzeslBityDanych.setText(Integer.toString(transmitter.getDataBitsNumber()));
                tvBityNadm.setText(Integer.toString(transmitter.getControlBitsNumber()));
                int detected = transmitter.getDetectedErrorsNumber();
                tvBledyWykr.setText(Integer.toString(detected));
                tvBledySkoryg.setText(Integer.toString(transmitter.getFixedErrorsNumber()));
                tvBledyNiewykr.setText(Integer.toString(errors - detected));
                break;
            case R.id.btnWyczysc:
                etDaneWejsc.setText("");
                etZakodow.setText("");
                etZakodowPoKorek.setText("");
                etDaneWyjsc.setText("");
                tvBledyNiewykr.setText("");
                tvBledySkoryg.setText("");
                tvBledyWykr.setText("");
                tvBityNadm.setText("");
                tvPrzeslBityDanych.setText("");
                break;
        }
    }

    private int countErrors() {
        String input = etZakodow.getText().toString();
        String output = etZakodow.getText().toString();
        if (input.length() != output.length()) return -1;
        else {
            int errors = 0;
            int l = input.length();
            for (int i = 0; i < l; i++) {
                if (input.charAt(i) != output.charAt(i)) errors++;
            }
            return errors;
        }
    }
}