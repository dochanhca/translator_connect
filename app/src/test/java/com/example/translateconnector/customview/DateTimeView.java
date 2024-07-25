package com.example.translateconnector.customview;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.imoktranslator.R;
import com.imoktranslator.utils.DateTimeUtils;

import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DateTimeView extends LinearLayout {

    @BindView(R.id.img_calendar)
    ImageView imgCalendar;
    @BindView(R.id.txt_date)
    OpenSansTextView txtDate;
    @BindView(R.id.txt_time)
    OpenSansTextView txtTime;
    @BindView(R.id.layout_date_time)
    CardView layoutDateTime;
    @BindView(R.id.root_view)
    LinearLayout rootView;
    @BindView(R.id.txt_error)
    TextView txtError;

    private Calendar selectedDate;
    private OnDateTimeSelectListener onDateTimeSelectListener;

    public void setOnDateTimeSelectListener(OnDateTimeSelectListener onDateTimeSelectListener) {
        this.onDateTimeSelectListener = onDateTimeSelectListener;
    }

    public DateTimeView(Context context) {
        super(context);
        initView(null, 0, 0);
    }

    public DateTimeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(attrs, 0, 0);
    }

    public DateTimeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(attrs, defStyleAttr, 0);
    }

    private void initView(AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        inflater.inflate(R.layout.layout_date_time_view, this, true);

        ButterKnife.bind(this);

        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.item_text_field_common);
        String error = typedArray.getString(R.styleable.DateTimeView_error_value);

        typedArray.recycle();

        txtError.setText(error);

        selectedDate = Calendar.getInstance();

       setDateDisplay();
    }

    private void setDateDisplay() {
        txtDate.setText(getContext().getString(R.string.MH11_022) + ": " +
                DateTimeUtils.convertDateToString(selectedDate.getTime(), DateTimeUtils.DATE_MONTH_YEAR_FORMAT));

        txtTime.setText(getContext().getString(R.string.MH11_023) + ": " +
                DateTimeUtils.convertDateToString(selectedDate.getTime(), DateTimeUtils.TIME_HOUR_MINUTES_24));
    }

    @OnClick({R.id.layout_date_time})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.layout_date_time:
                showDatePickerDialog();
                break;
        }
    }

    private void showDatePickerDialog() {
        // Get Current Date
        int mYear = selectedDate.get(Calendar.YEAR);
        int mMonth = selectedDate.get(Calendar.MONTH);
        int mDay = selectedDate.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                (view, year, monthOfYear, dayOfMonth) -> {
                    selectedDate.set(Calendar.YEAR, year);
                    selectedDate.set(Calendar.MONTH, monthOfYear);
                    selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    txtDate.setText(getContext().getString(R.string.MH11_022) + ": " +
                            DateTimeUtils.convertDateToString(selectedDate.getTime(),
                                    DateTimeUtils.DATE_MONTH_YEAR_FORMAT));

                    if (onDateTimeSelectListener != null) {
                        onDateTimeSelectListener.onDateSelected(selectedDate.getTime());
                    }
                    showTimePickerDialog();
                }, mYear, mMonth, mDay);

        datePickerDialog.getDatePicker().setMinDate(Calendar.getInstance().getTimeInMillis());
        datePickerDialog.show();
    }

    private void showTimePickerDialog() {
        int mHour = selectedDate.get(Calendar.HOUR_OF_DAY);
        int mMinute = selectedDate.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                (view, hourOfDay, minute) -> {
                    selectedDate.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    selectedDate.set(Calendar.MINUTE, minute);

                    txtTime.setText(getContext().getString(R.string.MH11_023) + ": " +
                            DateTimeUtils.convertDateToString(selectedDate.getTime(),
                                    DateTimeUtils.TIME_HOUR_MINUTES_24));

                    if (onDateTimeSelectListener != null) {
                        onDateTimeSelectListener.onTimeSelected(selectedDate.getTime());
                    }
                }
                , mHour, mMinute, true);

        timePickerDialog.setOnCancelListener(dialog ->
                onDateTimeSelectListener.onTimeSelected(selectedDate.getTime()));
        timePickerDialog.show();
    }

    public Calendar getCalendar() {
        return selectedDate;
    }

    public Date getSelectedDate() {
        return selectedDate.getTime();
    }

    public void setSelectedDate(Calendar selectedDate) {
        this.selectedDate = selectedDate;
        setDateDisplay();
    }

    public String getSelectedDate(String dateFormat) {
        return DateTimeUtils.convertDateToString(selectedDate.getTime(),
                dateFormat);
    }

    public void setError(String error) {
        txtError.setVisibility(TextUtils.isEmpty(error) ? GONE : VISIBLE);
        txtError.setText(error);
    }

    public void setErrorVisible(boolean isError) {
        txtError.setVisibility(isError ? VISIBLE : GONE);
    }

    public boolean isError() {
        return txtError.getVisibility() == VISIBLE;
    }

    public interface OnDateTimeSelectListener {
        void onDateSelected (Date selectedDate);

        void onTimeSelected (Date selectedTime);
    }
}
