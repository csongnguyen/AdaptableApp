package com.adaptableandroid;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.adaptableandroid.com.adaptableandroid.models.Task;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.Legend.LegendForm;
import com.github.mikephil.charting.components.Legend.LegendPosition;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.components.YAxis.AxisDependency;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.filter.Approximator;
import com.github.mikephil.charting.data.filter.Approximator.ApproximatorType;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.Highlight;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.OnWheelScrollListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.AbstractWheelTextAdapter;
import kankan.wheel.widget.adapters.ArrayWheelAdapter;

public class LineChartActivity2 extends ActionBarActivity implements OnSeekBarChangeListener,
        OnChartValueSelectedListener {

    private LineChart mChart;
    List<List<Task>> myLists;
    int wheelIndexStart = 1;
//    private SeekBar mSeekBarX, mSeekBarY;
//    private TextView tvX, tvY;
    private boolean scrolling = false; // Scrolling flag

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_linechart);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayShowTitleEnabled(true);
        actionbar.setDisplayUseLogoEnabled(true);
        actionbar.setDisplayShowHomeEnabled(true);
        actionbar.setBackgroundDrawable(new ColorDrawable(0xFF5BA4F3));//0xFF4697b5

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar);
        TextView tView = (TextView) getSupportActionBar().getCustomView().findViewById(R.id.actionbarTitle);
        tView.setText("Adaptable");

        /*************************WHEEL PICKER **********************************/
        final WheelView disasterWheel = (WheelView) findViewById(R.id.disasterWheelPicker);
        disasterWheel.setVisibleItems(3);
        disasterWheel.setViewAdapter(new CountryAdapter(this));

//        final WheelView city = (WheelView) findViewById(R.id.cityWheelPicker);
//        city.setVisibleItems(5);


        new DisplayChecklistPercent().execute();
        disasterWheel.setCurrentItem(wheelIndexStart); // Set at earthquake right now

        disasterWheel.addChangingListener(new OnWheelChangedListener() {
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                if (!scrolling) {
                    updateChecklistPreview(myLists, newValue);
                }
            }
        });

        disasterWheel.addScrollingListener( new OnWheelScrollListener() {
            public void onScrollingStarted(WheelView wheel) {
                scrolling = true;
            }
            public void onScrollingFinished(WheelView wheel) {
                scrolling = false;
                updateChecklistPreview(myLists, disasterWheel.getCurrentItem());
            }
        });
        /******************************************************************************/

//        tvX = (TextView) findViewById(R.id.tvXMax);
//        tvY = (TextView) findViewById(R.id.tvYMax);
//        mSeekBarX = (SeekBar) findViewById(R.id.seekBar1);
//        mSeekBarY = (SeekBar) findViewById(R.id.seekBar2);
//
//        mSeekBarX.setProgress(45);
//        mSeekBarY.setProgress(100);
//
//        mSeekBarY.setOnSeekBarChangeListener(this);
//        mSeekBarX.setOnSeekBarChangeListener(this);

        mChart = (LineChart) findViewById(R.id.lineChart1);
        mChart.setOnChartValueSelectedListener(this);

        // no description text
        mChart.setDescription("");
        mChart.setNoDataTextDescription("You need to provide data for the chart.");

        // enable value highlighting
        mChart.setHighlightEnabled(true);

        // enable touch gestures
        mChart.setTouchEnabled(true);

        mChart.setDragDecelerationFrictionCoef(0.9f);

        // enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setDrawGridBackground(false);
        mChart.setHighlightPerDragEnabled(true);

        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(true);

        // set an alternative background color
        mChart.setBackgroundColor(Color.WHITE);//Color.LTGRAY);

        // add data
        setData(8, 30);

        mChart.animateX(2500);


//        Typeface tf = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");

        // get the legend (only possible after setting data)
        Legend l = mChart.getLegend();

        // modify the legend ...
        // l.setPosition(LegendPosition.LEFT_OF_CHART);
