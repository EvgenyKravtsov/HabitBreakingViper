package evgenykravtsov.habitbreaking.view.fragment;

import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import evgenykravtsov.habitbreaking.R;
import evgenykravtsov.habitbreaking.domain.model.ConsumptionDetailsDataEntity;
import evgenykravtsov.habitbreaking.domain.model.CostDetailsDataEntity;
import evgenykravtsov.habitbreaking.presenter.ConsumptionDetailsViewPresenter;
import evgenykravtsov.habitbreaking.view.ConsumptionDetailsView;

public class ConsumptionDetailsFragment extends Fragment implements ConsumptionDetailsView {

    private static final String TAG = ConsumptionDetailsFragment.class.getSimpleName();
    private static final String CURRENCY_QUERY =
            "http://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml";

    private ConsumptionDetailsViewPresenter presenter;

    ////

    @BindString(R.string.enter_all_data_warning)
    String enterAllDataWarning;
    @BindString(R.string.details_saved_warning)
    String detailsSavedWarning;
    @BindString(R.string.milligram_label)
    String milligramLabel;

    @BindView(R.id.consumption_details_fragment_container_view)
    LinearLayout containerView;
    @BindView(R.id.consumption_details_fragment_input_label)
    TextView inputLabel;
    @BindView(R.id.consumption_details_fragment_resin_label)
    TextView resinLabel;
    @BindView(R.id.consumption_details_fragment_resin_edit_text)
    EditText resinEditText;
    @BindView(R.id.consumption_details_fragment_nicotine_label)
    TextView nicotineLabel;
    @BindView(R.id.consumption_details_fragment_nicotine_edit_text)
    EditText nicotineEditText;
    @BindView(R.id.consumption_details_fragment_co_label)
    TextView coLabel;
    @BindView(R.id.consumption_details_fragment_co_edit_text)
    EditText coEditText;
    @BindView(R.id.consumption_details_fragment_input_summary_label)
    TextView inputSummaryLabel;
    @BindView(R.id.consumption_details_fragment_resin_text_view)
    TextView resinSummaryTextView;
    @BindView(R.id.consumption_details_fragment_resin_summary_label)
    TextView resinSummaryLabel;
    @BindView(R.id.consumption_details_fragment_nicotine_text_view)
    TextView nicotineSummaryTextView;
    @BindView(R.id.consumption_details_fragment_nicotine_summary_label)
    TextView nicotineSummaryLabel;
    @BindView(R.id.consumption_details_fragment_co_text_view)
    TextView coSummaryTextView;
    @BindView(R.id.consumption_details_fragment_co_summary_label)
    TextView coSummaryLabel;
    @BindView(R.id.consumption_details_fragment_cost_edit_text)
    EditText costEditText;
    @BindView(R.id.consumption_details_fragment_currency_spinner)
    Spinner currencySpinner;

    @OnClick(R.id.consumption_details_fragment_save_button)
    public void onSaveButtonClick() {
        if (isRequiredDataEntered()) {
            ConsumptionDetailsDataEntity data = new ConsumptionDetailsDataEntity(
                    Double.parseDouble(resinEditText.getText().toString()),
                    Double.parseDouble(nicotineEditText.getText().toString()),
                    Double.parseDouble(coEditText.getText().toString()));
            presenter.saveConsumptionDetails(data);

            if (presenter.isConsumptionDetailsCalculatingAllowed()) {
                ConsumptionDetailsDataEntity consumptionDetailsSummary =
                        presenter.makeConsumptionDetailsInitialCalculating();

                double resinSummary = consumptionDetailsSummary.getResin();
                double nicotineSummary = consumptionDetailsSummary.getNicotine();
                double coSummary = consumptionDetailsSummary.getCo();

                setTextForDataSummary(resinSummary, nicotineSummary, coSummary);
            }
        } else {
            Snackbar.make(containerView, enterAllDataWarning, Snackbar.LENGTH_LONG).show();
        }
    }

