package com.example.translateconnector.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.imoktranslator.R;
import com.imoktranslator.customview.OpenSansEditText;
import com.imoktranslator.utils.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ducpv on 3/24/18.
 */

public class TextFieldView extends FrameLayout implements TextView.OnEditorActionListener, View.OnFocusChangeListener {

    @BindView(R.id.edt_value)
    OpenSansEditText edtValue;
    @BindView(R.id.txt_value)
    OpenSansTextView txtValue;
    @BindView(R.id.txt_required)
    OpenSansTextView txtRequired;
    @BindView(R.id.layout_text_field)
    CardView layoutTextField;
    @BindView(R.id.txt_error)
    OpenSansTextView txtError;
    @BindView(R.id.root_view)
    ViewGroup rootView;

    private int mNextFocusForward;
    private int maxLength;
    private OnTextFieldErrorListener onTextFieldErrorListener;
    private OnDataChangedListener onDataChangedListener;

    public void setOnDataChangedListener(OnDataChangedListener onDataChangedListener) {
        this.onDataChangedListener = onDataChangedListener;
    }

    public void setOnTextFieldErrorListener(OnTextFieldErrorListener onTextFieldErrorListener) {
        this.onTextFieldErrorListener = onTextFieldErrorListener;
    }

    public TextFieldView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public TextFieldView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public TextFieldView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {

        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.item_text_field_common, this, true);

        ButterKnife.bind(this);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.item_text_field_common);
        String text = typedArray.getString(R.styleable.item_text_field_common_text);
        String hint = typedArray.getString(R.styleable.item_text_field_common_hint);
        String error = typedArray.getString(R.styleable.item_text_field_common_error);
        float textSize = typedArray.getDimensionPixelSize(R.styleable.item_text_field_common_textSize,
                getContext().getResources().getDimensionPixelOffset(R.dimen.text_small));
        boolean isRequired = typedArray.getBoolean(R.styleable.item_text_field_common_isRequired, false);
        boolean isEditable = typedArray.getBoolean(R.styleable.item_text_field_common_editable, true);
        mNextFocusForward = typedArray.getResourceId(R.styleable.item_text_field_common_nextFocusForward, View.NO_ID);
        maxLength = typedArray.getInt(R.styleable.item_text_field_common_maxLength, Integer.MAX_VALUE);
        boolean isNumber = typedArray.getBoolean(R.styleable.item_text_field_common_isNumberOnly, false);

        typedArray.recycle();

        txtValue.setText(text);
        txtError.setText(error);

        edtValue.setText(text);
        edtValue.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        edtValue.setInputType(InputType.TYPE_CLASS_TEXT);
        edtValue.setMaxLines(1);

        if (isNumber) {
            edtValue.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength),
                    (source, start, end, dest, dstart, dend) -> Utils.numberFilter(source, start, end)});
        } else {
            edtValue.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
        }

        if (hint != null) {
            edtValue.setHint(hint);
        }

        edtValue.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        txtValue.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        txtRequired.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);

        txtRequired.setVisibility(isRequired ? VISIBLE : GONE);
        if (isEditable) {
            txtValue.setVisibility(GONE);
            edtValue.setVisibility(VISIBLE);
        } else {
            edtValue.setVisibility(GONE);
            txtValue.setVisibility(VISIBLE);
        }

        edtValue.setOnEditorActionListener(this);
        edtValue.setOnFocusChangeListener(this);
        initDataChangedListener();
    }

    private void initDataChangedListener() {
        edtValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (onDataChangedListener != null)
                    onDataChangedListener.onDataChanged(s.toString());
            }
        });
    }

    public void setText(String text) {
        this.edtValue.setText(text);
        this.txtValue.setText(text);
    }

    public String getText() {
        return edtValue.getVisibility() == VISIBLE ?
                edtValue.getText().toString().trim() : txtValue.getText().toString().trim();
    }

    public OpenSansEditText getEdtValue() {
        return edtValue;
    }

    public OpenSansTextView getTxtValue() {
        return txtValue;
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

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER))
                || (actionId == EditorInfo.IME_ACTION_DONE) || (actionId == EditorInfo.IME_ACTION_NEXT)) {
            if (onTextFieldErrorListener != null) {
                onTextFieldErrorListener.onInputError(edtValue.getText().toString().trim());
            }
        }
        return false;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (!hasFocus && onTextFieldErrorListener != null) {
            onTextFieldErrorListener.onInputError(edtValue.getText().toString().trim());
        }
    }

    /**
     * Param: a Constant in EditorInfo class
     * ex: EditorInfo.IME_ACTION_DONE
     */
    public void setImeOptions(int action) {
        edtValue.setImeOptions(action);
    }

    /**
     * Param: a Constant in InputType class
     * ex: InputType.TYPE_CLASS_PHONE
     */
    public void setInputType(int inputType) {
        edtValue.setInputType(inputType);
    }

    public void setHint(String hint) {
        edtValue.setHint(hint);
    }

    public int getMaxLength() {
        return maxLength;
    }

    public interface OnTextFieldErrorListener {
        void onInputError(String text);
    }

    public interface OnDataChangedListener {
        void onDataChanged(String data);
    }
}
