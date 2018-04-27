package me.shouheng.omnilist.fragment.setting;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import com.afollestad.materialdialogs.MaterialDialog;

import de.psdev.licensesdialog.LicensesDialog;
import me.shouheng.omnilist.BuildConfig;
import me.shouheng.omnilist.R;
import me.shouheng.omnilist.databinding.FragmentAppInfoBinding;
import me.shouheng.omnilist.fragment.base.BaseFragment;
import me.shouheng.omnilist.listener.OnFragmentDestroyListener;
import me.shouheng.omnilist.utils.IntentUtils;

/**
 * Created by wangshouheng on 2017/12/3.*/
public class AppInfoFragment extends BaseFragment<FragmentAppInfoBinding> {

    public static AppInfoFragment newInstance() {
        Bundle args = new Bundle();
        AppInfoFragment fragment = new AppInfoFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_app_info;
    }

    @Override
    protected void doCreateView(Bundle savedInstanceState) {
        configToolbar();

        configViews();
    }

    private void configToolbar() {
        if (getActivity() != null) {
            ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
            if (actionBar != null) {
                actionBar.setTitle(R.string.app_info);
            }
        }
    }

    private void configViews() {
        String verName = BuildConfig.FLAVOR + "-" + BuildConfig.VERSION_NAME;
        getBinding().tvVersionName.setText(verName);

        getBinding().ctvTranslation.setOnCardTitleClickListener(() ->
                IntentUtils.openGithubProject(getActivity()));
        getBinding().ctvTranslation.setSubTitle(
                String.format(getString(R.string.translate_to_other_languages), getString(R.string.app_name)));

        getBinding().ctvRating.setOnCardTitleClickListener(() ->
                IntentUtils.openInMarket(getActivity()));
        getBinding().ctvRating.setSubTitle(
                String.format(getString(R.string.give_good_rating_if_you_like), getString(R.string.app_name)));

        getBinding().ctvDeveloper.setOnCardTitleClickListener(() ->
                IntentUtils.openDeveloperPage(getActivity()));

        getBinding().ctvLicense.setOnCardTitleClickListener(this::showLicensesDialog);

        getBinding().ctvChangelog.setOnCardTitleClickListener(this::showChangeLogDialog);
    }

    private void showLicensesDialog() {
        assert getContext() != null;
        new LicensesDialog.Builder(getContext())
                .setNotices(R.raw.notices)
                .setIncludeOwnLicense(true)
                .build()
                .showAppCompat();
    }

    private void showChangeLogDialog() {
        assert getContext() != null;
        new MaterialDialog.Builder(getContext())
                .customView(R.layout.dialog_changelog, false)
                .positiveText(R.string.text_ok)
                .build()
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (getActivity() instanceof OnFragmentDestroyListener) {
            ((OnFragmentDestroyListener) getActivity()).onFragmentDestroy();
        }
    }
}
