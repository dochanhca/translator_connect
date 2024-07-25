package com.example.translateconnector.customview;

import android.content.Context;
import android.graphics.Color;
import android.os.SystemClock;
import android.text.Editable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.google.android.flexbox.FlexboxLayout;
import com.imoktranslator.R;
import com.imoktranslator.model.firebase.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CustomFlexBoxLayout extends FlexboxLayout {

    @BindView(R.id.edt_label)
    OpenSansEditText edtLabel;

    private List<User> selectedItems = new ArrayList<>();
    private HashMap<String, OpenSansTextView> hashMapView = new HashMap<>();
    private DataSetChangeListener dataSetChangeListener;
    private boolean textAutoChange = false;
    private long lastTimeClickDelete;

    public CustomFlexBoxLayout(Context context) {
        super(context);
        init();
    }

    public CustomFlexBoxLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomFlexBoxLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        inflater.inflate(R.layout.custom_flex_box_layout, this, true);
        ButterKnife.bind(this);

        edtLabel.addTextChangedListener(new TextWatcherExtended() {
            @Override
            public void afterTextChanged(Editable s, boolean backSpace) {
                if (handleEvent()) {
                    // Here you are! You got missing "backSpace" flag
                    Log.d("FlexBox", "query: " + s.toString());
                    if (backSpace) {
                        deleteTheLastFlexBoxItem();
                    } else {
                        Log.d("FlexBox", "textAutoChange: " + textAutoChange);
                        if (textAutoChange) {
                            textAutoChange = false;
                        } else {
                            //text change by user
                            dataSetChangeListener.query(s.toString());
                        }
                    }
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });
    }

    private boolean handleEvent() {
        if (SystemClock.elapsedRealtime() - lastTimeClickDelete < (200)) {
            return false;
        }
        lastTimeClickDelete = SystemClock.elapsedRealtime();
        return true;
    }

    private void deleteTheLastFlexBoxItem() {
        int count = getChildCount();
        if (count - 2 >= 0) {
            removeViewAt(count - 2);
            User targetUser = selectedItems.get(selectedItems.size() - 1);
            selectedItems.remove(targetUser);
            hashMapView.remove(targetUser.getName() + "-" + targetUser.getId());

            if (dataSetChangeListener != null) {
                dataSetChangeListener.onItemRemoved(targetUser);
            }
        }
    }

    public void deleteFlexBoxItem(User user) {
        String targetKey = user.getName() + "-" + user.getId();
        OpenSansTextView targetView = hashMapView.get(targetKey);

        if (targetView != null) {
            removeView(targetView);
            hashMapView.remove(targetKey);
            selectedItems.remove(user);
        }

        if (dataSetChangeListener != null) {
            dataSetChangeListener.onItemRemoved(user);
        }
    }

    public void addNewUser(User user) {
        OpenSansTextView tv = createViewItem(user);
        addView(tv, getChildCount() - 1);
        hashMapView.put(user.getName() + "-" + user.getId(), tv);
        selectedItems.add(user);

        if (edtLabel.getText().length() > 0) {
            textAutoChange = true;
            edtLabel.setText("");
        }

        if (dataSetChangeListener != null) {
            dataSetChangeListener.onItemAdded(user);
        }
    }

    private OpenSansTextView createViewItem(User user) {
        FlexboxLayout.LayoutParams params = new FlexboxLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        OpenSansTextView tv = new OpenSansTextView(getContext());
        tv.setText(user.getName() + ", ");
        tv.setLayoutParams(params);
        tv.setGravity(Gravity.CENTER);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.text_normal));
        tv.setTextColor(Color.BLACK);
        return tv;
    }

    public boolean isContainThisItem(User user) {
        return selectedItems.contains(user);
    }

    public String getTextSearch() {
        return edtLabel.getText().toString().trim();
    }

    public interface DataSetChangeListener {
        void onItemAdded(User user);

        void onItemRemoved(User user);

        void query(String text);
    }

    public void addDataSetChangeListener(DataSetChangeListener dataSetChangeListener) {
        this.dataSetChangeListener = dataSetChangeListener;
    }
}