//        l.setForm(LegendForm.LINE);
        l.setForm(LegendForm.SQUARE);
//        l.setTypeface(tf);
        l.setTextSize(11f);
        l.setTextColor(Color.BLACK);
        l.setPosition(LegendPosition.BELOW_CHART_LEFT);


        XAxis xAxis = mChart.getXAxis();
//        xAxis.setTypeface(tf);
        xAxis.setTextSize(12f);
        xAxis.setTextColor(Color.BLACK);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(true);
        xAxis.setSpaceBetweenLabels(1);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        YAxis leftAxis = mChart.getAxisLeft();
//        leftAxis.setTypeface(tf);
//        leftAxis.setTextColor(ColorTemplate.getHoloBlue());
        leftAxis.setDrawGridLines(false);
        leftAxis.setAxisMaxValue(50);//200f);
//        leftAxis.setDrawGridLines(true);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setDrawAxisLine(false);
        rightAxis.setDrawLabels(false);
//        rightAxis.setTypeface(tf);
        rightAxis.setTextColor(Color.RED);
//        rightAxis.setAxisMaxValue(900);
//        rightAxis.setStartAtZero(false);
//        rightAxis.setAxisMinValue(-200);
        rightAxis.setDrawGridLines(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_without_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            // Set the text view as the activity layout
            try{
//            Log.d("SETTINGS", "Trying to inflate settings");
//            // We need to get the instance of the LayoutInflater, use the context of this activity
//            LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            ViewGroup vGroup = (ViewGroup) findViewById(R.id.main);
//            View layout = inflater.inflate(R.layout.settings, (ViewGroup) findViewById(R.id.settings_layout));

//            layout.startAnimation(AnimationUtils.loadAnimation(layout.getContext(), R.anim.slide_in_right));


/***************************************************************/
                // We need to get the instance of the LayoutInflater, use the context of this activity
                LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                //Inflate the view from a predefined XML layout
                View layout = inflater.inflate(R.layout.popup_layout, (ViewGroup) findViewById(R.id.popup_element));

                // create a 300px width and 470px height PopupWindow
                final PopupWindow pw = new PopupWindow(layout, 400, 400, true);
                pw.showAtLocation(layout, Gravity.CENTER, 0, 0);

                // Create the text view
                TextView mResultText = (TextView) layout.findViewById(R.id.popup_text);
                mResultText.setText("Settings will be coming soon!");
                Button cancelButton = (Button) layout.findViewById(R.id.end_data_send_button);
                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pw.dismiss();
                    }
                });

                ObjectAnimator fadeIn = ObjectAnimator.ofFloat(layout, "alpha", .3f, 1f);
                fadeIn.setDuration(200);

                final AnimatorSet mAnimationSet = new AnimatorSet();
                mAnimationSet.play(fadeIn);
                mAnimationSet.start();

            }catch(Exception e){
                e.printStackTrace();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

//        tvX.setText("" + (mSeekBarX.getProgress() + 1));
//        tvY.setText("" + (mSeekBarY.getProgress()));
//
//        setData(mSeekBarX.getProgress() + 1, mSeekBarY.getProgress());

        // redraw
        mChart.invalidate();
    }

    private void setData(int count, float range) {

        ArrayList<String> xVals = new ArrayList<String>();
        for (int i = 0; i < count; i++) {
            xVals.add((i) + "");
        }

        ArrayList<Entry> yVals1 = new ArrayList<Entry>();

        for (int i = 0; i < count; i++) {
            float mult = range / 2f;
            float val = (float) (Math.random() * mult) + 5;// + (float)
            // ((mult *
            // 0.1) / 10);
            yVals1.add(new Entry(val, i));
        }

        // create a dataset and give it a type
        LineDataSet set1 = new LineDataSet(yVals1, "Tasks Completed");
        set1.setAxisDependency(AxisDependency.LEFT);
        set1.setColor(getResources().getColor(R.color.MediumSeaGreen));//ColorTemplate.getHoloBlue());
        set1.setCircleColor(getResources().getColor(R.color.MediumSeaGreen));
        set1.setLineWidth(2f);
        set1.setCircleSize(3f);
        set1.setFillAlpha(65);
        set1.setFillColor(getResources().getColor(R.color.MediumSeaGreen));//ColorTemplate.getHoloBlue());
        set1.setHighLightColor(Color.rgb(244, 117, 117));
        set1.setDrawCircleHole(false);
//        set1.setCircleHoleColor(Color.WHITE);

        ArrayList<Entry> yVals2 = new ArrayList<Entry>();

        for (int i = 0; i < count; i++) {
            float mult = range;
            float val = (float) (Math.random() * mult) + 10;// + (float)
            // ((mult *
            // 0.1) / 10);
            yVals2.add(new Entry(val, i));
        }

        // create a dataset and give it a type
        LineDataSet set2 = new LineDataSet(yVals2, "Articles Read");
//        set2.setAxisDependency(AxisDependency.RIGHT);
        set2.setColor(Color.RED);
        set2.setCircleColor(Color.RED);
        set2.setLineWidth(2f);
        set2.setCircleSize(3f);
        set2.setFillAlpha(65);
        set2.setFillColor(Color.RED);
        set2.setDrawCircleHole(false);
        set2.setHighLightColor(Color.rgb(244, 117, 117));

        ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
        dataSets.add(set2);
        dataSets.add(set1); // add the datasets

        // create a data object with the datasets
        LineData data = new LineData(xVals, dataSets);
        data.setValueTextColor(Color.BLACK);
        data.setValueTextSize(9f);
        data.setDrawValues(false);

        // set data
        mChart.setData(data);
    }

    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
        Log.i("Entry selected", e.toString());
    }

    @Override
    public void onNothingSelected() {
        Log.i("Nothing selected", "Nothing selected.");
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub

    }

    /**
     * Updates the city wheel
     */
    private void updateCities(WheelView city, String cities[][], int index) {
        ArrayWheelAdapter<String> adapter =
                new ArrayWheelAdapter<String>(this, cities[index]);
        adapter.setTextSize(18);
        city.setViewAdapter(adapter);
        city.setCurrentItem(cities[index].length / 2);
    }

    private void updateChecklistPreview(List<List<Task>> lists, int index) {
        TextView percentView = (TextView) findViewById(R.id.checklistPercent);
        TextView numberOfCompleteView = (TextView) findViewById(R.id.checklistNumberOfComplete);
        ProgressBar pbar = (ProgressBar) findViewById(R.id.progressBarChecklistInUserProfile);

        int numberCompleteTasks = getNumberOfComplete(lists.get(index));
        pbar.setProgress(numberCompleteTasks);
        pbar.setMax(lists.get(index).size());
        int percentFinished = (int)(100*((double)pbar.getProgress()/pbar.getMax()));

        percentView.setText(percentFinished + "%");
        numberOfCompleteView.setText(pbar.getProgress() + "/" + pbar.getMax() + " tasks complete");
    }
