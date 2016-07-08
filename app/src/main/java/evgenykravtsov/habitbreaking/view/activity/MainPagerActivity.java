package evgenykravtsov.habitbreaking.view.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import evgenykravtsov.habitbreaking.R;
import evgenykravtsov.habitbreaking.presenter.MainPagerViewPresenter;
import evgenykravtsov.habitbreaking.view.MainPagerView;
import evgenykravtsov.habitbreaking.view.fragment.ConsumptionDetailsFragment;
import evgenykravtsov.habitbreaking.view.fragment.ConsumptionFragment;

public class MainPagerActivity extends AppCompatActivity implements MainPagerView {

    @BindString(R.string.no_internet_connection_warning)
    String noInternetConnectionWarning;
    @BindString(R.string.statistic_restore_success_message)
    String statisticRestoreSuccessMessage;
    @BindString(R.string.statistic_cleared_warning)
    String statisticClearedWarning;
    @BindString(R.string.user_deleted_warning)
    String userDeletedWarning;
    @BindString(R.string.downloading_process_warning)
    String downloadingProcessWarning;
    @BindString(R.string.statistic_delete_warning)
    String statisticDeleteWarning;
    @BindString(R.string.user_delete_warning)
    String userDeleteWarning;

    @BindView(R.id.main_pager_activity_drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.main_pager_activity_view_pager)
    ViewPager viewPager;
    @BindView(R.id.main_pager_activity_modes_button)
    Button modesButton;
    @BindView(R.id.main_pager_activity_registraion_button)
    Button registrationButton;
    @BindView(R.id.main_pager_activity_delete_statistic_button)
    Button deleteStatisticButton;
    @BindView(R.id.main_pager_activity_restore_statistic_button)
    Button restoreStatisticButton;
    @BindView(R.id.main_pager_activity_delete_account_button)
    Button deleteAccountButton;
    @BindView(R.id.main_pager_activity_user_name_text_view)
    TextView userNameTextView;

    @OnClick(R.id.main_pager_activity_modes_button)
    public void onClickModesButton() {
        navigateToModesActivity();
    }

    @OnClick(R.id.main_pager_activity_registraion_button)
    public void onRegistrationButtonClicked() {
        navigateToRegistrationActivity();
    }

    @OnClick(R.id.main_pager_activity_delete_statistic_button)
    public void onDeleteStatisticButtonClicked() {
        userCheckDeleteStatistic();
    }

    @OnClick(R.id.main_pager_activity_restore_statistic_button)
    public void onRestoreStatisticButtonClicked() {
        navigateToRegistrationActivityForStatisticRestore();
    }

    @OnClick(R.id.main_pager_activity_delete_account_button)
    public void onDeleteAccountButtonClicked() {
        userCheckDeleteUser();
    }

    ////

    private MainPagerViewPresenter presenter;
    private ProgressDialog progressDialog;

    ////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pager_main);
        ButterKnife.bind(this);
        prepareToolbar();
        prepareViewPager();
        setFonts();
    }

    @Override
    protected void onStart() {
        super.onStart();
        bindPresenter();
        prepareDrawerMenu();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindPresenter();
    }

    ////

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.main_activity_menu_button:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.main_activity_statistic_button:
                navigateToStatisticActivity();
                break;
        }
        return true;
    }

    ////

    @Override
    public void notifyNoInternetConnection() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setMessage(noInternetConnectionWarning)
                .setPositiveButton(getString(android.R.string.ok), null)
                .create();
        dialog.show();
    }

    @Override
    public void notifyStatisticSaved() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setMessage(statisticRestoreSuccessMessage)
                .setPositiveButton(getString(android.R.string.ok), null)
                .create();
        dialog.show();
    }

    @Override
    public void notifyStatisticCleared() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setMessage(statisticClearedWarning)
                .setPositiveButton(getString(android.R.string.ok), null)
                .create();
        dialog.show();
    }

    @Override
    public void notifyUserDeleted() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setMessage(userDeletedWarning)
                .setPositiveButton(getString(android.R.string.ok), null)
                .create();
        dialog.show();
        prepareDrawerMenu();
    }

    @Override
    public void showProgress() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
        }
        progressDialog.setMessage(downloadingProcessWarning);
        progressDialog.setIndeterminate(true);
        progressDialog.show();
    }

    @Override
    public void hideProgress() {
        if (progressDialog != null) {
            progressDialog.hide();
        }
    }

    ////

    private void bindPresenter() {
        presenter = new MainPagerViewPresenter(this);
    }

    private void unbindPresenter() {
        presenter.unbindView();
        presenter = null;
    }

    private void prepareToolbar() {
        setSupportActionBar(toolbar);
    }

    private void prepareDrawerMenu() {
        if (presenter.isUserRegistered()) {
            userNameTextView.setVisibility(View.GONE);
            userNameTextView.setText(presenter.getUserName());
            registrationButton.setVisibility(View.GONE);
            deleteAccountButton.setVisibility(View.VISIBLE);
        } else {
            userNameTextView.setVisibility(View.GONE);
            registrationButton.setVisibility(View.VISIBLE);
            deleteAccountButton.setVisibility(View.GONE);
        }
    }

    private void prepareViewPager() {
        viewPager.setAdapter(new MainPagerAdapter(getSupportFragmentManager()));
    }

    private void navigateToModesActivity() {
        Intent startModesActivity = new Intent(this, ModesActivity.class);
        startActivity(startModesActivity);
    }

    private void navigateToRegistrationActivity() {
        Intent startRegistrationActivity = new Intent(this, RegistrationActivity.class);
        startActivity(startRegistrationActivity);
    }

    private void navigateToRegistrationActivityForStatisticRestore() {
        Intent startRegistrationActivity = new Intent(this, RegistrationActivity.class);
        startRegistrationActivity.putExtra(RegistrationActivity.KEY_STATISTIC_RESTORE_INTENT, true);
        startActivity(startRegistrationActivity);
    }

    private void navigateToStatisticActivity() {
        Intent intent = new Intent(this, StatisticActivity.class);
        startActivity(intent);
    }

    private void userCheckDeleteStatistic() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setMessage(statisticDeleteWarning)
                .setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        presenter.deleteStatistic();
                    }
                })
                .setNegativeButton(getString(android.R.string.cancel), null)
                .create();
        dialog.show();
    }

    private void userCheckDeleteUser() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setMessage(userDeleteWarning)
                .setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        presenter.deleteUser();
                    }
                })
                .setNegativeButton(getString(android.R.string.cancel), null)
                .create();
        dialog.show();
    }

    private void setFonts() {
        Typeface robotoTypeface = Typeface.createFromAsset(getAssets(), "Roboto-Light.ttf");
        modesButton.setTypeface(robotoTypeface);
        registrationButton.setTypeface(robotoTypeface);
        deleteStatisticButton.setTypeface(robotoTypeface);
        restoreStatisticButton.setTypeface(robotoTypeface);
        deleteAccountButton.setTypeface(robotoTypeface);
    }

    ////

    private class MainPagerAdapter extends FragmentPagerAdapter {

        public MainPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        ////

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0: return new ConsumptionFragment();
                case 1: return new ConsumptionDetailsFragment();
                default: return new ConsumptionFragment();
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}
