package com.inqbarna.tablefixheaders;

import com.inqbarna.tablefixheaders.adapters.TableAdapter;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableRow;

/**
 * This view shows a table which can scroll in both directions. Also still
 * leaves the headers fixed.
 * 
 * @author Brais Gabín
 */
public class TableFixHeaders extends LinearLayout {
	private final static int CLICK_SENSIVILITY = 2;

	private final Context context;

	private final LinearLayout headerLinearLayout;
	private final LinearLayout headerRowLinearLayout;
	private final LinearLayout headerColumnLinearLayout;
	private final LinearLayout bodyLinearLayout;

	private int currentX;
	private int currentY;

	private TableAdapter adapter;

	private ScrollView scrollView;

	private int maxScrollX;
	private int maxScrollY;

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
		headerLinearLayout = ((LinearLayout) findViewById(R.id.header_text));
		headerRowLinearLayout = ((LinearLayout) findViewById(R.id.header_row));
		headerColumnLinearLayout = ((LinearLayout) findViewById(R.id.header_column));
		bodyLinearLayout = ((LinearLayout) findViewById(R.id.body));
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
				int x2 = (int) (currentX - event.getRawX());
				int y2 = (int) (currentY - event.getRawY());
				if (!((x2 > -CLICK_SENSIVILITY && x2 < CLICK_SENSIVILITY) || (y2 > -CLICK_SENSIVILITY && y2 < CLICK_SENSIVILITY))) {
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
				int x2 = (int) event.getRawX();
				int y2 = (int) event.getRawY();
				int scrollX = currentX - x2;
				int scrollY = currentY - y2;
				currentX = x2;
				currentY = y2;

				maxScrollX = bodyLinearLayout.getWidth() - (scrollView.getWidth() - headerColumnLinearLayout.getWidth());
				maxScrollY = bodyLinearLayout.getHeight() - (scrollView.getHeight() - headerRowLinearLayout.getHeight());

				if (scrollX < 0) {
					scrollX = Math.max(scrollX + bodyLinearLayout.getScrollX(), 0);
				} else {
					scrollX = Math.min(scrollX + bodyLinearLayout.getScrollX(), maxScrollX);
				}
				if (scrollY < 0) {
					scrollY = Math.max(scrollY + bodyLinearLayout.getScrollY(), 0);
				} else {
					scrollY = Math.min(scrollY + bodyLinearLayout.getScrollY(), maxScrollY);
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

	private void showShadows(int scrollX, int scrollY) {
		final int visibilityLeft = scrollX == 0 ? View.GONE : View.VISIBLE;
		final int visibilityRight = scrollX == maxScrollX ? View.GONE : View.VISIBLE;
		final int visibilityTop = scrollY == 0 ? View.GONE : View.VISIBLE;
		final int visibilityBottom = scrollY == maxScrollY ? View.GONE : View.VISIBLE;

		findViewById(R.id.shadow_left_1).setVisibility(visibilityLeft);
		findViewById(R.id.shadow_left_2).setVisibility(visibilityLeft);
		findViewById(R.id.shadow_right).setVisibility(visibilityRight);
		findViewById(R.id.shadow_top_1).setVisibility(visibilityTop);
		findViewById(R.id.shadow_top_2).setVisibility(visibilityTop);
		findViewById(R.id.shadow_bottom).setVisibility(visibilityBottom);
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

		onDataChange();

		showShadows(0, 0);
	}

	private void onDataChange() {
		headerLinearLayout.removeAllViews();
		headerRowLinearLayout.removeAllViews();
		headerColumnLinearLayout.removeAllViews();
		bodyLinearLayout.removeAllViews();

		LinearLayout linearLayout = new LinearLayout(context);
		linearLayout.setLayoutParams(new TableRow.LayoutParams(adapter.getWidth(-1), adapter.getHeight(-1)));
		linearLayout.addView(adapter.getView(-1, -1, linearLayout));
		headerLinearLayout.addView(linearLayout);
		fillRow();
		fillColumn();
		fillBody();
	}

	private void fillRow() {
		final int count = adapter.getColumnCount();
		for (int i = 0; i < count; i++) {
			LinearLayout linearLayout = new LinearLayout(context);
			linearLayout.setLayoutParams(new LinearLayout.LayoutParams(adapter.getWidth(i), adapter.getHeight(-1)));
			linearLayout.addView(adapter.getView(-1, i, linearLayout));
			headerRowLinearLayout.addView(linearLayout);
		}
	}

	private void fillColumn() {
		final int count = adapter.getRowCount();
		for (int i = 0; i < count; i++) {
			LinearLayout linearLayout = new LinearLayout(context);
			linearLayout.setLayoutParams(new LinearLayout.LayoutParams(adapter.getWidth(-1), adapter.getHeight(i)));
			linearLayout.addView(adapter.getView(i, -1, linearLayout));
			headerColumnLinearLayout.addView(linearLayout);
		}
	}

	private void fillBody() {
		final int count = adapter.getRowCount();
		for (int i = 0; i < count; i++) {
			LinearLayout linearLayout = new LinearLayout(context);
			linearLayout.setOrientation(LinearLayout.HORIZONTAL);
			linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, adapter.getHeight(i)));
			fillBodyRow(linearLayout, i);
			bodyLinearLayout.addView(linearLayout);
		}
	}

	private void fillBodyRow(LinearLayout linearLayoutParent, int row) {
		final int count = adapter.getColumnCount();
		for (int i = 0; i < count; i++) {
			LinearLayout linearLayout = new LinearLayout(context);
			linearLayout.setLayoutParams(new LinearLayout.LayoutParams(adapter.getWidth(i), adapter.getHeight(row)));
			linearLayout.addView(adapter.getView(row, i, linearLayout));
			linearLayoutParent.addView(linearLayout);
		}
	}
}