/********************WHEEL PICKER ***************************************************/
    /**
     * Adapter for countries
     */
    private class CountryAdapter extends AbstractWheelTextAdapter {
        // Countries names
        private String disasters[] = StringUtils.disasterTypes;
//        private String countries[] = new String[] {"USA", "Canada", "Ukraine", "France"};
        // Countries flags
//        private int flags[] =
//                new int[] {kankan.wheel.R.drawable.usa, kankan.wheel.R.drawable.canada, kankan.wheel.R.drawable.ukraine, kankan.wheel.R.drawable.france};

        /**
         * Constructor
         */
        protected CountryAdapter(Context context) {
            super(context, R.layout.country_layout, NO_RESOURCE);

            setItemTextResource(R.id.country_name);
        }

        @Override
        public View getItem(int index, View cachedView, ViewGroup parent) {
            View view = super.getItem(index, cachedView, parent);
//            ImageView img = (ImageView) view.findViewById(kankan.wheel.R.id.flag);
//            img.setImageResource(flags[index]);
            return view;
        }

        @Override
        public int getItemsCount() {
            return disasters.length;
//            return countries.length;
        }

        @Override
        protected CharSequence getItemText(int index) {
            return disasters[index];
//            return countries[index];
        }
    }

    private int getNumberOfComplete(List<Task> mylist){
        int total = 0;
        for(int i = 0;i < mylist.size();i++){
            if(mylist.get(i).getStatus() == 1){
                total++;
            }
        }
        return total;
    }


    private class DisplayChecklistPercent extends AsyncTask<String, String, String> {
        private static final String POPULATE_CHECKLIST_URL = "http://ec2-54-149-172-15.us-west-2.compute.amazonaws.com/getChecklist.php";
        JSONObject[] jsonObjects = new JSONObject[StringUtils.disasterTypes.length];
        JSONParser jsonParser = new JSONParser();
        ProgressDialog pDialog;

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            pDialog = new ProgressDialog(LineChartActivity2.this);
            pDialog.setMessage("Setting up checklist percents...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... urls){
            // Check for success tag
            int success;
            try{
                Log.d("Checklist Request", "Starting");
                System.out.println("Checking checklist request right now....");

                for(int i = 0; i < jsonObjects.length; i++){
                    jsonObjects[i] = jsonParser.makeHttpGetRequest(POPULATE_CHECKLIST_URL, DisplayChecklistActivityWithFragment.DISASTER_TYPE, StringUtils.disasterTypes[i]);
                    Log.d("Checking result:", jsonObjects[i].toString());
                }
            } catch(Exception e){
                e.printStackTrace();
            }
            return null;
        }


        @Override
        protected void onPostExecute(String someString){
            try {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        pDialog.dismiss();
                    }}, 300);  // 1000 milliseconds
