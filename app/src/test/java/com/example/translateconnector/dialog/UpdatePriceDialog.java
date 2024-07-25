package com.example.translateconnector.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.imoktranslator.R;
import com.imoktranslator.adapter.CommonSpinnerAdapter;
import com.imoktranslator.customview.OpenSansBoldTextView;
import com.imoktranslator.customview.TextFieldView;
import com.imoktranslator.utils.Utils;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.OnClick;

public class UpdatePriceDialog extends BaseDialog {

    @BindView(R.id.field_price)
    TextFieldView fieldPrice;
    @BindView(R.id.btn_send_price)
    OpenSansBoldTextView btnSendPrice;
    @BindView(R.id.bt_cancel)
    OpenSansBoldTextView btCancel;
    @BindView(R.id.spinner_currency)
    Spinner spinnerCurrency;

    private String[] currencies;
    private CommonSpinnerAdapter commonSpinnerAdapter;

    private OnUpdatePriceDialogClickListener onUpdatePriceDialogClickListener;

    @Override
    protected int getDialogLayout() {
        return R.layout.dialog_update_price;
    }

    @Override
    public void onStart() {
        super.onStart();

        int width = (int) (Utils.getScreenWidth(getContext()) * 0.9);
        setupDialog(width, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setCancelable(false);
        getDialog().setCanceledOnTouchOutside(false);

        fieldPrice.setOnTextFieldErrorListener(s ->
                fieldPrice.setError(s.isEmpty() ?
                        String.format(getString(R.string.TB_1001), getString(R.string.MH63_001)) : ""));
        fieldPrice.getEdtValue().setInputType(InputType.TYPE_CLASS_NUMBER);

        currencies = getResources().getStringArray(R.array.arr_currency);
        commonSpinnerAdapter = new CommonSpinnerAdapter(getActivity().getApplicationContext(),
                Arrays.asList(currencies), getString(R.string.MH63_001));
        spinnerCurrency.setAdapter(commonSpinnerAdapter);
        spinnerCurrency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                commonSpinnerAdapter.setItemSelected(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    @OnClick({R.id.btn_send_price, R.id.bt_cancel})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_send_price:
                sendPrice();
                dismiss();
                break;
            case R.id.bt_cancel:
                dismiss();
                break;
        }
    }

    private void sendPrice() {
        if (fieldPrice.getText().isEmpty()) {
            fieldPrice.setError(String.format(getString(R.string.TB_1001), getString(R.string.MH63_001)));
        } else {
            try {
                onUpdatePriceDialogClickListener.
                        onUpdatePriceClickListener(Double.parseDouble(fieldPrice.getText()),
                                commonSpinnerAdapter.getSelectedPosition() + 1);
            } catch (NumberFormatException e) {
                fieldPrice.setError(getString(R.string.MH22_024));
            }
        }
    }

    public interface OnUpdatePriceDialogClickListener {
        void onUpdatePriceClickListener(double price, int currency);
    }

    public static void showDialog(FragmentManager fragmentManager,
                                  OnUpdatePriceDialogClickListener onUpdatePriceDialogClickListener) {
        UpdatePriceDialog updatePriceDialog = new UpdatePriceDialog();
        updatePriceDialog.onUpdatePriceDialogClickListener = onUpdatePriceDialogClickListener;
        updatePriceDialog.show(fragmentManager, UpdatePriceDialog.class.getSimpleName());
    }
}
