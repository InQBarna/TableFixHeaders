package com.inqbarna.tablefixheaders;

import com.inqbarna.tablefixheaders.adapters.TableAdapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.DataSetObserver;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;

/**
 * This view shows a table which can scroll in both directions. Also still
 * leaves the headers fixed.
 * 
 * @author Brais Gabín
 */
public class TableFixHeaders extends LinearLayout {
	private final static int CLICK_SENSIVILITY = 2;

	private final static float SHADOW_SMOOTHING_DIP = 5f;

	private final Context context;

	private ScrollView scrollView;

	private final LinearLayout headerLinearLayout;
	private final LinearLayout headerRowLinearLayout;
	private final LinearLayout headerColumnLinearLayout;
	private final LinearLayout bodyLinearLayout;

	private int currentX;
	private int currentY;

	private TableAdapter adapter;

	private int maxScrollX;
	private int maxScrollY;

	private final View shadowLeft1;
	private final View shadowLeft2;
	private final View shadowRight;
	private final View shadowTop1;
	private final View shadowTop2;
	private final View shadowBottom;

	private final float shadowSmoothingPixels;

	/**
	 * Simple constructor to use when creating a view from code.
	 * 
	 * @param context
	 *            The Context the view is running in, through which it can
	 *            access the current theme, resources, etc.
	 */
	public TableFixHeaders(Context context) {
		this(context, null);
	}