    @OnClick(R.id.consumption_details_fragment_save_cost_button)
    public void onSaveCostButtonClick() {
        if (isRequiredCostDataEntered()) {
            CostDetailsDataEntity data = new CostDetailsDataEntity(
                    Double.parseDouble(costEditText.getText().toString()),
                    currencySpinner.getSelectedItem().toString());
            presenter.saveCostDetails(data);
        } else {
            Snackbar.make(containerView, enterAllDataWarning, Snackbar.LENGTH_LONG).show();
        }
    }

    ////

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_consumption_details, container, false);
        ButterKnife.bind(this, view);
        setFonts();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        bindPresenter();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser) {
            establishInitialUiState();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        unbindPresenter();
    }

    ////

    @Override
    public void notifyConsumptionDetailsSaved() {
        Snackbar.make(containerView, detailsSavedWarning, Snackbar.LENGTH_LONG).show();
    }

    ////

    private void bindPresenter() {
        presenter = new ConsumptionDetailsViewPresenter(this);
    }

    private void unbindPresenter() {
        presenter.unbindView();
        presenter = null;
    }

    private boolean isRequiredDataEntered() {
        return !resinEditText.getText().toString().equals("") &&
                !nicotineEditText.getText().toString().equals("") &&
                !coEditText.getText().toString().equals("");
    }

    private boolean isRequiredCostDataEntered() {
        return !costEditText.getText().toString().equals("");
    }

    @SuppressLint("DefaultLocale")
    private void establishInitialUiState() {
        ConsumptionDetailsDataEntity data = presenter.loadConsumptionDetails();
        double resin = data.getResin();
        double nicotine = data.getNicotine();
        double co = data.getCo();
        resinEditText.setHint(String.format("%.1f", resin));
        nicotineEditText.setHint(String.format("%.1f", nicotine));
        coEditText.setHint(String.format("%.1f", co));

        ConsumptionDetailsDataEntity consumptionSummary = presenter.loadConsumptionDetailsSummary();
        double resinSummary = consumptionSummary.getResin();
        double nicotineSummary = consumptionSummary.getNicotine();
        double coSummary = consumptionSummary.getCo();
        setTextForDataSummary(resinSummary, nicotineSummary, coSummary);

        prepareCurrencySpinner();
    }

    @SuppressLint("DefaultLocale")
    private void setTextForDataSummary(double resin, double nicotine, double co) {
        resinSummaryTextView.setText(String.format("%.1f %s", resin, milligramLabel));
        nicotineSummaryTextView.setText(String.format("%.1f %s", nicotine, milligramLabel));
        coSummaryTextView.setText(String.format("%.1f %s", co, milligramLabel));
    }

    private void setFonts() {
        Typeface robotoTypeface =
                Typeface.createFromAsset(getActivity().getAssets(), "Roboto-Light.ttf");

        resinEditText.setTypeface(robotoTypeface);
        nicotineEditText.setTypeface(robotoTypeface);
        coEditText.setTypeface(robotoTypeface);
        inputLabel.setTypeface(robotoTypeface);
        resinLabel.setTypeface(robotoTypeface);
        nicotineLabel.setTypeface(robotoTypeface);
        coLabel.setTypeface(robotoTypeface);
        inputSummaryLabel.setTypeface(robotoTypeface);
        resinSummaryLabel.setTypeface(robotoTypeface);
        nicotineSummaryLabel.setTypeface(robotoTypeface);
        coSummaryLabel.setTypeface(robotoTypeface);
    }

    private void prepareCurrencySpinner() {
        String[] spinnerStrings = getResources().getStringArray(R.array.currency_label_array);
        final Typeface typeface = Typeface
                .createFromAsset(getActivity().getAssets(), "Roboto-Light.ttf");

        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(getActivity(), R.layout.spinner_item_registration) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view =  super.getView(position, convertView, parent);
                ((TextView) view).setTypeface(typeface);
                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view =  super.getDropDownView(position, convertView, parent);
                ((TextView) view).setTypeface(typeface);
                return view;
            }

            @Override
            public int getCount() {
                return super.getCount();
            }
        };
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item_registration);

        for (String str : spinnerStrings) {
            adapter.add(str);
        }

        currencySpinner.setAdapter(adapter);
    }
}