//                List<MyAdapter> myAdapters = new ArrayList<MyAdapter>();
                myLists = new ArrayList<List<Task>>();
                List<Task> mylist;
                for(int a = 0; a < jsonObjects.length; a++){
                    JSONObject jsonObject = jsonObjects[a];
                    if(!jsonObject.toString().isEmpty()){
                        JSONArray products = jsonObject.getJSONArray(StringUtils.TAG_PRODUCTS);
                        mylist = new ArrayList<Task>();

                        for(int i = 0; i < products.length(); i++){
                            JSONObject object = products.getJSONObject(i);
                            String shortWarning = object.getString(DisplayChecklistActivityWithFragment.TAG_SHORT);
                            String longWarning = object.getString(DisplayChecklistActivityWithFragment.TAG_LONG);
                            String status = object.getString(DisplayChecklistActivityWithFragment.TAG_STATUS);

                            if(!StringUtils.stringIsEmpty(shortWarning) && !StringUtils.stringIsEmpty(longWarning)
                                    && !StringUtils.stringIsEmpty(status)){// && !StringUtils.stringIsEmpty(id)){
                                mylist.add(new Task(shortWarning, longWarning, Integer.parseInt(status)));
                            }
                        }
                        myLists.add(mylist);

                    }
                }
                TextView percentView = (TextView) findViewById(R.id.checklistPercent);
                TextView numberOfCompleteView = (TextView) findViewById(R.id.checklistNumberOfComplete);
                ProgressBar pbar = (ProgressBar) findViewById(R.id.progressBarChecklistInUserProfile);

                int numberCompleteTasks = getNumberOfComplete(myLists.get(wheelIndexStart));
                pbar.setProgress(numberCompleteTasks);
                pbar.setMax(myLists.get(wheelIndexStart).size());
                int percentFinished = (int)(100*((double)pbar.getProgress()/pbar.getMax()));

                percentView.setText(percentFinished + "%");
                numberOfCompleteView.setText(pbar.getProgress() + "/" + pbar.getMax() + " tasks complete");
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
    }
}
