package evgenykravtsov.habitbreaking.view.activity;

import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindDrawable;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import evgenykravtsov.habitbreaking.R;
import evgenykravtsov.habitbreaking.domain.model.Mode;
import evgenykravtsov.habitbreaking.presenter.ModesViewPresenter;
import evgenykravtsov.habitbreaking.view.ModesView;

public class ModesActivity extends AppCompatActivity implements ModesView {

    private ModesViewPresenter presenter;

    //// BUTTERKNIFE BINDS

    @BindString(R.string.activate_button_label)
    String activateButtonLabel;
    @BindString(R.string.activated_button_label)
    String activatedButtonLabel;
    @BindString(R.string.not_enough_statistic_warning)
    String notEnoughStatisticWarning;

    @BindDrawable(R.drawable.free_mode_icon)
    Drawable freeModeIcon;
    @BindDrawable(R.drawable.free_mode__activated_icon)
    Drawable freeModeActivatedIcon;
    @BindDrawable(R.drawable.control_mode_icon)
    Drawable controlModeIcon;
    @BindDrawable(R.drawable.control_mode__activated_icon)
    Drawable controlModeActivatedIcon;
    @BindDrawable(R.drawable.health_mode_icon)
    Drawable healthModeIcon;
    @BindDrawable(R.drawable.health_mode_activated_icon)
    Drawable healthModeActivatedIcon;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.modes_activity_free_image_view)
    ImageView freeImageView;
    @BindView(R.id.modes_activity_free_title_text_view)
    TextView freeTitleTextView;
    @BindView(R.id.modes_activity_free_description_text_view)
    TextView freeDescriptionTextView;
    @BindView(R.id.modes_activity_free_button)
    Button freeButton;
    @BindView(R.id.modes_activity_control_image_view)
    ImageView controlImageView;
    @BindView(R.id.modes_activity_control_title_text_view)
    TextView controlTitleTextView;
    @BindView(R.id.modes_activity_control_description_text_view)
    TextView controlDescriptionTextView;
    @BindView(R.id.modes_activity_control_button)
    Button controlButton;
    @BindView(R.id.modes_activity_health_image_view)
    ImageView healthImageView;
    @BindView(R.id.modes_activity_health_title_text_view)
    TextView healthTitleTextView;
    @BindView(R.id.modes_activity_health_description_text_view)
    TextView healthDescriptionTextView;
    @BindView(R.id.modes_activity_health_button)
    Button healthButton;

    @OnClick(R.id.modes_activity_free_button)
    public void onClickFreeButton() {
        presenter.changeMode(Mode.FREE);
    }

    @OnClick(R.id.modes_activity_control_button)
    public void onClickControlButton() {
        presenter.changeMode(Mode.CONTROL);
    }

    @OnClick(R.id.modes_activity_health_button)
    public void onCliclHealthButton() {
        presenter.changeMode(Mode.HEALTH);
    }

    //// ACTIVITY LIFECYCLE

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modes);
        ButterKnife.bind(this);
        prepareToolbar();
        setFonts();
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter = new ModesViewPresenter();
        presenter.bind(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.unbind();
        presenter = null;
    }

    //// MENU LIFECYCLE

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                finish();
        }
        return true;
    }

    //// MODES VIEW INTERFACE

    @Override
    public void changeFreeModeState(boolean activated) {
        if (activated) {
            freeImageView.setImageDrawable(freeModeActivatedIcon);
            freeButton.setText(activatedButtonLabel);
            freeButton.setClickable(false);
        } else {
            freeImageView.setImageDrawable(freeModeIcon);
            freeButton.setText(activateButtonLabel);
            freeButton.setClickable(true);
        }
    }

    @Override
    public void changeControlModeState(boolean activated) {
        if (activated) {
            controlImageView.setImageDrawable(controlModeActivatedIcon);
            controlButton.setText(activatedButtonLabel);
            controlButton.setClickable(false);
        } else {
            controlImageView.setImageDrawable(controlModeIcon);
            controlButton.setText(activateButtonLabel);
            controlButton.setClickable(true);
        }
    }

    @Override
    public void changeHealthModeState(boolean activated) {
        if (activated) {
            healthImageView.setImageDrawable(healthModeActivatedIcon);
            healthButton.setText(activatedButtonLabel);
            healthButton.setClickable(false);
        } else {
            healthImageView.setImageDrawable(healthModeIcon);
            healthButton.setText(activateButtonLabel);
            healthButton.setClickable(true);
        }
    }

    @Override
    public void notifyNotEnoughStatistic(int remainingDays) {
        @SuppressLint("DefaultLocale") AlertDialog dialog = new AlertDialog.Builder(this)
                .setMessage(String.format(notEnoughStatisticWarning, remainingDays))
                .setPositiveButton(getString(android.R.string.ok), null)
                .create();
        dialog.show();
    }

    //// PRIVATE METHODS

    private void prepareToolbar() {
        toolbar.setNavigationIcon(R.drawable.navigation_arrow);
        setSupportActionBar(toolbar);
    }

    private void setFonts() {
        Typeface robotoRegular = Typeface.createFromAsset(getAssets(), "Roboto-Regular.ttf");
        Typeface robotoBold = Typeface.createFromAsset(getAssets(), "Roboto-Bold.ttf");

        freeTitleTextView.setTypeface(robotoBold);
        freeDescriptionTextView.setTypeface(robotoRegular);
        controlTitleTextView.setTypeface(robotoBold);
        controlDescriptionTextView.setTypeface(robotoRegular);
        healthTitleTextView.setTypeface(robotoBold);
        healthDescriptionTextView.setTypeface(robotoRegular);
    }
}
