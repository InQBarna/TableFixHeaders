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
		int h = 0;
		int nextH;
		for (int i = -1; i < rowCount; i++) {
			nextH = h + heights[i + 1];
			int w = 0;
			int nextW;
			for (int j = -1; j < columnCount; j++) {
				final View view = adapter.getView(i, j, this);
				view.measure(MeasureSpec.makeMeasureSpec(widths[j + 1], MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(heights[i + 1], MeasureSpec.EXACTLY));
				addView(i, j, view);
				nextW = w + widths[j + 1];
				int a[] = { w, h, nextW, nextH };
				if (i != -1) {
					a[1] -= scrollY;
					a[3] -= scrollY;
				}
				if (j != -1) {
					a[0] -= scrollX;
					a[2] -= scrollX;
				}
				view.layout(a[0], a[1], a[2], a[3]);
				w = nextW;
			}
			h = nextH;
		}
	}

	private void addView(int row, int column, View view) {
		if (row == -1 && column == -1) {
			addView(view);
		} else if (row == -1 || column == -1) {
			addView(view, getChildCount() - 1); // No problem with index -1
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
