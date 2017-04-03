package com.example.vera_liu.nyt;

import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.Spinner;

import java.util.Arrays;

/**
 * Created by vera_liu on 3/31/17.
 */

public class SearchFilterDialogFragment extends DialogFragment implements Button.OnClickListener {
    private Spinner orderSpinner;
    private DatePicker datePicker;
    private Button saveButton;
    private static int year, month, day;
    private static boolean artsChecked, fashionChecked, sportsChecked;
    private static String order;
    private CheckBox arts, fashion, sports;
    private String[] sortOrder = {"Newest", "Oldest"};
    public static SearchFilterDialogFragment newInstance(String date, String sort, boolean arts, boolean fashion, boolean sports) {
        SearchFilterDialogFragment fragment = new SearchFilterDialogFragment();
        year = Integer.parseInt(date.substring(0, 4));
        month = Integer.parseInt(date.substring(4, 6));
        day = Integer.parseInt(date.substring(6, 7));
        artsChecked = arts;
        fashionChecked = fashion;
        sportsChecked = sports;
        order = sort;
        return fragment;
    }
    public interface EditFilterListener {
        void onSaveEdit(String order, String date, boolean arts, boolean fashion, boolean sports);
    }
    @Override
    public void onClick(View v) {
        EditFilterListener listener = (EditFilterListener) getActivity();

        String mm = Integer.toString(month);
        String dd = Integer.toString(day);
        if (month < 10) mm = "0" + mm;
        if (day < 10) dd = "0" + dd;
        String date = Integer.toString(year) + mm + dd;

        listener.onSaveEdit(
                orderSpinner.getSelectedItem().toString(),
                date,
                arts.isChecked(),
                fashion.isChecked(),
                sports.isChecked());
        dismiss();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.edit_search_filter, container, false);
    }
    @Override
    public void onViewCreated(View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        orderSpinner = (Spinner) view.findViewById(R.id.orderSp);
        datePicker = (DatePicker) view.findViewById(R.id.datePicker);
        saveButton = (Button) view.findViewById(R.id.saveBtn);
        arts = (CheckBox) view.findViewById(R.id.checkbox_arts);
        fashion = (CheckBox) view.findViewById(R.id.checkbox_fashion);
        sports = (CheckBox) view.findViewById(R.id.checkbox_sports);
        saveButton.setOnClickListener(this);
        orderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                order = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        datePicker = (DatePicker) view.findViewById(R.id.datePicker);
        datePicker.init(year, month, day, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker datePicker, int y, int m, int d) {
                year = y;
                month = m;
                day = d;
            }
        });
        arts.setChecked(artsChecked);
        sports.setChecked(sportsChecked);
        fashion.setChecked(fashionChecked);
        orderSpinner.setSelection(Arrays.asList(sortOrder).indexOf(order));
    }
    public void onResume() {
        Window window = getDialog().getWindow();
        Point size = new Point();
        Display display = window.getWindowManager().getDefaultDisplay();
        display.getSize(size);
        window.setLayout((int) (size.x * 0.75), WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
        super.onResume();
    }
}
