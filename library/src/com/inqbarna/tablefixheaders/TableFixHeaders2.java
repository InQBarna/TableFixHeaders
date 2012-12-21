package com.inqbarna.tablefixheaders;

import java.util.List;

import com.inqbarna.tablefixheaders.adapters.TableAdapter;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

public class TableFixHeaders2 extends ViewGroup {
	private final static int CLICK_SENSIVILITY = 2;

	private int currentX;
	private int currentY;

	private TableAdapter adapter;
	private int scrollX = 0;
	private int scrollY = 0;
	private int firstRow;
	private int firstColumn;

	private View head;
	private List<View> row;
	private List<View> column;
	private List<List<View>> table;

	public TableFixHeaders2(Context context) {
		super(context);
	}

	public TableFixHeaders2(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public TableAdapter getAdapter() {
		return adapter;
	}

	public void setAdapter(TableAdapter adapter) {
		this.adapter = adapter;

		requestLayout();
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
				scrollX += currentX - x2;
				scrollY += currentY - y2;
				currentX = x2;
				currentY = y2;

				requestLayout();
				break;
			}
		}
		return true;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		final int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		final int heightSize = MeasureSpec.getSize(heightMeasureSpec);

		final int w;
		final int h;

		if (adapter != null) {
			if (widthMode == MeasureSpec.AT_MOST || widthMode == MeasureSpec.UNSPECIFIED) {
				w = Math.min(widthSize, getAdapterDesiredWidth());
			} else {
				w = widthSize;
			}

			if (heightMode == MeasureSpec.AT_MOST || widthMode == MeasureSpec.UNSPECIFIED) {
				h = Math.min(heightSize, getAdapterDesiredHeight());
			} else {
				h = heightSize;
			}
		} else {
			if (heightMode == MeasureSpec.AT_MOST || widthMode == MeasureSpec.UNSPECIFIED) {
				w = 0;
				h = 0;
			} else {
				w = widthSize;
				h = heightSize;
			}
		}

		setMeasuredDimension(w, h);
	}

	private int getAdapterDesiredWidth() {
		final int columnCount = adapter.getColumnCount();
		int desired = 0;
		for (int i = -1; i < columnCount; i++) {
			desired += adapter.getWidth(i);
		}
		return desired;
	}

	private int getAdapterDesiredHeight() {
		final int rowCount = adapter.getRowCount();
		int desired = 0;
		for (int i = -1; i < rowCount; i++) {
			desired += adapter.getHeight(i);
		}
		return desired;
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		System.out.println("boolean " + changed + ", int " + l + ", int " + t + ", int " + r + ", int " + b);

		removeAllViews();
		final int rowCount = adapter.getRowCount();
		final int columnCount = adapter.getColumnCount();

		final int[] widths = getAdapterWidths();
		final int[] heights = getAdapterHeights();
		int left, top, right, bottom;

		makeAndSetup(-1, -1, 0, 0, widths[0], heights[0]);

		left = widths[0] - scrollX;
		for (int i = firstColumn; i < columnCount; i++) {
			right = left + widths[i + 1];
			makeAndSetup(-1, i, left, 0, right, heights[0]);
			left = right;
		}

		top = heights[0] - scrollY;
		for (int i = firstColumn; i < columnCount; i++) {
			bottom = top + heights[i + 1];
			makeAndSetup(i, -1, 0, top, widths[0], bottom);
			top = bottom;
		}

		top = heights[0] - scrollY;
		for (int i = firstRow; i < rowCount; i++) {
			bottom = top + heights[i + 1];
			left = widths[0] - scrollX;
			for (int j = firstColumn; j < columnCount; j++) {
				right = left + widths[j + 1];
				makeAndSetup(i, j, left, top, right, bottom);
				left = right;
			}
			top = bottom;
		}
	}

	private void makeAndSetup(int row, int column, int left, int top, int right, int bottom) {
		final View view = adapter.getView(row, column, this);
		view.measure(MeasureSpec.makeMeasureSpec(left - right, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(top - bottom, MeasureSpec.EXACTLY));
		addTableView(view, row, column);
		view.layout(left, top, right, bottom);
	}

	private void addTableView(View view, int row, int column) {
		if (row == -1 && column == -1) {
			addView(view);
		} else if (row == -1 || column == -1) {
			addView(view, getChildCount() - 1);
		} else {
			addView(view, 0);
		}
	}

	private int[] getAdapterWidths() {
		final int columnCount = adapter.getColumnCount();
		int array[] = new int[columnCount + 1];
		for (int i = -1; i < columnCount; i++) {
			array[i + 1] += adapter.getWidth(i);
		}
		return array;
	}

	private int[] getAdapterHeights() {
		final int rowCount = adapter.getRowCount();
		int array[] = new int[rowCount + 1];
		for (int i = -1; i < rowCount; i++) {
			array[i + 1] += adapter.getHeight(i);
		}
		return array;
	}
}
