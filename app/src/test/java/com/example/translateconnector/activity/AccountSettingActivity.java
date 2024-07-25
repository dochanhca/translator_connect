package com.example.translateconnector.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.imoktranslator.R;
import com.imoktranslator.bottomsheet.CustomBottomSheetFragment;
import com.imoktranslator.customview.HeaderView;
import com.imoktranslator.customview.OpenSansBoldTextView;
import com.imoktranslator.customview.OpenSansTextView;
import com.imoktranslator.customview.SelectionView;
import com.imoktranslator.customview.TextFieldView;
import com.imoktranslator.presenter.AccountSettingPresenter;
import com.imoktranslator.utils.Constants;
import com.imoktranslator.utils.LocaleHelper;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class AccountSettingActivity extends BaseActivity implements
        AccountSettingPresenter.AccountSettingView {

    @BindView(R.id.header)
    HeaderView header;
    @BindView(R.id.txt_password_label)
    OpenSansBoldTextView txtPasswordLabel;
    @BindView(R.id.field_passwod)
    TextFieldView fieldPasswod;
    @BindView(R.id.txt_language_label)
    OpenSansBoldTextView txtLanguageLabel;
    @BindView(R.id.selection_language)
    SelectionView selectionLanguage;
    @BindView(R.id.txt_action_label)
    OpenSansBoldTextView txtActionLabel;
    @BindView(R.id.txt_priority_label)
    OpenSansTextView txtPriorityLabel;
    @BindView(R.id.selection_priority)
    SelectionView selectionPriority;

    private List<String> arrSettingLang;
    private List<String> arrLangCode;

    private String selectedLang;

    private AccountSettingPresenter presenter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_account_setting;
    }

    @Override
    protected void initViews() {
        selectedLang = getIntent().getStringExtra(Constants.SETTING_LANGUAGE);

        if (TextUtils.isEmpty(selectedLang)) {
            selectedLang = LocaleHelper.getLanguage(this);
        }

        presenter = new AccountSettingPresenter(this, this);
        presenter.getPrioritySetting();

        header.setCallback(() -> onBackPressed());

        arrSettingLang = Arrays.asList(getResources().getStringArray(R.array.arr_setting_lang));
        arrLangCode = Arrays.asList(getResources().getStringArray(R.array.arr_lang_code));

        fieldPasswod.getTxtValue().setTextColor(getResources().getColor(R.color.dark_sky_blue));
        fieldPasswod.getTxtValue().setGravity(Gravity.CENTER);

        selectionLanguage.getSeSelectionView().setTextColor(getResources().getColor(R.color.dark_sky_blue));
        selectionLanguage.getSeSelectionView().setGravity(Gravity.CENTER);
        selectionLanguage.setSelectionValue(arrSettingLang.get(getSelectedLang()));
    }

    @OnClick({R.id.field_passwod, R.id.selection_language, R.id.selection_priority})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.field_passwod:
                ChangePasswordActivity.startActivity(this, 0, false);
                break;
            case R.id.selection_language:
                showSelectLangs();
                break;
            case R.id.selection_priority:
                showPopup(selectionPriority);
                break;
        }
    }

    private void showSelectLangs() {
        CustomBottomSheetFragment bsTransLanguage = CustomBottomSheetFragment.newInstance(getString(R.string.MH42_007));
        bsTransLanguage.setOptions(arrSettingLang);
        bsTransLanguage.setSelectedPosition(getSelectedLang());
        bsTransLanguage.setListener(position -> {
            selectionLanguage.setSelectionValue(arrSettingLang.get(position));
            presenter.changeLanguage(arrLangCode.get(position));
        });
        bsTransLanguage.show(getSupportFragmentManager(), bsTransLanguage.getTag());
    }

    private int getSelectedLang() {
        int value;
        try {
            value = arrLangCode.indexOf(selectedLang);
        } catch (Exception e) {
            return 0;
        }
        return value < 0 ? 0 : value;
    }

    private void showPopup(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.inflate(R.menu.menu_select_priority);
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.priority_public:
                    presenter.changePostPriority(Constants.PUBLIC_MODE);
                    break;
                case R.id.priority_friend:
                    presenter.changePostPriority(Constants.FRIENDS_MODE);
                    break;
                case R.id.priority_private:
                    presenter.changePostPriority(Constants.PRIVATE_MODE);
                    break;
            }
            return false;
        });
    }

    @Override
    public void onSettingLanguage() {
        Intent i = getPackageManager()
                .getLaunchIntentForPackage(getPackageName() );
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

    @Override
    public void onChangePriority(String priority) {
        Toast.makeText(this, getString(R.string.TB_1079), Toast.LENGTH_SHORT).show();
        updatePriorityView(priority);
    }

    @Override
    public void onGetPrioritySetting(String priority) {
        updatePriorityView(priority);
    }

    private void updatePriorityView(String priority) {
        if (priority.equals(Constants.PUBLIC_MODE)) {
            selectionPriority.setSelectionValue(getString(R.string.MH45_004));
        } else if (priority.equals(Constants.FRIENDS_MODE)) {
            selectionPriority.setSelectionValue(getString(R.string.MH45_005));
        } else {
            selectionPriority.setSelectionValue(getString(R.string.MH45_010));
        }
    }

    public static void startActivity(BaseActivity activity, String language) {
        Intent intent = new Intent(activity, AccountSettingActivity.class);
        intent.putExtra(Constants.SETTING_LANGUAGE, language);
        activity.startActivity(intent);
    }

}
