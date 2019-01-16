//Класс для фрагмента, стандартный. 
//Сам фрагмент состоит из списка.

package com.example.testdemorest;

import android.os.Bundle;

import android.view.View;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import android.support.v4.app.Fragment;

public class ListClass extends Fragment{
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saves){
		View view = inflater.inflate(R.layout.list_fragment, container, false);
		
		return view;
	}
}