	/**
	 * Constructor that is called when inflating a view from XML. This is called
	 * when a view is being constructed from an XML file, supplying attributes
	 * that were specified in the XML file. This version uses a default style of
	 * 0, so the only attribute values applied are those in the Context's Theme
	 * and the given AttributeSet.
	 * 
	 * The method onFinishInflate() will be called after all children have been
	 * added.
	 * 
	 * @param context
	 *            The Context the view is running in, through which it can
	 *            access the current theme, resources, etc.
	 * @param attrs
	 *            The attributes of the XML tag that is inflating the view.
	 */
	public TableFixHeaders(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;

		LayoutInflater.from(context).inflate(R.layout.fix_table_layout, this, true);

		scrollView = (ScrollView) findViewById(R.id.scroll);
		headerLinearLayout = (LinearLayout) findViewById(R.id.header_text);
		headerRowLinearLayout = (LinearLayout) findViewById(R.id.header_row);
		headerColumnLinearLayout = (LinearLayout) findViewById(R.id.header_column);
		bodyLinearLayout = (LinearLayout) findViewById(R.id.body);

		shadowLeft1 = findViewById(R.id.shadow_left_1);
		shadowLeft2 = findViewById(R.id.shadow_left_2);
		shadowRight = findViewById(R.id.shadow_right);
		shadowTop1 = findViewById(R.id.shadow_top_1);
		shadowTop2 = findViewById(R.id.shadow_top_2);
		shadowBottom = findViewById(R.id.shadow_bottom);

		shadowSmoothingPixels = getResources().getDisplayMetrics().density * SHADOW_SMOOTHING_DIP;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		boolean intercept = false;
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN: {
				currentX = (int) event.getRawX();
				currentY = (int) event.getRawY();
				break;
			}
			case MotionEvent.ACTION_MOVE: {
				int x2 = currentX - (int) event.getRawX();
				int y2 = currentY - (int) event.getRawY();
				if (x2 < -CLICK_SENSIVILITY || x2 > CLICK_SENSIVILITY || y2 < -CLICK_SENSIVILITY || y2 > CLICK_SENSIVILITY) {
					intercept = true;
				}
				break;
			}
		}
		return intercept;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// Idea: http://stackoverflow.com/a/4991692/842697
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN: {
				currentX = (int) event.getRawX();
				currentY = (int) event.getRawY();
				break;
			}
			case MotionEvent.ACTION_MOVE: {
				final int x2 = (int) event.getRawX();
				final int y2 = (int) event.getRawY();
				int scrollX = currentX - x2 + bodyLinearLayout.getScrollX();
				int scrollY = currentY - y2 + bodyLinearLayout.getScrollY();
				currentX = x2;
				currentY = y2;

				maxScrollX = bodyLinearLayout.getWidth() - (scrollView.getWidth() - headerColumnLinearLayout.getWidth());
				maxScrollY = bodyLinearLayout.getHeight() - (scrollView.getHeight() - headerRowLinearLayout.getHeight());

				if (scrollX < 0) {
					scrollX = Math.max(scrollX, 0);
				} else {
					scrollX = Math.min(scrollX, maxScrollX);
				}
				if (scrollY < 0) {
					scrollY = Math.max(scrollY, 0);
				} else {
					scrollY = Math.min(scrollY, maxScrollY);
				}

				headerRowLinearLayout.scrollTo(scrollX, 0);
				headerColumnLinearLayout.scrollTo(0, scrollY);
				bodyLinearLayout.scrollTo(scrollX, scrollY);

				showShadows(scrollX, scrollY);

				break;
			}
		}
		return true;
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void showShadows(int scrollX, int scrollY) {
		final int visibilityLeft = scrollX == 0 ? View.GONE : View.VISIBLE;
		final int visibilityRight = scrollX == maxScrollX ? View.GONE : View.VISIBLE;
		final int visibilityTop = scrollY == 0 ? View.GONE : View.VISIBLE;
		final int visibilityBottom = scrollY == maxScrollY ? View.GONE : View.VISIBLE;

		shadowLeft1.setVisibility(visibilityLeft);
		shadowLeft2.setVisibility(visibilityLeft);
		shadowRight.setVisibility(visibilityRight);
		shadowTop1.setVisibility(visibilityTop);
		shadowTop2.setVisibility(visibilityTop);
		shadowBottom.setVisibility(visibilityBottom);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) { // API 11
			final float alphaLeft = scrollX <= shadowSmoothingPixels ? scrollX / shadowSmoothingPixels : 1f;
			final float alphaRight = maxScrollX - scrollX <= shadowSmoothingPixels ? (maxScrollX - scrollX) / shadowSmoothingPixels : 1f;
			final float alphaTop = scrollY <= shadowSmoothingPixels ? scrollY / shadowSmoothingPixels : 1f;
			final float alphaBottom = scrollY - maxScrollY <= shadowSmoothingPixels ? (maxScrollY - scrollY) / shadowSmoothingPixels : 1f;

			shadowLeft1.setAlpha(alphaLeft);
			shadowLeft2.setAlpha(alphaLeft);
			shadowRight.setAlpha(alphaRight);
			shadowTop1.setAlpha(alphaTop);
			shadowTop2.setAlpha(alphaTop);
			shadowBottom.setAlpha(alphaBottom);
		}
	}

	/**
	 * Sets the data behind this TableFixHeaders.
	 * 
	 * @param adapter
	 *            The TableAdapter which is responsible for maintaining the data
	 *            backing this list and for producing a view to represent an
	 *            item in that data set.
	 */
	public void setAdapter(TableAdapter adapter) {
		this.adapter = adapter;

		if (adapter instanceof TableAdapter.Observable) {
			((TableAdapter.Observable) this.adapter).registerDataSetObserver(new DataSetObserver() {

				/**
				 * This overrides onChanged
				 * 
				 * @see android.database.DataSetObserver#onChanged()
				 */
				@Override
				public void onChanged() {
					onDataChange();
				}
			});
		}

		onDataChange();

		// FIXME: HACK I do not know how to measure the view before it is included in the parent view.
		Handler handler = new Handler(context.getMainLooper());
		handler.postDelayed(new Runnable() {

			@Override
			public void run() {
				maxScrollX = bodyLinearLayout.getWidth() - (scrollView.getWidth() - headerColumnLinearLayout.getWidth());
				maxScrollY = bodyLinearLayout.getHeight() - (scrollView.getHeight() - headerRowLinearLayout.getHeight());

				showShadows(0, 0);
			}
		}, 35);
	}

	public void onDataChange() {
		headerLinearLayout.removeAllViews();
		headerRowLinearLayout.removeAllViews();
		headerColumnLinearLayout.removeAllViews();
		bodyLinearLayout.removeAllViews();

		final int width = adapter.getWidth(-1);
		final int height = adapter.getHeight(-1);

		final View view = adapter.getView(-1, -1, headerLinearLayout);
		setupView(view, width, height, -1);

		headerLinearLayout.addView(view);
		fillRow();
		fillColumn();
		fillBody();
	}

	private void fillRow() {
		final int count = adapter.getColumnCount();
		final int height = adapter.getHeight(-1);
		for (int i = 0; i < count; i++) {
			final int width = adapter.getWidth(i);

			final View view = adapter.getView(-1, i, headerRowLinearLayout);
			setupView(view, width, height, width);

			headerRowLinearLayout.addView(view);
		}
	}

	private void fillColumn() {
		final int count = adapter.getRowCount();
		final int width = adapter.getWidth(-1);
		for (int i = 0; i < count; i++) {
			final int height = adapter.getHeight(i);

			final View view = adapter.getView(i, -1, headerColumnLinearLayout);
			setupView(view, width, height, -1);

			headerColumnLinearLayout.addView(view);
		}
	}

	private void fillBody() {
		final int count = adapter.getRowCount();
		for (int i = 0; i < count; i++) {
			final int height = adapter.getHeight(i);

			LinearLayout linearLayout = new LinearLayout(context);
			linearLayout.setOrientation(LinearLayout.HORIZONTAL);
			linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height));
			fillBodyRow(linearLayout, i);
			bodyLinearLayout.addView(linearLayout);
		}
	}

	private void fillBodyRow(LinearLayout linearLayoutParent, int row) {
		final int count = adapter.getColumnCount();
		final int height = adapter.getHeight(row);
		for (int i = 0; i < count; i++) {
			final int width = adapter.getWidth(i);

			final View view = adapter.getView(row, i, linearLayoutParent);
			setupView(view, width, height, width);

			linearLayoutParent.addView(view);
		}
	}

	private void setupView(View view, int width, int height, int weight) {
		ViewGroup.LayoutParams layoutParams = view.getLayoutParams();

		if (layoutParams == null) {
			layoutParams = new LinearLayout.LayoutParams(width, height, weight);
		} else {
			if (!(layoutParams instanceof LinearLayout.LayoutParams)) {
				layoutParams = new LinearLayout.LayoutParams(layoutParams);
			}
			layoutParams.width = width;
			layoutParams.height = height;
			((LinearLayout.LayoutParams) layoutParams).weight = weight;
		}
		view.setLayoutParams(layoutParams);

		view.setMinimumWidth(width);
	}
}
